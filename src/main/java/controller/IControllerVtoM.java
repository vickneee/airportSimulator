package controller;

/* interface for the UI */
public interface IControllerVtoM {
    public void startSimulation();
    public void increaseSpeed();
    public void decreaseSpeed();
    public void pauseSimulation(); // Add this method
    public void resumeSimulation(); // Add this method

    // public void restartSimulation();
    // public void stopSimulation();
    // public void resetSimulation();
    // public void startNewSimulation();
}
