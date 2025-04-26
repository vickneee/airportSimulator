package database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;

public class ServicePointConfigDAO {
    private final MongoCollection<Document> collection;

    public ServicePointConfigDAO() {
        MongoDatabase db = MongoDBManager.getDatabase();
        this.collection = db.getCollection("servicePointConfigs");
    }

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
