package com.epam.training.service;

import com.epam.training.dao.UserDao;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return userDao.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
    }

    public User update(User user) {
        return userDao.merge(user);
    }
}