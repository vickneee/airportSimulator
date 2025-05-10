package simu.model;

import controller.Controller;

import simu.framework.Clock;
import simu.framework.Trace;

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
     * @param controller The controller instance, can be null in test environments.
     */
    public Customer(long isEUFlight, Controller controller) {
        id = i++;

        this.isEUFlight = (isEUFlight == 1);

        arrivalTime = Clock.getInstance().getTime();
        Trace.out(Trace.Level.INFO, "New customer #" + id + " arrived at  " + arrivalTime);
        // Show the line in the log area only if controller is not null
        if (controller != null) {
            controller.showLogArea("\nNew customer #" + id + " arrived at  " + String.format("%.2f", arrivalTime) + " (time units)");
        }
    }

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

    public double getArrivalTime() {
        return arrivalTime;
    }

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

    public int getId() {
        return id;
    }

    public void reportResults(Controller controller) {
        // Log to Trace
        Trace.out(Trace.Level.INFO, "\nCustomer #" + id + " ready! ");
        Trace.out(Trace.Level.INFO, "Customer #" + id + " arrived: " + String.format("%.2f", arrivalTime) + " (time units)");
        Trace.out(Trace.Level.INFO, "Customer #" + id + " removed: " + String.format("%.2f", removalTime) + " (time units)");
        Trace.out(Trace.Level.INFO, "Customer #" + id + " stayed: " + String.format("%.2f",(removalTime - arrivalTime))+ " (time units)");
        Trace.out(Trace.Level.INFO, "Customer #" + id + " flight type: " + (isEUFlight ? "EU flight" : "Non-EU flight"));

        // Log to GUI only if controller is not null
        if (controller != null) {
            controller.showLogArea("\nCustomer #" + id + " ready! ");
            controller.showLogArea("Customer #" + id + " arrived: " +  String.format("%.2f", arrivalTime) + " (time units)");
            controller.showLogArea("Customer #" + id + " removed: " + String.format("%.2f", removalTime) + " (time units)");
            controller.showLogArea("Customer #" + id + " stayed: " + String.format("%.2f",(removalTime - arrivalTime))+ " (time units)");
            controller.showLogArea("Customer #" + id + " flight type: " + (isEUFlight ? "EU flight" : "Non-EU flight"));
        }

        sum += (long) (removalTime - arrivalTime);
        double mean = (double) sum / id;
        // Log mean to GUI only if controller is not null
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
        sum = 0; // Also reset sum when resetting ID
    }

    // Added methods for waiting time calculation if needed by other parts
    public void startWaiting() {
        startWaitingTime = Clock.getInstance().getTime();
    }

    public void stopWaiting() {
        if (startWaitingTime > 0) { // Ensure startWaiting was called
            totalWaitingTime += (Clock.getInstance().getTime() - startWaitingTime);
            startWaitingTime = 0; // Reset for next potential wait
        }
    }

    public double getTotalWaitingTime() {
        return totalWaitingTime;
    }

    public double getTotalTimeInSystem() {
        return removalTime - arrivalTime;
    }
}