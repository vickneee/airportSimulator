package simu.framework;

import java.util.PriorityQueue;

public class EventList {
	private PriorityQueue<Event> array = new PriorityQueue<Event>();
	public EventList() {
	}
	public Event remove(){
		return array.remove();
	}
	public void add(Event t){
		array.add(t);
	}
	public double getNextTime(){
		return array.peek().getTime();
	}
    public void clear() {
    }
}
