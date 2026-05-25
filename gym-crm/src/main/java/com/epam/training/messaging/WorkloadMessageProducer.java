package com.epam.training.messaging;

import com.epam.training.config.JmsConfig;
import com.epam.training.dto.TrainerWorkloadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class WorkloadMessageProducer {

    private static final Logger log = LoggerFactory.getLogger(WorkloadMessageProducer.class);

    private final JmsTemplate jmsTemplate;

    public WorkloadMessageProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendWorkloadUpdate(TrainerWorkloadRequest request) {
        String transactionId = MDC.get("transactionId");
        log.info("[JMS] Sending workload update: trainer={}, action={}, transactionId={}",
                request.getTrainerUsername(), request.getActionType(), transactionId);
        jmsTemplate.convertAndSend(JmsConfig.WORKLOAD_QUEUE, request, message -> {
            if (transactionId != null) {
                message.setStringProperty("transactionId", transactionId);
            }
            return message;
        });
        log.info("[JMS] Workload message sent to queue '{}' for trainer '{}'",
                JmsConfig.WORKLOAD_QUEUE, request.getTrainerUsername());
    }
}