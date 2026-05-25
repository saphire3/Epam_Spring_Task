package com.epam.training.cucumber.component.steps;

import com.epam.training.model.Training;
import com.epam.training.test.shared.ApiScenarioContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class TrainingSteps {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ApiScenarioContext ctx;

    private void doRequest(MockHttpServletRequestBuilder request) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
        ctx.setLastStatus(response.getStatus());
        ctx.setLastResponseBody(response.getContentAsString());
    }

    @When("I create a training named {string} for the created trainer on {string} with duration {int}")
    public void createTrainingForTrainer(String name, String date, int duration) throws Exception {
        Training t = new Training();
        t.setTrainingName(name);
        t.setTrainerId(ctx.getLastTrainerId());
        t.setTrainingDate(LocalDate.parse(date));
        t.setDuration(duration);
        doRequest(post("/api/trainings")
                .with(httpBasic("admin", "admin123"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(t)));
        if (ctx.getLastStatus() == 201) {
            Training created = objectMapper.readValue(ctx.getLastResponseBody(), Training.class);
            ctx.setLastTrainingId(created.getId());
        }
    }

    @Given("a training named {string} for the created trainer on {string} with duration {int} has been created")
    public void givenTrainingCreated(String name, String date, int duration) throws Exception {
        createTrainingForTrainer(name, date, duration);
    }

    @When("I get all trainings")
    public void getAllTrainings() throws Exception {
        doRequest(get("/api/trainings").with(httpBasic("admin", "admin123")));
    }

    @When("I get the training by their stored ID")
    public void getTrainingByStoredId() throws Exception {
        doRequest(get("/api/trainings/" + ctx.getLastTrainingId())
                .with(httpBasic("admin", "admin123")));
    }

    @When("I get the training with ID {long}")
    public void getTrainingWithId(long id) throws Exception {
        doRequest(get("/api/trainings/" + id).with(httpBasic("admin", "admin123")));
    }

    @When("I delete the training by their stored ID")
    public void deleteTrainingByStoredId() throws Exception {
        doRequest(delete("/api/trainings/" + ctx.getLastTrainingId())
                .with(httpBasic("admin", "admin123"))
                .with(csrf()));
    }

    @When("I delete the training with ID {long}")
    public void deleteTrainingWithId(long id) throws Exception {
        doRequest(delete("/api/trainings/" + id)
                .with(httpBasic("admin", "admin123"))
                .with(csrf()));
    }

    @And("the training name should be {string}")
    public void trainingNameShouldBe(String expected) throws Exception {
        Training t = objectMapper.readValue(ctx.getLastResponseBody(), Training.class);
        assertThat(t.getTrainingName()).isEqualTo(expected);
    }
}