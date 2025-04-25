# Airport Simulator - Database Integration Roadmap (MongoDB Atlas)

This document outlines the steps to integrate a MongoDB Atlas database into the Airport Simulator project to store and retrieve airport configurations, including unique service point counts and service time parameters per location.

1.  **Choose a Database:**
    *   **Decision:** Use **MongoDB Atlas** (cloud-hosted MongoDB) for flexible, scalable, document-based storage.
    *   **Note:** You will need a free or paid MongoDB Atlas account and a cluster.

2.  **Add Database Dependency (Maven):**
    *   Add the MongoDB Java driver dependency to your `pom.xml`:
      ```xml
      <dependency>
          <groupId>org.mongodb</groupId>
          <artifactId>mongodb-driver-sync</artifactId>
          <version>4.11.1</version> <!-- Use the latest appropriate version -->
      </dependency>
      ```
    *   Reload your Maven project in your IDE to download the dependency.

3.  **Design the Database Schema:**
    *   Define the necessary collections (MongoDB uses collections, not tables):
        *   **`airports` Collection:**
            *   Example document:
              ```json
              {
                "_id": ObjectId,
                "name": "Airport Name",
                "description": "Optional description"
              }
              ```
        *   **`servicePointConfigs` Collection:**
            *   Each document must store, for each airport and service point location:
                *   The unique count of service points at that location (`numberOfServers`)
                *   The service time parameters for that location (`meanServiceTime`, `distributionType`, `param1`, `param2`)
            *   Example document:
              ```json
              {
                "_id": ObjectId,
                "airportId": ObjectId, // Reference to airports._id
                "pointType": "CHECKIN", // e.g., "CHECKIN", "SECURITY_EU"
                "numberOfServers": 3,
                "meanServiceTime": 5.0,
                "distributionType": "NORMAL",
                "param1": 1.0,
                "param2": 2.0
                // ...other relevant parameters...
              }
              ```

4.  **Create a Data Access Layer (DAL):**
    *   Create a new package (e.g., `database` or `persistence`).
    *   **Connection Manager:** Implement a class (e.g., `MongoDBManager.java`) to handle MongoDB client setup (using the Atlas connection string) and closing.
    *   **DAO (Data Access Object) Classes:** Create classes for interacting with each collection (e.g., `AirportDAO.java`, `ServicePointConfigDAO.java`).
    *   **Data Transfer Objects (DTOs) / Model Classes:** Create simple Java classes (e.g., `Airport.java`, `ServicePointConfig.java`) to represent the data retrieved from the collections.

5.  **Implement Database Operations (MongoDB Java Driver):**
    *   In the DAO classes, write methods using the MongoDB Java driver:
        *   Get `MongoClient` from `MongoDBManager`.
        *   Use the appropriate collection for queries (find, insert, update, etc.).
        *   Map BSON documents to your DTOs/Model classes.
        *   Handle exceptions.
        *   Use try-with-resources or ensure the client is closed properly.
    *   Example methods: `List<Airport> getAllAirports()`, `List<ServicePointConfig> getConfigsByAirportId(ObjectId airportId)`.

6.  **Integrate with GUI and Controller:**
    *   **`SimulatorGUI.java`:**
        *   Add a UI element (e.g., `ComboBox<Airport>`) to display airports loaded from MongoDB.
        *   Populate this list by calling the appropriate DAO method (e.g., `airportDAO.getAllAirports()`) likely via the `Controller`.
    *   **`Controller.java`:**
        *   Instantiate your DAO classes.
        *   Add methods to fetch data from DAOs and provide it to the GUI (e.g., `loadAirportList()`).
        *   When an airport is selected in the GUI:
            *   Get the selected `Airport` object's `_id`.
            *   Fetch the corresponding `ServicePointConfig` list using the DAO (e.g., `configDAO.getConfigsByAirportId(selectedId)`).
            *   Pass this configuration list to the simulation engine when starting the simulation.

7.  **Modify Simulation Engine (`MyEngine.java`):**
    *   Update the `MyEngine` constructor or add a configuration method (e.g., `configureEngine(List<ServicePointConfig> configs)`) to accept the database-loaded settings.
    *   In the engine's initialization (`init()` or constructor), use the provided `List<ServicePointConfig>` to create and configure the `ServicePoint` objects dynamically, instead of using hardcoded values or individual GUI inputs for parameters.

8.  **Database Initialization and Sample Data:**
    *   Create a script or Java method to insert initial data into MongoDB Atlas (optional).
    *   You can use MongoDB Atlas UI, Compass, or a Java method to insert sample airports and service point configs.
    *   No need for SQL scripts; use JSON documents for data.

**Note:**
- The database must store, for each airport and service point location, the unique service point count and service time parameters at minimum.
- If you want to store actual simulation results (e.g., observed service times, queue stats), consider adding a new collection (e.g., `simulationResults`) for this purpose.