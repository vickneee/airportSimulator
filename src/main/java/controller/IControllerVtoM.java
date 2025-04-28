package controller;

import view.SimulatorGUI;

import database.ServicePointConfig;
import database.Airport;

import org.bson.types.ObjectId;

import java.util.List;

/* Interface for the UI */
public interface IControllerVtoM {
    void startSimulation();
    // void start();
    void increaseSpeed();
    void decreaseSpeed();
    void pauseSimulation();
    void resumeSimulation();

    void stopSimulation();
    void resetSimulation();
    void setSimulatorGUI(SimulatorGUI simulatorGUI);

    void setServicePointConfigs(List<ServicePointConfig> configs);
    List<Airport> getAllAirports();
    List<ServicePointConfig> getConfigsByAirportId(ObjectId airportId);
}
