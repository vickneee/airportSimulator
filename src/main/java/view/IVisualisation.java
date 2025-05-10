package view;

import java.util.List;

/**
 * Interface for the visualization component of the simulation.
 * This interface defines methods that the controller can call to update the visualization.
 */
public interface IVisualisation {

    /**
     * Clears the display.
     * This method is called by the controller to clear the visualization.
     */
    void clearDisplay();

    /**
     * Displays the customer in the simulation.
     */
    void newCustomer(); // Called by Controller, doesn't have a Customer object

    /**
     * Updates the number of customers in the queue.
     * This method is called by the controller to update the queue length.
     *
     * @param lengths the length of the queue
     */
    void updateQueueLengths(List<List<Integer>> lengths);
}
