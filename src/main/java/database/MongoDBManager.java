package database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBManager {
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    // connection string to MongoDB Atlas
    private static final String CONNECTION_STRING = "<your-mongodb-atlas-connection-string>";
    private static final String DB_NAME = "airport_simulator";

    public static MongoDatabase getDatabase() {
        if (database == null) {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            database = mongoClient.getDatabase(DB_NAME);
        }
        return database;
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
        }
    }
}
