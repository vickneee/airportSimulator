package database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for ServicePointConfig entities.
 * Handles database operations related to service point configurations,
 * primarily retrieving configurations for a specific airport.
 */
public class ServicePointConfigDAO {
    private final MongoCollection<Document> collection;

    /**
     * Constructs a ServicePointConfigDAO and initializes the MongoDB collection for service point configurations.
     */
    public ServicePointConfigDAO() {
        MongoDatabase db = MongoDBManager.getDatabase();
        this.collection = db.getCollection("servicePointConfigs");
    }

    /**
     * Retrieves all service point configurations for a given airport ID.
     *
     * @param airportId The ObjectId of the airport for which to retrieve configurations.
     * @return A list of ServicePointConfig objects associated with the given airportId.
     *         Returns an empty list if no configurations are found or if an error occurs.
     */
    public List<ServicePointConfig> getConfigsByAirportId(ObjectId airportId) {
        List<ServicePointConfig> configs = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find(new Document("airportId", airportId)).iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                ServicePointConfig config = new ServicePointConfig(
                        doc.getObjectId("_id"),
                        doc.getObjectId("airportId"),
                        doc.getString("pointType"),
                        doc.getInteger("numberOfServers", 1),
                        doc.getDouble("meanServiceTime"),
                        doc.getString("distributionType"),
                        doc.get("param1", Double.class),
                        doc.get("param2", Double.class)
                );
                configs.add(config);
            }
        }
        return configs;
    }
}
