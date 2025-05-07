package database;

import org.bson.types.ObjectId;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;

/**
 * Handles the initial data migration for the airport simulator database.
 * This class is responsible for populating the database with initial airport data
 * and their corresponding service point configurations.
 * It should be run once to set up the database.
 */
public class DataMigration {
    /**
     * Main method to execute the data migration process.
     * Connects to the MongoDB database, clears existing data in airports and
     * servicePointConfigs collections for idempotency, and then inserts new
     * sample data for Helsinki, Oslo, and Arlanda airports along with their
     * service point configurations.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        MongoDatabase db = MongoDBManager.getDatabase();
        MongoCollection<Document> airports = db.getCollection("airports");
        MongoCollection<Document> servicePoints = db.getCollection("servicePointConfigs");

        // Insert airports
        Document helsinki = new Document("name", "Helsinki-Vantaa (HEL)")
                .append("description", "Main international airport in Finland");
        Document oslo = new Document("name", "Oslo Airport (OSL)")
                .append("description", "Main international airport in Norway");
        Document arlanda = new Document("name", "Stockholm Arlanda (ARN)")
                .append("description", "Main international airport in Sweden");

        airports.deleteMany(new Document()); // Clean for idempotency
        servicePoints.deleteMany(new Document());

        airports.insertMany(Arrays.asList(helsinki, oslo, arlanda));

        ObjectId helId = helsinki.getObjectId("_id");
        ObjectId oslId = oslo.getObjectId("_id");
        ObjectId arnId = arlanda.getObjectId("_id");

        // Example service point configs for each airport
        // Helsinki
        servicePoints.insertMany(Arrays.asList(
            new Document("airportId", helId).append("pointType", "CHECKIN").append("numberOfServers", 3).append("meanServiceTime", 12.0).append("distributionType", "NORMAL").append("param1", 2.0).append("param2", null),
            new Document("airportId", helId).append("pointType", "SECURITY").append("numberOfServers", 2).append("meanServiceTime", 8.0).append("distributionType", "NORMAL").append("param1", 1.5).append("param2", null),
            new Document("airportId", helId).append("pointType", "PASSPORT").append("numberOfServers", 2).append("meanServiceTime", 15.0).append("distributionType", "NORMAL").append("param1", 3.0).append("param2", null),
            new Document("airportId", helId).append("pointType", "GATE_EU").append("numberOfServers", 4).append("meanServiceTime", 10.0).append("distributionType", "UNIFORM").append("param1", 5.0).append("param2", 15.0),
            new Document("airportId", helId).append("pointType", "GATE_NONEU").append("numberOfServers", 3).append("meanServiceTime", 14.0).append("distributionType", "UNIFORM").append("param1", 8.0).append("param2", 20.0)
        ));
        // Oslo
        servicePoints.insertMany(Arrays.asList(
            new Document("airportId", oslId).append("pointType", "CHECKIN").append("numberOfServers", 2).append("meanServiceTime", 11.0).append("distributionType", "NORMAL").append("param1", 2.0).append("param2", null),
            new Document("airportId", oslId).append("pointType", "SECURITY").append("numberOfServers", 2).append("meanServiceTime", 7.0).append("distributionType", "NORMAL").append("param1", 1.0).append("param2", null),
            new Document("airportId", oslId).append("pointType", "PASSPORT").append("numberOfServers", 1).append("meanServiceTime", 13.0).append("distributionType", "NORMAL").append("param1", 2.5).append("param2", null),
            new Document("airportId", oslId).append("pointType", "GATE_EU").append("numberOfServers", 3).append("meanServiceTime", 9.0).append("distributionType", "UNIFORM").append("param1", 4.0).append("param2", 13.0),
            new Document("airportId", oslId).append("pointType", "GATE_NONEU").append("numberOfServers", 2).append("meanServiceTime", 13.0).append("distributionType", "UNIFORM").append("param1", 7.0).append("param2", 18.0)
        ));
        // Arlanda
        servicePoints.insertMany(Arrays.asList(
            new Document("airportId", arnId).append("pointType", "CHECKIN").append("numberOfServers", 2).append("meanServiceTime", 10.0).append("distributionType", "NORMAL").append("param1", 1.8).append("param2", null),
            new Document("airportId", arnId).append("pointType", "SECURITY").append("numberOfServers", 1).append("meanServiceTime", 6.0).append("distributionType", "NORMAL").append("param1", 1.2).append("param2", null),
            new Document("airportId", arnId).append("pointType", "PASSPORT").append("numberOfServers", 1).append("meanServiceTime", 12.0).append("distributionType", "NORMAL").append("param1", 2.0).append("param2", null),
            new Document("airportId", arnId).append("pointType", "GATE_EU").append("numberOfServers", 2).append("meanServiceTime", 8.0).append("distributionType", "UNIFORM").append("param1", 3.0).append("param2", 11.0),
            new Document("airportId", arnId).append("pointType", "GATE_NONEU").append("numberOfServers", 1).append("meanServiceTime", 11.0).append("distributionType", "UNIFORM").append("param1", 6.0).append("param2", 16.0)
        ));

        System.out.println("Initial migration complete. Airports and service point configs inserted.");
        MongoDBManager.close();
    }
}
