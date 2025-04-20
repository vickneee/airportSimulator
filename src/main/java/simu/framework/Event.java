package simu.framework;

import simu.model.ServicePoint;
/**
 * Represents an event that occurs within the simulation.
 * Includes an IEventType to classify the type of event, a timestamp indicating when it occurs,
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
	
	public Event(IEventType type, double time, ServicePoint sp) {
		this.type = type;
		this.time = time;
		servicePoint = sp;
	}
	
	public void setType(IEventType type) {
		this.type = type;
	}
	public IEventType getType() {
		return type;
	}
	public void setTime(double time) {
		this.time = time;
	}
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

	@Override
	public int compareTo(Event arg) {
		if (this.time < arg.time) return -1;
		else if (this.time > arg.time) return 1;
		return 0;
	}
}
