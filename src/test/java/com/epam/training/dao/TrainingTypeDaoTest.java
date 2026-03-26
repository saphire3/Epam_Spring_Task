package com.epam.training.dao;

import com.epam.training.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TrainingTypeDaoTest {

    @Autowired
    private TrainingTypeDao trainingTypeDao;

    @Test
    void findAll_returnsSeededTypes() {
        List<TrainingType> types = trainingTypeDao.findAll();

        assertNotNull(types);
        assertFalse(types.isEmpty());
    }

    @Test
    void save_andFindByName() {
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("BOXING_TEST");

        trainingTypeDao.save(type);

        Optional<TrainingType> found = trainingTypeDao.findByName("BOXING_TEST");

        assertTrue(found.isPresent());
        assertEquals("BOXING_TEST", found.get().getTrainingTypeName());
    }

    @Test
    void findByName_returnsEmpty_whenMissing() {
        Optional<TrainingType> found = trainingTypeDao.findByName("MISSING_TYPE");
        assertTrue(found.isEmpty());
    }
}