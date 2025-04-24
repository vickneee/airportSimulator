package simu.framework;

/* Controller uses this interface */
public interface IEngine extends Runnable { // NEW
	public void setSimulationTime(double time);
	public void setDelay(long time);
	public long getDelay();
	public void setEUFlightPercentage(double percentage); // Kysy Opelta vahvistus
    public void setArrivalInterval(int value); // Kysy Opelta vahvistus
}
