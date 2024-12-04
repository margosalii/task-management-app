package app.task.management.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.task.management.dto.task.CreateTaskRequestDto;
import app.task.management.dto.task.TaskDetailsDto;
import app.task.management.dto.task.TaskResponseDto;
import app.task.management.dto.task.UpdateTaskDto;
import app.task.management.model.Priority;
import app.task.management.model.Status;
import app.task.management.model.Task;
import app.task.management.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.sql.DataSource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerTest {
    private static MockMvc mockMvc;
    private static Set<TaskResponseDto> responseDtos;
    private static TaskDetailsDto detailsDto;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository repository;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext,
            @Autowired DataSource dataSource) throws Exception {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(applicationContext)
            .apply(springSecurity())
            .build();
        generateTaskDtos();
        executeSqlScript(dataSource, "database/users/add-users.sql");
    }

    private static void generateTaskDtos() {
        responseDtos = new HashSet<>();
        TaskResponseDto firstTask = new TaskResponseDto();
        firstTask.setId(1L);
        firstTask.setProjectId(1L);
        firstTask.setStatus(Status.COMPLETED);
        firstTask.setDescription("First description");
        firstTask.setDueDate(LocalDate.of(2023, 11, 11));

        TaskResponseDto secondTask = new TaskResponseDto();
        secondTask.setId(2L);
        secondTask.setProjectId(1L);
        secondTask.setStatus(Status.COMPLETED);
        secondTask.setDescription("Second description");
        secondTask.setDueDate(LocalDate.of(2013, 11, 11));

        responseDtos.add(firstTask);
        responseDtos.add(secondTask);

        detailsDto = new TaskDetailsDto();
        detailsDto.setId(firstTask.getId());
        detailsDto.setProjectId(firstTask.getProjectId());
        detailsDto.setDescription(firstTask.getDescription());
        detailsDto.setDueDate(firstTask.getDueDate());
        detailsDto.setStatus(firstTask.getStatus());
        detailsDto.setPriority(Priority.HIGH);
        detailsDto.setAssigneeId(3L);
        detailsDto.setLabels(new HashSet<>());

    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) throws Exception {
        executeSqlScript(dataSource, "database/users/delete-users.sql");
    }

    private static void executeSqlScript(DataSource dataSource, String scriptPath)
            throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(scriptPath));
        }
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(
            scripts = "classpath:database/projects/add-projects.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/tasks/delete-tasks.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Create a new task")
    void createTask_validRequest_ok() throws Exception {
        CreateTaskRequestDto requestDto = new CreateTaskRequestDto();
        requestDto.setPriority(Priority.HIGH);
        requestDto.setProjectId(1L);
        requestDto.setDescription("Description");
        requestDto.setDueDate(LocalDate.of(2024, 11,20));

        TaskDetailsDto expected = new TaskDetailsDto();
        expected.setId(1L);
        expected.setDescription(requestDto.getDescription());
        expected.setProjectId(requestDto.getProjectId());
        expected.setPriority(requestDto.getPriority());
        expected.setDueDate(requestDto.getDueDate());
        expected.setStatus(Status.NOT_STARTED);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                post("/api/tasks")
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andReturn();

        TaskDetailsDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), TaskDetailsDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(
            scripts = "classpath:database/tasks/add-tasks.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/tasks/delete-tasks.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Get all tasks")
    void getAllTasks_ok() throws Exception {
        Set<TaskResponseDto> expected = responseDtos;

        MvcResult result = mockMvc.perform(
                get("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        TaskResponseDto[] actualArray = objectMapper.readValue(result
            .getResponse()
            .getContentAsByteArray(), TaskResponseDto[].class);

        Set<TaskResponseDto> actual = new HashSet<>(Arrays.asList(actualArray));

        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/tasks/add-tasks.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/tasks/delete-tasks.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @DisplayName("Get task details")
    void getTaskDetails_validRequest_ok() throws Exception {
        TaskDetailsDto expected = detailsDto;

        MvcResult result = mockMvc.perform(
                get("/api/tasks/1")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        TaskDetailsDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), TaskDetailsDto.class);

        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/tasks/add-tasks.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/tasks/delete-tasks.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @DisplayName("Update task")
    void updateTask_validRequest_ok() throws Exception {
        TaskDetailsDto expected = new TaskDetailsDto();
        expected.setId(detailsDto.getId());
        expected.setProjectId(detailsDto.getProjectId());
        expected.setDescription("New description");
        expected.setDueDate(detailsDto.getDueDate());
        expected.setStatus(Status.IN_PROGRESS);
        expected.setPriority(Priority.LOW);
        expected.setAssigneeId(detailsDto.getAssigneeId());
        expected.setLabels(detailsDto.getLabels());

        UpdateTaskDto request = new UpdateTaskDto();
        request.setStatus(expected.getStatus());
        request.setDescription(expected.getDescription());
        request.setPriority(expected.getPriority());
        request.setDueDate(expected.getDueDate());

        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(
                put("/api/tasks/1")
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        TaskDetailsDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), TaskDetailsDto.class);

        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/tasks/add-tasks.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/tasks/delete-tasks.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @DisplayName("Delete task")
    void deleteTask_validRequest_ok() throws Exception {
        MvcResult result = mockMvc.perform(
                delete("/api/tasks/1")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Optional<Task> deletedTask = repository.findById(1L);
        Assertions.assertFalse(deletedTask.isPresent());
    }
}
