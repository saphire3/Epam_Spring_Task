package com.epam.workload.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "trainer_summaries")
@CompoundIndex(name = "name_idx", def = "{'firstName': 1, 'lastName': 1}")
public class TrainerSummary {

    @Id
    private String id;

    @Indexed(unique = true)
    @NotBlank
    private String username;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private boolean active;

    private List<YearSummary> years = new ArrayList<>();

    public TrainerSummary() {}

    public TrainerSummary(String username, String firstName, String lastName, boolean active) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public List<YearSummary> getYears() { return years; }
    public void setYears(List<YearSummary> years) { this.years = years; }
}