package com.epam.training.controller;

import com.epam.training.advice.GlobalExceptionHandler;
import com.epam.training.dto.request.ActiveStatusRequest;
import com.epam.training.dto.request.TrainerRegistrationRequest;
import com.epam.training.dto.request.UpdateTrainerRequest;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.model.TrainingType;
import com.epam.training.security.JwtAuthenticationFilter;
import com.epam.training.security.SecurityConfig;
import com.epam.training.service.TrainerService;
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

@WebMvcTest(controllers = TrainerController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
        })
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrainerService trainerService;

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
    void shouldRegisterTrainerWithoutAuth() throws Exception {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstName("Anna");
        request.setLastName("Brown");
        request.setSpecializationName("CARDIO");

        when(trainerService.create(any(Trainer.class), anyString()))
                .thenReturn(new String[]{"anna.brown", "rawpass"});

        mockMvc.perform(post("/api/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("anna.brown"))
                .andExpect(jsonPath("$.password").value("rawpass"));
    }

    @Test
    void shouldReturn400OnInvalidRegistration() throws Exception {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        // Missing firstName, lastName, specializationName

        mockMvc.perform(post("/api/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnUnauthorizedWhenNoToken() throws Exception {
        mockMvc.perform(get("/api/trainers/anna.brown"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void shouldGetTrainerProfile() throws Exception {
        Trainer trainer = new Trainer();
        trainer.setUsername("anna.brown");
        trainer.setFirstName("Anna");
        trainer.setLastName("Brown");
        trainer.setActive(true);
        trainer.setTrainees(Collections.emptySet());

        when(trainerService.findByUsername("anna.brown")).thenReturn(trainer);

        mockMvc.perform(get("/api/trainers/anna.brown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Anna"))
                .andExpect(jsonPath("$.lastName").value("Brown"));
    }

    @Test
    @WithMockUser
    void shouldUpdateTrainerProfile() throws Exception {
        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setFirstName("Anna");
        request.setLastName("Green");
        request.setSpecializationName("YOGA");

        Trainer updated = new Trainer();
        updated.setFirstName("Anna");
        updated.setLastName("Green");
        updated.setActive(true);
        updated.setTrainees(Collections.emptySet());

        when(trainerService.update(anyString(), any(Trainer.class), anyString())).thenReturn(updated);

        mockMvc.perform(put("/api/trainers/anna.brown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Anna"))
                .andExpect(jsonPath("$.lastName").value("Green"));
    }

    @Test
    @WithMockUser
    void shouldSetTrainerActive() throws Exception {
        ActiveStatusRequest request = new ActiveStatusRequest();
        request.setIsActive(true);
        doNothing().when(trainerService).activate("anna.brown");

        mockMvc.perform(patch("/api/trainers/anna.brown/active")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(trainerService).activate("anna.brown");
    }

    @Test
    @WithMockUser
    void shouldSetTrainerInactive() throws Exception {
        ActiveStatusRequest request = new ActiveStatusRequest();
        request.setIsActive(false);
        doNothing().when(trainerService).deactivate("anna.brown");

        mockMvc.perform(patch("/api/trainers/anna.brown/active")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(trainerService).deactivate("anna.brown");
    }

    @Test
    @WithMockUser
    void shouldGetTrainerTrainings() throws Exception {
        Training training = new Training();
        training.setTrainingName("Morning Yoga");
        training.setTrainingDate(LocalDate.of(2024, 1, 15));
        training.setDuration(60);
        TrainingType type = new TrainingType("YOGA");
        training.setTrainingType(type);

        when(trainingService.getTrainerTrainings("anna.brown", null, null, null))
                .thenReturn(List.of(training));

        mockMvc.perform(get("/api/trainers/anna.brown/trainings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Morning Yoga"));
    }
}
