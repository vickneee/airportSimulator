package controller;

import javafx.application.Platform;
import simu.framework.IEngine;
import simu.model.MyEngine;
import view.ISimulatorUI;
import view.SimulatorGUI;
import view.Visualisation;
import database.ServicePointConfig;
import database.AirportDAO;
import database.ServicePointConfigDAO;
import database.Airport;
import org.bson.types.ObjectId;

import java.util.List;

public class Controller implements IControllerVtoM, IControllerMtoV {   // NEW
    private IEngine engine;
    private ISimulatorUI ui;
    private boolean isRunning = false; // Flag to track running state
    private boolean isPaused = false; // Flag to track pause state
    private List<ServicePointConfig> servicePointConfigs;
    private AirportDAO airportDAO = new AirportDAO();
    private ServicePointConfigDAO servicePointConfigDAO = new ServicePointConfigDAO();
    private SimulatorGUI simulatorGUI;


    public Controller(ISimulatorUI ui) {
        this.ui = ui;

    }

    public void setSimulatorGUI(SimulatorGUI simulatorGUI) {
        this.simulatorGUI = simulatorGUI;

        // Arrival interval slider
        if (simulatorGUI.getArrivalSlider() == null) {
            throw new IllegalStateException("Arrival slider is not initialized in SimulatorGUI.");
        }

        // Bind slider to arrival interval
        simulatorGUI.getArrivalSlider().valueProperty().addListener((obs, oldVal, newVal) -> {
            if (engine != null) {
                engine.setArrivalInterval(newVal.intValue()); // Update arrival interval in engine
            }
        });

        // EU flight percentage slider
        if (simulatorGUI.getEUFlightPercentageSlider() == null) {
            throw new IllegalStateException("EU flight percentage slider is not initialized in SimulatorGUI.");
        }

        // Bind slider to EU flight percentage
        simulatorGUI.getEUFlightPercentageSlider().valueProperty().addListener((obs, oldVal, newVal) -> {
            if (engine != null) {
                engine.setEUFlightPercentage(newVal.doubleValue() / 100); // Update EU flight percentage in engine
            }
        });
    }

    /* UI control: */
    @Override
    public SimulatorGUI getSimulatorGUI() {
        return simulatorGUI;
    }

    /* Engine control: */
    @Override
    public void startSimulation() {
        initializeEngine();
    }

    /**
     * Initializes the simulation engine.
     * This method creates a new instance of the MyEngine class and starts the simulation.
     */
    private void initializeEngine() {
        // Use configs from DB if available, otherwise fallback to hardcoded
        if (servicePointConfigs != null && !servicePointConfigs.isEmpty()) {
            engine = new MyEngine(this, servicePointConfigs);
        } else {
            engine = new MyEngine(this, 5, 5, 3, 5, 3, 5);
        }
        engine.setSimulationTime(ui.getTime());
        engine.setDelay(ui.getDelay());
        engine.setEUFlightPercentage(0.3);
        ui.getVisualisation().clearDisplay();
        ((Thread) engine).start();
    }

    public void setServicePointConfigs(List<ServicePointConfig> configs) {
        this.servicePointConfigs = configs;
    }

    /**
     * Decreases the speed of the simulation.
     * This method increases the delay of the engine to slow down the simulation.
     */
    @Override
    public void decreaseSpeed() { // Decrease motor speed
        engine.setDelay((long) (engine.getDelay() * 1.10));
    }

    /**
     * Increases the speed of the simulation.
     * This method decreases the delay of the engine to speed up the simulation.
     */
    @Override
    public void increaseSpeed() { // Increase motor speed
        engine.setDelay((long) (engine.getDelay() * 0.9));
    }

    /**
     * Displays the end time of the simulation in the UI.
     * This method is called from the engine to update the visualisation with the end time.
     *
     * @param time The end time of the simulation.
     */
    @Override
    public void showEndTime(double time) {
        Platform.runLater(() -> ui.setEndingTime(time));
    }

    /**
     * Visualises a new customer in the UI.
     * This method is called from the engine to update the visualisation with a new customer.
     */
    @Override
    public void visualiseCustomer() {
        Platform.runLater(() -> ui.getVisualisation().newCustomer());
    }

    /**
     * Updates the queue lengths in the UI.
     * This method is called from the engine to update the visualisation with the current queue lengths.
     *
     * @param queueLengths A list of lists representing the lengths of different queues.
     */
    @Override
    public void updateQueueLengths(List<List<Integer>> queueLengths) {
        Platform.runLater(() -> {
            if (ui.getVisualisation() instanceof Visualisation) {
                Visualisation visualisation = (Visualisation) ui.getVisualisation();
                visualisation.updateQueueLengths(queueLengths);
            }
        });
    }

    /**
     * Pauses the simulation.
     * This method sets the isPaused flag to true and notifies the engine to pause.
     */
    public synchronized void pauseSimulation() {
        isPaused = true;
        if (engine instanceof MyEngine) {
            ((MyEngine) engine).pauseSimulation();
        }
    }

    /**
     * Resumes the simulation.
     * This method sets the isPaused flag to false and notifies the engine to resume.
     */

    public synchronized void resumeSimulation() {
        isPaused = false;
        notify();
        if (engine instanceof MyEngine) {
            ((MyEngine) engine).resumeSimulation();
        }
    }

    /**
     * Checks if the simulation is paused.
     * This method blocks the current thread until the simulation is resumed.
     */

    public synchronized void checkPaused() {
        while (isPaused) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public List<Airport> getAllAirports() {
        return airportDAO.getAllAirports();
    }

    @Override
    public List<ServicePointConfig> getConfigsByAirportId(ObjectId airportId) {
        return servicePointConfigDAO.getConfigsByAirportId(airportId);
    }

    /**
     * Stops the simulation.
     * This method stops the engine and clears the UI.
     */
    @Override
    public void stopSimulation() {
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
    }

    public void resetSimulation() {
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
    }

    /**
     * Displays the results of the simulation.
     * This method is called from the engine to update the visualisation with the results.
     */
    @Override
    public void showResults(String results) {
        Platform.runLater(() -> simulatorGUI.setResultsText(results));
    }

}
