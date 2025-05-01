package simu.framework;

import java.util.HashMap;

/* Controller uses this interface */
public interface IEngine extends Runnable { // NEW
	void setSimulationTime(double time);
	void setDelay(long time);
	long getDelay();
	void setEUFlightPercentage(double percentage);
    void setArrivalInterval(int value);
	public abstract HashMap<String, HashMap<String, Double>> getGraphData();
}
