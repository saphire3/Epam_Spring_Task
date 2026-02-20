package com.epam.training.dao;

import com.epam.training.model.Trainee;
import com.epam.training.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.stream.Collectors;

@Repository
public class TraineeDao {

    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public void save(Long id, Trainee trainee) {
        storage.getNamespace("trainees").put(id, trainee);
    }

    public Trainee findById(Long id) {
        return (Trainee) storage.getNamespace("trainees").get(id);
    }

    public void delete(Long id) {
        storage.getNamespace("trainees").remove(id);
    }

    public Collection<Trainee> findAll() {
        return storage.getNamespace("trainees")
                .values()
                .stream()
                .map(t -> (Trainee) t)
                .collect(Collectors.toList());
    }
}
