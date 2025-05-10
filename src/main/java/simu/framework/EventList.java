package simu.framework;

import java.util.PriorityQueue;

/**
 * The EventList class is responsible for managing a list of events in the simulation.
 * It uses a priority queue to store events based on their time.
 * The class provides methods to add, remove, and retrieve the next event.
 */
public class EventList {

	private PriorityQueue<Event> array = new PriorityQueue<Event>();

    /**
     * Constructor for the EventList class.
     * Initializes an empty priority queue for events.
     */
	public EventList() {
	}

    /**
     * Removes the next event from the priority queue.
     * This method is used to process the next event in the simulation.
     *
     * @return The next event in the priority queue.
     */
	public Event remove(){
		return array.remove();
	}

    /**
     * Adds a new event to the priority queue.
     * The event is added based on its time, ensuring that the events are processed in the correct order.
     *
     * @param t The event to be added to the priority queue.
     */
	public void add(Event t){
		array.add(t);
	}

    /**
     * Retrieves the next event in the queue without removing it.
     * This method is used to check the next event's time without modifying the queue.
     *
     * @return The next event in the priority queue.
     */
	public double getNextTime(){
		return array.peek().getTime();
	}

    /**
     * Clears the event list.
     * This method is used to remove all events from the priority queue.
     */
    public void clear() {
    }
}
