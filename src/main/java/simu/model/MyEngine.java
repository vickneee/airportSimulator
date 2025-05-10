package simu.model;

import controller.Controller;
import controller.IControllerMtoV;
import eduni.distributions.Bernoulli;
import eduni.distributions.DiscreteGenerator;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;
import simu.framework.*;
import database.ServicePointConfig;
import eduni.distributions.Uniform;
import eduni.distributions.ContinuousGenerator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MyEngine is the main simulation engine for the airport simulation.
 * It handles the initialization, event processing, and results reporting.
 * The engine manages various service points and customer flow through the airport.
 */
public class MyEngine extends Engine implements IEngine {
    private ArrayList<ServicePoint> checkinPoints;
    private ArrayList<ServicePoint> securityCheckPoints;
    private ArrayList<ServicePoint> passportControlPoints;
    private ArrayList<ServicePoint> EUGates;
    private ArrayList<ServicePoint> NonEUGates;
    private ArrivalProcess arrivalProcess;
    private DiscreteGenerator euFlightGenerator;
    private int arrivalInterval;
    private HashMap<String, HashMap<String, Double>> graphData;
    private HashMap<String, Double> servicePointsUsageRatio;
    private double checkpointUsageRatio;
    private double securityCheckpointUsageRatio;
    private double passportControlPointUsageRatio;
    private double EUGateUsageRatio;
    private double NonEUGateUsageRatio;
    private int totalEUServicedCustomer = 0;
    private int totalNonEUServicedCustomer = 0;
    private int totalServicedCustomer = 0;
    private double serviceThroughput;
    private HashMap<String, Double> averageServiceTimes;
    private double averageCheckinServiceTime;
    private double averageSecurityServiceTime;
    private double averagePassportControlServiceTime;
    private double averageEUGateServiceTime;
    private double averageNonEUGateServiceTime;
    private String selectedAirport;

    private boolean isRunning = true; // Flag to control running state
    private boolean isResetting = false; // Flag to control resetting state;

    private List<Customer> servicedCustomers;

    /**
     * Constructor for MyEngine.
     * Initializes the engine with the specified parameters and service points.
     *
     * @param controller      The controller for managing the simulation.
     * @param arrivalInterval  The interval between customer arrivals.
     * @param checkinNum      The number of check-in points.
     * @param securityNum     The number of security check points.
     * @param passportNum     The number of passport control points.
     * @param EUNum          The number of EU gates.
     * @param NonEUNum       The number of Non-EU gates.
     */
    public MyEngine(IControllerMtoV controller, int arrivalInterval, int checkinNum, int securityNum, int passportNum, int EUNum, int NonEUNum) { // NEW
        super(controller);
        this.arrivalInterval = arrivalInterval; // Set the arrival interval
        // Initialize the main list for all service points
        servicePoints = new ArrayList<>();
        // Separate lists for different categories of service points
        checkinPoints = new ArrayList<>();
        securityCheckPoints = new ArrayList<>();
        passportControlPoints = new ArrayList<>();
        EUGates = new ArrayList<>();
        NonEUGates = new ArrayList<>();
        selectedAirport = ""; // Initialize a selected airport
        servicedCustomers = new ArrayList<>(); // Add this line

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
        arrivalProcess = new ArrivalProcess(new Negexp(arrivalInterval, 1), eventList, EventType.ARR1);
    }

