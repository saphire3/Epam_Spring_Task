package com.epam.training.dao;

import com.epam.training.model.Trainee;
import com.epam.training.storage.InMemoryStorage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TraineeDao {

    private final InMemoryStorage storage;

    public TraineeDao(InMemoryStorage storage) {
        this.storage = storage;
    }

    public void save(Long id, Trainee trainee) {
        storage.getNamespace("trainees").put(id, trainee);
    }

    public Trainee getById(Long id) {
        return (Trainee) storage.getNamespace("trainees").get(id);
    }

    public void delete(Long id) {
        storage.getNamespace("trainees").remove(id);
    }

    public List<Trainee> findAll() {
        return storage.getNamespace("trainees")
                .values()
                .stream()
                .map(t -> (Trainee) t)
                .toList();
    }
}