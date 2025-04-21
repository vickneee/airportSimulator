package simu.framework;

/* Controller uses this interface */
public interface IEngine extends Runnable { // NEW
	public void setSimulationTime(double time);
	public void setDelay(long time);
	public long getDelay();
	public void setEUFlightPercentage(double percentage);
//    public void reset(); // Add this method
    public void setArrivalInterval(int arrivalInterval); // NEW
}
