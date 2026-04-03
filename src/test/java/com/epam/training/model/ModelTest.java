package com.epam.training.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void trainingTypeConstructorAndGetters() {
        TrainingType tt = new TrainingType("CARDIO");
        assertEquals("CARDIO", tt.getTrainingTypeName());
        assertNull(tt.getId());

        tt.setId(1L);
        tt.setTrainingTypeName("YOGA");
        assertEquals(1L, tt.getId());
        assertEquals("YOGA", tt.getTrainingTypeName());
    }

    @Test
    void trainingGettersAndSetters() {
        Training t = new Training();
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        TrainingType type = new TrainingType("STRENGTH");
        LocalDate date = LocalDate.of(2024, 3, 1);

        t.setId(1L);
        t.setTrainee(trainee);
        t.setTrainer(trainer);
        t.setTrainingType(type);
        t.setTrainingName("Power Session");
        t.setTrainingDate(date);
        t.setDuration(90);

        assertEquals(1L, t.getId());
        assertSame(trainee, t.getTrainee());
        assertSame(trainer, t.getTrainer());
        assertSame(type, t.getTrainingType());
        assertEquals("Power Session", t.getTrainingName());
        assertEquals(date, t.getTrainingDate());
        assertEquals(90, t.getDuration());
    }

    @Test
    void trainerGettersAndSetters() {
        Trainer trainer = new Trainer();
        TrainingType spec = new TrainingType("YOGA");

        trainer.setSpecialization(spec);
        trainer.setTrainees(new HashSet<>());

        assertSame(spec, trainer.getSpecialization());
        assertNotNull(trainer.getTrainees());
        assertTrue(trainer.getTrainees().isEmpty());
    }

    @Test
    void traineeGettersAndSetters() {
        Trainee trainee = new Trainee();
        LocalDate dob = LocalDate.of(1990, 5, 20);

        trainee.setDateOfBirth(dob);
        trainee.setAddress("123 Main St");
        trainee.setTrainers(new HashSet<>());

        assertEquals(dob, trainee.getDateOfBirth());
        assertEquals("123 Main St", trainee.getAddress());
        assertNotNull(trainee.getTrainers());
    }

    @Test
    void userGettersAndSetters() {
        Trainee user = new Trainee();
        user.setId(5L);
        user.setFirstName("John");
        user.setLastName("Smith");
        user.setUsername("john.smith");
        user.setPassword("hashed");
        user.setActive(true);

        assertEquals(5L, user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("john.smith", user.getUsername());
        assertEquals("hashed", user.getPassword());
        assertTrue(user.isActive());
    }
}