    /**
     * Constructor for MyEngine with service point configurations.
     * Initializes the engine with the specified parameters and service points based on configurations.
     *
     * @param controller The controller for managing the simulation.
     * @param configs    The list of service point configurations.
     */
    public MyEngine(IControllerMtoV controller, List<ServicePointConfig> configs) {
        super(controller);
        // Initialize the main list for all service points
        servicePoints = new ArrayList<>();
        checkinPoints = new ArrayList<>();
        securityCheckPoints = new ArrayList<>();
        passportControlPoints = new ArrayList<>();
        EUGates = new ArrayList<>();
        NonEUGates = new ArrayList<>();
        arrivalInterval = 5; // Default can be overridden by config or UI
        servicedCustomers = new ArrayList<>(); // Add this line

        // Map configs to service points
        for (ServicePointConfig config : configs) {
            int count = config.getNumberOfServers();
            ContinuousGenerator generator = null;
            String dist = config.getDistributionType();
            if ("NORMAL".equalsIgnoreCase(dist)) {
                double mean = config.getMeanServiceTime();
                double stddev = config.getParam1() != null ? config.getParam1() : 1.0;
                generator = new Normal(mean, stddev * stddev); // Normal expects variance
            } else if ("UNIFORM".equalsIgnoreCase(dist)) {
                double min = config.getParam1() != null ? config.getParam1() : 1.0;
                double max = config.getParam2() != null ? config.getParam2() : 2.0;
                generator = new Uniform(min, max);
            } else if ("NEGEXP".equalsIgnoreCase(dist) || "EXPONENTIAL".equalsIgnoreCase(dist)) {
                double mean = config.getMeanServiceTime();
                generator = new Negexp(mean);
            } else {
                // Default to Normal if unknown
                generator = new Normal(config.getMeanServiceTime(), 1.0);
            }
            EventType eventType;
            List<ServicePoint> targetList;
            switch (config.getPointType().toUpperCase()) {
                case "CHECKIN":
                    eventType = EventType.DEP1;
                    targetList = checkinPoints;
                    break;
                case "SECURITY":
                    eventType = EventType.DEP2;
                    targetList = securityCheckPoints;
                    break;
                case "PASSPORT":
                    eventType = EventType.DEP4;
                    targetList = passportControlPoints;
                    break;
                case "GATE_EU":
                    eventType = EventType.DEP3;
                    targetList = EUGates;
                    break;
                case "GATE_NONEU":
                    eventType = EventType.DEP5;
                    targetList = NonEUGates;
                    break;
                default:
                    continue; // Skip unknown types
            }
            for (int i = 0; i < count; i++) {
                ServicePoint sp = new ServicePoint(generator, eventList, eventType);
                targetList.add(sp);
                servicePoints.add(sp);
            }
        }
        // Default to 5 if not set by configs
        arrivalProcess = new ArrivalProcess(new Negexp(arrivalInterval, 1), eventList, EventType.ARR1);
    }

    /**
     * Method to initialize a specific category of service points.
     *
     * @param pointList The list to store the service points.
     * @param count     The number of service points to initialize.
     * @param mean      The mean value for the Normal distribution.
     * @param variance  The variance value for the Normal distribution.
     * @param eventType The event type associated with the service points.
     */
    private void initializeServicePoints(List<ServicePoint> pointList, int count, double mean, double variance, EventType eventType) {
        for (int i = 0; i < count; i++) {
            pointList.add(new ServicePoint(new Normal(mean, variance), eventList, eventType));
        }
    }

    /**
     * Initializes the simulation by generating the first arrival event.
     * This method is called at the start of the simulation to kick off the event generation process.
     */
    @Override
    protected void initialization() {
        arrivalProcess.generateNext();
    }

