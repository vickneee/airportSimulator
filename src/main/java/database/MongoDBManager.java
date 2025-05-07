package database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * Manages the connection to the MongoDB database.
 * This class provides a centralized way to get a database instance
 * and to close the connection when it's no longer needed.
 * It uses a singleton pattern for the MongoClient and MongoDatabase instances.
 */
public class MongoDBManager {
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    // connection string to MongoDB Atlas
    private static final String CONNECTION_STRING = "mongodb+srv://admin:admin@cluster0.deiexe5.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
    private static final String DB_NAME = "airport_simulator";

    /**
     * Gets the singleton instance of the MongoDatabase.
     * If the database instance does not exist, it creates a new MongoDB client
     * and gets the database using the predefined connection string and database name.
     *
     * @return The MongoDatabase instance for the airport_simulator database.
     */
    public static MongoDatabase getDatabase() {
        if (database == null) {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            database = mongoClient.getDatabase(DB_NAME);
        }
        return database;
    }

    /**
     * Closes the MongoDB client connection and resets the client and database instances.
     * This method should be called when the application is shutting down or when the
     * database connection is no longer needed to free up resources.
     */
    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
        }
    }
}
