package com.epam.training.controller;

import com.epam.training.model.Training;
import com.epam.training.service.TrainingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainings")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @GetMapping
    public List<Training> findAll() {
        return trainingService.findAll();
    }

    @GetMapping("/{id}")
    public Training getById(@PathVariable Long id) {
        return trainingService.getTrainingById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Training create(@RequestBody Training training) {
        return trainingService.create(training);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        trainingService.delete(id);
    }
}