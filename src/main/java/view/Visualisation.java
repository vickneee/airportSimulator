package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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
    private static final String CHECK_IN = "Check In";
    private static final String SECURITY_CHECK = "Security Check";
    private static final String PASSPORT_CONTROL = "Passport Control for non-EU flights";
    private static final String EU_GATE = "Boarding Gate for EU flights";
    private static final String NON_EU_GATE = "Boarding Gate for non-EU flights";
    private String[] servicePointNames = {CHECK_IN, SECURITY_CHECK, PASSPORT_CONTROL, EU_GATE, NON_EU_GATE};

    private Map<Integer, String> customerLocations = new HashMap<>();
    // private static final double CUSTOMER_SIZE = 10;
    private final int QUEUE_HEIGHT = 10;
    private final int QUEUE_WIDTH = 100;
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
        for (String servicePointName : servicePointNames) {
            Position position = getLocationPosition(servicePointName);
            drawLocation(servicePointName, position.x, position.y);
        }
    }

    private void drawLocation(String locationName, double x, double y) {
        gc.setFill(Color.BLACK);
        gc.fillText(locationName, x - 20, y + 25);
    }


    private void drawCustomer(int queueLength, double x, double y) {
        // Set color based on queue length
        Color queueColor;
        if (queueLength == 0) {
            queueColor = Color.WHITE;
        } else if (queueLength < 6) {
            queueColor = Color.LIGHTGREEN; // Green for short queues (1-5)
        } else if (queueLength < 26) {
            queueColor = Color.YELLOW; // Yellow for medium queues (6-25)
        } else {
            queueColor = Color.LIGHTCORAL; // Red for long queues (26+)
        }
        gc.setFill(queueColor);
        gc.fillRect(x, y, QUEUE_WIDTH, QUEUE_HEIGHT);
    }

    private void drawQueues() {
        double verticalSpacing = 50;
        double horizontalOffset = -20;
        double secondaryColumnOffset = 220;
        int maxColumns = 4;

        for (int i = 0; i < Math.min(queueLengths.size(), servicePointNames.length); i++) {
            List<Integer> servicePointQueues = queueLengths.get(i);
            double x = 0;
            double y = 0;
            for (int j = 0; j < servicePointQueues.size(); j++) {
                String queueName = "Queue Length: " + servicePointQueues.get(j);
                Position position = getLocationPosition(servicePointNames[i]);

                if (j < maxColumns) {
                    x = position.x + horizontalOffset;
                }else {
                    x = position.x + horizontalOffset + secondaryColumnOffset;
                }
                if(j == 0 || j == maxColumns){
                    y = position.y + verticalSpacing;
                } else{
                    y += 20;
                }

                drawQueue(queueName, x, y);

                int queueLength = servicePointQueues.get(j);
                drawCustomer(queueLength, x + 108, y - 10);
            }
        }
    }

    private void drawQueue(String name, double x, double y) {
        gc.setFill(Color.GRAY);
        gc.fillText(name, x, y);
    }

    private Position getLocationPosition(String location) {
        return switch (location) {
            case ARRIVAL -> new Position(600, 50);
            case CHECK_IN -> new Position(50, 5);
            case SECURITY_CHECK -> new Position(50, 125);
            case PASSPORT_CONTROL -> new Position(50, 245);
            case EU_GATE -> new Position(50, 365);
            case NON_EU_GATE -> new Position(50, 485);
            default -> new Position(600, 50); // Default to arrival
        };
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
    }

}
