package com.epam.workload.model;

import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class YearSummary {

    @NotNull
    private Integer year;

    private List<MonthSummary> months = new ArrayList<>();

    public YearSummary() {}

    public YearSummary(Integer year) {
        this.year = year;
    }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public List<MonthSummary> getMonths() { return months; }
    public void setMonths(List<MonthSummary> months) { this.months = months; }
}