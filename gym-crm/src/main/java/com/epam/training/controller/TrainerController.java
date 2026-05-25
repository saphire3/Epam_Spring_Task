package com.epam.training.controller;

import com.epam.training.model.Trainer;
import com.epam.training.service.TrainerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @GetMapping
    public List<Trainer> findAll() {
        return trainerService.findAll();
    }

    @GetMapping("/{id}")//
    public Trainer getById(@PathVariable Long id) {
        return trainerService.getTrainerById(id);
    }

    @PostMapping//
    @ResponseStatus(HttpStatus.CREATED)
    public Trainer create(@RequestBody Trainer trainer) {
        return trainerService.create(trainer);
    }

    @PutMapping("/{id}")
    public Trainer update(@PathVariable Long id, @RequestBody Trainer trainer) {
        return trainerService.update(id, trainer);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        trainerService.delete(id);
    }
}