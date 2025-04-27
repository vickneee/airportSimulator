package simu.model;

import controller.IControllerMtoV;
import eduni.distributions.Bernoulli;
import eduni.distributions.DiscreteGenerator;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;
import simu.framework.*;
import database.ServicePointConfig;
import eduni.distributions.Uniform;
import eduni.distributions.ContinuousGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MyEngine extends Engine implements IEngine {
    private ArrayList<ServicePoint> checkinPoints;
    private ArrayList<ServicePoint> securityCheckPoints;
    private ArrayList<ServicePoint> passportControlPoints;
    private ArrayList<ServicePoint> EUGates;
    private ArrayList<ServicePoint> NonEUGates;
    private ArrivalProcess arrivalProcess;
    private DiscreteGenerator euFlightGenerator;
    private int arrivalInterval;
    private double checkpointUsageRatio;
    private double securityCheckpointUsageRatio;
    private double passportControlPointUsageRatio;
    private double EUGateUsageRatio;
    private double NonEUGateUsageRatio;
    private int totalEUServicedCustomer = 0;
    private int totalNonEUServicedCustomer = 0;
    private int totalServicedCustomer = 0;
    private double serviceThroughput;
    private double averageCheckinServiceTime;
    private double averageSecurityServiceTime;
    private double averagePassportControlServiceTime;
    private double averageEUGateServiceTime;
    private double averageNonEUGateServiceTime;
    private double totalWaitingTime = 0;
    private double averageWaitingTime;
    private String selectedAirport;

    private boolean isRunning = true; // Flag to control running state

    public MyEngine(IControllerMtoV controller, int arrivalInterval, int checkinNum, int securityNum, int passportNum, int EUNum, int NonEUNum) { // NEW
        super(controller); // NEW
        this.arrivalInterval = arrivalInterval; // Set the arrival interval
        // Initialize the main list for all service points
        servicePoints = new ArrayList<>();
        // Separate lists for different categories of service points
        checkinPoints = new ArrayList<>();
        securityCheckPoints = new ArrayList<>();
        passportControlPoints = new ArrayList<>();
        EUGates = new ArrayList<>();
        NonEUGates = new ArrayList<>();
        selectedAirport = ""; // NEW

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

    public MyEngine(IControllerMtoV controller, List<ServicePointConfig> configs) {
        super(controller);
        // Initialize the main list for all service points
        servicePoints = new ArrayList<>();
        checkinPoints = new ArrayList<>();
        securityCheckPoints = new ArrayList<>();
        passportControlPoints = new ArrayList<>();
        EUGates = new ArrayList<>();
        NonEUGates = new ArrayList<>();
        arrivalInterval = 5; // Default, can be overridden by config or UI

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
        controller.checkPaused();// Pause the simulation if needed

        Customer a;// Temporary variable to hold the customer being processed.

        switch ((EventType) t.getType()) {
            case ARR1:// Customer arrival event.
                // Find the check-in point with the shortest queue.
                ServicePoint checkinPoint = Collections.min(checkinPoints);
                // Add a new customer to the chosen check-in point queue.
                // Generates a value of either 1 or 0 using the Bernoulli distribution and passes it as a parameter to create a new Customer object
                checkinPoint.addQueue(new Customer(euFlightGenerator.sample(), controller.getSimulatorGUI())); // Kysy Ope APUA getSimulatorGUI
                arrivalProcess.generateNext();
                controller.visualiseCustomer(); // NEW
                updateQueueLengths(); // Update queue lengths after arrival
                break;

            case DEP1: // Check-in completion event.
                // Remove the customer from the current check-in queue.
                a = t.getServicePoint().removeQueue();
                // Retrieves the service time from the associated ServicePoint in the event class
                // and passes it to the Customer class to store it.
                // This step is necessary for calculating the waiting time later.
                a.cumulateServicedTime(t.getServiceTime());
                // Find the security check point with the shortest queue
                ServicePoint securityCheckPoint = Collections.min(securityCheckPoints);
                // Move the customer to the security check queue.
                securityCheckPoint.addQueue(a);
                updateQueueLengths(); // Update queue lengths after departure and arrival
                break;

            case DEP2:// Security check completion event.
                // Remove the customer from the current security check queue.
                a = t.getServicePoint().removeQueue();
                // Retrieves the service time from the associated ServicePoint in the event class
                // and passes it to the Customer class to store it.
                // This step is necessary for calculating the waiting time later.
                a.cumulateServicedTime(t.getServiceTime());
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
                // Retrieves the service time from the associated ServicePoint in the event class
                // and passes it to the Customer class to store it.
                // This step is necessary for calculating the waiting time later.
                a.cumulateServicedTime(t.getServiceTime());
                a.setRemovalTime(Clock.getInstance().getTime());
                totalEUServicedCustomer += 1;
                a.reportResults();
                // When a customer exits the gate, their total waiting time is added to the overall totalWaitingTime.
                // This accumulated value will later be used to calculate the average waiting time.
                totalWaitingTime += a.calculateTotalWaitingTime();
                // a.reportResults(controller.getSimulatorGUI()); // Pass the simulator GUI to the reportResults method
                updateQueueLengths(); // Update queue lengths
                break;

            case DEP4:// Passport control processing event (Non-EU flights).
                // Remove the customer from the passport control queue.
                a = t.getServicePoint().removeQueue();
                // Retrieves the service time from the associated ServicePoint in the event class
                // and passes it to the Customer class to store it.
                // This step is necessary for calculating the waiting time later.
                a.cumulateServicedTime(t.getServiceTime());
                // Find the Non-EU gate with the shortest queue and move the customer there.
                ServicePoint NonEUGate = Collections.min(NonEUGates);
                NonEUGate.addQueue(a);
                updateQueueLengths(); // Update queue lengths
                break;

            case DEP5:// Non-EU gate processing event.
                // Remove the customer from the Non-EU gate queue.
                a = t.getServicePoint().removeQueue();
                // Retrieves the service time from the associated ServicePoint in the event class
                // and passes it to the Customer class to store it.
                // This step is necessary for calculating the waiting time later.
                a.cumulateServicedTime(t.getServiceTime());
                a.setRemovalTime(Clock.getInstance().getTime());
                totalNonEUServicedCustomer += 1;
                a.reportResults();
                // When a customer exits the gate, their total waiting time is added to the overall totalWaitingTime.
                // This accumulated value will later be used to calculate the average waiting time.
                totalWaitingTime += a.calculateTotalWaitingTime();
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

        System.out.println("\nSimulation ended.");
        String results = "\nSimulation ended.\n\n";

        calculateServiceTimesUsageRatio();
        results += "Selected airport: " + selectedAirport + "\n\n";
        results += "Checkin-point usage ratio: " + checkpointUsageRatio + "%\n\n";
        results += "Security check usage ratio: " + securityCheckpointUsageRatio + "%\n\n";
        results += "Passport control usage ratio: " + passportControlPointUsageRatio + "%\n\n";
        results += "EU gate usage ratio: " + EUGateUsageRatio + "%\n\n";
        results += "Non-EU gate usage ratio: " + NonEUGateUsageRatio + "%\n\n";
        System.out.println("Final count of passengers who exited through the EU gate: " + totalEUServicedCustomer);
        System.out.println("Final count of passengers who exited through the Non-EU gate: " + totalNonEUServicedCustomer);
        results += "Final count of passengers who exited through the EU gate: " + totalEUServicedCustomer + "\n\n";
        results += "Final count of passengers who exited through the Non-EU gate: " + totalNonEUServicedCustomer + "\n\n";
        totalServicedCustomer = totalEUServicedCustomer + totalNonEUServicedCustomer;
        System.out.println("Final total count of passengers who exited through gates: " + totalServicedCustomer);
        results += "Final total count of passengers who exited through gates: " + totalServicedCustomer + "\n\n";
        calculateServiceThroughput();
        System.out.println("The service throughput, number of clients serviced related to the time: " + serviceThroughput);
        results += "The service throughput, number of clients serviced related to the time: " + serviceThroughput + "\n\n";
        calculateAverageServiceTimes();
        results += "Checkin-point average service time: " + averageCheckinServiceTime + "\n\n";
        results += "Security check average service time: " + averageSecurityServiceTime + "\n\n";
        results += "Passport control average service time: " + averagePassportControlServiceTime + "\n\n";
        results += "EU gate average service time: " + averageEUGateServiceTime + "\n\n";
        results += "Non-EU gate average service time: " + averageNonEUGateServiceTime + "\n\n";
        calculateAverageWaitingTime();
        results += "The average waiting time: " + averageWaitingTime + "\n\n";

        controller.showResults(results);
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
     *                   *             Represents the likelihood of a "successful" outcome in the Bernoulli trial.
     */
    @Override
    public void setEUFlightPercentage(double percentage) {
        euFlightGenerator = new Bernoulli(percentage);
    }

    /**
     * Calculates the usage ratio for different service points in the airport simulation.
     * This method assigns the calculated usage ratio to corresponding variables.
     */
    private void calculateServiceTimesUsageRatio() {
        checkpointUsageRatio = calculateUsageRatio(checkinPoints, "Checkin-point");
        securityCheckpointUsageRatio = calculateUsageRatio(securityCheckPoints, "Security check");
        passportControlPointUsageRatio = calculateUsageRatio(passportControlPoints, "Passport control");
        EUGateUsageRatio = calculateUsageRatio(EUGates, "EU gate");
        NonEUGateUsageRatio = calculateUsageRatio(NonEUGates, "Non-EU gate");
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
        for (int i = 0; i < servicePointNum; i++) {
            totalServiceTime += sp.get(i).getTotalServiceTime();
        }

        double usageRatio = totalServiceTime / totalSimulationTime * 100;
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
        averageCheckinServiceTime = calculateAverageServiceTime(checkinPoints, "Checkin-point");
        averageSecurityServiceTime = calculateAverageServiceTime(securityCheckPoints, "Security check");
        averagePassportControlServiceTime = calculateAverageServiceTime(passportControlPoints, "Passport control");
        averageEUGateServiceTime = calculateAverageServiceTime(EUGates, "EU gate");
        averageNonEUGateServiceTime = calculateAverageServiceTime(NonEUGates, "Non-EU gate");
    }

    /**
     * Calculates the average service time for a given service point type.
     *
     * @param sp               List of service points.
     * @param servicePointName The name of the service point type.
     * @return The calculated average service time.
     */
    private double calculateAverageServiceTime(ArrayList<ServicePoint> sp, String servicePointName) {
        double servicePointNum = sp.size();
        double totalServiceTime = 0;
        double averageServicedTime = 0;
        for (int i = 0; i < servicePointNum; i++) {
            totalServiceTime += sp.get(i).getTotalServiceTime();
        }

        if (servicePointName == "Checkin-point" || servicePointName == "Security check") {
            if (totalServicedCustomer != 0) {
                System.out.println(servicePointName + "'s average service time: " + totalServiceTime / totalServicedCustomer);
                return totalServiceTime / totalServicedCustomer;
            }
        } else if (servicePointName == "Passport control" || servicePointName == "Non-EU gate") {
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
     * Calculates the average waiting time based on total waiting time and total serviced customers.
     */
    private void calculateAverageWaitingTime() {
        averageWaitingTime = totalWaitingTime / totalServicedCustomer;
        System.out.println("The average waiting time: " + averageWaitingTime);
    }

    /**
     * Sets the selected airport for the simulation.
     *
     * @param airport The name of the selected airport.
     */
    public void setSelectedAirport(String airport) {
        this.selectedAirport = airport;
    }

    private void displayResults() {
        // Generate the results string
        String results = generateResultsString();
        // Call the showResults method in the controller
        controller.showResults(results);
    }

    private String generateResultsString() {
        // Generate the results string based on your simulation data
        StringBuilder sb = new StringBuilder();
        sb.append("Simulation Results:\n");
        sb.append("Average waiting time: ").append(averageWaitingTime).append("\n");
        // Add more results as needed
        return sb.toString();
    }

}
