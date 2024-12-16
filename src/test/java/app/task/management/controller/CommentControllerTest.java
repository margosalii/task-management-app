package app.task.management.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.task.management.dto.comment.RequestCommentDto;
import app.task.management.dto.comment.ResponseCommentDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;
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
class CommentControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext,
            @Autowired DataSource dataSource) throws Exception {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(applicationContext)
            .apply(springSecurity())
            .build();
        executeSqlScript(dataSource, "database/users/add-users.sql");
    }

    private static void executeSqlScript(DataSource dataSource, String scriptPath)
            throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(scriptPath));
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) throws Exception {
        executeSqlScript(dataSource, "database/users/delete-users.sql");
    }

    @WithUserDetails(value = "admin")
    @Test
    @Sql(
            scripts = "classpath:database/tasks/add-tasks.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                "classpath:database/comments/delete-comments.sql",
                "classpath:database/tasks/delete-tasks.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Create a new comment")
    void createComment_validRequest_ok() throws Exception {
        RequestCommentDto requestDto = new RequestCommentDto();
        requestDto.setTaskId(1L);
        requestDto.setText("New comment");

        ResponseCommentDto expected = new ResponseCommentDto();
        expected.setId(3L);
        expected.setText(requestDto.getText());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                post("/comments")
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andReturn();

        ResponseCommentDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), ResponseCommentDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getText(), actual.getText());
    }

    @WithUserDetails(value = "admin")
    @Test
    @Sql(
            scripts = {
                "classpath:database/tasks/add-tasks.sql",
                "classpath:database/comments/add-comments.sql",
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                "classpath:database/comments/delete-comments.sql",
                "classpath:database/tasks/delete-tasks.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Get all task comments")
    void getAllComments_validRequest_ok() throws Exception {
        ResponseCommentDto firstComment = new ResponseCommentDto();
        firstComment.setId(1L);
        firstComment.setText("First comment");
        firstComment.setTimestamp(LocalDateTime.of(2024,11,20, 12,30, 45));

        Set<ResponseCommentDto> expected = new HashSet<>();
        expected.add(firstComment);

        MvcResult result = mockMvc.perform(
                get("/comments/1")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andReturn();

        ResponseCommentDto[] actualArray = objectMapper.readValue(result.getResponse()
            .getContentAsByteArray(), ResponseCommentDto[].class);

        Set<ResponseCommentDto> actual = new HashSet<>(Arrays.asList(actualArray));

        Assertions.assertEquals(expected, actual);
    }
}
