
package com.epam.training.controller;

import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainee;
import com.epam.training.service.TraineeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TraineeController.class)
class TraineeControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean TraineeService traineeService;

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetAllTrainees() throws Exception {
        when(traineeService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/trainees"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetTraineeById() throws Exception {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Smith");
        trainee.setUsername("john.smith");
        trainee.setPassword("pass");

        when(traineeService.getTraineeById(1L)).thenReturn(trainee);

        mockMvc.perform(get("/api/trainees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.smith"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldCreateTrainee() throws Exception {
        Trainee input = new Trainee();
        input.setFirstName("John");
        input.setLastName("Smith");

        Trainee saved = new Trainee();
        saved.setId(1L);
        saved.setFirstName("John");
        saved.setLastName("Smith");
        saved.setUsername("john.smith");
        saved.setPassword("pass1234");

        when(traineeService.create(any())).thenReturn(saved);

        mockMvc.perform(post("/api/trainees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john.smith"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldUpdateTrainee() throws Exception {
        Trainee input = new Trainee();
        input.setFirstName("Updated");
        input.setLastName("Name");

        Trainee updated = new Trainee();
        updated.setId(1L);
        updated.setFirstName("Updated");
        updated.setLastName("Name");
        updated.setUsername("updated.name");
        updated.setPassword("pass");

        when(traineeService.update(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/trainees/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldDeleteTrainee() throws Exception {
        doNothing().when(traineeService).delete(1L);
        mockMvc.perform(delete("/api/trainees/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn404WhenTraineeNotFound() throws Exception {
        when(traineeService.getTraineeById(99L))
                .thenThrow(new UserNotFoundException(99L));
        mockMvc.perform(get("/api/trainees/99"))
                .andExpect(status().isNotFound());
    }
}