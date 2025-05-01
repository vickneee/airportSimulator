package controller;

import java.util.List;

/* Interface for the engine */
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