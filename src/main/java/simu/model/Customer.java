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
    private double servicedTime = 0;

    /**
     * Initializes a Customer instance and determines whether the customer is on an EU flight.
     * The EU flight status is set based on the provided parameter.
     *
     * @param isEUFlight A numeric value indicating if the flight is an EU flight (1 for true, otherwise false).
     */
    public Customer(long isEUFlight, Controller controller) {
        id = i++;

        if (isEUFlight == 1) {
            this.isEUFlight = true;
        } else {
            this.isEUFlight = false;
        }
        arrivalTime = Clock.getInstance().getTime();
        Trace.out(Trace.Level.INFO, "New customer #" + id + " arrived at  " + arrivalTime);
        // Show the line in the log area
        controller.showLogArea("\nNew customer #" + id + " arrived at  " + String.format("%.2f", arrivalTime) + " (time units)");
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

        // Log to GUI
        if (controller != null) {
            controller.showLogArea("\nCustomer #" + id + " ready! ");
            controller.showLogArea("Customer #" + id + " arrived: " +  String.format("%.2f", arrivalTime) + " (time units)");
            controller.showLogArea("Customer #" + id + " removed: " + String.format("%.2f", removalTime) + " (time units)");
            controller.showLogArea("Customer #" + id + " stayed: " + String.format("%.2f",(removalTime - arrivalTime))+ " (time units)");
            controller.showLogArea("Customer #" + id + " flight type: " + (isEUFlight ? "EU flight" : "Non-EU flight"));
        }

        sum += (long) (removalTime - arrivalTime);
        double mean = (double) sum / id;
        System.out.println("Current mean of the customer service times " + mean);
        // Log to GUI
        if (controller != null) {
            controller.showLogArea("Current mean of the customer service times " + String.format("%.2f", mean) + " (time units)");
        }
    }

    /**
     * Resets the static ID counter for the Customer class.
     * This method should be called at the beginning of each new simulation.
     */
    public static void resetIdCounter() {
        i = 1;
        sum = 0; // Optionally reset the sum as well if needed
    }
}