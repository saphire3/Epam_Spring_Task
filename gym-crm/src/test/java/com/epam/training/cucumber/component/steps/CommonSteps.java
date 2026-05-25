package com.epam.training.cucumber.component.steps;

import com.epam.training.test.shared.ApiScenarioContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class CommonSteps {

    @Autowired private MockMvc mockMvc;
    @Autowired private ApiScenarioContext ctx;

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        assertThat(ctx.getLastStatus()).isEqualTo(expectedStatus);
    }

    @When("I request GET {string} without authentication")
    public void requestGetWithoutAuthentication(String path) throws Exception {
        var response = mockMvc.perform(get(path)).andReturn().getResponse();
        ctx.setLastStatus(response.getStatus());
        ctx.setLastResponseBody(response.getContentAsString());
    }
}