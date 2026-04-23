package com.epam.training.model;

import jakarta.persistence.*;

@Entity
@Table(name = "trainers")
public class Trainer extends User {

    private String specialization;

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
}