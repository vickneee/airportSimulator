package controller;

import javafx.application.Platform;
import simu.framework.IEngine;
import simu.model.MyEngine;
import view.ISimulatorUI;
import view.Visualisation;

import java.util.Random;

public class Controller implements IControllerVtoM, IControllerMtoV {   // NEW
	private IEngine engine;
	private ISimulatorUI ui;
    private Random random = new Random();


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

        // Example of updating queue lengths (replace with actual logic)
        // Simulate queue lengths changing over time
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(100); // Simulate time passing
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Generate random queue lengths for demonstration
                int queue1Length = random.nextInt(25); // Example length for queue 1
                int queue2Length = random.nextInt(25); // Example length for queue 2
                int queue3Length = random.nextInt(25); // Example length for queue 3
                int queue4Length = random.nextInt(25); // Example length for queue 4
                int queue5Length = random.nextInt(25); // Example length for queue 5

                // Update queue lengths on the JavaFX thread
                Platform.runLater(() -> {
                    updateQueue(0, queue1Length); // Update queue 1 length
                    updateQueue(1, queue2Length); // Update queue 2 length
                    updateQueue(2, queue3Length); // Update queue 3 length
                    updateQueue(3, queue4Length); // Update queue 4 length
                    updateQueue(4, queue5Length); // Update queue 5 length
                });
            }
        }).start();
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

    public void updateQueue(int queueIndex, int length) {
        if (ui.getVisualisation() instanceof Visualisation) {
            Visualisation visualisation = (Visualisation) ui.getVisualisation();
            visualisation.updateQueueLength(queueIndex, length);
        }
    }
}
