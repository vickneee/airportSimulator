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
    private SimulatorGUI simulatorGUI; // Reference to SimulatorGUI // Kysy Opelta APUA

    // Define locations as constants
    private static final String ARRIVAL = "Arrival";
    private static final String CHECK_IN = "CheckIn";
    private static final String SECURITY_CHECK = "Security Check";
    private static final String PASSPORT_CONTROL = "Passport Control for non-EU flights";
    private static final String EU_GATE = "Boarding Gate for EU flights";
    private static final String NON_EU_GATE = "Boarding Gate for non-EU flights";
    private String[] servicePointNames = {CHECK_IN, SECURITY_CHECK, PASSPORT_CONTROL, EU_GATE, NON_EU_GATE};

    private Map<Integer, String> customerLocations = new HashMap<>();
    private static final double CUSTOMER_SIZE = 10;
    private final int QUEUE_HEIGHT=10;
    private final int QUEUE_WIDTH=100;
    private List<List<Integer>> queueLengths = new ArrayList<>();

    public Visualisation(int w, int h, SimulatorGUI simulatorGUI) { // Kysy Opelta APUA
        super(w, h);
        this.width = w;
        this.height = h;
        this.simulatorGUI = simulatorGUI; // Kysy Opelta APUA
        gc = this.getGraphicsContext2D();

        clearDisplay();
        drawLocations();
    }

    @Override
    public void clearDisplay() {
        gc.setFill(Color.WHITE); // Or any background color you prefer
        gc.fillRect(0, 0, width, height);
    }

    private void drawLocations() {
        // drawLocation(ARRIVAL, 600, 50);
        for (int i=0; i < servicePointNames.length; i++) {
            Position position = getLocationPosition(servicePointNames[i]);
            drawLocation(servicePointNames[i], position.x, position.y);
        }

        /*drawLocation(CHECK_IN, 50, 25);
        drawLocation(SECURITY_CHECK, 50, 125);
        drawLocation(PASSPORT_CONTROL, 50, 225);
        drawLocation(EU_GATE, 50, 325);
        drawLocation(NON_EU_GATE, 50, 425);*/
    }

    private void drawLocation(String locationName, double x, double y) {
        gc.setFill(Color.BLACK);
        gc.fillText(locationName, x - 20, y + 25);
    }

    private void drawCustomer(String location, boolean isQueueShort, double x, double y) {
        // Set color based on queue length
        if (isQueueShort) {
            gc.setFill(Color.LIGHTGREEN); // Green for short queues
        } else {
            gc.setFill(Color.LIGHTCORAL); // Red for long queues
        }
        gc.fillRect(x, y, QUEUE_WIDTH, QUEUE_HEIGHT);
    }

    private void drawQueues() {
        double verticalSpacing = 50;
        double horizontalOffset = -20;
        double secondaryColumnOffset = 220;
        int maxColumns = 4;
        //String[] servicePointNames = {CHECK_IN, SECURITY_CHECK, PASSPORT_CONTROL, EU_GATE, NON_EU_GATE};

        for (int i = 0; i < Math.min(queueLengths.size(), servicePointNames.length); i++) {
            List<Integer> servicePointQueues = queueLengths.get(i);
            double x=0;
            double y=0;
            for (int j = 0; j < servicePointQueues.size(); j++) {
                String queueName = "Queue Length: " + servicePointQueues.get(j);
                Position position = getLocationPosition(servicePointNames[i]);

                if (j < maxColumns) {
                    x = position.x + horizontalOffset;
                }else {
                    x = position.x + horizontalOffset + secondaryColumnOffset;
                }
                if(j==0 || j==maxColumns){
                    y = position.y + verticalSpacing;
                } else{
                    y += 20;
                }

                drawQueue(queueName, x, y);

                boolean isQueueShort = servicePointQueues.get(j) < 5;
                drawCustomer(servicePointNames[i], isQueueShort, x+100, y-10);
            }
        }
    }

    private void drawQueue(String name, double x, double y) {
        gc.setFill(Color.GRAY);
        gc.fillText(name, x, y);
    }

    private Position getLocationPosition(String location) {
        switch (location) {
            case ARRIVAL:
                return new Position(600, 50);
            case CHECK_IN:
                return new Position(50, 5);
            case SECURITY_CHECK:
                return new Position(50, 125);
            case PASSPORT_CONTROL:
                return new Position(50, 245);
            case EU_GATE:
                return new Position(50, 365);
            case NON_EU_GATE:
                return new Position(50, 485);
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

    @Override
    public void updateQueueLengths(List<List<Integer>> lengths) {
        this.queueLengths = lengths;
        redrawCanvas();
    }

    private void redrawCanvas() {
        clearDisplay();
        drawLocations();
        drawQueues();
        // drawCustomers();
    }

}