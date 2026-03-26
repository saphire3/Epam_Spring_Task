package com.epam.training.dao;

import com.epam.training.model.Trainee;
import com.epam.training.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TraineeDaoTest {

    @Autowired
    private TraineeDao traineeDao;

    @Test
    void save_andFindByUsername() {
        Trainee trainee = buildTrainee("trainee.dao.test");

        traineeDao.save(trainee);

        Optional<Trainee> found = traineeDao.findByUsername("trainee.dao.test");

        assertTrue(found.isPresent());
        assertEquals("John", found.get().getUser().getFirstName());
        assertEquals("Yerevan", found.get().getAddress());
    }

    @Test
    void update_changesFields() {
        Trainee trainee = buildTrainee("trainee.update.test");
        traineeDao.save(trainee);

        Trainee saved = traineeDao.findByUsername("trainee.update.test").orElseThrow();
        saved.setAddress("Updated Address");

        Trainee updated = traineeDao.update(saved);

        assertEquals("Updated Address", updated.getAddress());
    }

    @Test
    void findAll_returnsList() {
        traineeDao.save(buildTrainee("trainee.all.test"));

        List<Trainee> list = traineeDao.findAll();

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    void delete_removesEntity() {
        Trainee trainee = buildTrainee("trainee.delete.test");
        traineeDao.save(trainee);

        Trainee saved = traineeDao.findByUsername("trainee.delete.test").orElseThrow();
        traineeDao.delete(saved);

        Optional<Trainee> found = traineeDao.findByUsername("trainee.delete.test");
        assertTrue(found.isEmpty());
    }

    @Test
    void findByUsername_returnsEmpty_whenMissing() {
        Optional<Trainee> found = traineeDao.findByUsername("missing.trainee");
        assertTrue(found.isEmpty());
    }

    private Trainee buildTrainee(String username) {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername(username);
        user.setPassword("pass123");
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
        trainee.setAddress("Yerevan");
        return trainee;
    }
}