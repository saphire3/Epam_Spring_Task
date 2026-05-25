package com.epam.training.test.shared;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("cucumber-glue")
public class ApiScenarioContext {

    private int lastStatus;
    private String lastResponseBody;
    private Long lastTraineeId;
    private Long lastTrainerId;
    private Long lastTrainingId;

    public int getLastStatus() { return lastStatus; }
    public void setLastStatus(int status) { this.lastStatus = status; }

    public String getLastResponseBody() { return lastResponseBody; }
    public void setLastResponseBody(String body) { this.lastResponseBody = body; }

    public Long getLastTraineeId() { return lastTraineeId; }
    public void setLastTraineeId(Long id) { this.lastTraineeId = id; }

    public Long getLastTrainerId() { return lastTrainerId; }
    public void setLastTrainerId(Long id) { this.lastTrainerId = id; }

    public Long getLastTrainingId() { return lastTrainingId; }
    public void setLastTrainingId(Long id) { this.lastTrainingId = id; }
}
