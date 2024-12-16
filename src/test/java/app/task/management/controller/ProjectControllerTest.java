package app.task.management.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.task.management.dto.project.CreateProjectDto;
import app.task.management.dto.project.ProjectDetailsResponseDto;
import app.task.management.dto.project.ProjectResponseDto;
import app.task.management.dto.project.UpdateRequestProjectDto;
import app.task.management.model.Project;
import app.task.management.model.Status;
import app.task.management.repository.ProjectRepository;
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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProjectControllerTest {
    private static MockMvc mockMvc;
    private static Set<ProjectResponseDto> projectResponseDtos;
    private static ProjectDetailsResponseDto detailsResponseDto;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository repository;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext,
            @Autowired DataSource dataSource) throws Exception {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(applicationContext)
            .apply(springSecurity())
            .build();
        generateProjectDtos();
        executeSqlScript(dataSource, "database/users/add-users.sql");
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) throws Exception {
        executeSqlScript(dataSource, "database/users/delete-users.sql");
    }

    private static void generateProjectDtos() {
        projectResponseDtos = new HashSet<>();
        ProjectResponseDto first = new ProjectResponseDto();
        first.setId(1L);
        first.setName("First project");
        first.setDescription("Description");
        first.setStatus(Status.INITIATED);

        ProjectResponseDto second = new ProjectResponseDto();
        second.setId(2L);
        second.setName("Second project");
        second.setDescription("Description");
        second.setStatus(Status.INITIATED);
        projectResponseDtos.add(first);
        projectResponseDtos.add(second);

        detailsResponseDto = new ProjectDetailsResponseDto();
        detailsResponseDto.setId(first.getId());
        detailsResponseDto.setName(first.getName());
        detailsResponseDto.setStatus(first.getStatus());
        detailsResponseDto.setDescription(first.getDescription());
        detailsResponseDto.setStartDate(LocalDate.of(2003,12,1));
        detailsResponseDto.setEndDate(LocalDate.of(2013, 12, 1));
        detailsResponseDto.setTasks(new HashSet<>());
    }

    private static void executeSqlScript(DataSource dataSource, String scriptPath)
            throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(scriptPath));
        }
    }

    @WithUserDetails(value = "admin")
    @Test
    @DisplayName("Create a new project")
    void createProject_validRequestDto_ok() throws Exception {
        CreateProjectDto projectDto = new CreateProjectDto();
        projectDto.setName("First");
        projectDto.setDescription("Description");
        projectDto.setStartDate(LocalDate.now());
        projectDto.setEndDate(LocalDate.now().plusDays(5));

        ProjectDetailsResponseDto expected = new ProjectDetailsResponseDto();
        expected.setId(1L);
        expected.setStatus(Status.INITIATED);
        expected.setDescription(projectDto.getDescription());
        expected.setStartDate(projectDto.getStartDate());
        expected.setEndDate(projectDto.getEndDate());

        String jsonRequest = objectMapper.writeValueAsString(projectDto);

        MvcResult result = mockMvc.perform(
                post("/projects")
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andReturn();

        ProjectDetailsResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ProjectDetailsResponseDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithUserDetails(value = "admin")
    @Test
    @Sql(
            scripts = "classpath:database/projects/add-projects.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/projects/delete-projects.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Get all projects")
    void getAllProjects_ok() throws Exception {
        Set<ProjectResponseDto> expected = projectResponseDtos;

        MvcResult result = mockMvc.perform(
                get("/projects").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ProjectResponseDto[] actualArray = objectMapper.readValue(result
            .getResponse()
            .getContentAsByteArray(), ProjectResponseDto[].class);

        Set<ProjectResponseDto> actual = new HashSet<>(Arrays.asList(actualArray));

        Assertions.assertEquals(expected, actual);
    }

    @WithUserDetails(value = "admin")
    @Test
    @Sql(
            scripts = "classpath:database/projects/add-projects.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/projects/delete-projects.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Get project by ID")
    void getProjectById_ok() throws Exception {
        ProjectDetailsResponseDto expected = detailsResponseDto;

        MvcResult result = mockMvc.perform(
                get("/projects/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ProjectDetailsResponseDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsByteArray(), ProjectDetailsResponseDto.class);

        Assertions.assertEquals(expected, actual);
    }

    @WithUserDetails(value = "admin")
    @Test
    @Sql(
            scripts = "classpath:database/projects/add-projects.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/projects/delete-projects.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Update project by ID")
    void updateProjectById_ok() throws Exception {
        ProjectDetailsResponseDto expected = new ProjectDetailsResponseDto();
        expected.setId(detailsResponseDto.getId());
        expected.setName("UpdatedName");
        expected.setDescription(detailsResponseDto.getDescription());
        expected.setStatus(Status.IN_PROGRESS);
        expected.setStartDate(detailsResponseDto.getStartDate());
        expected.setEndDate(detailsResponseDto.getEndDate());
        expected.setTasks(new HashSet<>(detailsResponseDto.getTasks()));

        UpdateRequestProjectDto request = new UpdateRequestProjectDto();
        request.setStatus(expected.getStatus());
        request.setName(expected.getName());
        request.setDescription(expected.getDescription());
        request.setStartDate(expected.getStartDate());
        request.setEndDate(expected.getEndDate());

        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(put("/projects/1")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andReturn();

        ProjectDetailsResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), ProjectDetailsResponseDto.class);

        Assertions.assertEquals(expected, actual);
    }

    @WithUserDetails(value = "admin")
    @Test
    @Sql(
            scripts = "classpath:database/projects/add-projects.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/projects/delete-projects.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Delete project by ID")
    void deleteProjectById_ok() throws Exception {
        mockMvc.perform(delete("/projects/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Optional<Project> deletedProject = repository.findById(1L);
        Assertions.assertFalse(deletedProject.isPresent());
    }
}
