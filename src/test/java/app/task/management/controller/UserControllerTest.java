package app.task.management.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.task.management.dto.user.UserDto;
import app.task.management.dto.user.UserRoleUpdateDto;
import app.task.management.dto.user.UserUpdateDto;
import app.task.management.model.RoleName;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
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
    }

    private static void executeSqlScript(DataSource dataSource, String scriptPath)
            throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(scriptPath));
        }
    }

    @BeforeEach
    void beforeEach(@Autowired DataSource dataSource) throws Exception {
        executeSqlScript(dataSource, "database/users/add-users.sql");
    }

    @AfterEach
    void afterEach(@Autowired DataSource dataSource) throws Exception {
        executeSqlScript(dataSource, "database/users/delete-users.sql");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Get profile info")
    void getUserInfo_ok() throws Exception {
        UserDto expected = new UserDto();
        expected.setFirstName("First");
        expected.setLastName("Last");
        expected.setUsername("admin");
        expected.setEmail("admin@gmail.com");

        MvcResult result = mockMvc.perform(
                get("/api/users/me")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andReturn();

        UserDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), UserDto.class);

        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update profile info")
    void updateProfileInfo_ok() throws Exception {
        UserUpdateDto requestDto = new UserUpdateDto();
        requestDto.setUsername("admin");
        requestDto.setFirstName("UPD first name");
        requestDto.setLastName("UPD last name");

        UserDto expected = new UserDto();
        expected.setFirstName(requestDto.getFirstName());
        expected.setLastName(requestDto.getLastName());
        expected.setUsername(requestDto.getUsername());
        expected.setEmail("admin@gmail.com");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                put("/api/users/me")
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andReturn();

        UserDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), UserDto.class);

        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update user role")
    void updateUserRole_validRequest_ok() throws Exception {
        UserRoleUpdateDto requestDto = new UserRoleUpdateDto();
        requestDto.setRole(RoleName.ROLE_ADMIN);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                put("/api/users/4/role")
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andReturn();
    }
}
