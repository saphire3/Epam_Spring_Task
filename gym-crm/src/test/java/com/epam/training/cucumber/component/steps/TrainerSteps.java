package com.epam.training.cucumber.component.steps;

import com.epam.training.model.Trainer;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class TrainerSteps {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ApiScenarioContext ctx;

    private void doRequest(MockHttpServletRequestBuilder request) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
        ctx.setLastStatus(response.getStatus());
        ctx.setLastResponseBody(response.getContentAsString());
    }

    @When("I create a trainer with firstName {string} and lastName {string}")
    public void createTrainer(String firstName, String lastName) throws Exception {
        Trainer t = new Trainer();
        t.setFirstName(firstName);
        t.setLastName(lastName);
        doRequest(post("/api/trainers")
                .with(httpBasic("admin", "admin123"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(t)));
        if (ctx.getLastStatus() == 201) {
            Trainer created = objectMapper.readValue(ctx.getLastResponseBody(), Trainer.class);
            ctx.setLastTrainerId(created.getId());
        }
    }

    @Given("a trainer with firstName {string} and lastName {string} has been created")
    public void givenTrainerCreated(String firstName, String lastName) throws Exception {
        createTrainer(firstName, lastName);
    }

    @When("I get all trainers")
    public void getAllTrainers() throws Exception {
        doRequest(get("/api/trainers").with(httpBasic("admin", "admin123")));
    }

    @When("I get the trainer by their stored ID")
    public void getTrainerByStoredId() throws Exception {
        doRequest(get("/api/trainers/" + ctx.getLastTrainerId())
                .with(httpBasic("admin", "admin123")));
    }

    @When("I get the trainer with ID {long}")
    public void getTrainerWithId(long id) throws Exception {
        doRequest(get("/api/trainers/" + id).with(httpBasic("admin", "admin123")));
    }

    @When("I update the trainer with firstName {string} and lastName {string}")
    public void updateTrainer(String firstName, String lastName) throws Exception {
        Trainer t = new Trainer();
        t.setFirstName(firstName);
        t.setLastName(lastName);
        doRequest(put("/api/trainers/" + ctx.getLastTrainerId())
                .with(httpBasic("admin", "admin123"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(t)));
    }

    @When("I update trainer ID {long} with firstName {string} and lastName {string}")
    public void updateTrainerWithId(long id, String firstName, String lastName) throws Exception {
        Trainer t = new Trainer();
        t.setFirstName(firstName);
        t.setLastName(lastName);
        doRequest(put("/api/trainers/" + id)
                .with(httpBasic("admin", "admin123"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(t)));
    }

    @When("I delete the trainer by their stored ID")
    public void deleteTrainerByStoredId() throws Exception {
        doRequest(delete("/api/trainers/" + ctx.getLastTrainerId())
                .with(httpBasic("admin", "admin123"))
                .with(csrf()));
    }

    @When("I delete the trainer with ID {long}")
    public void deleteTrainerWithId(long id) throws Exception {
        doRequest(delete("/api/trainers/" + id)
                .with(httpBasic("admin", "admin123"))
                .with(csrf()));
    }

    @And("the trainer username should be {string}")
    public void trainerUsernameShouldBe(String expected) throws Exception {
        Trainer t = objectMapper.readValue(ctx.getLastResponseBody(), Trainer.class);
        assertThat(t.getUsername()).isEqualTo(expected);
    }

    @And("the trainer firstName should be {string}")
    public void trainerFirstNameShouldBe(String expected) throws Exception {
        Trainer t = objectMapper.readValue(ctx.getLastResponseBody(), Trainer.class);
        assertThat(t.getFirstName()).isEqualTo(expected);
    }
}