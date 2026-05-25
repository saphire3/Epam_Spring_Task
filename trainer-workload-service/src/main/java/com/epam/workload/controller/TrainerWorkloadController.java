package com.epam.workload.controller;

import com.epam.workload.dto.TrainerWorkloadRequest;
import com.epam.workload.dto.TrainerSummaryResponse;
import com.epam.workload.service.TrainerWorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainer-workload")
public class TrainerWorkloadController {

    private static final Logger log = LoggerFactory.getLogger(TrainerWorkloadController.class);

    private final TrainerWorkloadService workloadService;

    public TrainerWorkloadController(TrainerWorkloadService workloadService) {
        this.workloadService = workloadService;
    }

    @PostMapping
    public ResponseEntity<Void> updateWorkload(@RequestBody TrainerWorkloadRequest request) {
        log.info("Updating workload for trainer '{}', action={}", request.getTrainerUsername(), request.getActionType());
        workloadService.processWorkload(request);
        log.info("Workload updated successfully for trainer '{}', transactionId={}",
                request.getTrainerUsername(), MDC.get("transactionId"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerSummaryResponse> getSummary(@PathVariable String username) {
        log.info("Fetching workload summary for trainer '{}'", username);
        TrainerSummaryResponse summary = workloadService.getSummary(username);
        if (summary == null) {
            log.warn("No summary found for trainer '{}'", username);
            return ResponseEntity.notFound().build();
        }
        log.info("Summary retrieved for trainer '{}', transactionId={}", username, MDC.get("transactionId"));
        return ResponseEntity.ok(summary);
    }
}
