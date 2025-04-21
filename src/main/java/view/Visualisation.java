package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import simu.model.Customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Visualisation extends Canvas implements IVisualisation {
    private GraphicsContext gc;
    private int width, height;
    private SimulatorGUI simulatorGUI; // Reference to SimulatorGUI

    // Define locations as constants
    private static final String ARRIVAL = "Arrival";
    private static final String CHECK_IN = "CheckIn";
    private static final String SECURITY_CHECK = "Security Check";
    private static final String PASSPORT_CONTROL = "Passport Control for non-EU flights";
    private static final String EU_GATE = "Boarding Gate for EU flights";
    private static final String NON_EU_GATE = "Boarding Gate for non-EU flights";

    private Map<Integer, String> customerLocations = new HashMap<>();
    private static final double CUSTOMER_SIZE = 10;
    private List<Integer> queueLengths = new ArrayList<>();

    public Visualisation(int w, int h, SimulatorGUI simulatorGUI) {
        super(w, h);
        this.width = w;
        this.height = h;
        this.simulatorGUI = simulatorGUI;
        gc = this.getGraphicsContext2D();

        // Initialize queue lengths for each service point (adjust size as needed)
        queueLengths = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            queueLengths.add(0);
        }

        clearDisplay();
        drawLocations();
        drawQueues();
    }

    @Override
    public void clearDisplay() {
        gc.setFill(Color.LIGHTBLUE); // Or any background color you prefer
        gc.fillRect(0, 0, width, height);
    }

    private void drawLocations() {
        drawLocation(ARRIVAL, 600, 50);
        drawLocation(CHECK_IN, 50, 25);
        drawLocation(SECURITY_CHECK, 50, 125);
        drawLocation(PASSPORT_CONTROL, 50, 225);
        drawLocation(EU_GATE, 50, 325);
        drawLocation(NON_EU_GATE, 50, 425);
    }

    private void drawLocation(String locationName, double x, double y) {
        gc.setFill(Color.BLACK);
        gc.fillText(locationName, x - 20, y + 25);
    }

    private void drawCustomer(int customerId, String location) {
        Position position = getLocationPosition(location);
        gc.setFill(Color.RED);
        gc.fillOval(position.x - CUSTOMER_SIZE / 2 + 50, position.y - CUSTOMER_SIZE / 2 + 46, CUSTOMER_SIZE, CUSTOMER_SIZE);
    }

    private void drawQueues() {
        double verticalSpacing = 50;
        double horizontalOffset = -20;
        String[] servicePointNames = {CHECK_IN, SECURITY_CHECK, PASSPORT_CONTROL, EU_GATE, NON_EU_GATE};

        for (int i = 0; i < Math.min(queueLengths.size(), servicePointNames.length); i++) {
            String queueName = "Queue: " + queueLengths.get(i);
            Position position = getLocationPosition(servicePointNames[i]);
            double x = position.x + horizontalOffset;
            double y = position.y + verticalSpacing;
            drawQueue(queueName, x, y);
            // Draw the customer in the queue
            for (int j = 0; j < queueLengths.get(i); j++) {
                drawCustomer(j, servicePointNames[i]);
            }
        }
    }

    private void drawQueue(String name, double x, double y) {
        gc.setFill(Color.BLUE);
        gc.fillText(name, x, y);
    }

    private Position getLocationPosition(String location) {
        switch (location) {
            case ARRIVAL:
                return new Position(600, 50);
            case CHECK_IN:
                return new Position(50, 25);
            case SECURITY_CHECK:
                return new Position(50, 125);
            case PASSPORT_CONTROL:
                return new Position(50, 225);
            case EU_GATE:
                return new Position(50, 325);
            case NON_EU_GATE:
                return new Position(50, 425);
            default:
                return new Position(600, 50); // Default to arrival
        }
    }

    private static class Position {
        double x;
        double y;

        public Position(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    @Override
    public void newCustomer() {
        // This method is called by the controller, but we need the Customer object to get its ID.
        // The actual visual creation happens in newCustomer(Customer customer).
    }

    public void newCustomer(Customer customer) {
        customerLocations.put(customer.getId(), ARRIVAL);
        redrawCanvas();
    }

    public void moveCustomer(int customerId, String toLocation) {
        customerLocations.put(customerId, toLocation);
        redrawCanvas();
    }

    @Override
    public void updateQueueLengths(List<Integer> lengths) {
        this.queueLengths = lengths;
        redrawCanvas();
    }

    private void redrawCanvas() {
        clearDisplay();
        drawLocations();
        drawQueues();
        // drawCustomers();
    }

//    private void drawCustomers() {
//        for (Map.Entry<Integer, String> entry : customerLocations.entrySet()) {
//            int customerId = entry.getKey();
//            String location = entry.getValue();
//            drawCustomer(customerId, location);
//        }
//    }

    @Override
    public void update(List<Customer> customers) {
        // This method seems redundant with newCustomer and moveCustomer in this setup.
        // You should likely rely on newCustomer and moveCustomer to handle customer visualization.
        // If you intend to use this, you'll need to define how it updates the customer locations.
        simulatorGUI.logEvent("Warning: Visualisation.update(List<Customer>) called but not fully implemented.");
    }

    @Override
    public void updateQueueLength(int queueIndex, int length) {
        if (queueIndex >= 0 && queueIndex < queueLengths.size()) {
            queueLengths.set(queueIndex, length);
            System.out.println("Updating queue " + queueIndex + " to length: " + length);
            redrawCanvas(); // This is crucial for updating the display
        }
    }

    @Override
    public void addCustomer(Customer customer) {
        // This method is called by the controller with a Customer object.
        // You can use it to add a new customer to the visualisation.
        newCustomer(customer);
    }
}