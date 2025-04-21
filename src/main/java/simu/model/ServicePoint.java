package simu.model;

import java.util.LinkedList;

import eduni.distributions.ContinuousGenerator;
import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;

// TODO:
// Service Point functionalities & calculations (+ variables needed) and reporting to be implemented
public class ServicePoint implements Comparable<ServicePoint>{
	private LinkedList<Customer> jono = new LinkedList<Customer>(); // Data Structure used
	private ContinuousGenerator generator;
	private EventList eventList;
	private EventType eventTypeScheduled;
	//Queuestrategy strategy; // option: ordering of the customer
	private boolean reserved = false;

	public ServicePoint(ContinuousGenerator generator, EventList tapahtumalista, EventType tyyppi){
		this.eventList = tapahtumalista;
		this.generator = generator;
		this.eventTypeScheduled = tyyppi;
	}

	public void addQueue(Customer a){   // First customer at the queue is always on the service
		jono.add(a);
	}

	public Customer removeQueue(){		// Remove serviced customer
		reserved = false;
		return jono.poll();
	}

	/**
	 * Starts a new service for a customer who is waiting in the queue.
	 * Marks this ServicePoint as reserved and generates the service time using the provided generator.
	 * Creates a new Event object to represent the scheduled event when the service will complete.
	 * This ServicePoint instance is passed as a parameter to the Event object, allowing the Event
	 * to reference the specific ServicePoint where the service is being executed.
	 */
	public void beginService() {  		// Begins a new service, customer is on the queue during the service
		reserved = true;
		double serviceTime = generator.sample();
		eventList.add(new Event(eventTypeScheduled, Clock.getInstance().getTime()+serviceTime, this));// Schedule the event and pass this ServicePoint instance.
	}

	public boolean isReserved(){
		return reserved;
	}

	public boolean isOnQueue(){
		return jono.size() != 0;
	}

	/**
	 * Compares this ServicePoint with another ServicePoint based on the size of their respective queues.
	 * This method is used to determine which ServicePoint has the shorter queue, enabling efficient
	 * allocation of customers to the least busy service point.
	 *
	 * The comparison is performed using the Integer.compare method, which ensures a consistent and
	 * natural ordering based on the queue lengths.
	 *
	 * This implementation is particularly useful in scenarios where multiple ServicePoints are available
	 * and a customer needs to be directed to the service point with the smallest queue for optimal processing.
	 *
	 * @param other The other ServicePoint to compare against.
	 * @return A negative integer, zero, or a positive integer as this ServicePoint's queue size is less than,
	 *         equal to, or greater than the other ServicePoint's queue size.
	 */
	@Override
	public int compareTo(ServicePoint other){
		return Integer.compare(this.jono.size(), other.jono.size());//compare the size of the queue.
	}

    public void resetQueue() {
        jono.clear(); // Clear the queue
    }

    public int getQueueLength() {
        return this.jono.size();
    }

}
