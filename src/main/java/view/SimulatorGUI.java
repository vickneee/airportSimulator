package view;

import java.text.DecimalFormat;
import controller.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import simu.framework.Trace;
import simu.framework.Trace.Level;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

import simu.model.Customer; // Import the correct Customer class

import java.util.ArrayList;
import java.util.List;

public class SimulatorGUI extends Application implements ISimulatorUI {

	// Controller object (UI needs)
	private IControllerVtoM controller;

	// UI Components:
	// private TextField time;
    private Spinner<Integer> timeSpinner; // Use Spinner instead of TextField
	private Spinner<Integer> delay;
	private Label results;
	private Label timeLabel;
	private Label delayLabel;
	private Label resultLabel;

	private Button startButton;
	private Button slowButton;
	private Button speedUpButton;
    private Button restartButton;

	private IVisualisation display;

    private Label mainTitle;
    private Label subTitle;

    private List<Customer> customers = new ArrayList<>();

    private TextArea logArea;

    private Label arrivalSliderLabel;
    private Slider arrivalSlider = new Slider(1, 5, 2); // Min: 1, Max: 5, Default: 2

	@Override
	public void start(Stage primaryStage) {
		// UI creation
		try {
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			    @Override
			    public void handle(WindowEvent t) {
			        Platform.exit();
			        System.exit(0);
			    }
			});

			primaryStage.setTitle("Simulator");

            // Initialize mainTitle here
            mainTitle = new Label("Airport Simulator");
            mainTitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 20));

            subTitle = new Label("Parameters:");
            subTitle .setFont(Font.font("Tahoma", FontWeight.BOLD, 16));

			startButton = new Button();
			startButton.setText("Start");
            startButton.setStyle("-fx-background-color: #7bb67d; -fx-text-fill: white; -fx-font-size: 12px;");
			startButton.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	                controller.startSimulation();
	                startButton.setDisable(true);

                    // Example: Add a customer when the simulation starts
                    // Use 0 or 1 for isEUFlight
                    Customer customer = new Customer (0, SimulatorGUI.this);
                    customers.add(customer);
                    display.addCustomer(customer);

	            }
	        });

            // Add a ToggleButton for play/pause
            ToggleButton playPauseButton = new ToggleButton("Play / Pause");
            playPauseButton.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
            playPauseButton.setOnAction(event -> {
                if (playPauseButton.isSelected()) {
                    playPauseButton.setText("Play");
                    controller.pauseSimulation(); // Resume the simulation
                    logEvent("Simulation paused.");
                } else {
                    playPauseButton.setText("Pause");
                    controller.resumeSimulation(); // Pause the simulation
                    logEvent("Simulation resumed.");
                }
            });

            // Add a Restart button
            restartButton = new Button();
            restartButton.setText("Restart");
            restartButton.setOnAction(e -> {
                controller.restartSimulation();
                startButton.setDisable(false);
            });

			slowButton = new Button();
			slowButton.setText("Slow down");
			slowButton.setOnAction(e -> controller.decreaseSpeed());
            slowButton.setOnAction(e -> {
                controller.decreaseSpeed();
                logEvent("Simulation speed decreased."); // Log only when the button is pressed
            });

            speedUpButton = new Button();
			speedUpButton.setText("Speed up");
			speedUpButton.setOnAction(e -> controller.increaseSpeed());
            speedUpButton.setOnAction(e -> {
                controller.increaseSpeed();
                logEvent("Simulation speed increased."); // Log only when the button is pressed
            });

            timeLabel = new Label("Simulation time:");
			timeLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
            if (timeLabel == null) {
                throw new IllegalStateException("timeLabel is not initialized");
            }
            // Create a Spinner for number selection
            timeSpinner = new Spinner<>();
            timeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 1000)); // Min: 1, Max: 1000, Initial: 10
            timeSpinner.setEditable(true); // Allow manual input
            timeSpinner.setPrefWidth(100);
            timeSpinner.setStyle("-fx-font-size: 12px;"); // Set font size for the Spinner

	        delayLabel = new Label("Delay:");
			delayLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
	        if (delayLabel == null) {
                throw new IllegalStateException("delayLabel is not initialized");
            }
            delay = new Spinner<>();
            delay.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 10)); // Min: 1, Max: 1000, Initial: 10
            delay.setEditable(true); // Allow manual input
            delay.setPrefWidth(100);
            delay.setStyle("-fx-font-size: 12px;"); // Set font size for the Spinner

	        resultLabel = new Label("Total time:");
			resultLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
	        results = new Label();
	        results.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
	        results.setPrefWidth(100);

            logArea = new TextArea();
            logArea.setEditable(false); // Make it read-only
            logArea.setWrapText(true);

            arrivalSliderLabel = new Label();
            arrivalSliderLabel.setText("Arrival interval (time units):");
            arrivalSliderLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
            arrivalSlider.setShowTickLabels(true);
            arrivalSlider.setShowTickMarks(true);
            arrivalSlider.setMajorTickUnit(1);
            arrivalSlider.setBlockIncrement(1);

            // Create a VBox layout
            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.setPadding(new Insets(10));
            vBox.setSpacing(10); // Set spacing between elements
            vBox.getChildren().add(mainTitle);

            // Create a VBox layout
            VBox vBoxSubTitle = new VBox();
            vBoxSubTitle.setAlignment(Pos.CENTER);
            vBoxSubTitle.setPadding(new Insets(10));
            vBoxSubTitle.setSpacing(10); // Set spacing between elements
            vBoxSubTitle.getChildren().add(subTitle);

            // Create a hBox layout
	        HBox hBox = new HBox();
	        hBox.setPadding(new Insets(15, 12, 15, 12)); // margins up, right, bottom, left
	        hBox.setSpacing(10);   // Node distance 10 pixels

            VBox logAreaBox = new VBox();

            GridPane grid = new GridPane();
