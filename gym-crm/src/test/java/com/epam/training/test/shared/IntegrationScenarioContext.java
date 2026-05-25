package com.epam.training.test.shared;

import com.epam.training.dto.TrainerWorkloadRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("cucumber-glue")
public class IntegrationScenarioContext {

    private Long lastTrainerId;
    private Long lastTrainingId;
    private String lastTrainerUsername;
    private TrainerWorkloadRequest capturedJmsMessage;

    public Long getLastTrainerId() { return lastTrainerId; }
    public void setLastTrainerId(Long id) { this.lastTrainerId = id; }

    public Long getLastTrainingId() { return lastTrainingId; }
    public void setLastTrainingId(Long id) { this.lastTrainingId = id; }

    public String getLastTrainerUsername() { return lastTrainerUsername; }
    public void setLastTrainerUsername(String username) { this.lastTrainerUsername = username; }

    public TrainerWorkloadRequest getCapturedJmsMessage() { return capturedJmsMessage; }
    public void setCapturedJmsMessage(TrainerWorkloadRequest msg) { this.capturedJmsMessage = msg; }
}