package com.epam.training.controller;

import com.epam.training.model.Trainee;
import com.epam.training.service.TraineeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainees")
public class TraineeController {

    private final TraineeService traineeService;

    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @GetMapping
    public List<Trainee> findAll() {
        return traineeService.findAll();
    }

    @GetMapping("/{id}")
    public Trainee getById(@PathVariable Long id) {
        return traineeService.getTraineeById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Trainee create(@RequestBody Trainee trainee) {
        return traineeService.create(trainee);
    }

    @PutMapping("/{id}")
    public Trainee update(@PathVariable Long id, @RequestBody Trainee trainee) {
        return traineeService.update(id, trainee);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        traineeService.delete(id);
    }
}