    /**
     * Handles events based on their type during the B-phase of the simulation.
     * Processes customer movement between service points and updates relevant data.
     *
     * @param t The event to be processed.
     */
    @Override
    protected void runEvent(Event t) {  // B phase events

        // Check if the simulation is paused
        controller.checkPaused(); // Pause the simulation if needed

        Customer a; // Temporary variable to hold the customer being processed.

        switch ((EventType) t.getType()) {
            case ARR1: // Customer arrival event.
                // Find the check-in point with the shortest queue.
                ServicePoint checkinPoint = Collections.min(checkinPoints);
                // Add a new customer to the chosen check-in point queue.
                // Generates a value of either 1 or 0 using the Bernoulli distribution and passes it as a parameter to create a new Customer object
                checkinPoint.addQueue(new Customer(euFlightGenerator.sample(), (Controller) controller));
                arrivalProcess.generateNext();
                controller.visualiseCustomer(); // Visualize the customer arrival
                updateQueueLengths(); // Update queue lengths after arrival
                break;

            case DEP1: // Check-in completion event.
                // Remove the customer from the current check-in queue.
                a = t.getServicePoint().removeQueue();
                // Find the security check point with the shortest queue
                ServicePoint securityCheckPoint = Collections.min(securityCheckPoints);
                // Move the customer to the security check queue.
                securityCheckPoint.addQueue(a);
                updateQueueLengths(); // Update queue lengths after departure and arrival
                break;

            case DEP2:// Security check completion event.
                // Remove the customer from the current security check queue.
                a = t.getServicePoint().removeQueue();
                // Determine the next service point based on the customer's flight type.
                if (a.getIsEUFlight()) {
                    // Find the EU gate with the shortest queue and move the customer there.
                    ServicePoint EUGate = Collections.min(EUGates);
                    EUGate.addQueue(a);
                } else {
                    // Find the passport control point with the shortest queue for Non-EU flights.
                    ServicePoint passportControlPoint = Collections.min(passportControlPoints);
                    passportControlPoint.addQueue(a);
                }
                updateQueueLengths(); // Update queue lengths
                break;

            case DEP3:// EU gate processing event.
                // Remove the customer from the EU gate queue.
                a = t.getServicePoint().removeQueue();
                a.setRemovalTime(Clock.getInstance().getTime());
                totalEUServicedCustomer += 1;
                servicedCustomers.add(a); // Add to the tracked customers
                a.reportResults((Controller) controller);
                updateQueueLengths(); // Update queue lengths
                break;

            case DEP4:// Passport control processing event (Non-EU flights).
                // Remove the customer from the passport control queue.
                a = t.getServicePoint().removeQueue();
                // Find the Non-EU gate with the shortest queue and move the customer there.
                ServicePoint NonEUGate = Collections.min(NonEUGates);
                NonEUGate.addQueue(a);
                updateQueueLengths(); // Update queue lengths
                break;

            case DEP5:// Non-EU gate processing event.
                // Remove the customer from the Non-EU gate queue.
                a = t.getServicePoint().removeQueue();
                a.setRemovalTime(Clock.getInstance().getTime());
                totalNonEUServicedCustomer += 1;
                servicedCustomers.add(a); // Add to the tracked customers
                a.reportResults((Controller) controller);
                updateQueueLengths(); // Update queue lengths
                break;
        }
    }

    /**
     * Updates the queue lengths for various service points in the airport simulation.
     * This method gathers the queue lengths from different service points and passes
     * them to the controller for updating the view.
     */
    private void updateQueueLengths() {
        List<List<Integer>> queueLengths = new ArrayList<>();
        // Collect queue lengths from different service points
        queueLengths.add(checkinPoints.stream().map(ServicePoint::getQueueLength).collect(Collectors.toList()));
        queueLengths.add(securityCheckPoints.stream().map(ServicePoint::getQueueLength).collect(Collectors.toList()));
        queueLengths.add(passportControlPoints.stream().map(ServicePoint::getQueueLength).collect(Collectors.toList()));
        queueLengths.add(EUGates.stream().map(ServicePoint::getQueueLength).collect(Collectors.toList()));
        queueLengths.add(NonEUGates.stream().map(ServicePoint::getQueueLength).collect(Collectors.toList()));

        controller.updateQueueLengths(queueLengths); // Call through the controller
    }

