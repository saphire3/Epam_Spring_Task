package com.epam.workload.messaging;

import com.epam.workload.dto.ActionType;
import com.epam.workload.dto.TrainerWorkloadRequest;
import com.epam.workload.service.TrainerWorkloadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadMessageListenerTest {

    @Mock
    private TrainerWorkloadService workloadService;

    @InjectMocks
    private WorkloadMessageListener listener;

    private TrainerWorkloadRequest validRequest() {
        return new TrainerWorkloadRequest(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2024, 3, 15), 60, ActionType.ADD);
    }

    @Test
    void onWorkloadMessage_valid_delegatesToService() {
        TrainerWorkloadRequest request = validRequest();
        listener.onWorkloadMessage(request, "tx-123");
        verify(workloadService).processWorkload(request);
    }

    @Test
    void onWorkloadMessage_withoutTransactionId_stillProcesses() {
        TrainerWorkloadRequest request = validRequest();
        listener.onWorkloadMessage(request, null);
        verify(workloadService).processWorkload(request);
    }

    @Test
    void onWorkloadMessage_nullUsername_throwsIllegalArgument() {
        TrainerWorkloadRequest request = validRequest();
        request.setTrainerUsername(null);
        assertThrows(IllegalArgumentException.class,
                () -> listener.onWorkloadMessage(request, null));
        verify(workloadService, never()).processWorkload(any());
    }

    @Test
    void onWorkloadMessage_blankUsername_throwsIllegalArgument() {
        TrainerWorkloadRequest request = validRequest();
        request.setTrainerUsername("   ");
        assertThrows(IllegalArgumentException.class,
                () -> listener.onWorkloadMessage(request, null));
        verify(workloadService, never()).processWorkload(any());
    }

    @Test
    void onWorkloadMessage_nullDate_throwsIllegalArgument() {
        TrainerWorkloadRequest request = validRequest();
        request.setTrainingDate(null);
        assertThrows(IllegalArgumentException.class,
                () -> listener.onWorkloadMessage(request, null));
        verify(workloadService, never()).processWorkload(any());
    }

    @Test
    void onWorkloadMessage_nullActionType_throwsIllegalArgument() {
        TrainerWorkloadRequest request = validRequest();
        request.setActionType(null);
        assertThrows(IllegalArgumentException.class,
                () -> listener.onWorkloadMessage(request, null));
        verify(workloadService, never()).processWorkload(any());
    }

    @Test
    void onWorkloadMessage_zeroDuration_throwsIllegalArgument() {
        TrainerWorkloadRequest request = validRequest();
        request.setTrainingDuration(0);
        assertThrows(IllegalArgumentException.class,
                () -> listener.onWorkloadMessage(request, null));
        verify(workloadService, never()).processWorkload(any());
    }

    @Test
    void onWorkloadMessage_negativeDuration_throwsIllegalArgument() {
        TrainerWorkloadRequest request = validRequest();
        request.setTrainingDuration(-5);
        assertThrows(IllegalArgumentException.class,
                () -> listener.onWorkloadMessage(request, null));
    }

    @Test
    void onDeadLetterMessage_logsWithoutThrowing() throws Exception {
        jakarta.jms.Message message = mock(jakarta.jms.Message.class);
        when(message.getJMSMessageID()).thenReturn("msg-001");
        when(message.getJMSType()).thenReturn("text");
        listener.onDeadLetterMessage(message);
        verify(message).getJMSMessageID();
    }

    @Test
    void onDeadLetterMessage_jmsExceptionHandled() throws Exception {
        jakarta.jms.Message message = mock(jakarta.jms.Message.class);
        when(message.getJMSMessageID()).thenThrow(new jakarta.jms.JMSException("fail"));
        listener.onDeadLetterMessage(message);
        // should not propagate exception
    }
}