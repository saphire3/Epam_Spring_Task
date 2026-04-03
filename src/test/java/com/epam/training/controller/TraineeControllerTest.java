package com.epam.training.controller;

import com.epam.training.advice.GlobalExceptionHandler;
import com.epam.training.dto.request.ActiveStatusRequest;
import com.epam.training.dto.request.TraineeRegistrationRequest;
import com.epam.training.dto.request.UpdateTraineeRequest;
import com.epam.training.dto.request.UpdateTraineeTrainersRequest;
import com.epam.training.model.Trainee;
import com.epam.training.model.Training;
import com.epam.training.model.TrainingType;
import com.epam.training.security.JwtAuthenticationFilter;
import com.epam.training.security.SecurityConfig;
import com.epam.training.service.TraineeService;
import com.epam.training.service.TrainingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TraineeController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
        })
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TraineeService traineeService;

    @MockBean
    private TrainingService trainingService;

    @MockBean
    private com.epam.training.security.CustomUserDetailsService customUserDetailsService;

    @MockBean
    private com.epam.training.security.JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(inv -> {
            ((FilterChain) inv.getArgument(2)).doFilter(inv.getArgument(0), inv.getArgument(1));
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    @Test
    void shouldRegisterTraineeWithoutAuth() throws Exception {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Smith");

        when(traineeService.create(any(Trainee.class))).thenReturn(new String[]{"john.smith", "rawpass"});

        mockMvc.perform(post("/api/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john.smith"))
                .andExpect(jsonPath("$.password").value("rawpass"));
    }

    @Test
    void shouldReturnUnauthorizedWhenNoToken() throws Exception {
        mockMvc.perform(get("/api/trainees/john.smith"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void shouldGetTraineeProfile() throws Exception {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.smith");
        trainee.setFirstName("John");
        trainee.setLastName("Smith");
        trainee.setActive(true);
        trainee.setTrainers(Collections.emptySet());

        when(traineeService.findByUsername("john.smith")).thenReturn(trainee);

        mockMvc.perform(get("/api/trainees/john.smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @WithMockUser
    void shouldDeleteTrainee() throws Exception {
        doNothing().when(traineeService).delete("john.smith");

        mockMvc.perform(delete("/api/trainees/john.smith"))
                .andExpect(status().isOk());

        verify(traineeService).delete("john.smith");
    }

    @Test
    void shouldReturn400OnInvalidRegistration() throws Exception {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        // Missing firstName, lastName - should fail validation

        mockMvc.perform(post("/api/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldGetUnassignedTrainers() throws Exception {
        when(traineeService.getUnassignedTrainers("john.smith")).thenReturn(List.of());

        mockMvc.perform(get("/api/trainees/john.smith/unassigned-trainers"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void shouldUpdateTraineeProfile() throws Exception {
        UpdateTraineeRequest request = new UpdateTraineeRequest();
        request.setFirstName("John");
        request.setLastName("Doe");

        Trainee updated = new Trainee();
        updated.setFirstName("John");
        updated.setLastName("Doe");
        updated.setActive(true);
        updated.setTrainers(Collections.emptySet());

        when(traineeService.update(anyString(), any(Trainee.class))).thenReturn(updated);

        mockMvc.perform(put("/api/trainees/john.smith")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @WithMockUser
    void shouldSetTraineeActive() throws Exception {
        ActiveStatusRequest request = new ActiveStatusRequest();
        request.setIsActive(true);
        doNothing().when(traineeService).activate("john.smith");

        mockMvc.perform(patch("/api/trainees/john.smith/active")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(traineeService).activate("john.smith");
    }

    @Test
    @WithMockUser
    void shouldSetTraineeInactive() throws Exception {
        ActiveStatusRequest request = new ActiveStatusRequest();
        request.setIsActive(false);
        doNothing().when(traineeService).deactivate("john.smith");

        mockMvc.perform(patch("/api/trainees/john.smith/active")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(traineeService).deactivate("john.smith");
    }

    @Test
    @WithMockUser
    void shouldUpdateTraineeTrainers() throws Exception {
        UpdateTraineeTrainersRequest request = new UpdateTraineeTrainersRequest();
        request.setTrainerUsernames(List.of("anna.brown"));

        when(traineeService.updateTrainerList(anyString(), any())).thenReturn(List.of());

        mockMvc.perform(put("/api/trainees/john.smith/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void shouldGetTraineeTrainings() throws Exception {
        Training training = new Training();
        training.setTrainingName("Morning Run");
        training.setTrainingDate(LocalDate.of(2024, 1, 10));
        training.setDuration(45);
        TrainingType type = new TrainingType("CARDIO");
        training.setTrainingType(type);

        when(trainingService.getTraineeTrainings("john.smith", null, null, null, null))
                .thenReturn(List.of(training));

        mockMvc.perform(get("/api/trainees/john.smith/trainings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Morning Run"));
    }
}
