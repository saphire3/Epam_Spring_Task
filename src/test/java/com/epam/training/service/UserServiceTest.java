package com.epam.training.service;

import com.epam.training.dao.UserDao;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @Test
    void getByUsername_returnsUser_whenFound() {
        User user = new User();
        user.setUsername("john.doe");

        when(userDao.findByUsername("john.doe")).thenReturn(Optional.of(user));

        User result = userService.getByUsername("john.doe");

        assertEquals("john.doe", result.getUsername());
    }

    @Test
    void getByUsername_throws_whenMissing() {
        when(userDao.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getByUsername("missing"));
    }

    @Test
    void update_returnsMergedUser() {
        User user = new User();
        user.setUsername("john.doe");

        when(userDao.merge(user)).thenReturn(user);

        User result = userService.update(user);

        assertSame(user, result);
    }
}