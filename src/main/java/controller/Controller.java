package controller;

import javafx.application.Platform;
import simu.framework.Engine;
import simu.framework.IEngine;
import simu.model.MyEngine;
import view.ISimulatorUI;
import view.SimulatorGUI;
import view.Visualisation;

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
                engine.setArrivalInterval((int) simulatorGUI.getArrivalSlider().getValue());            }
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
        // A new Engine thread is created for every simulation.
        // The first integer parameter represents the arrival frequency for customer arrivals.
        // The subsequent integer parameters represent the number of service points
        // for check-in, security, EU gates, passport control, and Non-EU gates, respectively.
        // The arrival frequency is set to 2, which means a new customer arrives every 2 time units.
        // Decrease the arrivalInterval (e.g., from 2 to 5) to decrease customer arrivals
        engine = new MyEngine(this, 2, 5, 5, 5, 5, 5);
        engine.setArrivalInterval((int) simulatorGUI.getArrivalSlider().getValue());        engine.setSimulationTime(ui.getTime());
        engine.setDelay(ui.getDelay());
        // Sets the percentage of flights within the EU
        engine.setEUFlightPercentage(0.3);
        ui.getVisualisation().clearDisplay();
        ((Thread) engine).start();
        //((Thread)engine).run(); // Never like this, why?
    }
	
	@Override
	public void decreaseSpeed() { // hidastetaan moottorisäiettä
		engine.setDelay((long)(engine.getDelay()*1.10));
	}

	@Override
	public void increaseSpeed() { // nopeutetaan moottorisäiettä
		engine.setDelay((long)(engine.getDelay()*0.9));
	}

	/* Simulation results passing to the UI
	 * Because FX-UI updates come from engine thread, they need to be directed to the JavaFX thread
	 */
	@Override
	public void showEndTime(double time) {
		Platform.runLater(()->ui.setEndingTime(time));
	}

    @Override
    public void visualiseCustomer() {
        Platform.runLater(() -> ui.getVisualisation().newCustomer());
    }

    @Override
    public void addCustomer(simu.model.Customer customer) {
        Platform.runLater(() -> ui.getVisualisation().newCustomer(customer));
    }

    @Override
    public void moveCustomer(int customerId, String toLocation) {
        Platform.runLater(() -> ui.getVisualisation().moveCustomer(customerId, toLocation));
    }

    @Override
    public void updateQueueLengths(java.util.List<Integer> queueLengths) {
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

    public void restartSimulation() {
        if (engine != null) {
            // Stop the current engine thread safely
            if (engine instanceof Engine) {
                ((Engine) engine).stopSimulation();
            }
            if (engine instanceof Thread) {
                Thread currentThread = (Thread) engine;
                if (currentThread.isAlive()) {
                    currentThread.interrupt(); // Still good to ensure interruption
                }
            }

            // Clear the UI display
            ui.getVisualisation().clearDisplay();

            // **Clear the log events in the SimulatorGUI**
            simulatorGUI.clearLogArea(); // Assuming you have a method like this in SimulatorGUI

            // **Create a new engine instance**
            engine = new MyEngine(this, 5, 5, 5, 5, 5, 5);
            engine.setSimulationTime(ui.getTime());
            engine.setDelay(ui.getDelay());
            engine.setEUFlightPercentage(0.3); // Reset EU flight percentage

            // **Start the new engine in a new thread**
            new Thread((Runnable) engine).start();
        } else {
            // If the engine hasn't been initialized yet, just start it
            startSimulation();
            simulatorGUI.clearLogArea();
            System.out.println("Initial simulation started.");
        }
        System.out.println("Simulation restarted.");
    }
}
