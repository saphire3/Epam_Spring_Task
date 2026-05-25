Feature: Trainer Workload Component Tests
  As the trainer workload service
  I want to process training workload requests and serve summaries
  So that trainer training hours can be tracked per month

  Scenario: Add workload for a new trainer creates a summary entry
    When I submit an ADD workload request for trainer "john.doe" on "2024-03-15" with duration 60
    Then the workload response status should be 200

  Scenario: Add workload accumulates duration for existing trainer
    Given trainer "jane.smith" already has a workload summary with 60 minutes on "2024-03-01"
    When I submit an ADD workload request for trainer "jane.smith" on "2024-03-20" with duration 30
    Then the workload response status should be 200
    And the workload summary for "jane.smith" should show 90 minutes in month 3 of year 2024

  Scenario: Delete workload reduces trainer duration
    Given trainer "bob.jones" already has a workload summary with 120 minutes on "2024-04-10"
    When I submit a DELETE workload request for trainer "bob.jones" on "2024-04-10" with duration 60
    Then the workload response status should be 200
    And the workload summary for "bob.jones" should show 60 minutes in month 4 of year 2024

  Scenario: Delete workload cannot reduce duration below zero
    Given trainer "neg.test" already has a workload summary with 10 minutes on "2024-05-01"
    When I submit a DELETE workload request for trainer "neg.test" on "2024-05-01" with duration 50
    Then the workload response status should be 200
    And the workload summary for "neg.test" should show 0 minutes in month 5 of year 2024

  Scenario: Get workload summary for an existing trainer
    Given trainer "alice.trainer" already has a workload summary with 45 minutes on "2024-06-20"
    When I get the workload summary for trainer "alice.trainer"
    Then the workload response status should be 200
    And the summary trainer username should be "alice.trainer"

  Scenario: Get workload summary for a non-existent trainer returns 404
    When I get the workload summary for trainer "nobody.here"
    Then the workload response status should be 404

  Scenario: POST workload without authentication returns 403
    When I submit an ADD workload request without authentication for trainer "x.y" on "2024-01-01" with duration 60
    Then the workload response status should be 403