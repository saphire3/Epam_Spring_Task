package com.epam.training.cucumber.component.steps;

import com.epam.training.model.Trainee;
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

public class TraineeSteps {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ApiScenarioContext ctx;

    private void doRequest(MockHttpServletRequestBuilder request) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
        ctx.setLastStatus(response.getStatus());
        ctx.setLastResponseBody(response.getContentAsString());
    }

    @When("I create a trainee with firstName {string} and lastName {string}")
    public void createTrainee(String firstName, String lastName) throws Exception {
        Trainee t = new Trainee();
        t.setFirstName(firstName);
        t.setLastName(lastName);
        doRequest(post("/api/trainees")
                .with(httpBasic("admin", "admin123"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(t)));
        if (ctx.getLastStatus() == 201) {
            Trainee created = objectMapper.readValue(ctx.getLastResponseBody(), Trainee.class);
            ctx.setLastTraineeId(created.getId());
        }
    }

    @Given("a trainee with firstName {string} and lastName {string} has been created")
    public void givenTraineeCreated(String firstName, String lastName) throws Exception {
        createTrainee(firstName, lastName);
    }

    @When("I get all trainees")
    public void getAllTrainees() throws Exception {
        doRequest(get("/api/trainees").with(httpBasic("admin", "admin123")));
    }

    @When("I get the trainee by their stored ID")
    public void getTraineeByStoredId() throws Exception {
        doRequest(get("/api/trainees/" + ctx.getLastTraineeId())
                .with(httpBasic("admin", "admin123")));
    }

    @When("I get the trainee with ID {long}")
    public void getTraineeWithId(long id) throws Exception {
        doRequest(get("/api/trainees/" + id).with(httpBasic("admin", "admin123")));
    }

    @When("I update the trainee with firstName {string} and lastName {string}")
    public void updateTrainee(String firstName, String lastName) throws Exception {
        Trainee t = new Trainee();
        t.setFirstName(firstName);
        t.setLastName(lastName);
        doRequest(put("/api/trainees/" + ctx.getLastTraineeId())
                .with(httpBasic("admin", "admin123"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(t)));
    }

    @When("I update trainee ID {long} with firstName {string} and lastName {string}")
    public void updateTraineeWithId(long id, String firstName, String lastName) throws Exception {
        Trainee t = new Trainee();
        t.setFirstName(firstName);
        t.setLastName(lastName);
        doRequest(put("/api/trainees/" + id)
                .with(httpBasic("admin", "admin123"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(t)));
    }

    @When("I delete the trainee by their stored ID")
    public void deleteTraineeByStoredId() throws Exception {
        doRequest(delete("/api/trainees/" + ctx.getLastTraineeId())
                .with(httpBasic("admin", "admin123"))
                .with(csrf()));
    }

    @When("I delete the trainee with ID {long}")
    public void deleteTraineeWithId(long id) throws Exception {
        doRequest(delete("/api/trainees/" + id)
                .with(httpBasic("admin", "admin123"))
                .with(csrf()));
    }

    @And("the trainee username should be {string}")
    public void traineeUsernameShouldBe(String expected) throws Exception {
        Trainee t = objectMapper.readValue(ctx.getLastResponseBody(), Trainee.class);
        assertThat(t.getUsername()).isEqualTo(expected);
    }

    @And("the trainee firstName should be {string}")
    public void traineeFirstNameShouldBe(String expected) throws Exception {
        Trainee t = objectMapper.readValue(ctx.getLastResponseBody(), Trainee.class);
        assertThat(t.getFirstName()).isEqualTo(expected);
    }
}