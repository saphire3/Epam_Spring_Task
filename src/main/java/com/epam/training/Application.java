package com.epam.training;

import com.epam.training.config.AppConfig;
import com.epam.training.facade.GymFacade;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;

import com.epam.training.service.TraineeService;
import com.epam.training.service.TrainerService;
import com.epam.training.service.TrainingService;
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

        System.out.println("Created Trainee: " + trainee.getFirstName() + " " + trainee.getLastName());
        System.out.println("ID: " + trainee.getId());
        System.out.println("Username: " + trainee.getUsername());
        System.out.println("Password: " + trainee.getPassword());

        System.out.println("\n--- Creating Trainer ---");
        Trainer trainer = new Trainer();
        trainer.setFirstName("Anna");
        trainer.setLastName("Brown");
        trainer.setSpecialization("Fitness");

        trainer = facade.trainer().create(trainer);

        System.out.println("Created Trainer: " + trainer.getFirstName() + " " + trainer.getLastName());
        System.out.println("ID: " + trainer.getId());
        System.out.println("Username: " + trainer.getUsername());
        System.out.println("Password: " + trainer.getPassword());
        System.out.println("Specialization: " + trainer.getSpecialization());

        System.out.println("\n--- Demonstrating Trainer Username Uniqueness ---");
        Trainer duplicateTrainer = new Trainer();
        duplicateTrainer.setFirstName("Anna");
        duplicateTrainer.setLastName("Brown");
        duplicateTrainer.setSpecialization("Fitness");

        duplicateTrainer = facade.trainer().create(duplicateTrainer);

        System.out.println("Second Anna Brown username: " + duplicateTrainer.getUsername());


        System.out.println("\n--- Creating Training ---");
        Training training = new Training();
        training.setTraineeId(trainee.getId());
        training.setTrainerId(trainer.getId());
        training.setTrainingName("Cardio");
        training.setTrainingDate(LocalDate.now());
        training.setDuration(60);

        training = facade.training().create(training);

        System.out.println("Created Training: " + training.getTrainingName()
                + " with trainee ID " + training.getTraineeId()
                + " with trainer ID " + training.getTrainerId());
        System.out.println("Training ID: " + training.getId());
        System.out.println("Name: " + training.getTrainingName());


        System.out.println("\n--- Demonstrating Training ID Uniqueness ---");
        Training duplicateTraining = new Training();
        duplicateTraining.setTraineeId(trainee.getId());
        duplicateTraining.setTrainerId(trainer.getId());
        duplicateTraining.setTrainingName("Cardio");
        duplicateTraining.setTrainingDate(LocalDate.now());
        duplicateTraining.setDuration(60);

        duplicateTraining = facade.training().create(duplicateTraining);

        System.out.println("Second Training ID: " + duplicateTraining.getId());


        System.out.println("\n--- Updating Trainee ---");
        System.out.println("Trainee address before: " + trainee.getAddress());
        trainee.setAddress("Gyumri");
        facade.trainee().update(trainee.getId(), trainee);
        System.out.println("Trainee updated successfully. The new address is: " + trainee.getAddress());


        System.out.println("\n--- Demonstrating Trainee Username Uniqueness ---");
        Trainee duplicate = new Trainee();
        duplicate.setFirstName("John");
        duplicate.setLastName("Smith");

        duplicate = facade.trainee().create(duplicate);

        System.out.println("Second John Smith username: " + duplicate.getUsername());


        TraineeService traineeService = context.getBean(TraineeService.class);

        System.out.println("\n--- Deleting Duplicate Trainee ---");
        System.out.println("Deleting Duplicate John Smith " + duplicate.getUsername());
        traineeService.delete(duplicate.getId());
        System.out.println("Duplicate Trainee deleted successfully.");

        Trainee duplicate2 = new Trainee();
        duplicate2.setFirstName("John");
        duplicate2.setLastName("Smith");

        duplicate2 = facade.trainee().create(duplicate2);
        System.out.println(" Duplicate John Smith again " + duplicate2.getUsername());


        Trainee duplicate3 = new Trainee();
        duplicate3.setFirstName("John");
        duplicate3.setLastName("Smith");

        duplicate3 = facade.trainee().create(duplicate3);
        System.out.println(" Duplicate John Smith again " + duplicate3.getUsername());


        Trainee duplicate4 = new Trainee();
        duplicate4.setFirstName("John");
        duplicate4.setLastName("Smith");

        duplicate4 = facade.trainee().create(duplicate4);
        System.out.println(" Duplicate John Smith again " + duplicate4.getUsername());

        traineeService.delete(duplicate3.getId());

        Trainee duplicate5 = new Trainee();
        duplicate5.setFirstName("John");
        duplicate5.setLastName("Smith");

        duplicate5 = facade.trainee().create(duplicate4);
        System.out.println(" Duplicate John Smith again " + duplicate5.getUsername());


        System.out.println("\n=================================");
        System.out.println("      GYM CRM SYSTEM FINISHED    ");
        System.out.println("=================================");

        context.close();
    }
}