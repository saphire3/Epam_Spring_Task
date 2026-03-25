package com.epam.training.controller;

import com.epam.training.advice.GlobalExceptionHandler;
import com.epam.training.dto.request.TrainerRegistrationRequest;
import com.epam.training.dto.request.UpdateActiveStatusRequest;
import com.epam.training.dto.request.UpdateTrainerRequest;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import com.epam.training.service.TrainerService;
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
import java.util.Set;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainerController.class)
@Import(GlobalExceptionHandler.class)
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrainerService trainerService;

    @MockBean
    private TrainingService trainingService;

    @Test
    void register_returnsOk() throws Exception {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setSpecializationName("YOGA");

        Trainer trainer = trainer();
        trainer.getUser().setUsername("jane.smith");
        trainer.getUser().setPassword("generated");

        when(trainerService.create(any(Trainer.class), eq("YOGA"))).thenReturn(trainer);

        mockMvc.perform(post("/api/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jane.smith"))
                .andExpect(jsonPath("$.password").value("generated"));
    }

    @Test
    void getProfile_returnsOk() throws Exception {
        when(trainerService.findByUsername("jane.smith", "pass")).thenReturn(trainer());

        mockMvc.perform(get("/api/trainers/jane.smith")
                        .param("password", "pass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jane.smith"));
    }

    @Test
    void update_returnsBadRequest_whenUsernameMismatch() throws Exception {
        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setUsername("another.user");
        request.setPassword("pass");
        request.setFirstName("Jane");
        request.setLastName("Smith");

        mockMvc.perform(put("/api/trainers/jane.smith")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateActiveStatus_activates() throws Exception {
        UpdateActiveStatusRequest request = new UpdateActiveStatusRequest();
        request.setUsername("jane.smith");
        request.setPassword("pass");
        request.setActive(true);

        mockMvc.perform(patch("/api/trainers/jane.smith/active")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Trainer activated successfully"));

        verify(trainerService).activate("jane.smith", "pass");
    }

    @Test
    void getTrainings_returnsOk() throws Exception {
        Training training = new Training();
        training.setTrainingName("Morning Workout");
        training.setTrainingDate(LocalDate.of(2026, 3, 20));
        training.setDuration(60);

        Trainee trainee = new Trainee();
        User traineeUser = new User();
        traineeUser.setFirstName("John");
        traineeUser.setLastName("Doe");
        trainee.setUser(traineeUser);
        training.setTrainee(trainee);

        TrainingType type = new TrainingType();
        type.setTrainingTypeName("FITNESS");
        training.setTrainingType(type);

        when(trainingService.getTrainerTrainings(eq("jane.smith"), eq("pass"), any()))
                .thenReturn(List.of(training));

        mockMvc.perform(get("/api/trainers/jane.smith/trainings")
                        .param("username", "jane.smith")
                        .param("password", "pass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Morning Workout"))
                .andExpect(jsonPath("$[0].traineeName").value("John Doe"));
    }

    private Trainer trainer() {
        User user = new User();
        user.setUsername("jane.smith");
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setPassword("generated");
        user.setActive(true);

        TrainingType type = new TrainingType();
        type.setTrainingTypeName("YOGA");

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(type);
        trainer.setTrainees(Set.of());
        return trainer;
    }
}