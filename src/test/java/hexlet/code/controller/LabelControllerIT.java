package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
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
import static hexlet.code.utils.TestUtils.USER_DTO;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.LABEL_DTO;
import static hexlet.code.utils.TestUtils.TEST_LABEL_NAME;
import static hexlet.code.utils.TestUtils.TEST_LABEL_NAME_2;
import static hexlet.code.utils.TestUtils.MAPPER;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class LabelControllerIT {

    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private TestUtils utils;

    @BeforeEach
    public void before() throws Exception {
        utils.regUser(USER_DTO);
    }

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void createLabel() throws Exception {
        assertThat(0).isEqualTo(labelRepository.count());

        final MockHttpServletResponse response = utils.createLabel(LABEL_DTO)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Label savedLabel = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertThat(1).isEqualTo(labelRepository.count());
        assertThat(labelRepository.getReferenceById(savedLabel.getId())).isNotNull();
    }

    @Test
    public void createLabelFail() throws Exception {
        final LabelDto labelDto = new LabelDto();

        final MockHttpServletRequestBuilder request = post("/api/labels")
                .content(MAPPER.writeValueAsString(labelDto))
                .contentType(APPLICATION_JSON);

        utils.perform(request).andExpect(status().isForbidden());
    }

    @Test
    public void twiceCreateTheSameLabelFail() throws Exception {
        utils.createLabel(LABEL_DTO).andExpect(status().isCreated());
        utils.createLabel(LABEL_DTO).andExpect(status().isUnprocessableEntity());

        assertThat(1).isEqualTo(labelRepository.count());
    }

    @Test
    public void getLabelById() throws Exception {
        utils.createLabel(LABEL_DTO);

        final Label expectedLabel = labelRepository.findAll().get(0);
        final MockHttpServletResponse response = utils.perform(
                        get("/api/labels/{id}", expectedLabel.getId()),
                        TEST_USERNAME
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Label label = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertThat(expectedLabel.getId()).isEqualTo(label.getId());
        assertThat(expectedLabel.getName()).isEqualTo(label.getName());
    }

    @Test
    public void getLabelByIdFail() throws Exception {
        utils.createLabel(LABEL_DTO);

        final Label expectedLabel = labelRepository.findAll().get(0);

        utils.perform(
                        get("/api/labels/{id}", expectedLabel.getId() + 1),
                        TEST_USERNAME
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllLabels() throws Exception {
        utils.createLabel(LABEL_DTO);

        final MockHttpServletResponse response = utils.perform(
                        get("/api/labels"),
                        TEST_USERNAME
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Label> labels = fromJson(response.getContentAsString(), new TypeReference<>() { });
        final List<Label> expectedLabels = labelRepository.findAll();

        assertThat(labels).hasSize(1);

        int i = 0;
        for (var label : labels) {
            assertThat(label.getId()).isEqualTo(expectedLabels.get(i).getId());
            assertThat(label.getName()).isEqualTo(expectedLabels.get(i).getName());
            i++;
        }
    }

    @Test
    public void updateLabel() throws Exception {
        utils.createLabel(LABEL_DTO);

        final Long labelId = labelRepository.findByName(TEST_LABEL_NAME).orElseThrow().getId();
        final LabelDto labelDtoForUpdate = new LabelDto(TEST_LABEL_NAME_2);

        final MockHttpServletRequestBuilder updateRequest = put(
                "/api/labels/{id}", labelId
        )
                .content(MAPPER.writeValueAsString(labelDtoForUpdate))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, TEST_USERNAME).andExpect(status().isOk());

        final Label expectedLabel = labelRepository.findAll().get(0);

        assertThat(expectedLabel.getId()).isEqualTo(labelId);
        assertThat(expectedLabel.getName()).isNotEqualTo(TEST_LABEL_NAME);
        assertThat(expectedLabel.getName()).isEqualTo(TEST_LABEL_NAME_2);
    }

    @Test
    public void updateLabelFail() throws Exception {
        utils.createLabel(LABEL_DTO);

        final Long labelId = labelRepository.findByName(TEST_LABEL_NAME).orElseThrow().getId();
        final LabelDto labelDtoForUpdate = new LabelDto("");

        final MockHttpServletRequestBuilder updateRequest = put(
                "/api/labels/{id}", labelId
        )
                .content(MAPPER.writeValueAsString(labelDtoForUpdate))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, TEST_USERNAME).andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void deleteLabel() throws Exception {
        utils.createLabel(LABEL_DTO);

        final Long labelId = labelRepository.findByName(TEST_LABEL_NAME).orElseThrow().getId();

        utils.perform(delete("/api/labels/{id}", labelId), TEST_USERNAME)
                .andExpect(status().isOk());

        assertThat(labelRepository.existsById(labelId)).isFalse();
    }

    @Test
    public void deleteLabelFail() throws Exception {
        utils.createLabel(LABEL_DTO);

        final Long labelId = labelRepository.findByName(TEST_LABEL_NAME).orElseThrow().getId();

        utils.perform(delete("/api/labels/{id}", labelId))
                .andExpect(status().isForbidden());
    }
}
