package com.epam.workload.messaging;

import com.epam.workload.config.JmsConfig;
import com.epam.workload.dto.TrainerWorkloadRequest;
import com.epam.workload.service.TrainerWorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class WorkloadMessageListener {

    private static final Logger log = LoggerFactory.getLogger(WorkloadMessageListener.class);

    private final TrainerWorkloadService workloadService;

    public WorkloadMessageListener(TrainerWorkloadService workloadService) {
        this.workloadService = workloadService;
    }

    @JmsListener(destination = JmsConfig.WORKLOAD_QUEUE, containerFactory = "jmsListenerContainerFactory")
    public void onWorkloadMessage(TrainerWorkloadRequest request,
                                  @Header(name = "transactionId", required = false) String transactionId) {
        if (transactionId != null) {
            MDC.put("transactionId", transactionId);
        }
        try {
            log.info("[JMS] Received workload message: trainer={}, action={}, transactionId={}",
                    request.getTrainerUsername(), request.getActionType(), transactionId);

            validate(request);
            workloadService.processWorkload(request);

            log.info("[JMS] Workload processed successfully for trainer '{}'", request.getTrainerUsername());
        } finally {
            MDC.clear();
        }
    }

    @JmsListener(destination = JmsConfig.WORKLOAD_DLQ, containerFactory = "jmsListenerContainerFactory")
    public void onDeadLetterMessage(jakarta.jms.Message message) {
        try {
            log.error("[DLQ] Dead letter received on '{}': messageId={}, type={}",
                    JmsConfig.WORKLOAD_DLQ, message.getJMSMessageID(), message.getJMSType());
        } catch (jakarta.jms.JMSException e) {
            log.error("[DLQ] Failed to read dead letter message properties", e);
        }
    }
//"Every incoming JMS message is validated before processing. If trainerUsername is blank, trainingDate is null,
// actionType is null, or duration is ≤ 0, an IllegalArgumentException is thrown
//  ▎ immediately and the message is rejected with an error log — it never reaches the service layer.
//▎ On the model side, @NotBlank guards username, firstName, lastName; @Min(1)/@Max(12) guards month;
// @Min(0) guards duration — these are declared on TrainerSummary, YearSummary, and MonthSummary.
    private void validate(TrainerWorkloadRequest request) {
        if (request.getTrainerUsername() == null || request.getTrainerUsername().isBlank()) {
            throw new IllegalArgumentException("Workload message missing required field: trainerUsername");
        }
        if (request.getTrainingDate() == null) {
            throw new IllegalArgumentException("Workload message missing required field: trainingDate");
        }
        if (request.getActionType() == null) {
            throw new IllegalArgumentException("Workload message missing required field: actionType");
        }
        if (request.getTrainingDuration() <= 0) {
            throw new IllegalArgumentException("Workload message has invalid trainingDuration: " + request.getTrainingDuration());
        }
    }
}