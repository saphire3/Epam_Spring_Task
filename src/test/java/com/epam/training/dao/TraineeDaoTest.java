package com.epam.training.dao;

import com.epam.training.model.Trainee;
import com.epam.training.model.Training;
import com.epam.training.storage.InMemoryStorage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TraineeDaoTest {

    @Test
    void shouldSaveAndFindTrainee() {

        InMemoryStorage storage = new InMemoryStorage();
        TraineeDao dao = new TraineeDao();
        dao.setStorage(storage);

        Trainee trainee = new Trainee();
        trainee.setId(1L);

        dao.save(1L, trainee);

        assertNotNull(dao.findById(1L));
    }
    @Test
    void shouldReturnEmptyListWhenNoEntities() {

        InMemoryStorage storage = new InMemoryStorage();
        TrainingDao dao = new TrainingDao();
        dao.setStorage(storage);

        assertTrue(dao.findAll().isEmpty());
    }
    @Test
    void shouldReturnAllTrainings() {

        InMemoryStorage storage = new InMemoryStorage();
        TrainingDao dao = new TrainingDao();
        dao.setStorage(storage);

        Training training = new Training();
        training.setId(1L);

        dao.save(1L, training);

        assertEquals(1, dao.findAll().size());
    }

    @Test
    void shouldFindTrainingById() {

        InMemoryStorage storage = new InMemoryStorage();
        TrainingDao dao = new TrainingDao();
        dao.setStorage(storage);

        Training training = new Training();
        training.setId(1L);

        dao.save(1L, training);

        assertNotNull(dao.findById(1L));
    }
}