package database;

import org.bson.types.ObjectId;

public class ServicePointConfig {
    private ObjectId id;
    private ObjectId airportId;
    private String pointType;
    private int numberOfServers;
    private double meanServiceTime;
    private String distributionType;
    private Double param1;
    private Double param2;

    public ServicePointConfig() {}

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

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public ObjectId getAirportId() { return airportId; }
    public void setAirportId(ObjectId airportId) { this.airportId = airportId; }

    public String getPointType() { return pointType; }
    public void setPointType(String pointType) { this.pointType = pointType; }

    public int getNumberOfServers() { return numberOfServers; }
    public void setNumberOfServers(int numberOfServers) { this.numberOfServers = numberOfServers; }

    public double getMeanServiceTime() { return meanServiceTime; }
    public void setMeanServiceTime(double meanServiceTime) { this.meanServiceTime = meanServiceTime; }

    public String getDistributionType() { return distributionType; }
    public void setDistributionType(String distributionType) { this.distributionType = distributionType; }

    public Double getParam1() { return param1; }
    public void setParam1(Double param1) { this.param1 = param1; }

    public Double getParam2() { return param2; }
    public void setParam2(Double param2) { this.param2 = param2; }
}
