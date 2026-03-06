package com.epam.training;

import com.epam.training.config.AppConfig;
import com.epam.training.dto.TraineeTrainingFilter;
import com.epam.training.dto.TrainerTrainingFilter;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.model.User;
import com.epam.training.service.TraineeService;
import com.epam.training.service.TrainerService;
import com.epam.training.service.TrainingService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.List;

public class Application {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {

            TraineeService traineeService = context.getBean(TraineeService.class);
            TrainerService trainerService = context.getBean(TrainerService.class);
            TrainingService trainingService = context.getBean(TrainingService.class);

            System.out.println("=================================");
            System.out.println("      GYM CRM SYSTEM STARTED     ");
            System.out.println("=================================");

            // Create trainee
            User traineeUser = new User();
            traineeUser.setFirstName("John");
            traineeUser.setLastName("Smith");

            Trainee trainee = new Trainee();
            trainee.setUser(traineeUser);
            trainee.setAddress("Yerevan");
            trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));

            trainee = traineeService.create(trainee);

            System.out.println("\n--- Created Trainee ---");
            System.out.println("Username: " + trainee.getUser().getUsername());
            System.out.println("Password: " + trainee.getUser().getPassword());

            // Create trainer
            User trainerUser = new User();
            trainerUser.setFirstName("Anna");
            trainerUser.setLastName("Brown");

            Trainer trainer = new Trainer();
            trainer.setUser(trainerUser);

            trainer = trainerService.create(trainer, "FITNESS");

            System.out.println("\n--- Created Trainer ---");
            System.out.println("Username: " + trainer.getUser().getUsername());
            System.out.println("Password: " + trainer.getUser().getPassword());

            // Get profiles by username + password
            Trainee loadedTrainee = traineeService.findByUsername(
                    trainee.getUser().getUsername(),
                    trainee.getUser().getPassword()
            );

            Trainer loadedTrainer = trainerService.findByUsername(
                    trainer.getUser().getUsername(),
                    trainer.getUser().getPassword()
            );

            System.out.println("\n--- Loaded Profiles ---");
            System.out.println("Trainee: " + loadedTrainee.getUser().getFirstName() + " " + loadedTrainee.getUser().getLastName());
            System.out.println("Trainer: " + loadedTrainer.getUser().getFirstName() + " " + loadedTrainer.getUser().getLastName());

            // Update trainee
            Trainee traineeUpdate = new Trainee();
            User updatedTraineeUser = new User();
            updatedTraineeUser.setFirstName("John");
            updatedTraineeUser.setLastName("Johnson");
            traineeUpdate.setUser(updatedTraineeUser);
            traineeUpdate.setAddress("Gyumri");
            traineeUpdate.setDateOfBirth(LocalDate.of(2000, 1, 1));

            trainee = traineeService.update(
                    trainee.getUser().getUsername(),
                    trainee.getUser().getPassword(),
                    traineeUpdate
            );

            System.out.println("\n--- Updated Trainee ---");
            System.out.println("New last name: " + trainee.getUser().getLastName());
            System.out.println("New address: " + trainee.getAddress());

            // Update trainer
            Trainer trainerUpdate = new Trainer();
            User updatedTrainerUser = new User();
            updatedTrainerUser.setFirstName("Anna");
            updatedTrainerUser.setLastName("Taylor");
            trainerUpdate.setUser(updatedTrainerUser);

            trainer = trainerService.update(
                    trainer.getUser().getUsername(),
                    trainer.getUser().getPassword(),
                    trainerUpdate,
                    "YOGA"
            );

            System.out.println("\n--- Updated Trainer ---");
            System.out.println("New last name: " + trainer.getUser().getLastName());
            System.out.println("New specialization: " + trainer.getSpecialization().getTrainingTypeName());

            // Change passwords
            String traineeOldPassword = trainee.getUser().getPassword();
            traineeService.changePassword(
                    trainee.getUser().getUsername(),
                    traineeOldPassword,
                    "NewPass123"
            );
            trainee.getUser().setPassword("NewPass123");

            String trainerOldPassword = trainer.getUser().getPassword();
            trainerService.changePassword(
                    trainer.getUser().getUsername(),
                    trainerOldPassword,
                    "TrainerPass123"
            );
            trainer.getUser().setPassword("TrainerPass123");

            System.out.println("\n--- Passwords Changed ---");
            System.out.println("Trainee new password: " + trainee.getUser().getPassword());
            System.out.println("Trainer new password: " + trainer.getUser().getPassword());

            // Deactivate / activate
            traineeService.deactivate(trainee.getUser().getUsername(), trainee.getUser().getPassword());
            System.out.println("\nTrainee deactivated");
            traineeService.activate(trainee.getUser().getUsername(), trainee.getUser().getPassword());
            System.out.println("Trainee activated");

            trainerService.deactivate(trainer.getUser().getUsername(), trainer.getUser().getPassword());
            System.out.println("Trainer deactivated");
            trainerService.activate(trainer.getUser().getUsername(), trainer.getUser().getPassword());
            System.out.println("Trainer activated");

            // Create second trainer for trainer-list demo
            User secondTrainerUser = new User();
            secondTrainerUser.setFirstName("Mike");
            secondTrainerUser.setLastName("Stone");

            Trainer secondTrainer = new Trainer();
            secondTrainer.setUser(secondTrainerUser);
            secondTrainer = trainerService.create(secondTrainer, "CARDIO");

            System.out.println("\n--- Created Second Trainer ---");
            System.out.println("Username: " + secondTrainer.getUser().getUsername());

            // Unassigned trainers before assignment
            List<Trainer> unassignedBefore = traineeService.getUnassignedTrainers(
                    trainee.getUser().getUsername(),
                    trainee.getUser().getPassword()
            );
            System.out.println("\nUnassigned trainers before assignment: " + unassignedBefore.size());

            // Update trainee trainer list
            traineeService.updateTrainerList(
                    trainee.getUser().getUsername(),
                    trainee.getUser().getPassword(),
                    List.of(
                            trainer.getUser().getUsername(),
                            secondTrainer.getUser().getUsername()
                    )
            );
            System.out.println("Updated trainee trainer list");

            // Create training
            Training training = new Training();
            training.setTrainingName("Morning Cardio");
            training.setTrainingDate(LocalDate.now());
            training.setDuration(60);

            trainingService.create(
                    trainee.getUser().getUsername(),
                    trainee.getUser().getPassword(),
                    trainer.getUser().getUsername(),
                    "CARDIO",
                    training
            );

            System.out.println("\n--- Training Created ---");
            System.out.println("Training: " + training.getTrainingName());

            // Query trainee trainings
            TraineeTrainingFilter traineeFilter = new TraineeTrainingFilter();
            traineeFilter.setTrainingType("CARDIO");

            List<Training> traineeTrainings = trainingService.getTraineeTrainings(
                    trainee.getUser().getUsername(),
                    trainee.getUser().getPassword(),
                    traineeFilter
            );

            System.out.println("\nTrainee trainings found: " + traineeTrainings.size());

            // Query trainer trainings
            TrainerTrainingFilter trainerFilter = new TrainerTrainingFilter();
            trainerFilter.setTraineeName("John");

            List<Training> trainerTrainings = trainingService.getTrainerTrainings(
                    trainer.getUser().getUsername(),
                    trainer.getUser().getPassword(),
                    trainerFilter
            );

            System.out.println("Trainer trainings found: " + trainerTrainings.size());

            // Delete trainee at the very end
            traineeService.deleteByUsername(
                    trainee.getUser().getUsername(),
                    trainee.getUser().getPassword()
            );
            System.out.println("\nTrainee deleted successfully");

            System.out.println("\n=================================");
            System.out.println("      GYM CRM SYSTEM FINISHED    ");
            System.out.println("=================================");
        }
    }
}