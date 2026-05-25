package com.epam.workload.service;

import com.epam.workload.dto.ActionType;
import com.epam.workload.dto.TrainerSummaryResponse;
import com.epam.workload.dto.TrainerWorkloadRequest;
import com.epam.workload.model.MonthSummary;
import com.epam.workload.model.TrainerSummary;
import com.epam.workload.model.YearSummary;
import com.epam.workload.repository.TrainerSummaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TrainerWorkloadService {

    private static final Logger log = LoggerFactory.getLogger(TrainerWorkloadService.class);

    private final TrainerSummaryRepository repository;

    public TrainerWorkloadService(TrainerSummaryRepository repository) {
        this.repository = repository;
    }

    public void processWorkload(TrainerWorkloadRequest request) {
        String txId = MDC.get("transactionId");
        String username = request.getTrainerUsername();
        int year = request.getTrainingDate().getYear();
        int month = request.getTrainingDate().getMonthValue();
        int duration = request.getTrainingDuration();

        log.info("[TX:{}] Processing workload for trainer='{}', year={}, month={}, duration={}, action={}",
                txId, username, year, month, duration, request.getActionType());

        Optional<TrainerSummary> existing = repository.findByUsername(username);
        log.debug("[TX:{}] Lookup by username='{}': found={}",
                txId, username, existing.isPresent());

        TrainerSummary summary = existing.orElseGet(() -> {
            log.info("[TX:{}] No existing record for trainer='{}'. Creating new document.", txId, username);
            return new TrainerSummary(username, request.getTrainerFirstName(),
                    request.getTrainerLastName(), request.isActive());
        });

        summary.setFirstName(request.getTrainerFirstName());
        summary.setLastName(request.getTrainerLastName());
        summary.setActive(request.isActive());

        YearSummary yearSummary = findOrCreateYear(summary, year);
        MonthSummary monthSummary = findOrCreateMonth(yearSummary, month);

        if (request.getActionType() == ActionType.ADD) {
            int updated = monthSummary.getTrainingSummaryDuration() + duration;
            monthSummary.setTrainingSummaryDuration(updated);
            log.info("[TX:{}] ADD: trainer='{}' {}/{} -> new duration={}",
                    txId, username, year, month, updated);
        } else {
            int updated = Math.max(0, monthSummary.getTrainingSummaryDuration() - duration);
            monthSummary.setTrainingSummaryDuration(updated);
            log.info("[TX:{}] DELETE: trainer='{}' {}/{} -> new duration={}",
                    txId, username, year, month, updated);
        }

        repository.save(summary);
        log.debug("[TX:{}] Saved trainer summary for username='{}'", txId, username);
    }

    public TrainerSummaryResponse getSummary(String username) {
        String txId = MDC.get("transactionId");
        log.debug("[TX:{}] Fetching summary for trainer='{}'", txId, username);

        return repository.findByUsername(username)
                .map(s -> {
                    log.info("[TX:{}] Summary found for trainer='{}'", txId, username);
                    return toResponse(s);
                })
                .orElseGet(() -> {
                    log.warn("[TX:{}] No summary found for trainer='{}'", txId, username);
                    return null;
                });
    }

    private YearSummary findOrCreateYear(TrainerSummary summary, int year) {
        return summary.getYears().stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseGet(() -> {
                    YearSummary ys = new YearSummary(year);
                    summary.getYears().add(ys);
                    return ys;
                });
    }

    private MonthSummary findOrCreateMonth(YearSummary yearSummary, int month) {
        return yearSummary.getMonths().stream()
                .filter(m -> m.getMonth() == month)
                .findFirst()
                .orElseGet(() -> {
                    MonthSummary ms = new MonthSummary(month, 0);
                    yearSummary.getMonths().add(ms);
                    return ms;
                });
    }

    private TrainerSummaryResponse toResponse(TrainerSummary summary) {
        Map<Integer, Map<Integer, Integer>> yearsMap = new LinkedHashMap<>();
        for (YearSummary ys : summary.getYears()) {
            Map<Integer, Integer> monthsMap = new LinkedHashMap<>();
            for (MonthSummary ms : ys.getMonths()) {
                monthsMap.put(ms.getMonth(), ms.getTrainingSummaryDuration());
            }
            yearsMap.put(ys.getYear(), monthsMap);
        }
        return new TrainerSummaryResponse(
                summary.getUsername(),
                summary.getFirstName(),
                summary.getLastName(),
                summary.isActive(),
                yearsMap
        );
    }
}