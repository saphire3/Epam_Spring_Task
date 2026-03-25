package com.epam.training.mapper;

import com.epam.training.dto.response.TrainerProfileResponse;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainerMapperTest {

    @Test
    void toProfileResponse_mapsCorrectly() {
        Trainer trainer = new Trainer();

        User user = new User();
        user.setUsername("jane.smith");
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setActive(true);
        trainer.setUser(user);

        TrainingType type = new TrainingType();
        type.setTrainingTypeName("YOGA");
        trainer.setSpecialization(type);

        Trainee trainee = new Trainee();
        User traineeUser = new User();
        traineeUser.setUsername("john.doe");
        traineeUser.setFirstName("John");
        traineeUser.setLastName("Doe");
        trainee.setUser(traineeUser);

        trainer.setTrainees(Set.of(trainee));

        TrainerProfileResponse response = TrainerMapper.toProfileResponse(trainer);

        assertEquals("jane.smith", response.getUsername());
        assertEquals("Jane", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals("YOGA", response.getSpecialization());
        assertEquals(1, response.getTrainees().size());
        assertEquals("john.doe", response.getTrainees().get(0).getUsername());
    }
}