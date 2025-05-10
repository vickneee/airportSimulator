package controller;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import simu.framework.IEngine;
import simu.model.MyEngine;
import view.ISimulatorGUI;
import view.SimulatorGUI;
import view.Visualisation;
import database.ServicePointConfig;
import database.AirportDAO;
import database.ServicePointConfigDAO;
import database.Airport;
import org.bson.types.ObjectId;

import java.util.List;

import simu.framework.ArrivalProcess; // Import the ArrivalProcess class

public class Controller implements IControllerVtoM, IControllerMtoV {   // NEW
    private IEngine engine;
    private ISimulatorGUI ui;
    private boolean isRunning = false; // Flag to track the running state
    private boolean isPaused = false; // Flag to track pause state
    private List<ServicePointConfig> servicePointConfigs;
    private AirportDAO airportDAO = new AirportDAO();
    private ServicePointConfigDAO servicePointConfigDAO = new ServicePointConfigDAO();
    private SimulatorGUI simulatorGUI;
    private boolean isStopping = false; // Flag to track stopping state
    private ArrivalProcess arrivalProcess; // Arrival process instance

    public Controller(ISimulatorGUI ui) {
        this.ui = ui;
    }

    /**
     * Sets the SimulatorGUI instance for this controller.
     * This method binds the arrival interval and EU flight percentage sliders to the engine.
     *
     * @param simulatorGUI The SimulatorGUI instance to be set.
     */
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

    /* Engine control: */
    @Override
    public void startSimulation() {
        start();
    }

    /**
     * Initializes the simulation engine.
     * This method creates a new instance of the MyEngine class and starts the simulation.
     */
    private void initializeEngine() {
        // Use the service point configurations if they are available
        if (servicePointConfigs != null && !servicePointConfigs.isEmpty()) {
            engine = new MyEngine(this, servicePointConfigs);
        } else {
            // Get the current slider value for arrival interval
            int arrivalInterval = (int) simulatorGUI.getArrivalSlider().getValue();
            engine = new MyEngine(this, arrivalInterval, 5, 3, 5, 5, 3);
        }

        // Then set all engine properties
        engine.setSimulationTime(ui.getTime());
        engine.setDelay(ui.getDelay());

        // Set an arrival interval if it wasn't passed to constructor
        int arrivalInterval = (int) simulatorGUI.getArrivalSlider().getValue();
        engine.setArrivalInterval(arrivalInterval);

        // Set EU flight percentage
        double euPercentage = simulatorGUI.getEUFlightPercentageSlider().getValue() / 100.0;
        engine.setEUFlightPercentage(euPercentage);

        // Pass the airport name if available
        ComboBox<Airport> airportComboBox = (ComboBox<Airport>) simulatorGUI.getAirportComboBox();
        if (airportComboBox.getValue() != null) {
            ((MyEngine) engine).setSelectedAirport(
                    airportComboBox.getValue().getName()
            );
        }

        // Finally, clear display and start engine
        ui.getVisualisation().clearDisplay();
        ((Thread) engine).start();
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
     * This method is called from the engine to update the visualization with the end time.
     *
     * @param time The end time of the simulation.
     */
    @Override
    public void showEndTime(double time) {
        Platform.runLater(() -> {
            ui.setEndingTime(time);
            // Enable the reset button when simulation shows end time
            simulatorGUI.setResetButtonDisabled(false);
            simulatorGUI.setArrivalSliderDisabled(true);
            simulatorGUI.setEUFlightPercentageSliderDisabled(true);
            simulatorGUI.setSlowDownButtonDisabled(true);
            simulatorGUI.setSpeedUpButtonDisabled(true);
            simulatorGUI.getPlayPauseButton().setDisable(true);
        });
    }

    /**
     * Visualizes a new customer in the UI.
     * This method is called from the engine to update the visualization with a new customer.
     */
    @Override
    public void visualiseCustomer() {
        Platform.runLater(() -> ui.getVisualisation().newCustomer());
    }

    /**
     * Updates the queue lengths in the UI.
     * This method is called from the engine to update the visualization with the current queue lengths.
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

    /**
     * Retrieves all airports from the database.
     * This method queries the database for all airports and returns them as a list.
     *
     * @return A list of Airport objects representing all airports in the database.
     */
    @Override
    public List<Airport> getAllAirports() {
        return airportDAO.getAllAirports();
    }

    /**
     * Retrieves service point configurations by airport ID.
     * This method queries the database for service point configurations associated with a specific airport ID.
     *
     * @param airportId The ObjectId of the airport for which to retrieve service point configurations.
     * @return A list of ServicePointConfig objects associated with the specified airport ID.
     */
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
        if (isStopping) return;
        isStopping = true;

        try {
            System.out.println("Stopping simulation...");
            if (engine != null) {
                // Don't call engine.stopSimulation() if it calls back to Controller
                if (engine instanceof Thread) {
                    ((Thread) engine).interrupt();
                }
                engine = null;
            }
            // Reset the running state flag
            isRunning = false;
            isPaused = false;
        } finally {
            isStopping = false;
        }
    }

