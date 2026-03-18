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
import com.epam.training.service.TrainingTypeService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.List;

public class Application {

    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("      GYM CRM SYSTEM STARTED     ");
        System.out.println("=================================");

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        TraineeService traineeService = context.getBean(TraineeService.class);
        TrainerService trainerService = context.getBean(TrainerService.class);
        TrainingService trainingService = context.getBean(TrainingService.class);
        TrainingTypeService trainingTypeService = context.getBean(TrainingTypeService.class);

        trainingTypeService.initializeDefaultTrainingTypes();

        // ---------------------------------------------------------
        // CREATE 4 TRAINEES WITH THE SAME NAME
        // ---------------------------------------------------------
        System.out.println("\n--- Creating 4 Trainees With The Same Name ---");

        Trainee trainee1 = traineeService.create(
                buildTrainee("John", "Smith", LocalDate.of(2000, 1, 10), "Yerevan")
        );
        Trainee trainee2 = traineeService.create(
                buildTrainee("John", "Smith", LocalDate.of(2001, 2, 11), "Gyumri")
        );
        Trainee trainee3 = traineeService.create(
                buildTrainee("John", "Smith", LocalDate.of(2002, 3, 12), "Vanadzor")
        );
        Trainee trainee4 = traineeService.create(
                buildTrainee("John", "Smith", LocalDate.of(2003, 4, 13), "Dilijan")
        );

        printTraineeCard(trainee1);
        printTraineeCard(trainee2);
        printTraineeCard(trainee3);
        printTraineeCard(trainee4);

        String mainTraineeUsername = trainee1.getUser().getUsername();
        String mainTraineePassword = trainee1.getUser().getPassword();

        // ---------------------------------------------------------
        // DELETE ONE DUPLICATE IN THE MIDDLE
        // ---------------------------------------------------------
        System.out.println("\n--- Deleting One Duplicate Trainee In The Middle ---");

        traineeService.deleteByUsername(
                trainee3.getUser().getUsername(),
                trainee3.getUser().getPassword()
        );

        System.out.println("Deleted trainee successfully:");
        System.out.println("Name: " + fullName(trainee3));
        System.out.println("Username: " + trainee3.getUser().getUsername());

        // ---------------------------------------------------------
        // ADD ANOTHER DUPLICATE AFTER DELETION
        // ---------------------------------------------------------
        System.out.println("\n--- Adding Another Duplicate After Deletion ---");

        Trainee trainee5 = traineeService.create(
                buildTrainee("John", "Smith", LocalDate.of(2004, 5, 14), "Abovyan")
        );

        printTraineeCard(trainee5);

        // ---------------------------------------------------------
        // CREATE TRAINERS WITH DUPLICATE NAMES
        // ---------------------------------------------------------
        System.out.println("\n--- Creating Trainers With Duplicate Names ---");

        Trainer trainer1 = trainerService.create(
                buildTrainer("Anna", "Brown"),
                "Fitness"
        );

        Trainer trainer2 = trainerService.create(
                buildTrainer("Anna", "Brown"),
                "Yoga"
        );

        printTrainerCard(trainer1);
        printTrainerCard(trainer2);

        String trainer1Username = trainer1.getUser().getUsername();
        String trainer1Password = trainer1.getUser().getPassword();

        String trainer2Username = trainer2.getUser().getUsername();
        String trainer2Password = trainer2.getUser().getPassword();

        // ---------------------------------------------------------
        // LOAD PROFILES BY USERNAME + PASSWORD
        // ---------------------------------------------------------
        System.out.println("\n--- Loading Profiles By Username And Password ---");

        Trainee loadedTrainee = traineeService.findByUsername(mainTraineeUsername, mainTraineePassword);
        Trainer loadedTrainer = trainerService.findByUsername(trainer1Username, trainer1Password);

        System.out.println("Loaded trainee successfully:");
        System.out.println("Name: " + fullName(loadedTrainee));
        System.out.println("Username: " + loadedTrainee.getUser().getUsername());

        System.out.println();
        System.out.println("Loaded trainer successfully:");
        System.out.println("Name: " + fullName(loadedTrainer));
        System.out.println("Username: " + loadedTrainer.getUser().getUsername());
        System.out.println("Specialization: " + loadedTrainer.getSpecialization().getTrainingTypeName());

