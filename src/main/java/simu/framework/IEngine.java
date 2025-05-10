package simu.framework;

import java.util.HashMap;

/**
 * Interface for the simulation engine.
 *
 * This interface defines methods that the controller can call to control the simulation.
 */
public interface IEngine extends Runnable {

    /**
     * Sets the simulation time.
     *
     * @param time the simulation time
     */
	void setSimulationTime(double time);

    /**
     * Sets the delay for the simulation.
     *
     * @param time the delay time
     */
	void setDelay(long time);

    /**
     * Gets the simulation delay.
     */
	long getDelay();


    /**
     * Sets the EU flight percentage.
     *
     * @param percentage the EU flight percentage
     */
	void setEUFlightPercentage(double percentage);

    /**
     * Sets the arrival interval.
     *
     * @param value the arrival interval
     */
    void setArrivalInterval(int value);

    /**
     * Gets the graph data.
     */
    HashMap<String, HashMap<String, Double>> getGraphData();
}
