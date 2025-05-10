package simu.model;

import controller.Controller;

import simu.framework.Clock;
import simu.framework.Trace;

/**
 * The Customer class represents a customer in the simulation.
 * It contains information about the customer's arrival time, removal time, ID, and flight type (EU or non-EU).
 * The class also provides methods to report results and manage customer IDs.
 */
public class Customer {
    private double arrivalTime;
    private double removalTime;
    private int id;
    private static int i = 1; // Static counter for Customer IDs
    private static long sum = 0;
    private boolean isEUFlight;
    private double totalWaitingTime = 0;
    private double startWaitingTime = 0;

    /**
     * Initializes a Customer instance and determines whether the customer is on an EU flight.
     * The EU flight status is set based on the provided parameter.
     *
     * @param isEUFlight A numeric value indicating if the flight is an EU flight (1 for true, otherwise false).
     * @param controller The controller instance can be null in test environments.
     */
    public Customer(long isEUFlight, Controller controller) {
        id = i++;

        this.isEUFlight = (isEUFlight == 1);

        arrivalTime = Clock.getInstance().getTime();
        Trace.out(Trace.Level.INFO, "New customer #" + id + " arrived at  " + arrivalTime);
        // Show the line in the log area only if the controller is not null
        if (controller != null) {
            controller.showLogArea("\nNew customer #" + id + " arrived at  " + String.format("%.2f", arrivalTime) + " (time units)");
        }
    }

    /**
     * Returns the removal time for the customer.
     *
     * @return The time when the customer is removed.
     */
    public double getRemovalTime() {
        return removalTime;
    }

    /**
     * Sets the removal time for the customer.
     *
     * @param removalTime The time when the customer is removed.
     */
    public void setRemovalTime(double removalTime) {
        this.removalTime = removalTime;
    }

    /**
     * Returns the arrival time for the customer.
     *
     * @return The time when the customer arrived.
     */
    public double getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Sets the arrival time for the customer.
     *
     * @param arrivalTime The time when the customer arrived.
     */
    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    /**
     * Determines whether the flight is an EU flight.
     *
     * @return {@code true} if the flight is an EU flight, {@code false} otherwise.
     */
    public boolean getIsEUFlight() {
        return isEUFlight;
    }

    /**
     * Returns the ID of the customer.
     *
     * @return The unique identifier for the customer.
     */
    public int getId() {
        return id;
    }

    /**
     * Reports the results of the customer's service.
     * Logs the arrival and removal times, the total time spent in the system,
     * the flight type, and the current mean service time.
     *
     * @param controller The controller instance can be null in test environments.
     */
    public void reportResults(Controller controller) {
        // Log to Trace
        Trace.out(Trace.Level.INFO, "\nCustomer #" + id + " ready! ");
        Trace.out(Trace.Level.INFO, "Customer #" + id + " arrived: " + String.format("%.2f", arrivalTime) + " (time units)");
        Trace.out(Trace.Level.INFO, "Customer #" + id + " removed: " + String.format("%.2f", removalTime) + " (time units)");
        Trace.out(Trace.Level.INFO, "Customer #" + id + " stayed: " + String.format("%.2f",(removalTime - arrivalTime))+ " (time units)");
        Trace.out(Trace.Level.INFO, "Customer #" + id + " flight type: " + (isEUFlight ? "EU flight" : "Non-EU flight"));

        // Log to GUI only if the controller is not null
        if (controller != null) {
            controller.showLogArea("\nCustomer #" + id + " ready! ");
            controller.showLogArea("Customer #" + id + " arrived: " +  String.format("%.2f", arrivalTime) + " (time units)");
            controller.showLogArea("Customer #" + id + " removed: " + String.format("%.2f", removalTime) + " (time units)");
            controller.showLogArea("Customer #" + id + " stayed: " + String.format("%.2f",(removalTime - arrivalTime))+ " (time units)");
            controller.showLogArea("Customer #" + id + " flight type: " + (isEUFlight ? "EU flight" : "Non-EU flight"));
        }

        sum += (long) (removalTime - arrivalTime);
        double mean = (double) sum / id;
        // Log mean to GUI only if the controller is not null
        if (controller != null) {
            controller.showLogArea("Current mean of the customer service times: " + String.format("%.2f", mean) + " (time units)");
        }
    }

    /**
     * Resets the static ID counter for the Customer class.
     * This method should be called at the beginning of each new simulation.
     */
    public static void resetIdCounter() {
        i = 1;
        sum = 0; // Also reset a sum when resetting ID
    }

    /**
     * Starts the waiting time for the customer.
     * This method should be called when the customer starts waiting for service.
     */
    public void startWaiting() {
        startWaitingTime = Clock.getInstance().getTime();
    }

    /**
     * Stops the waiting time for the customer.
     * This method should be called when the customer stops waiting for service.
     */
    public void stopWaiting() {
        if (startWaitingTime > 0) { // Ensure startWaiting was called
            totalWaitingTime += (Clock.getInstance().getTime() - startWaitingTime);
            startWaitingTime = 0; // Reset for the next potential wait
        }
    }

    /**
     * Returns the total waiting time for the customer.
     *
     * @return The total waiting time in time units.
     */
    public double getTotalWaitingTime() {
        return totalWaitingTime;
    }

    /**
     * Returns the total time the customer spent in the system.
     *
     * @return The total time in the system in time units.
     */
    public double getTotalTimeInSystem() {
        return removalTime - arrivalTime;
    }
}