package com.epam.training.dao;

import com.epam.training.dto.filter.TraineeTrainingFilter;
import com.epam.training.dto.filter.TrainerTrainingFilter;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.model.TrainingType;
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
class TrainingDaoTest {

    @Autowired
    private TrainingDao trainingDao;

    @Autowired
    private TraineeDao traineeDao;

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private TrainingTypeDao trainingTypeDao;

    @Test
    void save_andFindById() {
        Training training = buildTraining("trainee.training.id", "trainer.training.id", "FITNESS", "Morning Workout");
        trainingDao.save(training);

        List<Training> all = trainingDao.findAll();
        assertFalse(all.isEmpty());

        Long id = all.get(all.size() - 1).getId();
        Optional<Training> found = trainingDao.findById(id);

        assertTrue(found.isPresent());
        assertEquals("Morning Workout", found.get().getTrainingName());
    }

    @Test
    void findAll_returnsList() {
        trainingDao.save(buildTraining("trainee.training.all", "trainer.training.all", "YOGA", "Yoga Session"));

        List<Training> trainings = trainingDao.findAll();

        assertNotNull(trainings);
        assertFalse(trainings.isEmpty());
    }

    @Test
    void findTraineeTrainings_returnsMatchingTrainings() {
        trainingDao.save(buildTraining("trainee.filter.one", "trainer.filter.one", "FITNESS", "Workout A"));

        TraineeTrainingFilter filter = new TraineeTrainingFilter();
        filter.setTrainingTypeName("FITNESS");

        List<Training> result = trainingDao.findTraineeTrainings("trainee.filter.one", filter);

        assertEquals(1, result.size());
        assertEquals("Workout A", result.get(0).getTrainingName());
    }

    @Test
    void findTrainerTrainings_returnsMatchingTrainings() {
        trainingDao.save(buildTraining("trainee.filter.two", "trainer.filter.two", "YOGA", "Yoga A"));

        TrainerTrainingFilter filter = new TrainerTrainingFilter();
        filter.setTraineeName("John");

        List<Training> result = trainingDao.findTrainerTrainings("trainer.filter.two", filter);

        assertEquals(1, result.size());
        assertEquals("Yoga A", result.get(0).getTrainingName());
    }

    private Training buildTraining(String traineeUsername,
                                   String trainerUsername,
                                   String trainingTypeName,
                                   String trainingName) {

        Trainee trainee = buildTrainee(traineeUsername);
        Trainer trainer = buildTrainer(trainerUsername, trainingTypeName);
        TrainingType type = trainingTypeDao.findByName(trainingTypeName).orElseThrow();

        traineeDao.save(trainee);
        trainerDao.save(trainer);

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(type);
        training.setTrainingName(trainingName);
        training.setTrainingDate(LocalDate.of(2026, 3, 25));
        training.setDuration(60);
        return training;
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

    private Trainer buildTrainer(String username, String specializationName) {
        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setUsername(username);
        user.setPassword("pass123");
        user.setActive(true);

        TrainingType type = trainingTypeDao.findByName(specializationName).orElseThrow();

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(type);
        return trainer;
    }
}