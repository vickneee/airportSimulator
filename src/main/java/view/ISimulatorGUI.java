package view;

/**
 * Interface for the Simulator GUI.
 * This interface defines methods that the controller can call to interact with the GUI.
 */
public interface ISimulatorGUI {
	// The Controller needs input which is passed to the Engine
    /**
     * Gets the speed of the simulation.
     */
	double getTime();

    /**
     * Gets the delay for the simulation.
     */
    long getDelay();

    /**
     * Sets the end time for the simulation.
     *
     * @param time the end time
     */
	// Controller gives Engine produced results to the UI
	void setEndingTime(double time);

    /**
     * Gets the visualization component of the GUI.
     */
    // Controller requires
	IVisualisation getVisualisation();
}