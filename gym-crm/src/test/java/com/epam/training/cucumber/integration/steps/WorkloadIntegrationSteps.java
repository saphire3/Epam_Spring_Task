package com.epam.training.cucumber.integration.steps;

import com.epam.training.dto.ActionType;
import com.epam.training.dto.TrainerWorkloadRequest;
import com.epam.training.messaging.WorkloadMessageProducer;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.test.shared.IntegrationScenarioContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class WorkloadIntegrationSteps {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private WorkloadMessageProducer workloadMessageProducer;
    @Autowired private IntegrationScenarioContext ctx;

    @Before
    public void resetMock() {
        Mockito.reset(workloadMessageProducer);
    }

    @Given("a trainer with firstName {string} and lastName {string} is registered")
    public void registerTrainer(String firstName, String lastName) throws Exception {
        Trainer t = new Trainer();
        t.setFirstName(firstName);
        t.setLastName(lastName);
        MockHttpServletResponse resp = mockMvc.perform(post("/api/trainers")
                .with(httpBasic("admin", "admin123"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(t)))
                .andReturn().getResponse();
        assertThat(resp.getStatus()).isEqualTo(201);
        Trainer created = objectMapper.readValue(resp.getContentAsString(), Trainer.class);
        ctx.setLastTrainerId(created.getId());
        ctx.setLastTrainerUsername(created.getUsername());
    }

    @When("I create a training for the registered trainer with duration {int} on {string}")
    public void createTrainingForRegisteredTrainer(int duration, String date) throws Exception {
        Training t = new Training();
        t.setTrainingName("Test Training");
        t.setTrainerId(ctx.getLastTrainerId());
        t.setTrainingDate(LocalDate.parse(date));
        t.setDuration(duration);
        MockHttpServletResponse resp = mockMvc.perform(post("/api/trainings")
                .with(httpBasic("admin", "admin123"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(t)))
                .andReturn().getResponse();
        assertThat(resp.getStatus()).isEqualTo(201);
        Training created = objectMapper.readValue(resp.getContentAsString(), Training.class);
        ctx.setLastTrainingId(created.getId());
    }

    @Given("a training for the registered trainer with duration {int} on {string} has been persisted")
    public void persistTrainingForRegisteredTrainer(int duration, String date) throws Exception {
        createTrainingForRegisteredTrainer(duration, date);
        Mockito.reset(workloadMessageProducer);
    }

    @When("I delete the persisted training")
    public void deletePersistedTraining() throws Exception {
        MockHttpServletResponse resp = mockMvc.perform(delete("/api/trainings/" + ctx.getLastTrainingId())
                .with(httpBasic("admin", "admin123"))
                .with(csrf()))
                .andReturn().getResponse();
        assertThat(resp.getStatus()).isEqualTo(204);
    }

    @When("I create a training with no trainer with duration {int} on {string}")
    public void createTrainingWithNoTrainer(int duration, String date) throws Exception {
        Training t = new Training();
        t.setTrainingName("Orphan Training");
        t.setTrainerId(null);
        t.setTrainingDate(LocalDate.parse(date));
        t.setDuration(duration);
        mockMvc.perform(post("/api/trainings")
                .with(httpBasic("admin", "admin123"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(t)))
                .andReturn();
    }

    @Then("a workload JMS message should be published")
    public void workloadJmsMessagePublished() {
        ArgumentCaptor<TrainerWorkloadRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(workloadMessageProducer).sendWorkloadUpdate(captor.capture());
        ctx.setCapturedJmsMessage(captor.getValue());
    }

    @Then("no workload JMS message should be published")
    public void noWorkloadJmsMessagePublished() {
        verify(workloadMessageProducer, never()).sendWorkloadUpdate(Mockito.any());
    }

    @And("the JMS message action type should be {string}")
    public void jmsMessageActionType(String expectedAction) {
        ActionType expected = ActionType.valueOf(expectedAction);
        assertThat(ctx.getCapturedJmsMessage().getActionType()).isEqualTo(expected);
    }

    @And("the JMS message trainer username should match the registered trainer")
    public void jmsMessageTrainerUsernameMatchesRegistered() {
        assertThat(ctx.getCapturedJmsMessage().getTrainerUsername())
                .isEqualTo(ctx.getLastTrainerUsername());
    }

    @And("the JMS message duration should be {int}")
    public void jmsMessageDuration(int expected) {
        assertThat(ctx.getCapturedJmsMessage().getTrainingDuration()).isEqualTo(expected);
    }
}