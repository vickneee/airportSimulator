# Airport Simulator - MongoDB Integration Branch

This branch introduces MongoDB Atlas integration for storing and retrieving airport and service point configuration data. The main branch does not include any database integration; all database-related features are new in this branch.

## Key Changes

### 1. MongoDB Integration
- Added MongoDB Atlas as the database for storing airport and service point configuration data.
- Added MongoDB Java driver dependency to `pom.xml`.
- Created `MongoDBManager.java` for managing MongoDB connections.

### 2. Data Access Layer (DAOs)
- Added `AirportDAO.java` and `ServicePointConfigDAO.java` for database operations on airports and service point configurations.
- Added model classes: `Airport.java` and `ServicePointConfig.java`.

### 3. Data Migration
- Added `DataMigration.java` to populate the database with initial airport and service point configuration data (Helsinki, Oslo, Arlanda).

### 4. GUI and Controller Integration
- The airport selection ComboBox in the GUI is now populated from MongoDB.
- When an airport is selected, its service point configurations are loaded from the database and passed to the simulation engine.
- The number and type of service points for the selected airport are printed to the GUI log area before running the simulation.

### 5. Simulation Engine Refactor
- `MyEngine` now supports dynamic configuration of service points based on data loaded from MongoDB, instead of hardcoded values.

