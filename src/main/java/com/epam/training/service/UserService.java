package com.epam.training.service;

import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.User;
import com.epam.training.storage.UserStorage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUserById(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    public void deleteUser(Long id) {
        if (userStorage.findById(id).isEmpty()) {
            throw new UserNotFoundException(id);
        }
        userStorage.delete(id);
    }
}