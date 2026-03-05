package com.epam.training.dao;

import com.epam.training.model.Trainee;
import com.epam.training.storage.InMemoryStorage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TraineeDaoTest {

    @Test
    void shouldSaveAndFindTrainee() {

        InMemoryStorage storage = new InMemoryStorage();
        TraineeDao dao = new TraineeDao(storage);

        Trainee trainee = new Trainee();
        trainee.setId(1L);

        dao.save(1L, trainee);

        assertNotNull(dao.getById(1L));
    }

    @Test
    void shouldReturnEmptyListWhenNoEntities() {

        InMemoryStorage storage = new InMemoryStorage();
        TraineeDao dao = new TraineeDao(storage);

        assertTrue(dao.findAll().isEmpty());
    }
}