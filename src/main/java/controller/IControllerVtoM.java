package controller;

import view.SimulatorGUI;

import database.ServicePointConfig;
import database.Airport;

import org.bson.types.ObjectId;

import java.util.List;

/**
 * Interface for the controller to communicate with the model.
 * This interface defines methods that the view can call to update the model.
 */
public interface IControllerVtoM {

    /**
     * Starts the simulation.
     * The view calls this method to start the simulation.
     */
    void startSimulation();
    // void start();

    /**
     * Increases the speed of the simulation.
     */
    void increaseSpeed();

    /**
     * Decreases the speed of the simulation.
     */
    void decreaseSpeed();

    /**
     * Pauses the simulation.
     */
    void pauseSimulation();

    /**
     * Resumes the simulation.
     */
    void resumeSimulation();

    /**
     * Stops the simulation.
     */
    void stopSimulation();

    /**
     * Resets the simulation.
     */
    void resetSimulation();

    /**
     * Sets SimulatorGUI.
     *
     * @param simulatorGUI the SimulatorGUI instance
     */
    void setSimulatorGUI(SimulatorGUI simulatorGUI);

    /**
     * Sets the ServicePointConfig.
     *
     * @param configs the list of ServicePointConfig instances
     */
    void setServicePointConfigs(List<ServicePointConfig> configs);

    /**
     * Gets the all airports.
     */
    List<Airport> getAllAirports();

    /**
     * Gets configs by airport ID.
     *
     * @param airportId the airport ID
     */
    List<ServicePointConfig> getConfigsByAirportId(ObjectId airportId);

    /**
     * Get the graph data.
     */
    void getGraphData();
}
