package com.epam.workload.service;

import com.epam.workload.dto.ActionType;
import com.epam.workload.dto.TrainerWorkloadRequest;
import com.epam.workload.dto.TrainerSummaryResponse;
import com.epam.workload.model.TrainerSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TrainerWorkloadService {

    private static final Logger log = LoggerFactory.getLogger(TrainerWorkloadService.class);

    private final Map<String, TrainerSummary> store = new ConcurrentHashMap<>();

    public void processWorkload(TrainerWorkloadRequest request) {
        String username = request.getTrainerUsername();
        int year = request.getTrainingDate().getYear();
        int month = request.getTrainingDate().getMonthValue();
        int duration = request.getTrainingDuration();

        TrainerSummary summary = store.computeIfAbsent(username, u ->
                new TrainerSummary(username, request.getTrainerFirstName(),
                        request.getTrainerLastName(), request.isActive()));

        summary.setFirstName(request.getTrainerFirstName());
        summary.setLastName(request.getTrainerLastName());
        summary.setActive(request.isActive());

        Map<Integer, Integer> months = summary.getYears()
                .computeIfAbsent(year, y -> new ConcurrentHashMap<>());

        if (request.getActionType() == ActionType.ADD) {
            months.merge(month, duration, Integer::sum);
            log.info("Added {} minutes for trainer '{}' in {}/{}", duration, username, year, month);
        } else {
            months.merge(month, duration, (existing, d) -> Math.max(0, existing - d));
            if (months.get(month) != null && months.get(month) == 0) {
                months.remove(month);
            }
            log.info("Removed {} minutes for trainer '{}' in {}/{}", duration, username, year, month);
        }
    }

    public TrainerSummaryResponse getSummary(String username) {
        TrainerSummary summary = store.get(username);
        if (summary == null) {
            return null;
        }
        return new TrainerSummaryResponse(
                summary.getUsername(),
                summary.getFirstName(),
                summary.getLastName(),
                summary.isActive(),
                summary.getYears()
        );
    }
}