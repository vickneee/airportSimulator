package database;

import org.bson.types.ObjectId;

/**
 * Represents the configuration for a service point at an airport.
 * This includes details like the type of service point (e.g., CHECKIN, SECURITY),
 * the number of servers, mean service time, and parameters for the service time distribution.
 */
public class ServicePointConfig {
    private ObjectId id;
    private ObjectId airportId;
    private String pointType;
    private int numberOfServers;
    private double meanServiceTime;
    private String distributionType;
    private Double param1;
    private Double param2;

    /**
     * Default constructor.
     */
    public ServicePointConfig() {}

    /**
     * Constructs a ServicePointConfig object with specified parameters.
     *
     * @param id The unique identifier of the service point configuration.
     * @param airportId The identifier of the airport this configuration belongs to.
     * @param pointType The type of the service point (e.g., "CHECKIN", "SECURITY").
     * @param numberOfServers The number of servers available at this service point.
     * @param meanServiceTime The mean service time for this service point.
     * @param distributionType The type of probability distribution for service times (e.g., "NORMAL", "UNIFORM").
     * @param param1 The first parameter for the distribution (e.g., standard deviation for NORMAL, min for UNIFORM).
     * @param param2 The second parameter for the distribution (e.g., max for UNIFORM).
     */
    public ServicePointConfig(ObjectId id, ObjectId airportId, String pointType, int numberOfServers,
                              double meanServiceTime, String distributionType, Double param1, Double param2) {
        this.id = id;
        this.airportId = airportId;
        this.pointType = pointType;
        this.numberOfServers = numberOfServers;
        this.meanServiceTime = meanServiceTime;
        this.distributionType = distributionType;
        this.param1 = param1;
        this.param2 = param2;
    }

    /**
     * Gets the unique identifier of this service point configuration.
     * @return The ObjectId of the configuration.
     */
    public ObjectId getId() { return id; }
    /**
     * Sets the unique identifier of this service point configuration.
     * @param id The ObjectId to set.
     */
    public void setId(ObjectId id) { this.id = id; }

    /**
     * Gets the identifier of the airport this configuration belongs to.
     * @return The ObjectId of the airport.
     */
    public ObjectId getAirportId() { return airportId; }
    /**
     * Sets the identifier of the airport this configuration belongs to.
     * @param airportId The ObjectId of the airport to set.
     */
    public void setAirportId(ObjectId airportId) { this.airportId = airportId; }

    /**
     * Gets the type of the service point.
     * @return The service point type string.
     */
    public String getPointType() { return pointType; }
    /**
     * Sets the type of the service point.
     * @param pointType The service point type string to set.
     */
    public void setPointType(String pointType) { this.pointType = pointType; }

    /**
     * Gets the number of servers at this service point.
     * @return The number of servers.
     */
    public int getNumberOfServers() { return numberOfServers; }
    /**
     * Sets the number of servers at this service point.
     * @param numberOfServers The number of servers to set.
     */
    public void setNumberOfServers(int numberOfServers) { this.numberOfServers = numberOfServers; }

    /**
     * Gets the mean service time for this service point.
     * @return The mean service time.
     */
    public double getMeanServiceTime() { return meanServiceTime; }
    /**
     * Sets the mean service time for this service point.
     * @param meanServiceTime The mean service time to set.
     */
    public void setMeanServiceTime(double meanServiceTime) { this.meanServiceTime = meanServiceTime; }

    /**
     * Gets the type of probability distribution for service times.
     * @return The distribution type string.
     */
    public String getDistributionType() { return distributionType; }
    /**
     * Sets the type of probability distribution for service times.
     * @param distributionType The distribution type string to set.
     */
    public void setDistributionType(String distributionType) { this.distributionType = distributionType; }

    /**
     * Gets the first parameter for the service time distribution.
     * @return The first distribution parameter.
     */
    public Double getParam1() { return param1; }
    /**
     * Sets the first parameter for the service time distribution.
     * @param param1 The first distribution parameter to set.
     */
    public void setParam1(Double param1) { this.param1 = param1; }

    /**
     * Gets the second parameter for the service time distribution.
     * @return The second distribution parameter.
     */
    public Double getParam2() { return param2; }
    /**
     * Sets the second parameter for the service time distribution.
     * @param param2 The second distribution parameter to set.
     */
    public void setParam2(Double param2) { this.param2 = param2; }
}