        // ---------------------------------------------------------
        // UPDATE TRAINEE
        // ---------------------------------------------------------
        System.out.println("\n--- Updating Trainee ---");

        Trainee traineeUpdate = buildTrainee(
                "John",
                "Johnson",
                LocalDate.of(2000, 1, 10),
                "Updated Address"
        );

        Trainee updatedTrainee = traineeService.update(
                mainTraineeUsername,
                mainTraineePassword,
                traineeUpdate
        );

        System.out.println("Trainee updated successfully:");
        System.out.println("Username: " + updatedTrainee.getUser().getUsername());
        System.out.println("New full name: " + fullName(updatedTrainee));
        System.out.println("New address: " + updatedTrainee.getAddress());

        // ---------------------------------------------------------
        // UPDATE TRAINER
        // ---------------------------------------------------------
        System.out.println("\n--- Updating Trainer ---");

        Trainer trainerUpdate = buildTrainer("Anna", "Taylor");

        Trainer updatedTrainer = trainerService.update(
                trainer1Username,
                trainer1Password,
                trainerUpdate,
                "Cardio"
        );

        System.out.println("Trainer updated successfully:");
        System.out.println("Username: " + updatedTrainer.getUser().getUsername());
        System.out.println("New full name: " + fullName(updatedTrainer));
        System.out.println("New specialization: " + updatedTrainer.getSpecialization().getTrainingTypeName());

        // ---------------------------------------------------------
        // CHANGE PASSWORDS
        // ---------------------------------------------------------
        System.out.println("\n--- Changing Passwords ---");

        String newTraineePassword = "newTraineePassword123";
        String newTrainerPassword = "newTrainerPassword123";

        traineeService.changePassword(
                mainTraineeUsername,
                mainTraineePassword,
                newTraineePassword
        );
        mainTraineePassword = newTraineePassword;

        trainerService.changePassword(
                trainer1Username,
                trainer1Password,
                newTrainerPassword
        );
        trainer1Password = newTrainerPassword;

        System.out.println("Trainee password changed for username: " + mainTraineeUsername);
        System.out.println("Trainer password changed for username: " + trainer1Username);

        // ---------------------------------------------------------
        // DEACTIVATE / ACTIVATE
        // ---------------------------------------------------------
        System.out.println("\n--- Deactivating And Activating Trainee And Trainer ---");

        traineeService.deactivate(mainTraineeUsername, mainTraineePassword);
        System.out.println("Trainee deactivated: " + mainTraineeUsername);

        traineeService.activate(mainTraineeUsername, mainTraineePassword);
        System.out.println("Trainee activated: " + mainTraineeUsername);

        trainerService.deactivate(trainer1Username, trainer1Password);
        System.out.println("Trainer deactivated: " + trainer1Username);

        trainerService.activate(trainer1Username, trainer1Password);
        System.out.println("Trainer activated: " + trainer1Username);

        // ---------------------------------------------------------
        // GET UNASSIGNED TRAINERS BEFORE ASSIGNMENT
        // ---------------------------------------------------------
        System.out.println("\n--- Getting Unassigned Trainers Before Assignment ---");

        List<Trainer> unassignedBefore = traineeService.getUnassignedTrainers(
                mainTraineeUsername,
                mainTraineePassword
        );

        System.out.println("Unassigned trainers count before assignment: " + unassignedBefore.size());
        for (Trainer trainer : unassignedBefore) {
            System.out.println("- " + fullName(trainer)
                    + " | username=" + trainer.getUser().getUsername()
                    + " | specialization=" + trainer.getSpecialization().getTrainingTypeName());
        }

        // ---------------------------------------------------------
        // ASSIGN TRAINERS
        // ---------------------------------------------------------
        System.out.println("\n--- Assigning Trainers To Trainee ---");

        List<String> assignedTrainerUsernames = List.of(trainer1Username, trainer2Username);

        traineeService.updateTrainerList(
                mainTraineeUsername,
                mainTraineePassword,
                assignedTrainerUsernames
        );

        System.out.println("Assigned trainers successfully to: " + mainTraineeUsername);
        for (String username : assignedTrainerUsernames) {
            System.out.println("- " + username);
        }

