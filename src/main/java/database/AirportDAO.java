package database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;

public class AirportDAO {
    private final MongoCollection<Document> collection;

    public AirportDAO() {
        MongoDatabase db = MongoDBManager.getDatabase();
        this.collection = db.getCollection("airports");
    }

    public List<Airport> getAllAirports() {
        List<Airport> airports = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Airport airport = new Airport(
                        doc.getObjectId("_id"),
                        doc.getString("name"),
                        doc.getString("description")
                );
                airports.add(airport);
            }
        }
        return airports;
    }

    public Airport getAirportById(ObjectId id) {
        Document doc = collection.find(new Document("_id", id)).first();
        if (doc != null) {
            return new Airport(
                    doc.getObjectId("_id"),
                    doc.getString("name"),
                    doc.getString("description")
            );
        }
        return null;
    }
}