    /**
     * Handles the results of the simulation.
     */
    @Override
    protected void results() {
        controller.showEndTime(Clock.getInstance().getTime());
        updateQueueLengths(); // Final update

        // Calculate time metrics
        String averageSystemTime = getAverageTimeInSystem();

        // Parse values for calculation
        System.out.println("\nAverage customer total time in system: " + averageSystemTime);

        System.out.println("\nTotal serviced customers tracked: " + servicedCustomers.size());
        System.out.println("Total serviced customers through EU gate: " + totalEUServicedCustomer);
        System.out.println("Average customer time in system: " + averageSystemTime);

        // Print the final results to the console and log them
        System.out.println("\nSimulation ended.");

        // Log the final results
        String results = "Simulation ended.\n\n";
        results += "Selected airport: " + selectedAirport + "\n\n";
        // Add average waiting time
        results += "Average customer time in the system: " + averageSystemTime + " (time units)\n\n";
        // Calculate the total number of serviced customers
        System.out.println("Final count of passengers who exited through the EU gate: " + totalEUServicedCustomer);
        System.out.println("Final count of passengers who exited through the Non-EU gate: " + totalNonEUServicedCustomer);
        results += "Final count of passengers who exited through the EU gate: " + totalEUServicedCustomer + "\n\n";
        results += "Final count of passengers who exited through the Non-EU gate: " + totalNonEUServicedCustomer + "\n\n";
        // Calculate the total number of serviced customers
        totalServicedCustomer = totalEUServicedCustomer + totalNonEUServicedCustomer;
        System.out.println("Final total count of passengers who exited through gates: " + totalServicedCustomer);
        results += "Final total count of passengers who exited through gates: " + totalServicedCustomer + "\n\n";
        // Calculate the average service times for different service points
        calculateAverageServiceTimes();
        results += "Checkin-point average service time: " + averageCheckinServiceTime + " (time units) \n\n";
        results += "Security check average service time: " + averageSecurityServiceTime + " (time units)\n\n";
        results += "Passport control average service time: " + averagePassportControlServiceTime + " (time units)\n\n";
        results += "EU gate average service time: " + averageEUGateServiceTime + " (time units)\n\n";
        results += "Non-EU gate average service time: " + averageNonEUGateServiceTime + " (time units)\n\n";
        // Calculate the usage ratios for different service points
        calculateServiceTimesUsageRatio();
        results += "Checkin-point usage ratio: " + checkpointUsageRatio + "%\n\n";
        results += "Security check usage ratio: " + securityCheckpointUsageRatio + "%\n\n";
        results += "Passport control usage ratio: " + passportControlPointUsageRatio + "%\n\n";
        results += "EU gate usage ratio: " + EUGateUsageRatio + "%\n\n";
        results += "Non-EU gate usage ratio: " + NonEUGateUsageRatio + "%\n\n";
        // Calculate the service throughput
        calculateServiceThroughput();
        System.out.println("The service throughput is " + serviceThroughput + " passengers per time unit.");
        results += "The service throughput is " + serviceThroughput + " passengers per time unit." + "\n\n";

        // Print the results to the console
        controller.showResults(results);

        // Print the results to the logArea in the GUI
        controller.showLogArea("\nSimulation ended.");

        // Prepare data for the graph to be sent to the view
        graphData = new HashMap<>();
        graphData.put("usageRatio", servicePointsUsageRatio);
        graphData.put("averageServiceTime", averageServiceTimes);

        // Make the external view button clickable by setting disabling to false
        controller.setExternalViewButton();
    }

    /**
     * Pauses the simulation by setting the isRunning flag to false.
     * This method can be called to temporarily halt the simulation process.
     */
    public void pauseSimulation() {
        isRunning = false;
    }

    /**
     * Resumes the simulation by setting the isRunning flag to true.
     * This method can be called to continue the simulation process after a pause.
     */
    public void resumeSimulation() {
        isRunning = true;
    }

    /**
     * Checks if the simulation is currently running.
     *
     * @return {@code true} if the simulation is running, {@code false} otherwise.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Sets the arrival interval for the arrival process.
     *
     * @param arrivalInterval The new arrival interval in seconds.
     */
    @Override
    public void setArrivalInterval(int arrivalInterval) {
        this.arrivalInterval = arrivalInterval;
        arrivalProcess.setGenerator(new Negexp(arrivalInterval, 1));
    }

    /**
     * Sets the percentage of flights allocated to the EU region based on a Bernoulli distribution.
     *
     * <p>This method initializes a Bernoulli distribution generator with the given percentage,
     * which simulates the likelihood of assigning a flight to the EU region.</p>
     *
     * @param percentage The probability of success (0.0 <= prob <= 1.0).
     * Represents the likelihood of a "successful" outcome in the Bernoulli trial.
     */
    @Override
    public void setEUFlightPercentage(double percentage) {
        euFlightGenerator = new Bernoulli(percentage);
    }

