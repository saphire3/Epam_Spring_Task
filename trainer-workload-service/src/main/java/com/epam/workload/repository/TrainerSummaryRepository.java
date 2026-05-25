package com.epam.workload.repository;

import com.epam.workload.model.TrainerSummary;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TrainerSummaryRepository extends MongoRepository<TrainerSummary, String> {

    Optional<TrainerSummary> findByUsername(String username);
}