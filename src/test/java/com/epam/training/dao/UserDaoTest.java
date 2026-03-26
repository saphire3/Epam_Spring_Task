package com.epam.training.dao;

import com.epam.training.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @Test
    void save_andFindByUsername() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("user.dao.test");
        user.setPassword("pass123");
        user.setActive(true);

        userDao.save(user);

        Optional<User> found = userDao.findByUsername("user.dao.test");

        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
        assertEquals("Doe", found.get().getLastName());
    }

    @Test
    void merge_updatesUser() {
        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setUsername("merge.user.test");
        user.setPassword("pass123");
        user.setActive(true);

        userDao.save(user);

        User saved = userDao.findByUsername("merge.user.test").orElseThrow();
        saved.setLastName("Updated");

        User merged = userDao.merge(saved);

        assertEquals("Updated", merged.getLastName());
    }

    @Test
    void findByUsername_returnsEmpty_whenMissing() {
        Optional<User> found = userDao.findByUsername("missing.user");
        assertTrue(found.isEmpty());
    }
}