package com.epam.workload.test.shared;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("cucumber-glue")
public class WorkloadScenarioContext {

    private int lastStatus;
    private String lastResponseBody;

    public int getLastStatus() { return lastStatus; }
    public void setLastStatus(int status) { this.lastStatus = status; }

    public String getLastResponseBody() { return lastResponseBody; }
    public void setLastResponseBody(String body) { this.lastResponseBody = body; }
}