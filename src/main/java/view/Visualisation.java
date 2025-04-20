package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import simu.model.Customer;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

public class Visualisation extends Canvas implements IVisualisation {
    private GraphicsContext gc;
    private int width, height;
//    double i = 0;
//    double j = 10;
//    private Map<Integer, String> customerLocations = new HashMap<>(); // Store current location of each customer
//    private static final double CUSTOMER_SIZE = 10;

    // Define locations as constants
    private static final String ARRIVAL = "Arrival";
    private static final String SERVICE_POINT_1 = "CheckIn";
    private static final String SERVICE_POINT_2 = "Security Check";
    private static final String SERVICE_POINT_3 = "Passport Control for non-EU flights";
    private static final String SERVICE_POINT_4 = "Boarding Gate for EU flights";
    private static final String SERVICE_POINT_5 = "Boarding Gate for non-EU flights";

    private List<Customer> customers;

    private List<Integer> queueLengths;

    private static final double QUEUE_WIDTH = 65;
    private static final double QUEUE_HEIGHT = 20;

    public Visualisation(int w, int h) {
        super(w, h);
        this.width = w;
        this.height = h;
        gc = this.getGraphicsContext2D();
        this.customers = new ArrayList<>();

        // Initialize queue lengths for each service point
        queueLengths = new ArrayList<>();
        for (int i = 0; i < 5; i++) { // Assuming 5 queues, initialize lengths to 0
            queueLengths.add(0); // Initialize all queue lengths to 0
        }

        clearDisplay();
        drawLocations(); // Draw service points initially
        drawQueues(); // Ensure queues are drawn initially
    }

    @Override
    public void addCustomer(Customer customer) {
        this.customers.add(customer);
        drawCustomer(customer , customers.size() - 1); // Pass the index of the customer
    }

    @Override
    public void clearDisplay() {
//        gc.setFill(Color.WHITE);
//        gc.fillRect(10, 0, width - 10, height);
    }

    private void drawLocations() {
        // Define positions for each location
        // drawLocation(ARRIVAL, 50, 0);
        drawLocation(SERVICE_POINT_1, 50, 25);
        drawLocation(SERVICE_POINT_2, 50, 125);
        drawLocation(SERVICE_POINT_3, 50, 225);
        drawLocation(SERVICE_POINT_4, 50, 325);
        drawLocation(SERVICE_POINT_5, 50, 425);
        // Draw the arrival location
        drawQueues();
    }

//    private void drawLocations() {
//        // drawLocation(ARRIVAL, getLocationPosition(ARRIVAL).x, getLocationPosition(ARRIVAL).y);
//        drawLocation(SERVICE_POINT_1, getLocationPosition(SERVICE_POINT_1).x, getLocationPosition(SERVICE_POINT_1).y);
//        drawLocation(SERVICE_POINT_2, getLocationPosition(SERVICE_POINT_2).x, getLocationPosition(SERVICE_POINT_2).y);
//        drawLocation(SERVICE_POINT_3, getLocationPosition(SERVICE_POINT_3).x, getLocationPosition(SERVICE_POINT_3).y);
//        drawLocation(SERVICE_POINT_4, getLocationPosition(SERVICE_POINT_4).x, getLocationPosition(SERVICE_POINT_4).y);
//        drawLocation(SERVICE_POINT_5, getLocationPosition(SERVICE_POINT_5).x, getLocationPosition(SERVICE_POINT_5).y);
//        drawQueues();
//    }

    private void drawLocation(String locationName, double x, double y) {
        gc.setFill(Color.BLACK);
        gc.fillText(locationName, x - 20, y + 25);
    }

    private void drawCustomer(Customer customer, int queuePosition) {
        String location = customer.getLocation();
        Position position = getLocationPosition(location);
        gc.setFill(Color.BLUE);

        double spacing = 20; // horizontal space between customers in the same queue
        double x = position.x + 100 + spacing * queuePosition; // Start a bit right from the label
        double y = position.y + 10;

        gc.fillOval(x - 5, y - 5, 10, 10);
        gc.setFill(Color.BLACK);
        gc.fillText("Customer " + customer.getId(), x + 10, y);
    }

    private void drawQueues() {
        double verticalSpacing = 50;
        // Define spacing between queues and service points
        double horizontalOffset = -20; // Offset for queue numbers
        // Draw queues and display their lengths
        for (int i = 0; i < queueLengths.size(); i++) {
            String queueName = "Queue " + (i + 1) + ": " + queueLengths.get(i);
            Position position = getLocationPosition(getServicePointName(i));
            double x = position.x + horizontalOffset;
            double y = position.y + verticalSpacing; // Adjusted y-coordinate
            drawQueue(queueName, x, y);
        }
    }

    private void drawQueue(String name, double x, double y) {
        gc.setFill(Color.GREEN);
        gc.fillText(name, x, y);
    }

    private Position getLocationPosition(String location) {
        switch (location) {
            case ARRIVAL:
                return new Position(50, 0);
            case SERVICE_POINT_1:
                return new Position(50, 25);
            case SERVICE_POINT_2:
                return new Position(50, 125);
            case SERVICE_POINT_3:
                return new Position(50, 225);
            case SERVICE_POINT_4:
                return new Position(50, 325);
            case SERVICE_POINT_5:
                return new Position(50, 425);
            default:
                return new Position(50, 0); // Default to arrival
        }
    }

    private String getServicePointName(int index) {
        switch (index) {
            case 0:
                return SERVICE_POINT_1;
            case 1:
                return SERVICE_POINT_2;
            case 2:
                return SERVICE_POINT_3;
            case 3:
                return SERVICE_POINT_4;
            case 4:
                return SERVICE_POINT_5;
            default:
                return SERVICE_POINT_1; // Default case
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

    public void newCustomer() {
    }


    @Override
    public void update(List<Customer> customers) {
        System.out.println("Updating visualization for " + customers.size() + " customers");

        clearDisplay();      // Clear everything
        drawLocations();     // Draw service point names
        drawQueues();        // Draw queue numbers

        // Track how many customers are at each location
        Map<String, Integer> locationCounts = new HashMap<>();

        for (Customer customer : customers) {
            String location = customer.getLocation();
            int positionInQueue = locationCounts.getOrDefault(location, 0);
            System.out.println("Customer " + customer.getId() + " at " + location + " position " + positionInQueue);
            drawCustomer(customer, positionInQueue); // Pass position in line

            locationCounts.put(location, positionInQueue + 1); // Update for next one
        }
    }

    // Method to update queue length
    public void updateQueueLength(int queueIndex, int length) {
        if (queueIndex >= 0 && queueIndex < queueLengths.size()) {
            queueLengths.set(queueIndex, length);
            drawQueues(); // Redraw the queues to reflect the updated lengths
        }
    }
}
