package com.epam.training.service;

import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Training;
import com.epam.training.repository.TrainingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TrainingService {

    private final TrainingRepository trainingRepository;

    public TrainingService(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    public Training create(Training training) {
        return trainingRepository.save(training);
    }

    public void delete(Long id) {
        if (!trainingRepository.existsById(id)) throw new UserNotFoundException(id);
        trainingRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Training getTrainingById(Long id) {
        return trainingRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Training> findAll() {
        return trainingRepository.findAll();
    }
}