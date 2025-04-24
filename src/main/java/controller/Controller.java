package controller;

import javafx.application.Platform;
import simu.framework.Engine;
import simu.framework.IEngine;
import simu.model.MyEngine;
import view.ISimulatorUI;
import view.SimulatorGUI;
import view.Visualisation;

import java.util.List;

public class Controller implements IControllerVtoM, IControllerMtoV {   // NEW
    private IEngine engine;
    private ISimulatorUI ui;
    private SimulatorGUI simulatorGUI; // Add SimulatorGUI field
    private boolean isPaused = false; // Flag to track pause state

    public Controller(ISimulatorUI ui, SimulatorGUI simulatorGUI) { // Constructor with SimulatorGUI parameter
        this.ui = ui;
        this.simulatorGUI = simulatorGUI; // Initialize SimulatorGUI

        if (simulatorGUI.getArrivalSlider() == null) {
            throw new IllegalStateException("Arrival slider is not initialized in SimulatorGUI.");
        }

        // Bind slider to arrival interval
        simulatorGUI.getArrivalSlider().valueProperty().addListener((obs, oldVal, newVal) -> {
            if (engine != null) {
                engine.setArrivalInterval(newVal.intValue()); // Update arrival interval in engine
            }
        });

    }

    @Override
    public SimulatorGUI getSimulatorGUI() {
        return simulatorGUI;
    }

    /* Engine control: */
    @Override
    public void startSimulation() {
        initializeEngine();
    }

    private void initializeEngine() {
        // int arrivalTime = (int) simulatorGUI.getArrivalSlider().getValue();
        /*No, the line int arrivalTime = (int) simulatorGUI.getArrivalSlider().getValue(); does not listen to the slider
         in real time. It only retrieves the current value of the slider at the moment this line is executed.*/

        // A new Engine thread is created for every simulation.
        // The first integer parameter represents the arrival frequency for customer arrivals.
        // The subsequent integer parameters represent the number of service points
        // for check-in, security, EU gates, passport control, and Non-EU gates, respectively.
        // The arrival frequency is set to 5, which means a new customer arrives every 5 time units.
        // Decrease the arrivalInterval (e.g., from 2 to 5) to decrease customer arrivals
        engine = new MyEngine(this, 5, 5, 3, 5, 3, 5);
        engine.setSimulationTime(ui.getTime());
        engine.setDelay(ui.getDelay());
        // Sets the percentage of flights within the EU
        engine.setEUFlightPercentage(0.3);
        ui.getVisualisation().clearDisplay();
        ((Thread) engine).start();
        //((Thread)engine).run(); // Never like this, why?
    }

    @Override
    public void decreaseSpeed() { // Decrease motor speed
        engine.setDelay((long) (engine.getDelay() * 1.10));
    }

    @Override
    public void increaseSpeed() { // Increase motor speed
        engine.setDelay((long) (engine.getDelay() * 0.9));
    }

    /* Simulation results passing to the UI
     * Because FX-UI updates come from engine thread, they need to be directed to the JavaFX thread
     */
    @Override
    public void showEndTime(double time) {
        Platform.runLater(() -> ui.setEndingTime(time));
    }

    @Override
    public void visualiseCustomer() {
        Platform.runLater(() -> ui.getVisualisation().newCustomer());
    }

    /*@Override
    public void addCustomer(simu.model.Customer customer) {
        Platform.runLater(() -> ui.getVisualisation().newCustomer(customer));
    }*/

    /*@Override
    public void moveCustomer(int customerId, String toLocation) {
        Platform.runLater(() -> ui.getVisualisation().moveCustomer(customerId, toLocation));
    }*/

    @Override
    public void updateQueueLengths(List<List<Integer>> queueLengths) {
        Platform.runLater(() -> {
            if (ui.getVisualisation() instanceof Visualisation) {
                Visualisation visualisation = (Visualisation) ui.getVisualisation();
                visualisation.updateQueueLengths(queueLengths);
            }
        });
    }

    /* Pause and Resume Simulation */
    public synchronized void pauseSimulation() {
        isPaused = true;
        if (engine instanceof MyEngine) {
            ((MyEngine) engine).pauseSimulation();
        }
    }

    public synchronized void resumeSimulation() {
        isPaused = false;
        notify();
        if (engine instanceof MyEngine) {
            ((MyEngine) engine).resumeSimulation();
        }
    }

