package com.epam.workload.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class MonthSummary {

    @NotNull
    @Min(1)
    @Max(12)
    private Integer month;

    @NotNull
    @Min(0)
    private Integer trainingSummaryDuration;

    public MonthSummary() {}

    public MonthSummary(Integer month, Integer trainingSummaryDuration) {
        this.month = month;
        this.trainingSummaryDuration = trainingSummaryDuration;
    }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getTrainingSummaryDuration() { return trainingSummaryDuration; }
    public void setTrainingSummaryDuration(Integer trainingSummaryDuration) {
        this.trainingSummaryDuration = trainingSummaryDuration;
    }
}