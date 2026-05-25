package com.epam.training.controller;

import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainer;
import com.epam.training.service.TrainerService;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainerController.class)
class TrainerControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean TrainerService trainerService;

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetAllTrainers() throws Exception {
        when(trainerService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/trainers"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldCreateTrainer() throws Exception {
        Trainer input = new Trainer();
        input.setFirstName("Anna");
        input.setLastName("Brown");

        Trainer saved = new Trainer();
        saved.setId(1L);
        saved.setFirstName("Anna");
        saved.setLastName("Brown");
        saved.setUsername("anna.brown");
        saved.setPassword("pass1234");

        when(trainerService.create(any())).thenReturn(saved);

        mockMvc.perform(post("/api/trainers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("anna.brown"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn404WhenTrainerNotFound() throws Exception {
        when(trainerService.getTrainerById(99L))
                .thenThrow(new UserNotFoundException(99L));
        mockMvc.perform(get("/api/trainers/99"))
                .andExpect(status().isNotFound());
    }
}