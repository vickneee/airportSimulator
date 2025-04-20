package controller;

import javafx.application.Platform;
import simu.framework.IEngine;
import simu.model.MyEngine;
import view.ISimulatorUI;

public class Controller implements IControllerVtoM, IControllerMtoV {   // NEW
	private IEngine engine;
	private ISimulatorUI ui;
	
	public Controller(ISimulatorUI ui) {
		this.ui = ui;
	}

	/* Engine control: */
	@Override
	public void startSimulation() {
		// A new Engine thread is created for every simulation.
		// The first integer parameter represents the arrival frequency for customer arrivals.
		// The subsequent integer parameters represent the number of service points
		// for check-in, security, EU gates, passport control, and Non-EU gates, respectively.
		engine = new MyEngine(this, 5, 5,5,5,5,5);
		engine.setSimulationTime(ui.getTime());
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
		Platform.runLater(new Runnable(){
			public void run(){
				ui.getVisualisation().newCustomer();
			}
		});
	}
}
