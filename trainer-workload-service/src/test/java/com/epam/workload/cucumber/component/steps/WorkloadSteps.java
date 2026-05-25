package com.epam.workload.cucumber.component.steps;

import com.epam.workload.dto.ActionType;
import com.epam.workload.dto.TrainerSummaryResponse;
import com.epam.workload.dto.TrainerWorkloadRequest;
import com.epam.workload.test.shared.WorkloadScenarioContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class WorkloadSteps {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private WorkloadScenarioContext ctx;

    private void submitWorkload(ActionType action, String username, String date, int duration,
                                boolean authenticated) throws Exception {
        TrainerWorkloadRequest req = new TrainerWorkloadRequest(
                username, "Test", "Trainer", true,
                LocalDate.parse(date), duration, action);

        var requestBuilder = post("/api/trainer-workload")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req));

        if (authenticated) {
            requestBuilder = requestBuilder.with(user("svc").roles("SERVICE"));
        }

        MockHttpServletResponse resp = mockMvc.perform(requestBuilder).andReturn().getResponse();
        ctx.setLastStatus(resp.getStatus());
        ctx.setLastResponseBody(resp.getContentAsString());
    }

    @When("I submit an ADD workload request for trainer {string} on {string} with duration {int}")
    public void submitAddWorkload(String username, String date, int duration) throws Exception {
        submitWorkload(ActionType.ADD, username, date, duration, true);
    }

    @When("I submit a DELETE workload request for trainer {string} on {string} with duration {int}")
    public void submitDeleteWorkload(String username, String date, int duration) throws Exception {
        submitWorkload(ActionType.DELETE, username, date, duration, true);
    }

    @When("I submit an ADD workload request without authentication for trainer {string} on {string} with duration {int}")
    public void submitAddWorkloadNoAuth(String username, String date, int duration) throws Exception {
        submitWorkload(ActionType.ADD, username, date, duration, false);
    }

    @Given("trainer {string} already has a workload summary with {int} minutes on {string}")
    public void givenTrainerHasWorkload(String username, int minutes, String date) throws Exception {
        submitWorkload(ActionType.ADD, username, date, minutes, true);
        assertThat(ctx.getLastStatus()).isEqualTo(200);
    }

    @When("I get the workload summary for trainer {string}")
    public void getWorkloadSummary(String username) throws Exception {
        MockHttpServletResponse resp = mockMvc.perform(get("/api/trainer-workload/" + username))
                .andReturn().getResponse();
        ctx.setLastStatus(resp.getStatus());
        ctx.setLastResponseBody(resp.getContentAsString());
    }

    @Then("the workload response status should be {int}")
    public void workloadResponseStatus(int expected) {
        assertThat(ctx.getLastStatus()).isEqualTo(expected);
    }

    @And("the workload summary for {string} should show {int} minutes in month {int} of year {int}")
    public void checkWorkloadSummary(String username, int expectedMinutes, int month, int year) throws Exception {
        MockHttpServletResponse resp = mockMvc.perform(get("/api/trainer-workload/" + username))
                .andReturn().getResponse();
        assertThat(resp.getStatus()).isEqualTo(200);
        TrainerSummaryResponse summary = objectMapper.readValue(resp.getContentAsString(), TrainerSummaryResponse.class);
        Integer actual = summary.getYears().get(year).get(month);
        assertThat(actual).isEqualTo(expectedMinutes);
    }

    @And("the summary trainer username should be {string}")
    public void summaryTrainerUsername(String expected) throws Exception {
        TrainerSummaryResponse summary = objectMapper.readValue(ctx.getLastResponseBody(), TrainerSummaryResponse.class);
        assertThat(summary.getTrainerUsername()).isEqualTo(expected);
    }
}