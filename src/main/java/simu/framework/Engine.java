package simu.framework;

import controller.IControllerMtoV;

import simu.model.ServicePoint;

import java.util.ArrayList;

/**
 * The Engine class is responsible for managing the simulation process.
 * It extends the Thread class to allow for concurrent execution.
 * The Engine class handles the simulation time, event list, and service points.
 * It also provides methods to set the simulation time and delay between events.
 */
public abstract class Engine extends Thread implements IEngine {  // NEW DEFINITIONS
	protected double simulationTime = 0;	// time when the simulation will be stopped
	private long delay = 0;
	private Clock clock; // in order to simplify the code (clock.getClock() instead Clock.getInstance().getClock())
	protected EventList eventList;
	protected ArrayList<ServicePoint> servicePoints;
	protected IControllerMtoV controller; // NEW

    /**
     * Indicates whether the simulation is running.
     * This variable is marked as volatile to ensure thread safety.
     */
    private volatile boolean isRunning = true; // Use volatile for thread safety

    /**
     * Constructor for the Engine class.
     * Initializes the clock and event list.
     */
    public Engine(IControllerMtoV controller) {	// NEW
		this.controller = controller;  			// NEW
		clock = Clock.getInstance();
		eventList = new EventList();
		/* Service Points are created in simu.model-package's class who is inheriting the Engine class */
	}

    /**
     * Sets the simulation time.
     *
     * @param time The time to be set for the simulation.
     */
	@Override
	public void setSimulationTime(double time) {
		simulationTime = time;
	}

    /**
     * Returns the simulation time.
     *
     * @param time The time to be set for the simulation.
     */
	@Override // NEW
	public void setDelay(long time) {
		this.delay = time;
	}

    /**
     * Returns the delay time.
     *
     * @return The current delay time.
     */
	@Override // NEW
	public long getDelay() {
		return delay;
	}

    /**
     * Starts the simulation process.
     * This method is called when the simulation is started.
     * It initializes the simulation and enters the main simulation loop.
     */
	@Override
	public void run() {
        System.out.println("Simulation started.");
        initialization(); // creating, e.g., the first event

        while (isRunning && clock.getTime() < simulationTime && !Thread.currentThread().isInterrupted()) {
            delay(); // NEW
            clock.setTime(currentTime());
            runBEvents();
            tryCEvents();
        }

		results();
    }

    /**
     * Runs the B events in the event list.
     * This method checks if the next event in the list is of type B and processes it.
     */
    private void runBEvents() {
        while (eventList.getNextTime() == clock.getTime() && isRunning && !Thread.currentThread().isInterrupted()) {
            runEvent(eventList.remove());
        }
    }

    /**
     * Attempts to process C events for service points.
     * This method checks if any service points are available and processes them.
     */
	private void tryCEvents() {    // define protected, if you want to overwrite
		for (ServicePoint p: servicePoints){
			if (!p.isReserved() && p.isOnQueue()){
				p.beginService();
			}
		}
	}

    /**
     * Returns the current time of the simulation.
     *
     * @return The current time of the simulation.
     */
	private double currentTime(){
		return eventList.getNextTime();
	}

    /**
     * Simulates the simulation process.
     * This method checks if the simulation is running and if the current time is less than the simulation time.
     */
    private boolean simulate() {
        Trace.out(Trace.Level.INFO, "Time is: " + clock.getTime());
        return isRunning && clock.getTime() < simulationTime && !Thread.currentThread().isInterrupted();
    }

    /**
     * Sets delay time for the simulation.
     * This method is used to pause the simulation for a specified amount of time.
     */
	private void delay() {
		// Trace.out(Trace.Level.INFO, "Delay " + delay);
		try {
			sleep(delay);
		} catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Re-interrupt to ensure the flag is set
            isRunning = false; // Stop the simulation loop
            System.out.println("Simulation interrupted during delay.");
        }
	}

    protected void reset() {
    }

    protected abstract void initialization(); 	// Defined in simu.model-package's class who is inheriting the Engine class
	protected abstract void runEvent(Event t);	// Defined in simu.model-package's class who is inheriting the Engine class
	protected abstract void results(); 			// Defined in simu.model-package's class who is inheriting the Engine class
}