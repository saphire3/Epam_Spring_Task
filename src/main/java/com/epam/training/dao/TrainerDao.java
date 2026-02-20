package com.epam.training.dao;

import com.epam.training.model.Trainer;
import com.epam.training.storage.InMemoryStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.stream.Collectors;

@Repository
public class TrainerDao {

    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public void save(Long id, Trainer trainer) {
        storage.getNamespace("trainers").put(id, trainer);
    }

    public Trainer findById(Long id) {
        return (Trainer) storage.getNamespace("trainers").get(id);
    }

    public Collection<Trainer> findAll() {
        return storage.getNamespace("trainers")
                .values()
                .stream()
                .map(t -> (Trainer) t)
                .collect(Collectors.toList());
    }
}
