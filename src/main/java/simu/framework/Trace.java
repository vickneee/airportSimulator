package simu.framework;

/**
 * Interface representing an event type in the simulation framework.
 * This interface can be implemented by different event types to categorize events.
 */
public class Trace {

    /**
     * Public
     * Enum representing the different levels of tracing.
     * The levels are INFO, WAR (Warning), and ERR (Error).
     *
     * INFO: General information messages.
     * WAR: Warning messages indicating potential issues.
     * ERR: Error messages indicating critical issues.
     */
	public enum Level { INFO, WAR, ERR }

    /**
     * Private static variable to hold the current trace level.
     * The trace level determines the verbosity of the output.
     */
	private static Level traceLevel;

    /**
     * Sets the trace level for the simulation.
     * The trace level determines the verbosity of the output.
     *
     * @param lvl The trace level to be set.
     */
	public static void setTraceLevel(Level lvl){
		traceLevel = lvl;
	}

    /**
     * Prints a message to the console if the specified level is greater than or equal to the current trace level.
     *
     * @param lvl The level of the message to be printed.
     * @param txt The message to be printed.
     */
	public static void out(Level lvl, String txt){
		if (lvl.ordinal() >= traceLevel.ordinal()){
			System.out.println(txt);
		}
	}
}