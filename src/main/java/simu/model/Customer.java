package simu.model;

import simu.framework.Clock;
import simu.framework.Trace;


// TODO:
// Customer to be implemented according to the requirements of the simulation model (data!)
public class Customer {
	private double arrivalTime;
	private double removalTime;
	private int id;
	private static int i = 1;
	private static long sum = 0;
	private boolean isEUFlight;

	/**
	 * Constructor for the Customer class.
	 *
	 * Initializes a new Customer object with the following:
	 * - The 'isEUFlight' variable is set based on the input parameter,
	 *   where 1 indicates an EU flight (true) and 0 indicates a non-EU flight (false).
	 * - The 'arrivalTime' is recorded using the current simulation clock time.
	 *   This value represents the time the customer is created or arrives in the system.
	 * - Additionally, a log entry is made to record the arrival of the new customer.
	 *
	 * @param isEUFlight A numeric flag indicating whether the flight is within the EU.
	 *                   Use 1 for an EU flight and 0 for a non-EU flight.
	 */
	public Customer(long isEUFlight) {
	    id = i++;
		if(isEUFlight == 1){
			this.isEUFlight = true;
		}else{
			this.isEUFlight = false;
		}
		arrivalTime = Clock.getInstance().getTime();
		Trace.out(Trace.Level.INFO, "New customer #" + id + " arrived at  " + arrivalTime);
	}

	public double getRemovalTime() {
		return removalTime;
	}

	public void setRemovalTime(double removalTime) {
		this.removalTime = removalTime;
	}

	public double getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public boolean getIsEUFlight(){
		return isEUFlight;
	}
	
	public void reportResults() {
		Trace.out(Trace.Level.INFO, "\nCustomer " + id + " ready! ");
		Trace.out(Trace.Level.INFO, "Customer "   + id + " arrived: " + arrivalTime);
		Trace.out(Trace.Level.INFO,"Customer "    + id + " removed: " + removalTime);
		Trace.out(Trace.Level.INFO,"Customer "    + id + " stayed: "  + (removalTime - arrivalTime));
		Trace.out(Trace.Level.INFO, "Customer " + id + " flight type: " + (isEUFlight? "EU flight" : "Non-EU flight"));

		sum += (removalTime - arrivalTime);
		double mean = sum/id;
		System.out.println("Current mean of the customer service times " + mean);
	}

}
