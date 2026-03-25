package com.epam.training.controller;

import com.epam.training.advice.GlobalExceptionHandler;
import com.epam.training.dto.request.TraineeRegistrationRequest;
import com.epam.training.dto.request.UpdateActiveStatusRequest;
import com.epam.training.dto.request.UpdateTraineeRequest;
import com.epam.training.dto.request.UpdateTraineeTrainerListRequest;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import com.epam.training.service.TraineeService;
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
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TraineeController.class)
@Import(GlobalExceptionHandler.class)
class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TraineeService traineeService;

    @MockBean
    private TrainingService trainingService;

    @Test
    void register_returnsOk() throws Exception {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
        request.setAddress("Yerevan");

        Trainee trainee = trainee();
        trainee.getUser().setUsername("john.doe");
        trainee.getUser().setPassword("generated");

        when(traineeService.create(any(Trainee.class))).thenReturn(trainee);

        mockMvc.perform(post("/api/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.password").value("generated"));
    }

    @Test
    void getProfile_returnsOk() throws Exception {
        when(traineeService.findByUsername("john.doe", "pass")).thenReturn(trainee());

        mockMvc.perform(get("/api/trainees/john.doe")
                        .param("password", "pass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"));
    }

    @Test
    void update_returnsBadRequest_whenUsernameMismatch() throws Exception {
        UpdateTraineeRequest request = new UpdateTraineeRequest();
        request.setUsername("another.user");
        request.setPassword("pass");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));

        mockMvc.perform(put("/api/trainees/john.doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_returnsOk() throws Exception {
        mockMvc.perform(delete("/api/trainees/john.doe")
                        .param("password", "pass"))
                .andExpect(status().isOk())
                .andExpect(content().string("Trainee deleted successfully"));

        verify(traineeService).deleteByUsername("john.doe", "pass");
    }

    @Test
    void updateActiveStatus_deactivates() throws Exception {
        UpdateActiveStatusRequest request = new UpdateActiveStatusRequest();
        request.setUsername("john.doe");
        request.setPassword("pass");
        request.setActive(false);

        mockMvc.perform(patch("/api/trainees/john.doe/active")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Trainee deactivated successfully"));

        verify(traineeService).deactivate("john.doe", "pass");
    }

    @Test
    void getUnassignedTrainers_returnsOk() throws Exception {
        Trainer trainer = trainer("jane.smith", "Jane", "Smith", "YOGA");

        when(traineeService.getUnassignedTrainers("john.doe", "pass"))
                .thenReturn(List.of(trainer));

        mockMvc.perform(get("/api/trainees/john.doe/unassigned-trainers")
                        .param("password", "pass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("jane.smith"));
    }

    @Test
    void updateTrainerList_returnsOk() throws Exception {
        UpdateTraineeTrainerListRequest request = new UpdateTraineeTrainerListRequest();
        request.setTraineeUsername("john.doe");
        request.setPassword("pass");
        request.setTrainerUsernames(List.of("jane.smith"));

        Trainer trainer = trainer("jane.smith", "Jane", "Smith", "YOGA");

        when(traineeService.updateTrainerList("john.doe", "pass", List.of("jane.smith")))
                .thenReturn(List.of(trainer));

        mockMvc.perform(put("/api/trainees/john.doe/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("jane.smith"));
    }

    @Test
    void getTrainings_returnsOk() throws Exception {
        Training training = new Training();
        training.setTrainingName("Morning Workout");
        training.setTrainingDate(LocalDate.of(2026, 3, 20));
        training.setDuration(60);

        Trainer trainer = new Trainer();
        User trainerUser = new User();
        trainerUser.setFirstName("Jane");
        trainerUser.setLastName("Smith");
        trainer.setUser(trainerUser);
        training.setTrainer(trainer);

        TrainingType type = new TrainingType();
        type.setTrainingTypeName("FITNESS");
        training.setTrainingType(type);

        when(trainingService.getTraineeTrainings(eq("john.doe"), eq("pass"), any()))
                .thenReturn(List.of(training));

        mockMvc.perform(get("/api/trainees/john.doe/trainings")
                        .param("username", "john.doe")
                        .param("password", "pass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Morning Workout"))
                .andExpect(jsonPath("$[0].trainerName").value("Jane Smith"));
    }

    private Trainee trainee() {
        User user = new User();
        user.setUsername("john.doe");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("generated");
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
        trainee.setAddress("Yerevan");
        trainee.setTrainers(Set.of());
        return trainee;
    }

    private Trainer trainer(String username, String firstName, String lastName, String specializationName) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        TrainingType type = new TrainingType();
        type.setTrainingTypeName(specializationName);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(type);
        return trainer;
    }
}