    /**
     * Rounds a double value to two decimal places.
     *
     * @param value The value to be rounded.
     * @return The rounded value.
     */
    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    /**
     * Calculates the usage ratio for different service points in the airport simulation.
     * This method assigns the calculated usage ratio to corresponding variables.
     */
    private void calculateServiceTimesUsageRatio() {
        servicePointsUsageRatio = new HashMap<>();
        checkpointUsageRatio = roundToTwoDecimals(calculateUsageRatio(checkinPoints, "Checkin-point"));
        servicePointsUsageRatio.put("Check-in", checkpointUsageRatio);
        securityCheckpointUsageRatio = roundToTwoDecimals(calculateUsageRatio(securityCheckPoints, "Security check"));
        servicePointsUsageRatio.put("Security", securityCheckpointUsageRatio);
        passportControlPointUsageRatio = roundToTwoDecimals(calculateUsageRatio(passportControlPoints, "Passport control"));
        servicePointsUsageRatio.put("Passport", passportControlPointUsageRatio);
        EUGateUsageRatio = roundToTwoDecimals(calculateUsageRatio(EUGates, "EU gate"));
        servicePointsUsageRatio.put("EU Gate", EUGateUsageRatio);
        NonEUGateUsageRatio = roundToTwoDecimals(calculateUsageRatio(NonEUGates, "Non-EU gate"));
        servicePointsUsageRatio.put("Non-EU Gate", NonEUGateUsageRatio);
    }

    /**
     * Calculates the usage ratio of a given service point type.
     *
     * @param sp               List of service points.
     * @param servicePointName The name of the service point type.
     * @return The calculated usage ratio (percentage).
     */
    private double calculateUsageRatio(ArrayList<ServicePoint> sp, String servicePointName) {
        double servicePointNum = sp.size();
        double totalSimulationTime = simulationTime * servicePointNum;
        double totalServiceTime = 0;
        for (ServicePoint servicePoint : sp) {
            totalServiceTime += servicePoint.getTotalServiceTime();
        }

        double usageRatio = totalServiceTime / totalSimulationTime * 100;
        if(usageRatio > 100){
            usageRatio = 100;
        }
        System.out.println(servicePointName + "'s usage ratio is " + usageRatio + "%.");
        return usageRatio;
    }

    /**
     * Calculates the service throughput based on the number of serviced customers and simulation time.
     */
    private void calculateServiceThroughput() {
        serviceThroughput = (totalEUServicedCustomer + totalNonEUServicedCustomer) / simulationTime;
    }

    /**
     * Calculates the average service times for different service points in the airport simulation.
     */
    private void calculateAverageServiceTimes() {
        averageServiceTimes = new HashMap<>();
        averageCheckinServiceTime = roundToTwoDecimals(calculateAverageServiceTime(checkinPoints, "Checkin-point"));
        averageServiceTimes.put("Check-in", averageCheckinServiceTime);
        averageSecurityServiceTime = roundToTwoDecimals(calculateAverageServiceTime(securityCheckPoints, "Security check"));
        averageServiceTimes.put("Security", averageSecurityServiceTime);
        averagePassportControlServiceTime = roundToTwoDecimals(calculateAverageServiceTime(passportControlPoints, "Passport control"));
        averageServiceTimes.put("Passport", averagePassportControlServiceTime);
        averageEUGateServiceTime = roundToTwoDecimals(calculateAverageServiceTime(EUGates, "EU gate"));
        averageServiceTimes.put("EU Gate", averageEUGateServiceTime);
        averageNonEUGateServiceTime = roundToTwoDecimals(calculateAverageServiceTime(NonEUGates, "Non-EU gate"));
        averageServiceTimes.put("Non-EU Gate", averageNonEUGateServiceTime);
    }

