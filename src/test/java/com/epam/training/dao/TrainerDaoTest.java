package com.epam.training.dao;

import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TrainerDaoTest {

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private TraineeDao traineeDao;

    @Autowired
    private TrainingTypeDao trainingTypeDao;

    @Test
    void save_andFindByUsername() {
        Trainer trainer = buildTrainer("trainer.dao.test", "YOGA");

        trainerDao.save(trainer);

        Optional<Trainer> found = trainerDao.findByUsername("trainer.dao.test");

        assertTrue(found.isPresent());
        assertEquals("Jane", found.get().getUser().getFirstName());
        assertEquals("YOGA", found.get().getSpecialization().getTrainingTypeName());
    }

    @Test
    void update_changesName() {
        Trainer trainer = buildTrainer("trainer.update.test", "FITNESS");
        trainerDao.save(trainer);

        Trainer saved = trainerDao.findByUsername("trainer.update.test").orElseThrow();
        saved.getUser().setLastName("Updated");

        Trainer updated = trainerDao.update(saved);

        assertEquals("Updated", updated.getUser().getLastName());
    }

    @Test
    void findAll_returnsList() {
        trainerDao.save(buildTrainer("trainer.all.test", "YOGA"));

        List<Trainer> list = trainerDao.findAll();

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    void delete_removesEntity() {
        Trainer trainer = buildTrainer("trainer.delete.test", "FITNESS");
        trainerDao.save(trainer);

        Trainer saved = trainerDao.findByUsername("trainer.delete.test").orElseThrow();
        trainerDao.delete(saved);

        Optional<Trainer> found = trainerDao.findByUsername("trainer.delete.test");
        assertTrue(found.isEmpty());
    }

    @Test
    void findNotAssignedToTrainee_returnsOnlyNotAssignedActive() {
        Trainer assignedTrainer = buildTrainer("assigned.trainer", "YOGA");
        Trainer unassignedTrainer = buildTrainer("unassigned.trainer", "FITNESS");
        Trainer inactiveTrainer = buildTrainer("inactive.trainer", "YOGA");
        inactiveTrainer.getUser().setActive(false);

        trainerDao.save(assignedTrainer);
        trainerDao.save(unassignedTrainer);
        trainerDao.save(inactiveTrainer);

        Trainee trainee = buildTrainee("trainee.for.assignment");
        trainee.setTrainers(Set.of(assignedTrainer));
        traineeDao.save(trainee);

        List<Trainer> result = trainerDao.findNotAssignedToTrainee("trainee.for.assignment");

        assertTrue(result.stream().anyMatch(t -> "unassigned.trainer".equals(t.getUser().getUsername())));
        assertTrue(result.stream().noneMatch(t -> "assigned.trainer".equals(t.getUser().getUsername())));
        assertTrue(result.stream().noneMatch(t -> "inactive.trainer".equals(t.getUser().getUsername())));
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