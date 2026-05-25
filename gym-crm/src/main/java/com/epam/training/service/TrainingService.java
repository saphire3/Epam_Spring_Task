package com.epam.training.service;

import com.epam.training.dto.ActionType;
import com.epam.training.dto.TrainerWorkloadRequest;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.messaging.WorkloadMessageProducer;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.repository.TrainerRepository;
import com.epam.training.repository.TrainingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);

    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final WorkloadMessageProducer workloadMessageProducer;

    public TrainingService(TrainingRepository trainingRepository,
                           TrainerRepository trainerRepository,
                           WorkloadMessageProducer workloadMessageProducer) {
        this.trainingRepository = trainingRepository;
        this.trainerRepository = trainerRepository;
        this.workloadMessageProducer = workloadMessageProducer;
    }

    public Training create(Training training) {
        Training saved = trainingRepository.save(training);
        notifyWorkloadService(saved, ActionType.ADD);
        return saved;
    }

    public void delete(Long id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        trainingRepository.deleteById(id);
        notifyWorkloadService(training, ActionType.DELETE);
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

    private void notifyWorkloadService(Training training, ActionType actionType) {
        if (training.getTrainerId() == null) return;
        Trainer trainer = trainerRepository.findById(training.getTrainerId()).orElse(null);
        if (trainer == null) {
            log.warn("Trainer id={} not found, skipping workload notification", training.getTrainerId());
            return;
        }
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.isActive(),
                training.getTrainingDate(),
                training.getDuration(),
                actionType
        );
        workloadMessageProducer.sendWorkloadUpdate(request);
    }
}