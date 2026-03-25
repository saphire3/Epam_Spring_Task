package com.epam.training.controller;

import com.epam.training.advice.GlobalExceptionHandler;
import com.epam.training.dto.request.AddTrainingRequest;
import com.epam.training.service.TrainingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainingController.class)
@Import(GlobalExceptionHandler.class)
class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrainingService trainingService;

    @Test
    void addTraining_returnsOk() throws Exception {
        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("john.doe");
        request.setTraineePassword("pass123");
        request.setTrainerUsername("jane.smith");
        request.setTrainingTypeName("FITNESS");
        request.setTrainingName("Morning Workout");
        request.setTrainingDate(LocalDate.of(2026, 3, 20));
        request.setDuration(60);

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Training added successfully"));

        verify(trainingService).create(eq("john.doe"), eq("pass123"), eq("jane.smith"), eq("FITNESS"), any());
    }

    @Test
    void addTraining_returnsBadRequest_whenInvalidBody() throws Exception {
        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("");
        request.setTraineePassword("");
        request.setTrainerUsername("");
        request.setTrainingTypeName("");
        request.setTrainingName("");
        request.setDuration(0);

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}