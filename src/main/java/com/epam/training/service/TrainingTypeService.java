package com.epam.training.service;

import com.epam.training.dao.TrainingTypeDao;
import com.epam.training.dto.response.TrainingTypeResponse;
import com.epam.training.model.TrainingType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TrainingTypeService {

    private final TrainingTypeDao trainingTypeDao;

    public TrainingTypeService(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    public List<TrainingTypeResponse> findAll() {
        List<TrainingType> types = trainingTypeDao.findAll();
        return types.stream()
                .map(type -> new TrainingTypeResponse(type.getId(), type.getTrainingTypeName()))
                .toList();
    }
}