package com.epam.training.dto.request;

import jakarta.validation.constraints.NotNull;

public class ActiveStatusRequest {

    @NotNull
    private Boolean isActive;

    public ActiveStatusRequest() {
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
