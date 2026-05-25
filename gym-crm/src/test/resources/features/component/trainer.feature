Feature: Trainer Management Component Tests
  As a gym administrator
  I want to manage trainer profiles via the REST API
  So that gym trainers can be registered and updated

  Scenario: Create a new trainer successfully
    When I create a trainer with firstName "Anna" and lastName "Coach"
    Then the response status should be 201
    And the trainer username should be "anna.coach"

  Scenario: Fail to create trainer with blank first name
    When I create a trainer with firstName "" and lastName "Coach"
    Then the response status should be 400

  Scenario: Fail to create trainer with blank last name
    When I create a trainer with firstName "Anna" and lastName ""
    Then the response status should be 400

  Scenario: Get all trainers returns OK
    When I get all trainers
    Then the response status should be 200

  Scenario: Get trainer by ID returns the correct trainer
    Given a trainer with firstName "David" and lastName "Fit" has been created
    When I get the trainer by their stored ID
    Then the response status should be 200
    And the trainer firstName should be "David"

  Scenario: Get a non-existent trainer returns 404
    When I get the trainer with ID 999998
    Then the response status should be 404

  Scenario: Update a trainer successfully
    Given a trainer with firstName "Eve" and lastName "Run" has been created
    When I update the trainer with firstName "Eva" and lastName "Runner"
    Then the response status should be 200
    And the trainer firstName should be "Eva"

  Scenario: Update a non-existent trainer returns 404
    When I update trainer ID 999998 with firstName "A" and lastName "B"
    Then the response status should be 404

  Scenario: Delete a trainer successfully
    Given a trainer with firstName "Frank" and lastName "Lift" has been created
    When I delete the trainer by their stored ID
    Then the response status should be 204

  Scenario: Delete a non-existent trainer returns 404
    When I delete the trainer with ID 999998
    Then the response status should be 404