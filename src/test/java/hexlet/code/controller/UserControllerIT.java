package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.UserDto;
import hexlet.code.dto.LoginDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.utils.TestUtils.USER_DTO;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.TEST_USERNAME_2;
import static hexlet.code.utils.TestUtils.MAPPER;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestUtils utils;

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void registration() throws Exception {

        assertThat(0).isEqualTo(userRepository.count());

        final MockHttpServletRequestBuilder request = post("/api/users")
                .content(MAPPER.writeValueAsString(USER_DTO))
                .contentType(APPLICATION_JSON);

        final MockHttpServletResponse response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        assertThat(1).isEqualTo(userRepository.count());
    }

    @Test
    public void twiceRegTheSameUserFail() throws Exception {

        utils.regUser(USER_DTO).andExpect(status().isCreated());
        utils.regUser(USER_DTO).andExpect(status().isUnprocessableEntity());

        assertThat(1).isEqualTo(userRepository.count());
    }

    @Test
    public void getUserById() throws Exception {
        utils.regUser(USER_DTO);

        final User expectedUser = userRepository.findAll().get(0);

        final MockHttpServletResponse response = utils.perform(
                        get("/api/users/{id}", expectedUser.getId()),
                        expectedUser.getEmail()
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final User user = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertThat(expectedUser.getId()).isEqualTo(user.getId());
        assertThat(expectedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(expectedUser.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(expectedUser.getLastName()).isEqualTo(user.getLastName());
    }

    @Test
    public void getUserByIdFail() throws Exception {
        utils.regUser(USER_DTO);

        final User expectedUser = userRepository.findAll().get(0);

        utils.perform(
                        get("/api/users/{id}", expectedUser.getId() + 1),
                        expectedUser.getEmail()
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllUsers() throws Exception {
        utils.regUser(USER_DTO);

        final MockHttpServletResponse response = utils.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<User> users = fromJson(response.getContentAsString(), new TypeReference<>() { });
        final List<User> expectedUsers =  userRepository.findAll();

        assertThat(users).hasSize(1);
        assertThat(users).containsAll(expectedUsers);
    }

    @Test
    public void login() throws Exception {
        utils.regUser(USER_DTO);

        final LoginDto loginDto = new LoginDto(
                USER_DTO.getEmail(),
                USER_DTO.getPassword()
        );

        final MockHttpServletRequestBuilder loginRequest = post("/api/login")
                .content(MAPPER.writeValueAsString(loginDto))
                .contentType(APPLICATION_JSON);

        utils.perform(loginRequest).andExpect(status().isOk());
    }

    @Test
    public void loginFail() throws Exception {
        final LoginDto loginDto = new LoginDto(
                USER_DTO.getEmail(),
                USER_DTO.getPassword()
        );

        final MockHttpServletRequestBuilder loginRequest = post("/api/login")
                .content(MAPPER.writeValueAsString(loginDto))
                .contentType(APPLICATION_JSON);

        utils.perform(loginRequest).andExpect(status().isUnauthorized());
    }

    @Test
    public void updateUser() throws Exception {
        utils.regUser(USER_DTO);

        final Long userId = userRepository.findByEmail(TEST_USERNAME).orElseThrow().getId();

        final var userDto = new UserDto("newEmail@mail.ru", "new name", "new last name", "newpassword");

        final var updateRequest = put("/api/users/{id}", userId)
                .content(MAPPER.writeValueAsString(userDto))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, TEST_USERNAME).andExpect(status().isOk());

        assertThat(userRepository.existsById(userId)).isTrue();
        assertThat(userRepository.findByEmail(TEST_USERNAME).orElse(null)).isNull();
        assertThat(userRepository.findByEmail("newEmail@mail.ru").orElse(null)).isNotNull();
    }

    @Test
    public void deleteUser() throws Exception {
        utils.regUser(USER_DTO);

        final Long userId = userRepository.findByEmail(TEST_USERNAME).orElseThrow().getId();

        utils.perform(delete("/api/users/{id}", userId), TEST_USERNAME)
                .andExpect(status().isOk());

        assertThat(0).isEqualTo(userRepository.count());
    }

    @Test
    public void deleteUserFails() throws Exception {
        utils.regUser(USER_DTO);

        UserDto secondUser = new UserDto(
                TEST_USERNAME_2,
                "Max",
                "Max",
                "password");

        final MockHttpServletRequestBuilder request = post("/api/users")
                .content(MAPPER.writeValueAsString(secondUser))
                .contentType(APPLICATION_JSON);
        mockMvc.perform(request);

        final Long userId = userRepository.findByEmail(TEST_USERNAME).orElseThrow().getId();

        utils.perform(delete("/api/users/{id}", userId), TEST_USERNAME_2)
                .andExpect(status().isForbidden());

        assertThat(2).isEqualTo(userRepository.count());
    }
}
