package com.epam.training.messaging;

import com.epam.training.dto.ActionType;
import com.epam.training.dto.TrainerWorkloadRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDate;

import static com.epam.training.config.JmsConfig.WORKLOAD_QUEUE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WorkloadMessageProducerTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private WorkloadMessageProducer producer;

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void sendWorkloadUpdate_sendsToCorrectQueue() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2024, 3, 15), 60, ActionType.ADD);

        producer.sendWorkloadUpdate(request);

        verify(jmsTemplate).convertAndSend(eq(WORKLOAD_QUEUE), eq(request), any());
    }

    @Test
    void sendWorkloadUpdate_withTransactionId_sendsMessage() {
        MDC.put("transactionId", "tx-999");
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "anna.brown", "Anna", "Brown", true,
                LocalDate.of(2024, 5, 10), 90, ActionType.DELETE);

        producer.sendWorkloadUpdate(request);

        verify(jmsTemplate).convertAndSend(eq(WORKLOAD_QUEUE), eq(request), any());
    }

    @Test
    void sendWorkloadUpdate_withoutTransactionId_sendsMessage() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "bob.smith", "Bob", "Smith", false,
                LocalDate.of(2024, 1, 20), 45, ActionType.ADD);

        producer.sendWorkloadUpdate(request);

        verify(jmsTemplate).convertAndSend(eq(WORKLOAD_QUEUE), eq(request), any());
    }
}