package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.utils.TestUtils.TEST_TASK_STATUS_NAME;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.REGISTRATION_DTO;
import static hexlet.code.utils.TestUtils.MAPPER;
import static hexlet.code.utils.TestUtils.TASK_STATUS_DTO;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskStatusControllerIT {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TestUtils utils;

    @BeforeEach
    public void before() throws Exception {
        utils.regUser(REGISTRATION_DTO);
    }

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void createTaskStatus() throws Exception {
        assertThat(0).isEqualTo(taskStatusRepository.count());

        MockHttpServletResponse response = utils.createTaskStatus(TASK_STATUS_DTO)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final TaskStatus savedTaskStatus = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertThat(1).isEqualTo(taskStatusRepository.count());
        assertThat(taskStatusRepository.getReferenceById(savedTaskStatus.getId())).isNotNull();
    }

    @Test
    public void createTaskStatusFail() throws Exception {
        final TaskStatusDto taskStatusDto = new TaskStatusDto("some other task");

        final MockHttpServletRequestBuilder request = post("/api/statuses")
                .content(MAPPER.writeValueAsString(taskStatusDto))
                .contentType(APPLICATION_JSON);

        utils.perform(request).andExpect(status().isForbidden());
    }

    @Test
    public void twiceCreateTheSameTaskStatusFail() throws Exception {
        utils.createTaskStatus(TASK_STATUS_DTO).andExpect(status().isCreated());
        utils.createTaskStatus(TASK_STATUS_DTO).andExpect(status().isUnprocessableEntity());

        assertThat(1).isEqualTo(taskStatusRepository.count());
    }

    @Test
    public void getTaskStatusById() throws Exception {
        utils.createTaskStatus(TASK_STATUS_DTO);

        final TaskStatus expectedTaskStatus = taskStatusRepository.findAll().get(0);
        final MockHttpServletResponse response = utils.perform(
                        get("/api/statuses" + "/{id}", expectedTaskStatus.getId()),
                        TEST_USERNAME
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus taskStatus = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertThat(expectedTaskStatus.getId()).isEqualTo(taskStatus.getId());
        assertThat(expectedTaskStatus.getName()).isEqualTo(taskStatus.getName());
    }

    @Test
    public void getTaskStatusByIdFail() throws Exception {
        utils.createTaskStatus(TASK_STATUS_DTO);

        final TaskStatus expectedTaskStatus = taskStatusRepository.findAll().get(0);

        utils.perform(
                        get("/api/statuses" + "/{id}", expectedTaskStatus.getId() + 1),
                        TEST_USERNAME
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllTaskStatuses() throws Exception {
        utils.createTaskStatus(TASK_STATUS_DTO);

        final MockHttpServletResponse response = utils.perform(
                        get("/api/statuses"),
                        TEST_USERNAME
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<TaskStatus> taskStatuses = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertThat(taskStatuses).hasSize(1);
    }

    @Test
    public void updateTaskStatus() throws Exception {
        utils.createTaskStatus(TASK_STATUS_DTO);

        final Long taskStatusId = taskStatusRepository.findByName(TEST_TASK_STATUS_NAME).orElseThrow().getId();

        final TaskStatusDto taskStatusDtoForUpdate = new TaskStatusDto("some other task");

        final MockHttpServletRequestBuilder updateRequest = put(
                "/api/statuses" + "/{id}", taskStatusId
        )
                .content(MAPPER.writeValueAsString(taskStatusDtoForUpdate))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, TEST_USERNAME).andExpect(status().isOk());

        final TaskStatus expectedTaskStatus = taskStatusRepository.findAll().get(0);

        assertThat(expectedTaskStatus.getId()).isEqualTo(taskStatusId);
        assertThat(expectedTaskStatus.getName()).isNotEqualTo(TEST_TASK_STATUS_NAME);
        assertThat(expectedTaskStatus.getName()).isEqualTo(taskStatusDtoForUpdate.getName());
    }

    @Test
    public void updateTaskStatusFail() throws Exception {
        utils.createTaskStatus(TASK_STATUS_DTO);

        final Long taskStatusId = taskStatusRepository.findByName(TEST_TASK_STATUS_NAME).orElseThrow().getId();

        final TaskStatusDto taskStatusDtoForUpdate = new TaskStatusDto("");

        final MockHttpServletRequestBuilder updateRequest = put(
                "/api/statuses" + "/{id}", taskStatusId
        )
                .content(MAPPER.writeValueAsString(taskStatusDtoForUpdate))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, TEST_USERNAME).andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void deleteTaskStatus() throws Exception {
        utils.createTaskStatus(TASK_STATUS_DTO);

        final Long taskStatusId = taskStatusRepository.findByName(TEST_TASK_STATUS_NAME).orElseThrow().getId();

        utils.perform(delete("/api/statuses" + "/{id}", taskStatusId), TEST_USERNAME)
                .andExpect(status().isOk());

        assertThat(taskStatusRepository.existsById(taskStatusId)).isFalse();
    }

    @Test
    public void deleteTaskStatusFail() throws Exception {
        utils.createTaskStatus(TASK_STATUS_DTO);

        final Long taskStatusId = taskStatusRepository.findByName(TEST_TASK_STATUS_NAME).orElseThrow().getId();

        utils.perform(delete("/api/statuses" + "/{id}", taskStatusId))
                .andExpect(status().isForbidden());
    }
}