        // ---------------------------------------------------------
        // GET UNASSIGNED TRAINERS AFTER ASSIGNMENT
        // ---------------------------------------------------------
        System.out.println("\n--- Getting Unassigned Trainers After Assignment ---");

        List<Trainer> unassignedAfter = traineeService.getUnassignedTrainers(
                mainTraineeUsername,
                mainTraineePassword
        );

        System.out.println("Unassigned trainers count after assignment: " + unassignedAfter.size());
        if (unassignedAfter.isEmpty()) {
            System.out.println("No unassigned trainers left for this trainee.");
        } else {
            for (Trainer trainer : unassignedAfter) {
                System.out.println("- " + fullName(trainer)
                        + " | username=" + trainer.getUser().getUsername()
                        + " | specialization=" + trainer.getSpecialization().getTrainingTypeName());
            }
        }

        boolean trainer1StillUnassigned = containsTrainer(unassignedAfter, trainer1Username);
        boolean trainer2StillUnassigned = containsTrainer(unassignedAfter, trainer2Username);

        System.out.println("Assignment check:");
        System.out.println("- " + trainer1Username + " removed from unassigned list: " + !trainer1StillUnassigned);
        System.out.println("- " + trainer2Username + " removed from unassigned list: " + !trainer2StillUnassigned);
        System.out.println("Assigned trainer update works: " + (!trainer1StillUnassigned && !trainer2StillUnassigned));

        // ---------------------------------------------------------
        // ADD DUPLICATE TRAININGS
        // ---------------------------------------------------------
        System.out.println("\n--- Creating Duplicate Trainings ---");

        Training training1 = trainingService.create(
                mainTraineeUsername,
                mainTraineePassword,
                trainer1Username,
                "Cardio",
                buildTraining("Morning Cardio", LocalDate.now(), 60)
        );

        Training training2 = trainingService.create(
                mainTraineeUsername,
                mainTraineePassword,
                trainer1Username,
                "Cardio",
                buildTraining("Morning Cardio", LocalDate.now(), 60)
        );

        Training training3 = trainingService.create(
                mainTraineeUsername,
                mainTraineePassword,
                trainer2Username,
                "Yoga",
                buildTraining("Morning Cardio", LocalDate.now().plusDays(1), 45)
        );

        System.out.println("Created trainings successfully:");
        printTrainingCard(training1);
        printTrainingCard(training2);
        printTrainingCard(training3);

        // ---------------------------------------------------------
        // SHOW ALL TRAININGS BEFORE CASCADE DELETE
        // ---------------------------------------------------------
        System.out.println("\n--- Showing All Trainings Before Cascade Delete ---");

        List<Training> allBeforeDelete = trainingService.findAll();
        System.out.println("Total trainings before delete: " + allBeforeDelete.size());
        for (Training training : allBeforeDelete) {
            printTrainingLine(training);
        }

        // ---------------------------------------------------------
        // FILTER TRAINEE TRAININGS
        // ---------------------------------------------------------
        System.out.println("\n--- Filtering Trainee Trainings ---");

        TraineeTrainingFilter traineeFilter = new TraineeTrainingFilter();
        traineeFilter.setTrainingType("Cardio");

        List<Training> filteredTraineeTrainings = trainingService.getTraineeTrainings(
                mainTraineeUsername,
                mainTraineePassword,
                traineeFilter
        );

        System.out.println("Filtered trainee trainings count: " + filteredTraineeTrainings.size());
        for (Training training : filteredTraineeTrainings) {
            printTrainingLine(training);
        }

        // ---------------------------------------------------------
        // FILTER TRAINER TRAININGS
        // ---------------------------------------------------------
        System.out.println("\n--- Filtering Trainer Trainings ---");

        TrainerTrainingFilter trainerFilter = new TrainerTrainingFilter();
        trainerFilter.setTraineeName("John Johnson");

        List<Training> filteredTrainerTrainings = trainingService.getTrainerTrainings(
                trainer1Username,
                trainer1Password,
                trainerFilter
        );

        System.out.println("Filtered trainer trainings count: " + filteredTrainerTrainings.size());
        for (Training training : filteredTrainerTrainings) {
            printTrainingLine(training);
        }