    /**
     * Calculates the average service time for a given service point type.
     *
     * @param sp               List of service points.
     * @param servicePointName The name of the service point type.
     * @return The calculated average service time.
     */
    private double calculateAverageServiceTime(ArrayList<ServicePoint> sp, String servicePointName) {
        // double servicePointNum = sp.size();
        double totalServiceTime = 0;
        double averageServicedTime = 0;
        for (ServicePoint servicePoint : sp) {
            totalServiceTime += servicePoint.getTotalServiceTime();
        }

        if (Objects.equals(servicePointName, "Checkin-point") || Objects.equals(servicePointName, "Security check")) {
            if (totalServicedCustomer != 0) {
                System.out.println(servicePointName + "'s average service time: " + totalServiceTime / totalServicedCustomer);
                return totalServiceTime / totalServicedCustomer;
            }
        } else if (Objects.equals(servicePointName, "Passport control") || Objects.equals(servicePointName, "Non-EU gate")) {
            if (totalNonEUServicedCustomer != 0) {
                System.out.println(servicePointName + "'s average service time: " + totalServiceTime / totalNonEUServicedCustomer);
                return totalServiceTime / totalNonEUServicedCustomer;
            }
        } else {
            if (totalEUServicedCustomer != 0) {
                System.out.println(servicePointName + "'s average service time: " + totalServiceTime / totalEUServicedCustomer);
                return totalServiceTime / totalEUServicedCustomer;
            }
        }
        return averageServicedTime;
    }

    /**
     * Retrieves graph data from the model and sends it to the view through the controller.
     * If the graph data is available, it returns the stored data.
     * If no data is present, it returns null.
     *
     * @return A HashMap containing graph data, or null if no data is available.
     */
    public HashMap<String, HashMap<String, Double>> getGraphData(){
        if(graphData.size() != 0){
            return graphData;
        } else{
            return null;
        }
    }

    /**
     * Resets the simulation by clearing all service points and resetting the engine.
     */
    public void reset() {
        if (isResetting) return;
        isResetting = true;

        // Stop the simulation if it's running
        try {
            // Stop simulation first
            isRunning = false;

            // Reset clock
            Clock.getInstance().setTime(0);

            // Clear event list
            eventList.clear();

            // Clear all service points
            for (ServicePoint sp : servicePoints) {
                sp.clear(); // Clear the service point queues
            }

            // Reset counters and statistics
            totalEUServicedCustomer = 0;
            totalNonEUServicedCustomer = 0;
            totalServicedCustomer = 0;
            averageCheckinServiceTime = 0;
            averageSecurityServiceTime = 0;
            averagePassportControlServiceTime = 0;
            averageEUGateServiceTime = 0;
            averageNonEUGateServiceTime = 0;
            serviceThroughput = 0;
            checkpointUsageRatio = 0;
            securityCheckpointUsageRatio = 0;
            passportControlPointUsageRatio = 0;
            EUGateUsageRatio = 0;
            NonEUGateUsageRatio = 0;

            // Reset the Customer ID counter
            Customer.resetIdCounter(); // Add this line

            // Clear serviced customers list
            servicedCustomers.clear();

            // Reinitialize the arrival process with fresh random generators
            arrivalProcess = new ArrivalProcess(new Negexp(arrivalInterval, 1), eventList, EventType.ARR1);

            // Update UI - first call all the individual methods
            controller.clearLogArea();
            controller.clearVisualisation();
            controller.showLogArea("Simulation reset completed");

            // Update UI - then call the update method
            controller.updateUIAfterReset();
        } catch (Exception e) {
            Trace.out(Trace.Level.ERR, "Error during simulation reset: " + e.getMessage());
        } finally {
            isResetting = false; // Reset the flag
        }
    }

    /**
     * Sets the selected airport for the simulation.
     *
     * @param airport The name of the selected airport.
     */
    public void setSelectedAirport(String airport) {
        this.selectedAirport = airport;
    }

    /**
     * Retrieves the average time spent in the system by all serviced customers.
     *
     * @return A string representing the average time in the system.
     */
    private String getAverageTimeInSystem() {
        double totalTimeInSystem = 0;
        for (Customer customer : servicedCustomers) {
            totalTimeInSystem += customer.getTotalTimeInSystem();
        }

        if (!servicedCustomers.isEmpty()) {
            double averageTimeInSystem = totalTimeInSystem / servicedCustomers.size();
            return String.format("%.2f", averageTimeInSystem);
        } else {
            return "0.00";
        }
    }

}