//	        grid.setAlignment(Pos.BOTTOM_CENTER);
	        grid.setVgap(10);
	        grid.setHgap(5);
            grid.setPadding(new Insets(15, 15, 15, 15));
            grid.setStyle("-fx-background-color: #f0f0f0; "
                    + "-fx-border-color: #d3d1d1; "
                    + "-fx-border-width: 1px; "
                    + "-fx-border-radius: 5px; "
                    + "-fx-border-style: solid;");// Set border style

            grid.add(subTitle , 0, 0); // Add subtitle to the grid
            grid.add(arrivalSliderLabel, 0, 2); // Add the arrival slider label to the grid
            grid.add(arrivalSlider, 0, 3); // Add the arrival slider to the grid
            grid.add(timeLabel, 0, 31);   // column, row
	        // grid.add(time, 1, 0);
            grid.add(timeSpinner, 1, 31); // Use the initialized Spinner instead of the uninitialized TextField
	        grid.add(delayLabel, 0, 32);
	        grid.add(delay, 1, 32);
	        grid.add(resultLabel, 0, 33);
	        grid.add(results, 1, 33);
	        grid.add(startButton,0, 34);
            grid.add(playPauseButton, 1, 34);
	        grid.add(speedUpButton, 0, 35);
	        grid.add(slowButton, 1, 35);
            grid.add(restartButton, 0, 36);

	        display = new Visualisation(500,550 , this);

	        // Fill the box:
	        hBox.getChildren().addAll(grid, (Canvas) display);
            logAreaBox.getChildren().addAll(logArea);

            // Create a root BorderPane and set the VBox and HBox
            BorderPane root = new BorderPane();
            root.setTop(vBox); // Place the title at the top
            root.setCenter(hBox); // Place the grid and canvas in the center
            root.setBottom(logAreaBox);

	        Scene scene = new Scene(root, 1000, 800);
	        primaryStage.setScene(scene);
	        primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

    @Override
    public void init(){
        Trace.setTraceLevel(Level.INFO);
        controller = new Controller(this, this);

        if (arrivalSlider == null) {
            throw new IllegalStateException("Arrival slider is not initialized in SimulatorGUI.");
        }
    }

	/* UI interface methods (controller calls) */
	@Override
	public double getTime(){
		return timeSpinner.getValue(); // Use the Spinner value
	}

	@Override
	public long getDelay(){
		return delay.getValue(); // Use the Spinner value
	}

	@Override
	public void setEndingTime(double time) {
		 DecimalFormat formatter = new DecimalFormat("#0.00");
		 this.results.setText(formatter.format(time));
	}

	@Override
	public IVisualisation getVisualisation() {
		 return display;
	}

	/* JavaFX-application (UI) start-up */
	public static void main(String[] args) {
		launch(args);
	}

    public void logEvent(String message) {
        Platform.runLater(() -> logArea.appendText(message + "\n"));
    }

    public void clearLogArea() {
        Platform.runLater(() -> logArea.clear());
    }

    public Slider getArrivalSlider() {
        return arrivalSlider; // Ensure `arrivalSlider` is properly initialized in the GUI
    }
}
