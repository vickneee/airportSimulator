package view;

import java.text.DecimalFormat;
import controller.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SimulatorGUI extends Application implements ISimulatorUI {

	// Controller object (UI needs)
	private IControllerVtoM controller;

	// UI Components:
    private Spinner<Integer> timeSpinner; // Use Spinner instead of TextField
	private Spinner<Integer> delay;
	private Label results;
	private Label timeLabel;
	private Label delayLabel;
	private Label resultLabel;

	private Button startButton;
	private Button slowButton;
	private Button speedUpButton;
    private Button stopButton;
    private ToggleButton playPauseButton;
    private Button resetButton;

	private IVisualisation display;

    private Label mainTitle;
    private Label subTitle;
    private Label subTitle2;

    private List<Customer> customers = new ArrayList<>();

    private TextArea logArea;
    private TextArea resultArea;

    private Label arrivalSliderLabel;
    private Slider arrivalSlider = new Slider(1, 10, 5); // Min: 1, Max: 10, Default: 5

    private Label euProcentSliderLabel;
    private Slider euProcentSlider = new Slider(1, 100, 50); // Min: 1, Max: 99, Default: 50

    private Label airportChoiceLabel; // Label for the airport choice
    private ChoiceBox<String> airportChoiceBox; // ChoiceBox for selecting the airport

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

            mainTitle = new Label("Airport Simulator");
            mainTitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 20));

            subTitle = new Label("Parameters");
            subTitle .setFont(Font.font("Tahoma", FontWeight.BOLD, 18));

            subTitle2 = new Label("Results");
            subTitle2 .setFont(Font.font("Tahoma", FontWeight.BOLD, 18));

			slowButton = new Button();
			slowButton.setText("Slow down");
            slowButton.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
			slowButton.setOnAction(e -> controller.decreaseSpeed());

            speedUpButton = new Button();
			speedUpButton.setText("Speed up");
            speedUpButton.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
            speedUpButton.setOnAction(e -> controller.increaseSpeed());

            startButton = new Button();
            startButton.setText("Start");
            startButton.setStyle("-fx-background-color: #7bb67d; -fx-text-fill: white; -fx-font-size: 12px;");

            playPauseButton = new ToggleButton("Play / Pause");
            playPauseButton.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));

            stopButton = new Button();
            stopButton.setText("Stop");
            stopButton.setStyle("-fx-background-color: rgba(240,93,93,0.8); -fx-text-fill: white; -fx-font-size: 12px;");

            resetButton = new Button();
            resetButton.setText("Reset");
            resetButton.setStyle("-fx-font-size: 12px;");

            timeLabel = new Label("Simulation time:");
			timeLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
            if (timeLabel == null) {
                throw new IllegalStateException("timeLabel is not initialized");
            }

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
            logArea.setPrefWidth(450); // Set a preferred width for the log area

            resultArea = new TextArea();
            resultArea.setEditable(false); // Make it read-only
            resultArea.setWrapText(true);
            resultArea.setPrefWidth(450); // Set a preferred height for the result area
            resultArea.setPrefHeight(400); // Set a preferred height for the result area

            arrivalSliderLabel = new Label();
            arrivalSliderLabel.setText("Arrival interval (time units):");
            arrivalSliderLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
            arrivalSlider.setShowTickLabels(true);
            arrivalSlider.setShowTickMarks(true);
            arrivalSlider.setMajorTickUnit(1);
            arrivalSlider.setBlockIncrement(1);

            euProcentSliderLabel = new Label();
            euProcentSliderLabel.setText("Amount of EU Passengers (%):");
            euProcentSliderLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
            euProcentSlider.setMin(0);
            euProcentSlider.setMax(100);
            euProcentSlider.setValue(50); // Default value
            euProcentSlider.setStyle("-fx-font-size: 14px;"); // Set font size for the Slider
            euProcentSlider.setShowTickLabels(true);
            euProcentSlider.setShowTickMarks(true);
            euProcentSlider.setMajorTickUnit(10);
            euProcentSlider.setBlockIncrement(10);

            // Initialize the airport choice components
            airportChoiceLabel = new Label("Choose Airport:"); // Label for the dropdown
            airportChoiceLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
            airportChoiceBox = new ChoiceBox<>();
            ObservableList<String> airportOptions = FXCollections.observableArrayList("Helsinki Airport", "Oslo Airport", "Stockholm Airport"); // Replace with actual airport names
            airportChoiceBox.setItems(airportOptions);
            airportChoiceBox.setValue("Helsinki Airport"); // Set a default value
            airportChoiceBox.setStyle("-fx-font-size: 12px;");

            // Event Handlers
            slowButton.setOnAction(e -> {
                controller.decreaseSpeed();
                logEvent("Simulation speed decreased.");
            });

            speedUpButton.setOnAction(e -> {
                controller.increaseSpeed();
                logEvent("Simulation speed increased.");
            });

            startButton.setOnAction(event -> {
                controller.startSimulation();
                startButton.setDisable(true);
            });

            playPauseButton.setOnAction(event -> {
                if (playPauseButton.isSelected()) {
                    playPauseButton.setText("Play");
                    controller.pauseSimulation();
                    logEvent("Simulation paused.");
                } else {
                    playPauseButton.setText("Pause");
                    controller.resumeSimulation();
                    logEvent("Simulation resumed.");
                }
            });

            stopButton.setOnAction(e -> {
                controller.stopSimulation();
            });

            resetButton.setOnAction(e -> {
                controller.resetSimulation();
                startButton.setDisable(false); // Enable the button after resetting
            });

            // Layouts
            HBox canvas1 = new HBox();
            canvas1.setPadding(new Insets(0, 15, 15, 15)); // margins up, right, bottom, left
            canvas1.setSpacing(20); // Node distance 10 pixels

            VBox titleBox = new VBox();
            titleBox.setAlignment(Pos.CENTER);
            titleBox.setPadding(new Insets(10));
            titleBox.setSpacing(10); // Set spacing between elements
            titleBox.getChildren().add(mainTitle);

            VBox parametersBox = new VBox();
            parametersBox.setAlignment(Pos.CENTER);
            parametersBox.setPadding(new Insets(10));
            parametersBox.setSpacing(10); // Set spacing between elements
            parametersBox.getChildren().add(subTitle);

            GridPane grid = new GridPane();
	        grid.setVgap(10);
	        grid.setHgap(5);
            grid.setPadding(new Insets(15, 15, 15, 15));
            grid.setStyle("-fx-background-color: #f0f0f0; "
                    + "-fx-border-color: #d3d1d1; "
                    + "-fx-border-width: 1px; "
                    + "-fx-border-radius: 5px; "
                    + "-fx-border-style: solid;");

            grid.add(subTitle , 0, 0); // Add subtitle to the grid
            grid.add(new Separator(), 0, 1, 2, 1); // Add a separator line
            grid.add(airportChoiceLabel, 0, 3); // Add the airport choice label
            grid.add(airportChoiceBox, 0, 4); // Add the airport choice box
            grid.add(new Separator(), 0, 6, 2, 1); // Add a separator line
            grid.add(arrivalSliderLabel, 0, 8); // Add the arrival slider label to the grid
            grid.add(arrivalSlider, 0, 9); // Add the arrival slider to the grid
            grid.add(euProcentSliderLabel, 0, 11); // Add the arrival slider label to the grid
            grid.add(euProcentSlider, 0, 12); // Add the arrival slider to the grid
            grid.add(new Separator(), 0, 14, 2, 1); // Add a separator line
            grid.add(timeLabel, 0, 16);   // column, row
            grid.add(timeSpinner, 1, 16);
            grid.add(delayLabel, 0, 18);
            grid.add(delay, 1, 18);
            grid.add(slowButton, 0, 20);
	        grid.add(speedUpButton, 1, 20);
            grid.add(startButton,0, 22);
            grid.add(playPauseButton, 1, 22);
            grid.add(stopButton, 0, 24);
            grid.add(resetButton, 1, 24);
            grid.add(new Separator(), 0, 26, 2, 1); // Add a separator line
            grid.add(resultLabel, 0, 28);
            grid.add(results, 1, 28);

            VBox resultsBox = new VBox();
            resultsBox.setSpacing(10); // Node distance 10 pixels
            resultsBox.getChildren().add(subTitle2);

            GridPane grid2 = new GridPane();
            grid2.setVgap(10);
            grid2.setHgap(5);
            grid2.setPadding(new Insets(15, 15, 15, 15));
            grid2.setStyle("-fx-background-color: #f0f0f0; "
                    + "-fx-border-color: #d3d1d1; "
                    + "-fx-border-width: 1px; "
                    + "-fx-border-radius: 5px; "
                    + "-fx-border-style: solid;");

            grid2.add(subTitle2 , 0, 0); // Add subtitle to the grid
            grid2.add(new Separator(), 0, 1, 2, 1); // Add a separator line
            grid2.add(resultArea, 0, 3); // Add the result area to the grid

            VBox logAreaBox = new VBox();
            logAreaBox.setPadding(new Insets(15, 15, 15, 15)); // margins up, right, bottom, left
            logAreaBox.setSpacing(10); // Node distance 10 pixels
            logAreaBox.setAlignment(Pos.CENTER);
            logAreaBox.setPrefHeight(210);
            logAreaBox.setPrefWidth(450);
            logAreaBox.setStyle("-fx-background-color: #eae9e9; "
                    + "-fx-prompt-text-fill: #d3d1d1; "
                    + "-fx-border-color: #d3d1d1; "
                    + "-fx-border-width: 1px; "
                    + "-fx-border-radius: 5px; "
                    + "-fx-border-style: solid;");

