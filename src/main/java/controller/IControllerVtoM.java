package controller;

import database.ServicePointConfig;
import database.Airport;
import org.bson.types.ObjectId;
import java.util.List;

/* interface for the UI */
public interface IControllerVtoM {
    public void startSimulation();
    public void increaseSpeed();
    public void decreaseSpeed();
    public void pauseSimulation(); // Add this method
    public void resumeSimulation(); // Add this method

    void setServicePointConfigs(List<ServicePointConfig> configs);

    List<Airport> getAllAirports();
    List<ServicePointConfig> getConfigsByAirportId(ObjectId airportId);

    // public void restartSimulation();
    // public void stopSimulation();
    // public void resetSimulation();
    // public void startNewSimulation();
}
