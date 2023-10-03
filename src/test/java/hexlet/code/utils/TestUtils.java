package hexlet.code.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.JWTHelper;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {

    public static final String TEST_USERNAME = "alex@mail.ru";
    public static final String TEST_USERNAME_2 = "max@mail.ru";
    public static final String TEST_TASK_STATUS_NAME = "TaskStatusName";
    public static final String TEST_TASK_NAME = "TaskName";
    public static final String TEST_TASK_DESCRIPTION = "TaskDescription";
    public static final UserDto REGISTRATION_DTO = new UserDto(
            TEST_USERNAME,
            "Alex",
            "Alex",
            "password");
    public static final TaskStatusDto TASK_STATUS_DTO = new TaskStatusDto(TEST_TASK_STATUS_NAME);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private JWTHelper jwtHelper;

    public static final ObjectMapper MAPPER = new ObjectMapper();


    public void tearDown() {
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();
    }

    public User getUserByEmail(final String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public ResultActions regUser(final UserDto userDto) throws Exception {
        MockHttpServletRequestBuilder request = post("/api/users")
                .content(MAPPER.writeValueAsString(userDto))
                .contentType(APPLICATION_JSON);

        return perform(request);
    }

    public ResultActions createTaskStatus(final TaskStatusDto taskStatusDto) throws Exception {
        final MockHttpServletRequestBuilder request = post("/api/statuses")
                .content(MAPPER.writeValueAsString(taskStatusDto))
                .contentType(APPLICATION_JSON);

        return perform(request, TEST_USERNAME);
    }

    public ResultActions createTask(final TaskDto taskDto) throws Exception {
        final MockHttpServletRequestBuilder request = post("/api/tasks")
                .content(MAPPER.writeValueAsString(taskDto))
                .contentType(APPLICATION_JSON);

        return perform(request, TEST_USERNAME);
    }

    public ResultActions createLabel(final LabelDto labelDto) throws Exception {
        final MockHttpServletRequestBuilder request = post("/api/labels")
                .content(MAPPER.writeValueAsString(labelDto))
                .contentType(APPLICATION_JSON);

        return perform(request, TEST_USERNAME);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request, final String byUser) throws Exception {
        final String token = jwtHelper.expiring(Map.of("username", byUser));
        request.header(AUTHORIZATION, token);

        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }
}
