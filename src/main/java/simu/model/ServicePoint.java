package simu.model;

import java.util.LinkedList;

import eduni.distributions.ContinuousGenerator;

import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;

/**
 * ServicePoint class represents a service point in the simulation.
 * It manages a queue of customers and handles service operations.
 * The class implements Comparable to allow sorting based on queue size.
 *
 * The ServicePoint class is responsible for managing the queue of customers,
 * handling service operations, and scheduling events related to customer service.
 * It provides methods to add and remove customers from the queue, begin service,
 * and check the status of the service point (reserved or not).
 *
 * The class also implements the Comparable interface to allow sorting based on
 * the size of the queue, enabling efficient allocation of customers to the least busy service point.
 */
// Service Point functionalities & calculations (+ variables needed) and reporting to be implemented
public class ServicePoint implements Comparable<ServicePoint>{
	private LinkedList<Customer> jono = new LinkedList<Customer>(); // Data Structure used
	private ContinuousGenerator generator;
	private EventList eventList;
	private EventType eventTypeScheduled;
	private double totalServiceTime;
	private boolean reserved = false;

    /**
     * Constructor for the ServicePoint class.
     * Initializes the service point with a generator, event list, and event type.
     *
     * @param generator The ContinuousGenerator used to sample service times.
     * @param tapahtumalista The EventList where events will be scheduled.
     * @param tyyppi The type of event to be scheduled.
     */
	public ServicePoint(ContinuousGenerator generator, EventList tapahtumalista, EventType tyyppi){
		this.eventList = tapahtumalista;
		this.generator = generator;
		this.eventTypeScheduled = tyyppi;
	}

    /**
     * Adds a customer to the queue.
     * The first customer in the queue is always on service.
     * The waiting time for the customer starts when they join the queue.
     *
     * @param a The customer to be added to the queue.
     */
	public void addQueue(Customer a) {   // The first customer at the queue is always on the service
        // Start measuring waiting time when the customer joins the queue
        a.startWaiting(); // New call without argument
        jono.add(a);
	}

    /**
     * Removes the first customer from the queue.
     * The reserved status is set to false when a customer is removed.
     * The waiting time for the customer stops when they are removed from the queue.
     *
     * @return The customer that was removed from the queue.
     */
    public Customer removeQueue() { // Removes the first customer from the queue
        reserved = false;
        Customer customer = jono.poll();

        // Stop measuring waiting time when a customer is removed from the queue
        if (customer != null) {
            customer.stopWaiting(); // New call without argument
        }
        return customer;
    }

	/**
	 * Starts a new service for a customer who is waiting in the queue.
	 * Marks this ServicePoint as reserved and generates the service time using the provided generator.
	 * Creates a new Event object to represent the scheduled event when the service will complete.
	 * This ServicePoint instance is passed as a parameter to the Event object, allowing the Event
	 * to reference the specific ServicePoint where the service is being executed.
	 */
	public void beginService() { // Begins a new service, customer is on the queue during the service
		reserved = true;
		double serviceTime = generator.sample();
		totalServiceTime += serviceTime;
		// Schedules a new event and passes this ServicePoint instance.
		eventList.add(new Event(eventTypeScheduled, Clock.getInstance().getTime()+serviceTime, this));
	}

    /**
     * Sets the reserved status of this ServicePoint.
     * This method is used to mark the service point as reserved or not.
     */
	public boolean isReserved(){
		return reserved;
	}

    /**
     * Sets the queue to be not reserved.
     * This method is used to mark the service point as not reserved.
     */
	public boolean isOnQueue(){
		return !jono.isEmpty();
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
		return Integer.compare(this.jono.size(), other.jono.size()); //compare the size of the queue.
	}

    /**
     * Returns the size of the queue.
     * This method is used to retrieve the number of customers currently waiting in the queue.
     *
     * @return The size of the queue.
     */
    public int getQueueLength() {
        return this.jono.size();
    }

    /**
     * Returns the total service time for this ServicePoint.
     * This method is used to retrieve the total time spent servicing customers.
     *
     * @return The total service time.
     */
	public double getTotalServiceTime(){
		return totalServiceTime;
	}

    /**
     * Clears the queue and resets the reserved status.
     * This method is used to remove all customers from the queue and
     * reset the reserved status to false.
     */
    public void clear() {
        jono.clear();
        reserved = false;
    }
}
