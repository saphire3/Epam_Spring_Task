package com.epam.training.dao;

import com.epam.training.model.Trainer;
import com.epam.training.storage.InMemoryStorage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TrainerDao {

    private final InMemoryStorage storage;

    public void delete(Long id) {
        storage.getNamespace("trainers").remove(id);
    }
    public TrainerDao(InMemoryStorage storage) {
        this.storage = storage;
    }

    public void save(Long id, Trainer trainer) {
        storage.getNamespace("trainers").put(id, trainer);
    }

    public Trainer getById(Long id) {
        return (Trainer) storage.getNamespace("trainers").get(id);
    }

    public List<Trainer> findAll() {
        return storage.getNamespace("trainers")
                .values()
                .stream()
                .map(t -> (Trainer) t)
                .toList();
    }
}