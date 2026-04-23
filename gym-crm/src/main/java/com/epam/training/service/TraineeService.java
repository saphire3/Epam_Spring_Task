package com.epam.training.service;

import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainee;
import com.epam.training.repository.TraineeRepository;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TraineeService {

    private final TraineeRepository traineeRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    public TraineeService(TraineeRepository traineeRepository,
                          UsernameGenerator usernameGenerator,
                          PasswordGenerator passwordGenerator) {
        this.traineeRepository = traineeRepository;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
    }

    public Trainee create(Trainee trainee) {
        validate(trainee);
        trainee.setUsername(usernameGenerator.generate(trainee.getFirstName(), trainee.getLastName()));
        trainee.setPassword(passwordGenerator.generate());
        return traineeRepository.save(trainee);
    }

    public Trainee update(Long id, Trainee updated) {
        Trainee existing = traineeRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        validate(updated);
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setAddress(updated.getAddress());
        existing.setDateOfBirth(updated.getDateOfBirth());
        return traineeRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public Trainee getTraineeById(Long id) {
        return traineeRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public void delete(Long id) {
        if (!traineeRepository.existsById(id)) throw new UserNotFoundException(id);
        traineeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Trainee> findAll() {
        return traineeRepository.findAll();
    }

    private void validate(Trainee trainee) {
        if (StringUtils.isBlank(trainee.getFirstName()))
            throw new IllegalArgumentException("First name cannot be null or blank");
        if (StringUtils.isBlank(trainee.getLastName()))
            throw new IllegalArgumentException("Last name cannot be null or blank");
    }
}