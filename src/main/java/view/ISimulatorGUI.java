package view;

public interface ISimulatorGUI {
	// The Controller needs input which is passed to the Engine
	double getTime();
    long getDelay();

	// Controller gives Engine produced results to the UI
	void setEndingTime(double time);

    // Controller requires
	IVisualisation getVisualisation();
}