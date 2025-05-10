package simu.model;

import simu.framework.IEventType;

/**
 * Enum representing different types of events in the simulation.
 * Each event type corresponds to a specific action or occurrence within the simulation.
 * The enum implements the IEventType interface to categorize events.
 */
// Event types are defined by the requirements of the simulation model
public enum EventType implements IEventType {
	ARR1, DEP1, DEP2, DEP3, DEP4, DEP5;
}
