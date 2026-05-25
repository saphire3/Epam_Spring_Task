Feature: Training Management Component Tests
  As a gym administrator
  I want to manage training sessions via the REST API
  So that scheduled trainings can be tracked

  Scenario: Create a training successfully
    Given a trainer with firstName "Train" and lastName "Er" has been created
    When I create a training named "Yoga" for the created trainer on "2024-03-15" with duration 60
    Then the response status should be 201
    And the training name should be "Yoga"

  Scenario: Get all trainings returns OK
    When I get all trainings
    Then the response status should be 200

  Scenario: Get a training by ID returns the correct training
    Given a trainer with firstName "Fit" and lastName "Ness" has been created
    And a training named "Boxing" for the created trainer on "2024-04-20" with duration 45 has been created
    When I get the training by their stored ID
    Then the response status should be 200
    And the training name should be "Boxing"

  Scenario: Get a non-existent training returns 404
    When I get the training with ID 999997
    Then the response status should be 404

  Scenario: Delete a training successfully
    Given a trainer with firstName "Coach" and lastName "Mike" has been created
    And a training named "Cardio" for the created trainer on "2024-05-10" with duration 30 has been created
    When I delete the training by their stored ID
    Then the response status should be 204

  Scenario: Delete a non-existent training returns 404
    When I delete the training with ID 999997
    Then the response status should be 404