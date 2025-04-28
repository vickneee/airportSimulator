package simu.framework;

/**
 * The Clock class is a singleton that keeps track of the simulation time.
 * It provides methods to set and get the current time.
 */
public class Clock {
	private double time;
	private static Clock instance;

    /**
     * Private constructor to prevent instantiation from outside the class.
     * Initializes the time to 0.
     */
	private Clock(){
		time = 0;
	}

    /**
     * Returns the singleton instance of the Clock class.
     * If the instance is null, it creates a new instance.
     *
     * @return The singleton instance of the Clock class.
     */
	public static Clock getInstance(){
		if (instance == null){
			instance = new Clock();
		}
		return instance;
	}

    /**
     * Sets the current time of the simulation.
     *
     * @param time The new time to be set.
     */
	public void setTime(double time){
		this.time = time;
	}

    /**
     * Returns the current time of the simulation.
     *
     * @return The current time of the simulation.
     */
	public double getTime(){
		return time;
	}
}
