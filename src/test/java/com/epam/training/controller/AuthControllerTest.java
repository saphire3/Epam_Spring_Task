package com.epam.training.controller;

import com.epam.training.advice.GlobalExceptionHandler;
import com.epam.training.dto.request.ChangePasswordRequest;
import com.epam.training.exception.AuthenticationException;
import com.epam.training.service.AuthService;
import com.epam.training.service.TraineeService;
import com.epam.training.service.TrainerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;
    @MockBean
    private TraineeService traineeService;
    @MockBean
    private TrainerService trainerService;

    @Test
    void login_returnsOk() throws Exception {
        mockMvc.perform(get("/api/auth/login")
                        .param("username", "john.doe")
                        .param("password", "pass123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));

        verify(authService).requireAnyUserAuth("john.doe", "pass123");
    }

    @Test
    void login_returnsUnauthorized_whenInvalid() throws Exception {
        doThrow(new AuthenticationException("Invalid username or password"))
                .when(authService).requireAnyUserAuth("john.doe", "bad");

        mockMvc.perform(get("/api/auth/login")
                        .param("username", "john.doe")
                        .param("password", "bad"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changePassword_changesForTrainee() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setUsername("john.doe");
        request.setOldPassword("old");
        request.setNewPassword("new");

        when(authService.authenticateTrainee("john.doe", "old")).thenReturn(true);

        mockMvc.perform(put("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password changed successfully"));

        verify(traineeService).changePassword("john.doe", "old", "new");
    }

    @Test
    void changePassword_changesForTrainer() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setUsername("jane.smith");
        request.setOldPassword("old");
        request.setNewPassword("new");

        when(authService.authenticateTrainee("jane.smith", "old")).thenReturn(false);
        when(authService.authenticateTrainer("jane.smith", "old")).thenReturn(true);

        mockMvc.perform(put("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(trainerService).changePassword("jane.smith", "old", "new");
    }

    @Test
    void changePassword_returnsUnauthorized_whenNoMatch() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setUsername("ghost");
        request.setOldPassword("old");
        request.setNewPassword("new");

        when(authService.authenticateTrainee("ghost", "old")).thenReturn(false);
        when(authService.authenticateTrainer("ghost", "old")).thenReturn(false);

        mockMvc.perform(put("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}