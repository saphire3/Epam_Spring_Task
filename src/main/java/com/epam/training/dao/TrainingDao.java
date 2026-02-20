package com.epam.training.dao;

import com.epam.training.model.Training;
import com.epam.training.storage.InMemoryStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.stream.Collectors;

@Repository
public class TrainingDao {

    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public void save(Long id, Training training) {
        storage.getNamespace("trainings").put(id, training);
    }

    public Training findById(Long id) {
        return (Training) storage.getNamespace("trainings").get(id);
    }

    public Collection<Training> findAll() {
        return storage.getNamespace("trainings")
                .values()
                .stream()
                .map(t -> (Training) t)
                .collect(Collectors.toList());
    }
}
