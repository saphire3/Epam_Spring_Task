package com.epam.training;

import com.epam.training.config.AppConfig;
import com.epam.training.facade.GymFacade;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

public class Application {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("      GYM CRM SYSTEM STARTED     ");
        System.out.println("=================================");

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        GymFacade facade = context.getBean(GymFacade.class);

        System.out.println("\n--- Creating Trainee ---");
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Smith");
        trainee.setAddress("Yerevan");
        trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));

        trainee = facade.trainee().create(trainee);

        System.out.println("Created Trainee:");
        System.out.println("ID: " + trainee.getId());
        System.out.println("Username: " + trainee.getUsername());
        System.out.println("Password: " + trainee.getPassword());

        System.out.println("\n--- Creating Trainer ---");
        Trainer trainer = new Trainer();
        trainer.setFirstName("Anna");
        trainer.setLastName("Brown");
        trainer.setSpecialization("Fitness");

        trainer = facade.trainer().create(trainer);

        System.out.println("Created Trainer:");
        System.out.println("ID: " + trainer.getId());
        System.out.println("Username: " + trainer.getUsername());

        System.out.println("\n--- Creating Training ---");
        Training training = new Training();
        training.setTraineeId(trainee.getId());
        training.setTrainerId(trainer.getId());
        training.setTrainingName("Cardio");
        training.setTrainingDate(LocalDate.now());
        training.setDuration(60);

        training = facade.training().create(training);

        System.out.println("Created Training:");
        System.out.println("Training ID: " + training.getId());
        System.out.println("Name: " + training.getTrainingName());

        System.out.println("\n--- Updating Trainee ---");
        trainee.setAddress("Updated Address");
        facade.trainee().update(trainee.getId(), trainee);
        System.out.println("Trainee updated successfully.");

        System.out.println("\n--- Demonstrating Username Uniqueness ---");
        Trainee duplicate = new Trainee();
        duplicate.setFirstName("John");
        duplicate.setLastName("Smith");

        duplicate = facade.trainee().create(duplicate);

        System.out.println("Second John Smith username: " + duplicate.getUsername());

        System.out.println("\n=================================");
        System.out.println("      GYM CRM SYSTEM FINISHED    ");
        System.out.println("=================================");

        context.close();
    }
}