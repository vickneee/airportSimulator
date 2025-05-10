package controller;

import java.util.List;

/**
 * Interface for the controller to communicate with the view.
 * This interface defines methods that the controller can call to update the view.
 */
public interface IControllerMtoV {
    void showEndTime(double time);
    void visualiseCustomer();
    void updateQueueLengths(List<List<Integer>> queueLengths);
    void checkPaused();
    void showResults(String results);
    void showLogArea(String log);
    void clearLogArea();
    void clearVisualisation();
    void updateUIAfterReset();
    void setExternalViewButton();
}