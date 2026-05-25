package com.epam.training.client;

import com.epam.training.dto.TrainerWorkloadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WorkloadClientFallback implements TrainerWorkloadClient {

    private static final Logger log = LoggerFactory.getLogger(WorkloadClientFallback.class);

    @Override
    public void updateWorkload(String bearerToken, String transactionId, TrainerWorkloadRequest request) {
        log.error("[CIRCUIT BREAKER] Trainer workload service unavailable. " +
                  "Could not process workload for trainer '{}', action={}. transactionId={}",
                  request.getTrainerUsername(), request.getActionType(), transactionId);
    }
}