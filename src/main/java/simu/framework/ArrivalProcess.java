package simu.framework;
import simu.model.EventType;

import eduni.distributions.*;

/**
 * ArrivalProcess is responsible for generating events based on a given continuous distribution.
 * It uses a ContinuousGenerator to sample the time until the next event occurs and adds it to the event list.
 */
public class ArrivalProcess {
	private ContinuousGenerator generator;
	private EventList eventList;
	private EventType type;

    /**
     * Constructor for ArrivalProcess.
     *
     * @param g     The ContinuousGenerator used to sample the time until the next event.
     * @param tl    The EventList where the generated events will be added.
     * @param type  The type of event to be generated.
     */
	public ArrivalProcess(ContinuousGenerator g, EventList tl, EventType type) {
		this.generator = g;
		this.eventList = tl;
		this.type = type;
	}

    /**
     * Generates the next event based on the current time and the sampled time from the generator.
     * The generated event is added to the event list.
     */
	public void generateNext() {
		Event t = new Event(type, Clock.getInstance().getTime() + generator.sample(), null, 0);
		eventList.add(t);
	}

    /**
     * Sets a new generator for the ArrivalProcess.
     *
     * @param newGenerator The new ContinuousGenerator to be used.
     */
    public void setGenerator(ContinuousGenerator newGenerator) {
        this.generator = newGenerator;
    }

}