        // ---------------------------------------------------------
        // DELETE TRAINEE WITH CASCADE DELETE
        // ---------------------------------------------------------
        System.out.println("\n--- Deleting Trainee With Cascade Delete ---");

        int beforeDeleteCount = trainingService.findAll().size();

        traineeService.deleteByUsername(mainTraineeUsername, mainTraineePassword);

        int afterDeleteCount = trainingService.findAll().size();

        System.out.println("Deleted trainee username: " + mainTraineeUsername);
        System.out.println("Trainings before deleting trainee: " + beforeDeleteCount);
        System.out.println("Trainings after deleting trainee: " + afterDeleteCount);
        System.out.println("Cascade delete successful: " + (afterDeleteCount == 0));

        // ---------------------------------------------------------
        // SHOW ALL TRAININGS AFTER CASCADE DELETE
        // ---------------------------------------------------------
        System.out.println("\n--- Showing All Trainings After Cascade Delete ---");

        List<Training> allAfterDelete = trainingService.findAll();
        if (allAfterDelete.isEmpty()) {
            System.out.println("No trainings found.");
        } else {
            for (Training training : allAfterDelete) {
                printTrainingLine(training);
            }
        }

        System.out.println("\n=================================");
        System.out.println("      GYM CRM SYSTEM FINISHED    ");
        System.out.println("=================================");

        context.close();
    }

    private static Trainee buildTrainee(String firstName, String lastName, LocalDate dob, String address) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(dob);
        trainee.setAddress(address);
        return trainee;
    }

    private static Trainer buildTrainer(String firstName, String lastName) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        return trainer;
    }

    private static Training buildTraining(String name, LocalDate date, int duration) {
        Training training = new Training();
        training.setTrainingName(name);
        training.setTrainingDate(date);
        training.setDuration(duration);
        return training;
    }

    private static void printTraineeCard(Trainee trainee) {
        System.out.println();
        System.out.println("Created Trainee: " + fullName(trainee));
        System.out.println("Username: " + trainee.getUser().getUsername());
        System.out.println("Password: " + trainee.getUser().getPassword());
        System.out.println("Address: " + trainee.getAddress());
        System.out.println("Date of birth: " + trainee.getDateOfBirth());
    }

    private static void printTrainerCard(Trainer trainer) {
        System.out.println();
        System.out.println("Created Trainer: " + fullName(trainer));
        System.out.println("Username: " + trainer.getUser().getUsername());
        System.out.println("Password: " + trainer.getUser().getPassword());
        System.out.println("Specialization: " + trainer.getSpecialization().getTrainingTypeName());
    }

    private static void printTrainingCard(Training training) {
        System.out.println();
        System.out.println("Training ID: " + training.getId());
        System.out.println("Name: " + training.getTrainingName());
        System.out.println("Date: " + training.getTrainingDate());
        System.out.println("Duration: " + training.getDuration() + " minutes");
        System.out.println("Trainee: " + fullName(training.getTrainee())
                + " (" + training.getTrainee().getUser().getUsername() + ")");
        System.out.println("Trainer: " + fullName(training.getTrainer())
                + " (" + training.getTrainer().getUser().getUsername() + ")");
        System.out.println("Type: " + training.getTrainingType().getTrainingTypeName());
    }

    private static void printTrainingLine(Training training) {
        System.out.println("- Training #" + training.getId()
                + " | " + training.getTrainingName()
                + " | " + training.getTrainingDate()
                + " | " + training.getDuration() + " min"
                + " | trainee=" + training.getTrainee().getUser().getUsername()
                + " | trainer=" + training.getTrainer().getUser().getUsername()
                + " | type=" + training.getTrainingType().getTrainingTypeName());
    }

    private static boolean containsTrainer(List<Trainer> trainers, String username) {
        for (Trainer trainer : trainers) {
            if (trainer.getUser() != null
                    && trainer.getUser().getUsername() != null
                    && trainer.getUser().getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    private static String fullName(Trainee trainee) {
        return trainee.getUser().getFirstName() + " " + trainee.getUser().getLastName();
    }

    private static String fullName(Trainer trainer) {
        return trainer.getUser().getFirstName() + " " + trainer.getUser().getLastName();
    }
}