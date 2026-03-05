package com.epam.training.storage;

import com.epam.training.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    Optional<User> findById(Long id);

    List<User> findAll();

    void save(User user);

    void delete(Long id);
}