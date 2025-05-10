package simu.framework;

import simu.model.ServicePoint;

/**
 * Represents an event that occurs within the simulation.
 * Includes an IEventType to classify the type of event
 * and a reference to the ServicePoint where the event is associated.
 *
 * The ServicePoint is stored as a class-level variable to enable access to the specific
 * service point involved in the event. This allows the simulation to determine which
 * ServicePoint is being utilized during the processing of the event.
 */
public class Event implements Comparable<Event> {
	private IEventType type;
	private double time;
	private ServicePoint servicePoint;// The associated ServicePoint for this event.

    /**
     * Constructor for the Event class.
     * Initializes the event with a type, time, and associated ServicePoint.
     *
     * @param type The type of the event.
     * @param time The time at which the event occurs.
     * @param sp   The ServicePoint associated with this event.
     */
	public Event(IEventType type, double time, ServicePoint sp) {
		this.type = type;
		this.time = time;
		servicePoint = sp;
	}

    /**
     * Sets the type of the event.
     * @param type The type to be set.
     */
	public void setType(IEventType type) {
		this.type = type;
	}

    /**
     * Retrieves the type of the event.
     * @return The type of the event.
     */
	public IEventType getType() {
		return type;
	}

    /**
     * Sets the time of the event.
     * @param time The time to be set.
     */
	public void setTime(double time) {
		this.time = time;
	}

    /**
     * Retrieves the time of the event.
     * @return The time of the event.
     */
	public double getTime() {
		return time;
	}

	/**
	 * Retrieves the associated ServicePoint for this event.
	 * This provides a direct reference to the specific service point being utilized
	 * during the event's execution.
	 */
	public ServicePoint getServicePoint(){
		return servicePoint;
	}

    /**
     * Compares this event with another event based on their time.
     *
     * @param arg The event to compare with.
     * @return A negative integer, zero, or a positive integer as this event is less than, equal to, or greater than the specified event.
     */
	@Override
	public int compareTo(Event arg) {
		if (this.time < arg.time) return -1;
		else if (this.time > arg.time) return 1;
		return 0;
	}

}