    public synchronized void checkPaused() {
        while (isPaused) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

//    public void stopSimulation() {
//        System.out.println("Stopping simulation...");
//        if (engine != null) {
//            if (engine instanceof Engine) {
//                ((Engine) engine).stopSimulation();
//                System.out.println("stopSimulation() called on engine.");
//            }
//            if (engine instanceof Thread) {
//                Thread currentThread = (Thread) engine;
//                if (currentThread.isAlive()) {
//                    currentThread.interrupt();
//                    System.out.println("interrupt() called on engine thread.");
//                }
//            }
//            engine = null; // Dereference the old engine
//            System.out.println("Old engine stopped and dereferenced.");
//        } else {
//            System.out.println("No engine to stop.");
//        }
//    }

//    public void resetSimulation() {
//        System.out.println("Resetting simulation...");
//        ui.getVisualisation().clearDisplay();
//        simulatorGUI.clearLogArea();
//        if (engine instanceof MyEngine) {
//            ((MyEngine) engine).reset();
//            System.out.println("Engine reset.");
//        } else {
//            System.out.println("No engine to reset or engine is not MyEngine.");
//        }
//        System.out.println("Simulation reset complete.");
//    }

//    public void startNewSimulation() {
//        System.out.println("Starting new simulation...");
//        initializeEngine();
//        // Reset the UI and logs
//        ui.getVisualisation().clearDisplay();
//        simulatorGUI.clearLogArea(); // Clear logs in the GUI
//        // Reinitialize the engine with the current slider value
//        int arrivalTime = (int) simulatorGUI.getArrivalSlider().getValue(); // Get the slider value
//        System.out.println("Creating new engine with arrivalTime: " + arrivalTime);
//        engine = new MyEngine(this, arrivalTime, 5, 3, 5, 3, 5); // Create a new engine instance
//        engine.setSimulationTime(ui.getTime());
//        engine.setDelay(ui.getDelay());
//        engine.setEUFlightPercentage(0.3); // Reset EU flight percentage
//        // Start the new engine in a new thread
//        if (engine instanceof Runnable) {
//            Thread engineThread = new Thread((Runnable) engine);
//            System.out.println("Starting new engine thread.");
//            engineThread.start(); // Start the engine thread
//            System.out.println("New engine thread started.");
//        } else {
//            System.err.println("Engine is not runnable. Cannot start simulation.");
//        }
//        System.out.println("New simulation started.");
//    }

//    public void restartSimulation() {
//        System.out.println("Restarting simulation...");
//        // Stop the current engine if it exists
//        if (engine != null) {
//            System.out.println("Stopping old engine...");
//            if (engine instanceof Engine) {
//                ((Engine) engine).stopSimulation(); // Stop the simulation safely
//                System.out.println("stopSimulation() called on old engine.");
//            }
//            if (engine instanceof Thread) {
//                Thread currentThread = (Thread) engine;
//                if (currentThread.isAlive()) {
//                    currentThread.interrupt(); // Interrupt the thread if it's still running
//                    System.out.println("interrupt() called on old engine thread.");
//                }
//            }
//            // Wait a short time to allow the old thread to potentially stop (for debugging)
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        } else {
//            System.out.println("No old engine to stop.");
//        }
//
//        // Clear the UI display and logs
//        ui.getVisualisation().clearDisplay();
//        simulatorGUI.clearLogArea(); // Clear logs in the GUI
//        System.out.println("UI cleared.");
//
//        // Reinitialize the engine with the current slider value
//        int arrivalTime = (int) simulatorGUI.getArrivalSlider().getValue(); // Get the slider value
//        System.out.println("Creating new engine with arrivalTime: " + arrivalTime);
//        engine = new MyEngine(this, arrivalTime, 5, 3, 5, 3, 5); // Create a new engine instance
//        engine.setSimulationTime(ui.getTime());
//        engine.setDelay(ui.getDelay());
//        engine.setEUFlightPercentage(0.3); // Reset EU flight percentage
//
//        // Start the new engine in a new thread
//        if (engine instanceof Runnable) {
//            Thread engineThread = new Thread((Runnable) engine);
//            System.out.println("Starting new engine thread.");
//            engineThread.start(); // Start the engine thread
//            System.out.println("New engine thread started.");
//        } else {
//            System.err.println("Engine is not runnable. Cannot start simulation.");
//        }
//        System.out.println("Restart process completed.");
//    }
}
