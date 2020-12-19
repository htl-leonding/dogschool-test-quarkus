Feature: Create a new booking for a course

  Background:
    * url baseUrl

  Scenario: create booking - works
    Given path 'booking'
    And header Content-Type = 'application/json'
    And request {"bookingDate":"2020-03-07","price":45.0,"course":{"id": 1},"dog":{"id": 3}}
    When method POST
    Then status 201
    And header Location = 'http://localhost:8080/api/booking/1'

  Scenario: create booking - course is missing
    Given path 'booking'
    And header Content-Type = 'application/json'
    And request {"bookingDate":"2020-03-07","price":45.0,"dog":{"id": 3}}
    When method POST
    Then status 400
    And header reason = 'course-id is missing or out of range (>0)'

  Scenario: create booking - course-id is negative
    Given path 'booking'
    And header Content-Type = 'application/json'
    And request {"bookingDate":"2020-03-07","price":45.0,"course":{"id": -1},"dog":{"id": 3}}
    When method POST
    Then status 400
    And header reason = 'course-id is missing or out of range (>0)'

  Scenario: create booking - course-id not found in database table
    Given path 'booking'
    And header Content-Type = 'application/json'
    And request {"bookingDate":"2020-03-07","price":45.0,"course":{"id": 100},"dog":{"id": 3}}
    When method POST
    Then status 400
    And header reason = 'Course with id=100 is not available'

  Scenario: create booking - dog is missing
    Given path 'booking'
    And header Content-Type = 'application/json'
    And request {"bookingDate":"2020-03-07","price":45.0,"course":{"id": 1}}
    When method POST
    Then status 400
    And header reason = 'dog-id is missing or out of range (>0)'

  Scenario: create booking - dog-id is negative
    Given path 'booking'
    And header Content-Type = 'application/json'
    And request {"bookingDate":"2020-03-07","price":45.0,"course":{"id": 1},"dog":{"id": -3}}
    When method POST
    Then status 400
    And header reason = 'dog-id is missing or out of range (>0)'

  Scenario: create booking - dog-id not found in database table
    Given path 'booking'
    And header Content-Type = 'application/json'
    And request {"bookingDate":"2020-03-07","price":45.0,"course":{"id": 1},"dog":{"id": 300}}
    When method POST
    Then status 400
    And header reason = 'Dog with id=300 is not available'