    /**
     * Resets the simulation.
     * This method clears the UI elements and resets the engine if it exists.
     */
    public void resetSimulation() {
        System.out.println("Resetting simulation...");
        // Clear the UI elements
        ui.getVisualisation().clearDisplay();
        simulatorGUI.clearResultsArea();
        simulatorGUI.clearLogArea();

        // Reset the simulation time display to empty
        Platform.runLater(() -> {
            // Access the text field directly through simulatorGUI if possible
            if (simulatorGUI != null && simulatorGUI instanceof SimulatorGUI) {
                simulatorGUI.totalTime.setText("");  // Clear the field completely
            }
        });

        // Reset the engine if it exists
        if (engine instanceof MyEngine) {
            ((MyEngine) engine).reset();
            System.out.println("Engine reset.");
        } else {
            System.out.println("No engine to reset or engine is not MyEngine.");
        }
        // Reset the running state flag so we can start a new simulation
        isRunning = false;
        isPaused = false;
        System.out.println("Simulation reset complete.");
    }

    /**
     * Displays the results of the simulation.
     * This method is called from the engine to update the visualization with the results.
     */
    @Override
    public void showResults(String results) {
        Platform.runLater(() -> simulatorGUI.setResultsText(results));
    }

    /**
     * Retrieves graph data from the model and updates the view.
     * This method ensures the UI updates safely by executing the data
     * retrieval on the JavaFX application thread using Platform.runLater().
     */
    public void getGraphData(){
        Platform.runLater(() -> simulatorGUI.setGraphData(engine.getGraphData()));
    }

    /**
     * Enables the external view button in the simulator GUI.
     * This method sets the button's disabled state to false,
     * allowing user interaction.
     */
    @Override
    public void setExternalViewButton(){
        simulatorGUI.getExternalViewButton().setDisable(false);
    }

    /**
     * Displays a log message in the UI.
     * This method is called from the engine to update the visualization with a log message.
     *
     * @param log The log message to be displayed.
     */
    @Override
    public void showLogArea(String log) {
        Platform.runLater(() -> simulatorGUI.logEvent(log));
    }

    /**
     * Clears the log area in the UI.
     * This method is called from the engine to clear the log area.
     */
    @Override
    public void clearLogArea() {
        Platform.runLater(() -> simulatorGUI.clearLogArea());
    }

    /**
     * Clears the visualization display.
     * This method is called from the engine to clear the visualization.
     */
    @Override
    public void clearVisualisation() {
        Platform.runLater(() -> ui.getVisualisation().clearDisplay());
    }

    /**
     * Updates the UI after resetting the simulation.
     * This method is called from the engine to update the UI elements after a reset.
     */
    @Override
    public void updateUIAfterReset() {
        Platform.runLater(() -> {
            simulatorGUI.getStartButton().setDisable(false);
            simulatorGUI.getPlayPauseButton().setDisable(true);
            simulatorGUI.getAirportComboBox().setDisable(false);
            simulatorGUI.getTimeSpinner().setDisable(false);
            simulatorGUI.getDelaySpinner().setDisable(false);
            simulatorGUI.getSlowDownButton().setDisable(true);
            simulatorGUI.getSpeedUpButton().setDisable(true);
            simulatorGUI.getResetButton().setDisable(true);
            simulatorGUI.getArrivalSlider().setDisable(false);
            simulatorGUI.getEUFlightPercentageSlider().setDisable(false);
        });
    }

    /**
     * Starts the simulation.
     * This method creates a new engine instance and initializes it.
     */
    public void start() {
        // Make sure any old engine is properly cleaned up
        if (engine != null) {
            // If there's an existing engine instance, make sure it's fully stopped
            if (engine instanceof Thread && ((Thread) engine).isAlive()) {
                ((Thread) engine).interrupt();
            }
            // Clear reference to the old engine
            engine = null;
        }

        // Create a completely new engine instance and initialize it
        initializeEngine();
    }

    @Override
    public void setServicePointConfigs(List<ServicePointConfig> configs) {
        this.servicePointConfigs = configs;
        // If the engine exists, we need to also pass the selected airport name
        if (engine instanceof MyEngine && simulatorGUI != null) {
            ComboBox<Airport> airportComboBox = (ComboBox<Airport>) simulatorGUI.getAirportComboBox();
            if (airportComboBox.getValue() != null) {
                ((MyEngine) engine).setSelectedAirport(airportComboBox.getValue().getName());
            }
        }
    }

}
