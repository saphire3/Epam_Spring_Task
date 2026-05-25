package com.epam.training.controller;

import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Training;
import com.epam.training.service.TrainingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainingController.class)
class TrainingControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean TrainingService trainingService;

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetAllTrainings() throws Exception {
        when(trainingService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/trainings"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetTrainingById() throws Exception {
        Training training = new Training();
        training.setId(1L);
        training.setTrainingName("Cardio");
        training.setTrainingDate(LocalDate.now());

        when(trainingService.getTrainingById(1L)).thenReturn(training);

        mockMvc.perform(get("/api/trainings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainingName").value("Cardio"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldCreateTraining() throws Exception {
        Training input = new Training();
        input.setTraineeId(1L);
        input.setTrainerId(1L);
        input.setTrainingName("Cardio");
        input.setTrainingDate(LocalDate.now());
        input.setDuration(60);

        Training saved = new Training();
        saved.setId(1L);
        saved.setTrainingName("Cardio");

        when(trainingService.create(any())).thenReturn(saved);

        mockMvc.perform(post("/api/trainings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldDeleteTraining() throws Exception {
        doNothing().when(trainingService).delete(1L);
        mockMvc.perform(delete("/api/trainings/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn404WhenTrainingNotFound() throws Exception {
        when(trainingService.getTrainingById(99L))
                .thenThrow(new UserNotFoundException(99L));
        mockMvc.perform(get("/api/trainings/99"))
                .andExpect(status().isNotFound());
    }
}