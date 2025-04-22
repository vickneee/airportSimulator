package controller;

import java.util.List;
import simu.model.Customer;
import view.SimulatorGUI; // Ensure this import is correct

/* interface for the engine */
public interface IControllerMtoV {
    void showEndTime(double time);
    void visualiseCustomer();
    void moveCustomer(int customerId, String toLocation);
    void updateQueueLengths(List<Integer> queueLengths);
    void addCustomer(Customer customer);
    void checkPaused(); // Check if the simulation is paused
    SimulatorGUI getSimulatorGUI();
}