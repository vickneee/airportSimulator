package database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Airport entities.
 * Handles database operations related to airports, such as retrieving all airports
 * or fetching a specific airport by its ID.
 */
public class AirportDAO {
    private final MongoCollection<Document> collection;

    /**
     * Constructs an AirportDAO and initializes the MongoDB collection for airports.
     */
    public AirportDAO() {
        MongoDatabase db = MongoDBManager.getDatabase();
        this.collection = db.getCollection("airports");
    }

    /**
     * Retrieves all airports from the database.
     * @return A list of all Airport objects; an empty list if no airports are found.
     */
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

    /**
     * Retrieves a specific airport from the database by its ID.
     * @param id The ObjectId of the airport to retrieve.
     * @return The Airport object if found, otherwise null.
     */
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
