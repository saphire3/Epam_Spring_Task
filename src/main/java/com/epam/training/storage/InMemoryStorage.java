package com.epam.training.storage;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryStorage {

    private final Map<String, Map<Long, Object>> storage = new ConcurrentHashMap<>();

    public InMemoryStorage() {
        storage.put("trainers", new ConcurrentHashMap<>());
        storage.put("trainees", new ConcurrentHashMap<>());
        storage.put("trainings", new ConcurrentHashMap<>());
    }

    public Map<Long, Object> getNamespace(String key) {
        return storage.get(key);
    }
}
