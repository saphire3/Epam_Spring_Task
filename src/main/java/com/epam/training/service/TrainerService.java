package com.epam.training.service;

import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainer;
import com.epam.training.repository.TrainerRepository;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    public TrainerService(TrainerRepository trainerRepository,
                          UsernameGenerator usernameGenerator,
                          PasswordGenerator passwordGenerator) {
        this.trainerRepository = trainerRepository;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
    }

    public Trainer create(Trainer trainer) {
        validate(trainer);
        trainer.setUsername(usernameGenerator.generate(trainer.getFirstName(), trainer.getLastName()));
        trainer.setPassword(passwordGenerator.generate());
        return trainerRepository.save(trainer);
    }

    public Trainer update(Long id, Trainer updated) {
        Trainer existing = trainerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        validate(updated);
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setSpecialization(updated.getSpecialization());
        return trainerRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public Trainer getTrainerById(Long id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public void delete(Long id) {
        if (!trainerRepository.existsById(id)) throw new UserNotFoundException(id);
        trainerRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Trainer> findAll() {
        return trainerRepository.findAll();
    }

    private void validate(Trainer trainer) {
        if (StringUtils.isBlank(trainer.getFirstName()))
            throw new IllegalArgumentException("First name cannot be null or blank");
        if (StringUtils.isBlank(trainer.getLastName()))
            throw new IllegalArgumentException("Last name cannot be null or blank");
    }
}