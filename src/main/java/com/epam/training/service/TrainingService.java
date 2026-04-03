package com.epam.training.service;

import com.epam.training.exception.TrainingTypeNotFoundException;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.model.TrainingType;
import com.epam.training.repository.TraineeRepository;
import com.epam.training.repository.TrainerRepository;
import com.epam.training.repository.TrainingRepository;
import com.epam.training.repository.TrainingTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingService(TrainingRepository trainingRepository,
                           TraineeRepository traineeRepository,
                           TrainerRepository trainerRepository,
                           TrainingTypeRepository trainingTypeRepository) {
        this.trainingRepository = trainingRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingTypeRepository = trainingTypeRepository;
    }

    public Training create(String traineeUsername, String trainerUsername,
                           String trainingTypeName, String trainingName,
                           LocalDate trainingDate, int duration) {
        Trainee trainee = traineeRepository.findByUsername(traineeUsername)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found: " + traineeUsername));
        Trainer trainer = trainerRepository.findByUsername(trainerUsername)
                .orElseThrow(() -> new UserNotFoundException("Trainer not found: " + trainerUsername));
        TrainingType trainingType = trainingTypeRepository.findByTrainingTypeName(trainingTypeName)
                .orElseThrow(() -> new TrainingTypeNotFoundException(trainingTypeName));

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setTrainingName(trainingName);
        training.setTrainingDate(trainingDate);
        training.setDuration(duration);

        Training saved = trainingRepository.save(training);
        log.info("Created training '{}' for trainee '{}' with trainer '{}'",
                trainingName, traineeUsername, trainerUsername);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainings(String traineeUsername,
                                               LocalDate fromDate, LocalDate toDate,
                                               String trainerName, String trainingType) {
        return trainingRepository.findTraineeTrainings(
                traineeUsername, fromDate, toDate, trainerName, trainingType);
    }

    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainings(String trainerUsername,
                                               LocalDate fromDate, LocalDate toDate,
                                               String traineeName) {
        return trainingRepository.findTrainerTrainings(
                trainerUsername, fromDate, toDate, traineeName);
    }
}
