package com.epam.training.storage;

import com.epam.training.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryStorage implements UserStorage {

    private Map<Long, Object> getUserNamespace() {
        return storage.computeIfAbsent("users", k -> new HashMap<>());
    }

    @Override
    public java.util.Optional<com.epam.training.model.User> findById(Long id) {
        return java.util.Optional.ofNullable(
                (com.epam.training.model.User) getUserNamespace().get(id)
        );
    }

    @Override
    public java.util.List<com.epam.training.model.User> findAll() {
        return getUserNamespace()
                .values()
                .stream()
                .map(obj -> (com.epam.training.model.User) obj)
                .toList();
    }

    @Override
    public void save(com.epam.training.model.User user) {
        getUserNamespace().put(user.getId(), user);
    }

    @Override
    public void delete(Long id) {
        getUserNamespace().remove(id);
    }

    private static final String FILE_PATH = "storage.json";

    private final Map<String, Map<Long, Object>> storage = new HashMap<>();
    //private final ObjectMapper objectMapper = new ObjectMapper();

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();


    public InMemoryStorage() {
        storage.put("trainees", new HashMap<>());
        storage.put("trainers", new HashMap<>());
        storage.put("trainings", new HashMap<>());
        storage.put("users", new HashMap<>());
    }

    public Map<Long, Object> getNamespace(String key) {
        return storage.get(key);
    }

    @PostConstruct
    public void loadFromFile() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return;

            StorageData data = objectMapper.readValue(file, StorageData.class);

            storage.get("trainees").putAll(data.getTrainees());
            storage.get("trainers").putAll(data.getTrainers());
            storage.get("trainings").putAll(data.getTrainings());

        } catch (IOException e) {
            System.out.println("Failed to load storage file");
        }
    }

    @PreDestroy
    public void saveToFile() {
        try {
            StorageData data = new StorageData();
            data.setTrainees(cast(storage.get("trainees")));
            data.setTrainers(cast(storage.get("trainers")));
            data.setTrainings(cast(storage.get("trainings")));

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(FILE_PATH), data);

        } catch (IOException e) {
            System.out.println("Failed to save storage file");
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Map<Long, T> cast(Map<Long, Object> map) {
        Map<Long, T> result = new HashMap<>();
        map.forEach((k, v) -> result.put(k, (T) v));
        return result;
    }
}