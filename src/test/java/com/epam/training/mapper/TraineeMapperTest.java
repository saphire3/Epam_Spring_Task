package com.epam.training.mapper;

import com.epam.training.dto.response.TraineeProfileResponse;
import com.epam.training.dto.response.TraineeSummaryResponse;
import com.epam.training.dto.response.TrainerSummaryResponse;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TraineeMapperTest {

    @Test
    void toProfileResponse_mapsCorrectly() {
        Trainee trainee = new Trainee();

        User user = new User();
        user.setUsername("john.doe");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setActive(true);
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
        trainee.setAddress("Yerevan");

        Trainer trainer = new Trainer();
        User trainerUser = new User();
        trainerUser.setUsername("jane.smith");
        trainerUser.setFirstName("Jane");
        trainerUser.setLastName("Smith");
        trainer.setUser(trainerUser);

        TrainingType type = new TrainingType();
        type.setTrainingTypeName("YOGA");
        trainer.setSpecialization(type);

        trainee.setTrainers(Set.of(trainer));

        TraineeProfileResponse response = TraineeMapper.toProfileResponse(trainee);

        assertEquals("john.doe", response.getUsername());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals(LocalDate.of(2000, 1, 1), response.getDateOfBirth());
        assertEquals("Yerevan", response.getAddress());
        assertEquals(1, response.getTrainers().size());
        assertEquals("jane.smith", response.getTrainers().get(0).getUsername());
    }

    @Test
    void toTrainerSummaryResponse_mapsCorrectly() {
        Trainer trainer = new Trainer();

        User user = new User();
        user.setUsername("jane.smith");
        user.setFirstName("Jane");
        user.setLastName("Smith");
        trainer.setUser(user);

        TrainingType type = new TrainingType();
        type.setTrainingTypeName("YOGA");
        trainer.setSpecialization(type);

        TrainerSummaryResponse response = TraineeMapper.toTrainerSummaryResponse(trainer);

        assertEquals("jane.smith", response.getUsername());
        assertEquals("Jane", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals("YOGA", response.getSpecialization());
    }

    @Test
    void toTraineeSummaryResponse_mapsCorrectly() {
        Trainee trainee = new Trainee();

        User user = new User();
        user.setUsername("john.doe");
        user.setFirstName("John");
        user.setLastName("Doe");
        trainee.setUser(user);

        TraineeSummaryResponse response = TraineeMapper.toTraineeSummaryResponse(trainee);

        assertEquals("john.doe", response.getUsername());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
    }
}