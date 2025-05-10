package simu.framework;

import java.util.HashMap;

/**
 * Interface for the simulation engine.
 */
public interface IEngine extends Runnable { // NEW
	void setSimulationTime(double time);
	void setDelay(long time);
	long getDelay();
	void setEUFlightPercentage(double percentage);
    void setArrivalInterval(int value);
    HashMap<String, HashMap<String, Double>> getGraphData();
}
