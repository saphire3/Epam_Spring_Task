Feature: Trainee Management Component Tests
  As a gym administrator
  I want to manage trainee profiles via the REST API
  So that gym members can be registered and maintained

  Scenario: Create a new trainee successfully
    When I create a trainee with firstName "John" and lastName "Doe"
    Then the response status should be 201
    And the trainee username should be "john.doe"

  Scenario: Fail to create trainee with blank first name
    When I create a trainee with firstName "" and lastName "Doe"
    Then the response status should be 400

  Scenario: Fail to create trainee with blank last name
    When I create a trainee with firstName "John" and lastName ""
    Then the response status should be 400

  Scenario: Get all trainees returns OK
    When I get all trainees
    Then the response status should be 200

  Scenario: Get trainee by ID returns the correct trainee
    Given a trainee with firstName "Alice" and lastName "Smith" has been created
    When I get the trainee by their stored ID
    Then the response status should be 200
    And the trainee firstName should be "Alice"

  Scenario: Get a non-existent trainee returns 404
    When I get the trainee with ID 999999
    Then the response status should be 404

  Scenario: Update a trainee successfully
    Given a trainee with firstName "Bob" and lastName "Brown" has been created
    When I update the trainee with firstName "Robert" and lastName "Brown"
    Then the response status should be 200
    And the trainee firstName should be "Robert"

  Scenario: Update a non-existent trainee returns 404
    When I update trainee ID 999999 with firstName "X" and lastName "Y"
    Then the response status should be 404

  Scenario: Delete a trainee successfully
    Given a trainee with firstName "Charlie" and lastName "Green" has been created
    When I delete the trainee by their stored ID
    Then the response status should be 204

  Scenario: Delete a non-existent trainee returns 404
    When I delete the trainee with ID 999999
    Then the response status should be 404

  Scenario: Unauthenticated request returns 401
    When I request GET "/api/trainees" without authentication
    Then the response status should be 401