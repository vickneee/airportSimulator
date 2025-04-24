package simu.framework;

import controller.IControllerMtoV;

import eduni.distributions.Bernoulli;
import eduni.distributions.DiscreteGenerator;
import simu.model.ServicePoint;

import java.util.ArrayList;

public abstract class Engine extends Thread implements IEngine {  // NEW DEFINITIONS
	protected double simulationTime = 0;	// time when the simulation will be stopped
	private long delay = 0;
	private Clock clock;				// in order to simplify the code (clock.getClock() instead Clock.getInstance().getClock())
	protected EventList eventList;
	protected ArrayList<ServicePoint> servicePoints;
	protected IControllerMtoV controller; // NEW


    private volatile boolean isRunning = true; // Use volatile for thread safety

    public Engine(IControllerMtoV controller) {	// NEW
		this.controller = controller;  			// NEW
		clock = Clock.getInstance();
		eventList = new EventList();
		/* Service Points are created in simu.model-package's class who is inheriting the Engine class */
	}

	@Override
	public void setSimulationTime(double time) {
		simulationTime = time;
	}
	
	@Override // NEW
	public void setDelay(long time) {
		this.delay = time;
	}
	
	@Override // NEW
	public long getDelay() {
		return delay;
	}
	
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

    public void stopSimulation() {
        isRunning = false;
        interrupt(); // Interrupt the thread as well
    }

    private void runBEvents() {
        while (eventList.getNextTime() == clock.getTime() && isRunning && !Thread.currentThread().isInterrupted()) {
            runEvent(eventList.remove());
        }
    }

	private void tryCEvents() {    // define protected, if you want to overwrite
		for (ServicePoint p: servicePoints){
			if (!p.isReserved() && p.isOnQueue()){
				p.beginService();
			}
		}
	}

	private double currentTime(){
		return eventList.getNextTime();
	}

    private boolean simulate() {
        Trace.out(Trace.Level.INFO, "Time is: " + clock.getTime());
        return isRunning && clock.getTime() < simulationTime && !Thread.currentThread().isInterrupted();
    }

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


	protected abstract void initialization(); 	// Defined in simu.model-package's class who is inheriting the Engine class
	protected abstract void runEvent(Event t);	// Defined in simu.model-package's class who is inheriting the Engine class
	protected abstract void results(); 			// Defined in simu.model-package's class who is inheriting the Engine class
}