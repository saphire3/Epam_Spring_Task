package com.epam.workload.controller;

import com.epam.workload.dto.ActionType;
import com.epam.workload.dto.TrainerSummaryResponse;
import com.epam.workload.dto.TrainerWorkloadRequest;
import com.epam.workload.security.JwtTokenValidator;
import com.epam.workload.service.TrainerWorkloadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainerWorkloadController.class)
class TrainerWorkloadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainerWorkloadService workloadService;

    @MockBean
    private JwtTokenValidator jwtTokenValidator;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Test
    @WithMockUser(roles = "SERVICE")
    void updateWorkload_validRequest_returns200() throws Exception {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2024, 3, 15), 60, ActionType.ADD);
        doNothing().when(workloadService).processWorkload(any());

        mockMvc.perform(post("/api/trainer-workload")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(workloadService).processWorkload(any());
    }

    @Test
    @WithMockUser
    void getSummary_trainerExists_returns200() throws Exception {
        TrainerSummaryResponse response = new TrainerSummaryResponse(
                "john.doe", "John", "Doe", true,
                Map.of(2024, Map.of(3, 60)));
        when(workloadService.getSummary("john.doe")).thenReturn(response);

        mockMvc.perform(get("/api/trainer-workload/john.doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainerUsername").value("john.doe"));
    }

    @Test
    @WithMockUser
    void getSummary_trainerNotFound_returns404() throws Exception {
        when(workloadService.getSummary("unknown")).thenReturn(null);

        mockMvc.perform(get("/api/trainer-workload/unknown"))
                .andExpect(status().isNotFound());
    }
}