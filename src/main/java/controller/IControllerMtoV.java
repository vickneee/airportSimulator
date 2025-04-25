package controller;

import java.util.List;
import view.SimulatorGUI;

/* interface for the engine */
public interface IControllerMtoV {
    void showEndTime(double time);
    void visualiseCustomer();
    void updateQueueLengths(List<List<Integer>> queueLengths);
    void checkPaused(); // Check if the simulation is paused
    SimulatorGUI getSimulatorGUI(); // Kysy Opelta APUA
    public void showResults(String results);
}