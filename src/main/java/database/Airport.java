package database;

import org.bson.types.ObjectId;

/**
 * Represents an airport entity in the database.
 * Contains information such as the airport's ID, name, and description.
 */
public class Airport {
    private ObjectId id;
    private String name;
    private String description;

    /**
     * Default constructor.
     */
    public Airport() {}

    /**
     * Constructs an Airport object with specified ID, name, and description.
     * @param id The unique identifier of the airport.
     * @param name The name of the airport.
     * @param description A brief description of the airport.
     */
    public Airport(ObjectId id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * Gets the unique identifier of the airport.
     * @return The ObjectId of the airport.
     */
    public ObjectId getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the airport.
     * @param id The ObjectId to set.
     */
    public void setId(ObjectId id) {
        this.id = id;
    }

    /**
     * Gets the name of the airport.
     * @return The name of the airport.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the airport.
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the airport.
     * @return The description of the airport.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the airport.
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
