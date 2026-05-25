Feature: Workload Integration - JMS Message Publishing
  As the gym CRM system
  I want to publish workload updates via JMS when trainings are created or deleted
  So that the trainer workload service can process trainer workload asynchronously

  Scenario: Creating a training publishes an ADD workload JMS message
    Given a trainer with firstName "Integration" and lastName "TrainerA" is registered
    When I create a training for the registered trainer with duration 90 on "2024-06-15"
    Then a workload JMS message should be published
    And the JMS message action type should be "ADD"
    And the JMS message trainer username should match the registered trainer
    And the JMS message duration should be 90

  Scenario: Deleting a training publishes a DELETE workload JMS message
    Given a trainer with firstName "Integration" and lastName "TrainerB" is registered
    And a training for the registered trainer with duration 60 on "2024-07-20" has been persisted
    When I delete the persisted training
    Then a workload JMS message should be published
    And the JMS message action type should be "DELETE"
    And the JMS message trainer username should match the registered trainer

  Scenario: Creating a training without a trainer does not publish a JMS message
    When I create a training with no trainer with duration 45 on "2024-08-10"
    Then no workload JMS message should be published