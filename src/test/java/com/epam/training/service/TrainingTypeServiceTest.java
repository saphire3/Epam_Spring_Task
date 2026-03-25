package com.epam.training.service;

import com.epam.training.dao.TrainingTypeDao;
import com.epam.training.dto.response.TrainingTypeResponse;
import com.epam.training.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceTest {

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @InjectMocks
    private TrainingTypeService trainingTypeService;

    @Test
    void findAll_mapsToResponse() throws Exception {
        TrainingType one = new TrainingType();
        setId(one, 1L);
        one.setTrainingTypeName("FITNESS");

        TrainingType two = new TrainingType();
        setId(two, 2L);
        two.setTrainingTypeName("YOGA");

        when(trainingTypeDao.findAll()).thenReturn(List.of(one, two));

        List<TrainingTypeResponse> result = trainingTypeService.findAll();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("FITNESS", result.get(0).getTrainingTypeName());
        assertEquals("YOGA", result.get(1).getTrainingTypeName());
    }

    private void setId(TrainingType type, Long id) throws Exception {
        Field field = TrainingType.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(type, id);
    }
}