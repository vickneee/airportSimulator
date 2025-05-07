# Airport Simulator Tests

This document provides information on how to run the tests for the Airport Simulator project, what the current tests cover, and suggestions for future test development.

## How to Run Tests

Tests for this project are built using JUnit 5 and can be run in the following ways:

1.  **Using Maven (Recommended)**:
    Open a terminal or command prompt, navigate to the root directory of the project (where `pom.xml` is located), and run the following command:
    ```bash
    mvn test
    ```
    Maven will compile the project, compile the tests, and then execute them. Test results will be displayed in the console, and reports are typically generated in the `target/surefire-reports` directory.

2.  **From an IDE (e.g., IntelliJ IDEA, Eclipse)**:
    *   Most Java IDEs provide built-in support for running JUnit tests.
    *   You can typically right-click on:
        *   The `src/test/java` directory to run all tests.
        *   A specific package (e.g., `simu.model` or `simu.framework`) to run tests within that package.
        *   An individual test class (e.g., `ServicePointTest.java`) to run only the tests in that class.
        *   A specific test method within a class.
    *   Look for options like "Run Tests", "Run 'TestClassName'", or a green play button next to test classes/methods.

## Current Test Coverage

The existing tests cover fundamental components of the simulation model and framework:

### `simu.model.CustomerTest`
*   **ID Generation**: Verifies that customer IDs are unique and auto-incremented.
*   **Timestamps**: Checks the correct setting and retrieval of customer arrival and removal times.
*   **System Time Calculation**: Ensures the `getTotalTimeInSystem()` method accurately calculates the duration a customer spends in the system.
*   **Flight Type**: Validates that the `isEUFlight` status is correctly assigned based on constructor parameters.

### `simu.model.ServicePointTest`
*   **Queue Management**: Tests adding customers to the queue, verifying queue length, and ensuring FIFO (First-In, First-Out) removal of customers.
*   **Customer State Hooks**: Verifies that `customer.startWaiting()` is called when a customer is added to the queue and `customer.stopWaiting()` when removed (assuming these methods exist and are relevant for metrics).
*   **Empty Queue Behavior**: Checks that attempting to remove a customer from an empty queue behaves as expected (e.g., returns null).
*   **Service Initiation**: Confirms that `beginService()` correctly sets the service point to a reserved state, updates total service time, and schedules the appropriate departure event in the `EventList` with the correct time and type.
*   **Queue Status**: Tests the `isOnQueue()` method to correctly reflect whether the queue contains customers.
*   **Comparison Logic**: Validates the `compareTo()` method, which is likely used to find the shortest queue among multiple service points of the same type.
*   **State Clearing**: Tests the `clear()` method to ensure it resets the queue and reservation status of the service point.

### `simu.framework.ClockTest`
*   **Singleton Instance**: Verifies that `Clock.getInstance()` consistently returns the same non-null instance.
*   **Time Management**: Tests setting and getting the simulation time, ensuring it can be updated and retrieved correctly.

### `simu.framework.EventListTest`
*   **Event Addition & Ordering**: Ensures that events added to the list are correctly ordered by time, and `getNextTime()` returns the time of the earliest event.
*   **Event Removal**: Verifies that `remove()` returns events in chronological order (earliest first).
*   **Empty List Handling**: Checks that the list behaves correctly when empty (e.g., `remove()` throws `NoSuchElementException`).
*   **List Clearing**: Tests the `clear()` functionality to empty the event list.

## Future Tests to Add

While the current tests cover individual components, the following areas should be considered for future test development to ensure comprehensive coverage and robustness:

### 1. `simu.model.MyEngineTest.java`
This is a high-priority test suite to create, as `MyEngine` orchestrates the core simulation.
*   **Overall Simulation Flow**: Test the simulation from start to finish with various simple scenarios and parameter configurations.
*   **Customer Routing Logic**: 
    *   Verify correct routing of EU vs. Non-EU customers through the defined service points (Check-in -> Security -> Passport/Gate -> Gate).
    *   Test selection of the shortest queue when multiple service points of the same type are available.
*   **Parameter Impact**: 
    *   Test how changes in `arrivalInterval` affect the rate of customer arrivals (`ARR1` events).
    *   Test the `euFlightGenerator` (Bernoulli distribution) to ensure it produces the expected ratio of EU to Non-EU customers over a number of samples.
*   **Results Calculation**: 
    *   Verify the accuracy of calculated metrics like average service times, service point utilization ratios, and overall system throughput.
    *   Test with known inputs and expected outputs.
*   **Engine Controls**: Test `pauseSimulation()`, `resumeSimulation()`, and `reset()` functionalities to ensure they correctly manage the simulation state.
*   **Configuration Loading**: Test `MyEngine` initialization when provided with `List<ServicePointConfig>` (potentially mocking the DAO layer or using a test database setup to provide these configs).

### 2. `simu.model.ArrivalProcessTest.java`
*   **Event Generation**: Verify that `arrivalProcess.generateNext()` correctly creates `ARR1` events.
*   **Event Scheduling**: Ensure the generated arrival events are added to the `EventList` with the correct time, calculated based on the arrival distribution (e.g., `Negexp`).
*   **Distribution Usage**: Test with different parameters for the arrival time distribution to see its effect.

### 3. Integration Tests
*   **Component Interaction**: Design tests that verify the seamless interaction between `MyEngine`, `ServicePoint`, `Customer`, and `EventList` in more complex scenarios than unit tests might cover.
    *   E.g., a customer successfully traversing multiple service points, with events being generated and processed correctly at each step.
*   **Controller-Engine Logic**: If there's significant non-UI logic in the `Controller` that interacts with `MyEngine`, unit/integration test those interactions (e.g., how parameter changes from UI are passed to and handled by the engine).

### 4. Database Interaction Tests (if not covered by existing or engine tests)
*   While DAOs (`AirportDAO`, `ServicePointConfigDAO`) are simple, ensure they correctly map data from MongoDB `Document`s to your Java objects (`Airport`, `ServicePointConfig`) and vice-versa if applicable.
*   These could use an embedded/in-memory MongoDB instance (like Fongo or Flapdoodle) or a dedicated test database for isolated testing.

### 5. Edge Cases and Error Handling
*   **Zero Configurations**: Test behavior when a configuration might lead to zero service points of a particular type.
*   **Extreme Parameters**: Test with very high arrival rates, very low/high service times, or simulation time of zero to check system stability and defined behavior.
*   **Invalid Inputs/Configs**: If applicable, test how the system handles potentially invalid or inconsistent configurations loaded from the database or set via UI parameters.

### 6. Performance (Optional, but good for complex simulations)
*   For very long simulations or a high number of events, basic performance tests could be considered to identify bottlenecks, especially around event list management or frequent calculations.

By implementing these additional tests, the reliability and correctness of the Airport Simulator can be significantly enhanced. 