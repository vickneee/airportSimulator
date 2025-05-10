package controller;

import java.util.List;

/**
 * Interface for the controller to communicate with the view.
 * This interface defines methods that the controller can call to update the view.
 */
public interface IControllerMtoV {

    /**
     * Shows the end time of the simulation.
     *
     * @param time the end time
     */
    void showEndTime(double time);

    /**
     * Visualizes a new customer in the simulation.
     */
    void visualiseCustomer();

    /**
     * Updates the queue lengths in the visualization.
     *
     * @param queueLengths the lengths of the queues
     */
    void updateQueueLengths(List<List<Integer>> queueLengths);

    /**
     * Checks if the simulation is paused.
     */
    void checkPaused();

    /**
     * Shows the results of the simulation.
     *
     * @param results the results of the simulation
     */
    void showResults(String results);

    /**
     * Shows the log area with the given log message.
     *
     * @param log the log message
     */
    void showLogArea(String log);

    /**
     * Clears the log area.
     * This method is called by the controller to clear the log area.
     */
    void clearLogArea();

    /**
     * Clears the visualization.
     * This method is called by the controller to clear the visualization.
     */
    void clearVisualisation();

    /**
     * Updates the UI after the simulation is reset.
     * This method is called by the controller to update the UI after the simulation is reset.
     */
    void updateUIAfterReset();

    /**
     * Sets the external view button.
     * This method is called by the controller to set the external view button.
     */
    void setExternalViewButton();
}