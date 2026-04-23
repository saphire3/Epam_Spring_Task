package com.epam.training.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "trainings")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trainee_id")
    private Long traineeId;

    @Column(name = "trainer_id")
    private Long trainerId;

    @Column(name = "training_name")
    private String trainingName;

    @Column(name = "training_type")
    private String trainingType;

    @Column(name = "training_date")
    private LocalDate trainingDate;

    private int duration;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTraineeId() { return traineeId; }
    public void setTraineeId(Long traineeId) { this.traineeId = traineeId; }

    public Long getTrainerId() { return trainerId; }
    public void setTrainerId(Long trainerId) { this.trainerId = trainerId; }

    public String getTrainingName() { return trainingName; }
    public void setTrainingName(String trainingName) { this.trainingName = trainingName; }

    public String getTrainingType() { return trainingType; }
    public void setTrainingType(String trainingType) { this.trainingType = trainingType; }

    public LocalDate getTrainingDate() { return trainingDate; }
    public void setTrainingDate(LocalDate trainingDate) { this.trainingDate = trainingDate; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
}