//            HBox footer = new HBox();
//            footer.setPadding(new Insets(15, 15, 15, 15)); // margins up, right, bottom, left
//            footer.setSpacing(10); // Node distance 10 pixels
//            footer.setAlignment(Pos.CENTER);
//            footer.setStyle("-fx-background-color: #833b3b; "
//                    + "-fx-border-color: #d3d1d1; "
//                    + "-fx-border-width: 1px; "
//                    + "-fx-border-radius: 5px; "
//                    + "-fx-border-style: solid;");

            display = new Visualisation(500,620 , this);

	        // Fill the box:
	        canvas1.getChildren().addAll(grid, (Canvas) display, resultsBox);
            logAreaBox.getChildren().addAll(logArea);
            resultsBox.getChildren().addAll(grid2);

            // Create a root BorderPane and set the VBox and HBox
            BorderPane root = new BorderPane();
            root.setTop(titleBox); // Place the title at the top
            root.setCenter(canvas1); // Place the grid and canvas in the center
            root.setBottom(logAreaBox); // Place the log area on the left
            // root.setBottom(footer);

	        Scene scene = new Scene(root, 1370, 890);
	        primaryStage.setScene(scene);
	        primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * Initializes the GUI components and sets the trace level.
     * This method is called when the application is launched.
     */
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


    public void logEvent(String message) {
        Platform.runLater(() -> logArea.appendText(message + "\n"));
    }

    public void setResultsText(String results) {
        resultArea.setText(results);
    }

    public TextArea getResultArea() {
        return resultArea;
    }

    public void clearLogArea() {
        Platform.runLater(() -> logArea.clear());
    }

    public Slider getArrivalSlider() {
        return arrivalSlider; // Ensure `arrivalSlider` is properly initialized in the GUI
    }

    public Slider getEUFlightPercentageSlider() {
        return euProcentSlider; // Ensure `euProcentSlider` is properly initialized in the GUI
    }

    public ChoiceBox<String> getAirportChoiceBox() { return airportChoiceBox; }

    public SimulatorGUI() {
        // Initialize resultsArea
        resultArea = new TextArea();
    }

    /* JavaFX-application (UI) start-up */
    public static void main(String[] args) {
        launch(args);
    }
}
