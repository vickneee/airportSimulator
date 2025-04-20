package simu.model;

import controller.IControllerMtoV;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;
import simu.framework.Clock;
import simu.framework.Engine;
import simu.framework.ArrivalProcess;
import simu.framework.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MyEngine extends Engine {
    private ArrivalProcess arrivalProcess;

    public MyEngine(IControllerMtoV controller, int arrivalInterval, int checkinNum, int securityNum, int passportNum, int EUNum, int NonEUNum) { // NEW
        super(controller); // NEW
        // Initialize the main list for all service points
        servicePoints = new ArrayList<>();

        // Separate lists for different categories of service points
        checkinPoints = new ArrayList<>();
        securityCheckPoints = new ArrayList<>();
        passportControlPoints = new ArrayList<>();
        EUGates = new ArrayList<>();
        NonEUGates = new ArrayList<>();

        // Initialize each category with the specified number of service points
        initializeServicePoints(checkinPoints, checkinNum, 10, 10, EventType.DEP1);
        initializeServicePoints(securityCheckPoints, securityNum, 7, 10, EventType.DEP2);
        initializeServicePoints(EUGates, EUNum, 10, 3, EventType.DEP3);
        initializeServicePoints(passportControlPoints, passportNum, 5, 10, EventType.DEP4);
        initializeServicePoints(NonEUGates, NonEUNum, 10, 3, EventType.DEP5);

        // Add all service points to the main servicePoints list
        servicePoints.addAll(checkinPoints);
        servicePoints.addAll(securityCheckPoints);
        servicePoints.addAll(passportControlPoints);
        servicePoints.addAll(EUGates);
        servicePoints.addAll(NonEUGates);


        arrivalProcess = new ArrivalProcess(new Negexp(arrivalInterval, 5), eventList, EventType.ARR1);
    }

    /**
     * Method to initialize a specific category of service points.
     *
     * @param pointList  The list to store the service points.
     * @param count      The number of service points to initialize.
     * @param mean       The mean value for the Normal distribution.
     * @param variance   The variance value for the Normal distribution.
     * @param eventType  The event type associated with the service points.
     */
    private void initializeServicePoints(List<ServicePoint> pointList, int count, double mean, double variance, EventType eventType) {
        for (int i = 0; i < count; i++) {
            pointList.add(new ServicePoint(new Normal(mean, variance), eventList, eventType));
        }
    }

    @Override
    protected void initialization() {
        arrivalProcess.generateNext();     // First arrival in the system
    }

    /**
     * Handles events based on their type during the B-phase of the simulation.
     * Processes customer movement between service points and updates relevant data.
     *
     * @param t The event to be processed.
     */
    @Override
    protected void runEvent(Event t) {  // B phase events
        Customer a;// Temporary variable to hold the customer being processed.

        switch ((EventType) t.getType()) {
            case ARR1:// Customer arrival event.
                // Find the check-in point with the shortest queue.
                ServicePoint checkinPoint = Collections.min(checkinPoints);
                // Add a new customer to the chosen check-in point queue.
                // Generates a value of either 1 or 0 using the Bernoulli distribution and passes it as a parameter to create a new Customer object
                checkinPoint.addQueue(new Customer(euFlightGenerator.sample()));
                arrivalProcess.generateNext();
                controller.visualiseCustomer(); // NEW
                break;

            case DEP1: // Check-in completion event.
                // Remove the customer from the current check-in queue.
                a = t.getServicePoint().removeQueue();
                // Find the security check point with the shortest queue
                ServicePoint securityCheckPoint = Collections.min(securityCheckPoints);
                // Move the customer to the security check queue.
                securityCheckPoint.addQueue(a);
                break;

            case DEP2:// Security check completion event.
                // Remove the customer from the current security check queue.
                a = t.getServicePoint().removeQueue();
                // Determine the next service point based on the customer's flight type.
                if(a.getIsEUFlight()){
                    // Find the EU gate with the shortest queue and move the customer there.
                    ServicePoint EUGate = Collections.min(EUGates);
                    EUGate.addQueue(a);
                } else{
                    // Find the passport control point with the shortest queue for Non-EU flights.
                    ServicePoint passportControlPoint = Collections.min(passportControlPoints);
                    passportControlPoint.addQueue(a);
                }
                break;

            case DEP3:// EU gate processing event.
                // Remove the customer from the EU gate queue.
                a = t.getServicePoint().removeQueue();
                a.setRemovalTime(Clock.getInstance().getTime());
                a.reportResults();
                break;

            case DEP4:// Passport control processing event (Non-EU flights).
                // Remove the customer from the passport control queue.
                a = t.getServicePoint().removeQueue();
                // Find the Non-EU gate with the shortest queue and move the customer there.
                ServicePoint NonEUGate = Collections.min(NonEUGates);
                NonEUGate.addQueue(a);
                break;

            case DEP5:// Non-EU gate processing event.
                // Remove the customer from the Non-EU gate queue.
                a = t.getServicePoint().removeQueue();
                a.setRemovalTime(Clock.getInstance().getTime());
                a.reportResults();
                break;
        }
    }

    @Override
    protected void results() {
        // OLD text UI
        //System.out.println("Simulation ended at " + Clock.getInstance().getClock());
        //System.out.println("Results ... are currently missing");

        // NEW GUI
        controller.showEndTime(Clock.getInstance().getTime());
    }
}
