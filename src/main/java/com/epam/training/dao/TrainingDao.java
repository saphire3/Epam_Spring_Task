package com.epam.training.dao;

import com.epam.training.model.Training;
import com.epam.training.storage.InMemoryStorage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TrainingDao {

    private final InMemoryStorage storage;

    public TrainingDao(InMemoryStorage storage) {
        this.storage = storage;
    }

    public void delete(Long id) {
        storage.getNamespace("trainings").remove(id);
    }

    public void save(Long id, Training training) {
        storage.getNamespace("trainings").put(id, training);
    }

    public Training getById(Long id) {
        return (Training) storage.getNamespace("trainings").get(id);
    }

    public List<Training> findAll() {
        return storage.getNamespace("trainings")
                .values()
                .stream()
                .map(t -> (Training) t)
                .toList();
    }
}