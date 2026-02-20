package com.epam.training.storage;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class StorageInitializer implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(StorageInitializer.class);

    @Value("${storage.init.file}")
    private String fileName;

    private final InMemoryStorage storage;

    public StorageInitializer(InMemoryStorage storage) {
        this.storage = storage;
    }

    @Override
    public void afterPropertiesSet() {

        try {
            InputStream is = getClass()
                    .getClassLoader()
                    .getResourceAsStream(fileName);

            if (is == null) {
                log.warn("Initialization file not found: {}", fileName);
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            InitData data = mapper.readValue(is, InitData.class);

            if (data.getTrainees() != null)
                storage.getNamespace("trainees").putAll(data.getTrainees());

            if (data.getTrainers() != null)
                storage.getNamespace("trainers").putAll(data.getTrainers());

            if (data.getTrainings() != null)
                storage.getNamespace("trainings").putAll(data.getTrainings());

            log.info("Storage initialized successfully from {}", fileName);

        } catch (Exception e) {
            log.error("Failed to initialize storage", e);
        }
    }
}
