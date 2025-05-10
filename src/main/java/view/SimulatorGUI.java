package view;

import java.text.DecimalFormat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

import controller.*;

import simu.framework.Trace;
import simu.framework.Trace.Level;

import database.Airport;
import database.ServicePointConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SimulatorGUI class is responsible for creating the graphical user interface (GUI)
 * for the airport simulation. It extends the JavaFX Application class and implements
 * the ISimulatorGUI interface.
 */
public class SimulatorGUI extends Application implements ISimulatorGUI {

	// Controller object (UI needs)
	private IControllerVtoM controller;

    // Labels + Spinners
	private Label timeLabel;
    private Spinner<Integer> timeSpinner; // Use Spinner instead of TextField
	private Label delayLabel;
    private Spinner<Integer> delay;
	private Label totalTimeLabel;
    public Label totalTime;

    // UI buttons
	private Button startButton;
	private Button slowButton;
	private Button speedUpButton;
    private ToggleButton playPauseButton;
    private Button stopButton;
    private Button resetButton;
    private Button externalViewButton; // New button for showing graphs

    // UI display components
	private IVisualisation display;

    // Title and subtitles
    private Label mainTitle;
    private Label subTitle;
    private Label subTitle2;

    // Log and result areas
    private TextArea logArea;
    private TextArea resultArea;

    // Airport selection
    private ComboBox<Airport> airportComboBox;
    private List<ServicePointConfig> currentConfigs = new ArrayList<>();

    // Arrival interval slider
    private Label arrivalSliderLabel;
    private Slider arrivalSlider = new Slider(1, 10, 5); // Min: 1, Max: 10, Default: 5

    // EU flight percentage slider
    private Slider euPercentSlider = new Slider(0, 100, 30);
    private Label euPercentSliderLabel = new Label("Amount of EU Passengers (%):");

    // Data for creating graphs
    private HashMap<String, Double> graphDataUsageRatio;
    private HashMap<String, Double> graphDataAverageTimes;

    /**
     * The start method is the main entry point for JavaFX applications.
     * It sets up the primary stage and initializes the GUI components.
     *
     * @param primaryStage The primary stage for this application.
     */
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

            // External view button
            externalViewButton = new Button("Graphical View");
            externalViewButton.setDisable(true);
            externalViewButton.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
            externalViewButton.setStyle("-fx-background-color: #7bb67d; -fx-text-fill: white; -fx-font-size: 12px;");
            externalViewButton.setOnAction(e -> {
                controller.getGraphData();
            }); // Pass the data to the external view

            timeLabel = new Label("Simulation time (time units):");
			timeLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
            if (timeLabel == null) {
                throw new IllegalStateException("timeLabel is not initialized");
            }
            timeSpinner = new Spinner<>();
            timeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 1000)); // Min: 1, Max: 10000, Initial: 1000
            timeSpinner.setEditable(true); // Allow manual input
            timeSpinner.setPrefWidth(100);
            timeSpinner.setStyle("-fx-font-size: 12px;"); // Set font size for the Spinner

	        delayLabel = new Label("Delay (time units):");
			delayLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
	        if (delayLabel == null) {
                throw new IllegalStateException("delayLabel is not initialized");
            }
            delay = new Spinner<>();
            delay.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 10)); // Min: 1, Max: 1000, Initial: 10
            delay.setEditable(true); // Allow manual input
            delay.setPrefWidth(100);
            delay.setStyle("-fx-font-size: 12px;"); // Set font size for the Spinner

	        totalTimeLabel = new Label("Total time:");
			totalTimeLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
	        totalTime = new Label();
	        totalTime.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
	        totalTime.setPrefWidth(100);

            logArea = new TextArea();
            logArea.setEditable(false); // Make it read-only
            logArea.setWrapText(true);
            logArea.setPrefWidth(450); // Set a preferred width for the log area

            resultArea = new TextArea();
            resultArea.setEditable(false); // Make it read-only
            resultArea.setWrapText(true);
            resultArea.setPrefWidth(450); // Set a preferred height for the result area
            resultArea.setPrefHeight(310); // Set a preferred height for the result area

            arrivalSliderLabel = new Label();
            arrivalSliderLabel.setText("Arrival interval (time units):");
            arrivalSliderLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
            arrivalSlider.setShowTickLabels(true);
            arrivalSlider.setShowTickMarks(true);
            arrivalSlider.setMajorTickUnit(1);
            arrivalSlider.setBlockIncrement(1);

            euPercentSliderLabel = new Label();
            euPercentSliderLabel.setText("Amount of EU Passengers (%):");
            euPercentSliderLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
            euPercentSlider.setMin(0);
            euPercentSlider.setMax(100);
            euPercentSlider.setValue(30); // Default value
            euPercentSlider.setStyle("-fx-font-size: 14px;"); // Set font size for the Slider
            euPercentSlider.setShowTickLabels(true);
            euPercentSlider.setShowTickMarks(true);
            euPercentSlider.setMajorTickUnit(20);
            euPercentSlider.setBlockIncrement(10);

            // Add airport ComboBox
            airportComboBox = new ComboBox<>();
            airportComboBox.setPromptText("Choose Airport");
            airportComboBox.setMinWidth(200);
            // Populate ComboBox from database
            List<Airport> airports = controller.getAllAirports();
            airportComboBox.getItems().addAll(airports);
            // Show airport name in ComboBox
            airportComboBox.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Airport item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getName());
                }
            });
            airportComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Airport item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getName());
                }
            });

            // Listen for airport selection changes
            airportComboBox.setOnAction(event -> {
                Airport selectedAirport = airportComboBox.getValue();
                if (selectedAirport != null) {
                    // Fetch configs from DB
                    currentConfigs = controller.getConfigsByAirportId(selectedAirport.getId());
                    // Pass configs to controller (add a method if needed)
                    controller.setServicePointConfigs(currentConfigs);
                    logEvent("Loaded service point configs for: " + selectedAirport.getName());
                    // Print service point summary to the GUI log area
                    printServicePointSummaryToLog(currentConfigs);
                }
            });

            // Event Handlers
            slowButton.setOnAction(e -> {
                controller.decreaseSpeed();
                logEvent("\nSimulation speed decreased.");
            });

            speedUpButton.setOnAction(e -> {
                controller.increaseSpeed();
                logEvent("\nSimulation speed increased.");
            });

            startButton.setOnAction(event -> {
                controller.startSimulation();
                airportComboBox.setDisable(true); // Disable airport selection after starting
                arrivalSlider.setDisable(false); // Enable arrival slider after starting
                euPercentSlider.setDisable(false); // Enable EU flight percentage slider after starting
                timeSpinner.setDisable(true); // Disable time spinner after starting
                delay.setDisable(true); // Disable delay spinner after starting
                slowButton.setDisable(false); // Enable the slow-down button after starting
                speedUpButton.setDisable(false); // Enable the speed-up button after starting
                startButton.setDisable(true); // Disable the button after starting
                playPauseButton.setDisable(false); // Enable the play/pause button after starting
                resetButton.setDisable(true);
                externalViewButton.setDisable(true);// Disable the externalViewButton after starting
            });

            playPauseButton.setOnAction(event -> {
                if (playPauseButton.isSelected()) {
                    playPauseButton.setText("Play");
                    controller.pauseSimulation();
                    logEvent("\nSimulation paused.");
                } else {
                    playPauseButton.setText("Pause");
                    controller.resumeSimulation();
                    logEvent("\nSimulation resumed.");
                }
            });

            stopButton.setOnAction(e -> {
                controller.stopSimulation();
            });

            resetButton.setOnAction(e -> {
                controller.resetSimulation();
            });

            // Set initial states of the controls
            airportComboBox.setDisable(false);
            arrivalSlider.setDisable(false);
            euPercentSlider.setDisable(false);
            timeSpinner.setDisable(false);
            delay.setDisable(false);
            slowButton.setDisable(true);
            speedUpButton.setDisable(true);
            startButton.setDisable(false);
            playPauseButton.setDisable(true);
            resetButton.setDisable(true);
            externalViewButton.setDisable(true); // Initially disabled

            // Layouts
            HBox canvas1 = new HBox();
            canvas1.setPadding(new Insets(0, 15, 15, 15)); // margins up, right, bottom, left
            canvas1.setSpacing(20); // Node distance 10 pixels

            VBox titleBox = new VBox();
            titleBox.setAlignment(Pos.CENTER);
            titleBox.setPadding(new Insets(20));
            titleBox.setSpacing(10); // Set spacing between elements
            titleBox.getChildren().add(mainTitle);

            VBox parametersBox = new VBox();
            parametersBox.setSpacing(10); // Set spacing between elements
            parametersBox.getChildren().add(subTitle);
            parametersBox.setPrefHeight(620);
            parametersBox.setMaxHeight(620);
            parametersBox.setPrefWidth(450);
            parametersBox.setMinWidth(450);

            GridPane grid = new GridPane();
	        grid.setVgap(10);
	        grid.setHgap(5);
            grid.setPadding(new Insets(15));
            grid.setMinWidth(320); // Set a preferred height for the grid
            grid.setMaxHeight(620); // Set a preferred height for the grid
            grid.setStyle("-fx-background-color: #f0f0f0; "
                    + "-fx-border-color: #d3d1d1; "
                    + "-fx-border-width: 1px; "
                    + "-fx-border-radius: 5px; "
                    + "-fx-border-style: solid;");

            grid.add(subTitle , 0, 0); // Add subtitle to the grid
            grid.add(new Separator(), 0, 1, 2, 1); // Add a separator line
            grid.add(new Label("Airports:"), 0, 3);
            grid.add(airportComboBox, 0, 4);
            grid.add(new Separator(), 0, 6, 2, 1); // Add a separator line
            grid.add(arrivalSliderLabel, 0, 8); // Add the arrival slider label to the grid
            grid.add(arrivalSlider, 0, 9); // Add the arrival slider to the grid
            grid.add(euPercentSliderLabel, 0, 11); // Add the EU percent slider label to the grid
            grid.add(euPercentSlider, 0, 12); // Add the EU percent slider to the grid
            grid.add(new Separator(), 0, 14, 2, 1); // Add a separator line
            grid.add(timeLabel, 0, 16);   // column, row
            grid.add(timeSpinner, 1, 16);
            grid.add(delayLabel, 0, 18);
            grid.add(delay, 1, 18);
            grid.add(slowButton, 0, 20);
	        grid.add(speedUpButton, 1, 20);
            grid.add(startButton,0, 22);
            grid.add(playPauseButton, 1, 22);
            // grid.add(stopButton, 0, 24);
            grid.add(resetButton, 0, 24);
            grid.add(new Separator(), 0, 26, 2, 1); // Add a separator line
            grid.add(totalTimeLabel, 0, 28);
            grid.add(totalTime, 1, 28);

            VBox resultsBox = new VBox();
            resultsBox.setSpacing(10); // Node distance 10 pixels
            resultsBox.getChildren().add(subTitle2);

            HBox resultsTitleBox = new HBox(10);
            resultsTitleBox.setAlignment(Pos.CENTER_LEFT);
            resultsTitleBox.getChildren().addAll(subTitle2, externalViewButton);

            resultsBox.setPrefHeight(630);
            resultsBox.setMaxHeight(630);
            resultsBox.setPrefWidth(485);
            resultsBox.setMinWidth(485);

            GridPane grid2 = new GridPane();
            grid2.setVgap(10);
            grid2.setHgap(5);
            grid2.setPadding(new Insets(15));
            grid2.setStyle("-fx-background-color: #f0f0f0; "
                    + "-fx-border-color: #d3d1d1; "
                    + "-fx-border-width: 1px; "
                    + "-fx-border-radius: 5px; "
                    + "-fx-border-style: solid;");

            grid2.add(resultsTitleBox, 0, 0); // Add subtitle to the grid
            grid2.add(new Separator(), 0, 1, 2, 1); // Add a separator line
            grid2.add(resultArea, 0, 3); // Add the result area to the grid
            grid2.add(new Separator(), 0, 5, 2, 1); // Add a separator line
            grid2.add(logArea, 0, 7); // Add the log area to the grid

            HBox footer = new HBox();
            footer.setPadding(new Insets ((15))); // margins up, right, bottom, left
            footer.setSpacing(10); // Node distance 10 pixels
            footer.setAlignment(Pos.CENTER);
            footer.setPrefHeight(40);
            footer.setMaxHeight(40);

            display = new Visualisation(500,620);

            // Add a container for the visualization canvas with rounded corners
            StackPane displayContainer = new StackPane();
            displayContainer.getChildren().add((Canvas)display);
            displayContainer.setPrefHeight(630);
            displayContainer.setMaxHeight(630);
            displayContainer.setPrefWidth(500);
            displayContainer.setMinWidth(500);
            displayContainer.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-border-color: #d3d1d1; " +
                            "-fx-border-width: 1px; " +
                            "-fx-border-radius: 5px; " +
                            "-fx-background-radius: 5px; " +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);"
            );

	        // Fill the box:
	        canvas1.getChildren().addAll(grid, displayContainer, resultsBox);
            resultsBox.getChildren().addAll(grid2);

            // Create a root BorderPane and set the VBox and HBox
            BorderPane root = new BorderPane();
            root.setTop(titleBox); // Place the title at the top
            root.setCenter(canvas1); // Place the grid and canvas in the center
            root.setBottom(footer); // Place the footer at the bottom

	        Scene scene = new Scene(root, 1400, 750);
	        primaryStage.setScene(scene);
	        primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * Initializes the GUI components and sets the trace level.
     * This method is called when the application is launched.
     * It sets up the controller and initializes the GUI components.
     */
    @Override
    public void init(){
        Trace.setTraceLevel(Level.INFO);
        controller = new Controller(this);
        controller.setSimulatorGUI(this);

        if (arrivalSlider == null) {
            throw new IllegalStateException("Arrival slider is not initialized in SimulatorGUI.");
        }

        if (euPercentSlider == null) {
            throw new IllegalStateException("EU flight percentage slider is not initialized in SimulatorGUI.");
        }
    }

    /**
     * GetTime method returns the current time value from the time spinner.
     * This method is called by the controller to get the time value.
     *
     * @return The current time value from the time spinner.
     */
	/* UI interface methods (controller calls) */
	@Override
	public double getTime(){
		return timeSpinner.getValue(); // Use the Spinner value
	}

    /**
     * GetDelay method returns the current delay value from the delay spinner.
     * This method is called by the controller to get the delay value.
     *
     * @return The current delay value from the delay spinner.
     */
	@Override
	public long getDelay(){
		return delay.getValue(); // Use the Spinner value
	}

    /**
     * SetEndingTime method sets the total time label to the given time.
     * This method is called by the controller to update the total time.
     *
     * @param time The total time to be set.
     */
	@Override
	public void setEndingTime(double time) {
		 DecimalFormat formatter = new DecimalFormat("#0.00");
		 this.totalTime.setText(formatter.format(time));
	}

    /**
     * GetVisualisation method returns the visualization component of the GUI.
     * This method is called by the controller to get the visualization.
     *
     * @return The visualization component of the GUI.
     */
	@Override
	public IVisualisation getVisualisation() {
		 return display;
	}

    /**
     * LogEvent method logs the given message to the log area.
     * This method is called by the controller to log events.
     *
     * @param message The message to be logged.
     */
    public void logEvent(String message) {
        Platform.runLater(() -> logArea.appendText(message + "\n"));
    }

    /**
     * SetResultsText method sets the result text in the result area.
     * This method is called by the controller to update the results.
     *
     * @param results The results text to be set.
     */
    public void setResultsText(String results) {
        resultArea.setText(results);
    }

    /**
     * GetLogArea method returns the log area.
     * This method is called by the controller to get the log area.
     *
     * @return The log area.
     */
    public TextArea getResultArea() {
        return resultArea;
    }

    /**
     * ClearLogArea method clears the log area.
     * This method is called by the controller to clear the log.
     *
     * Use Platform.runLater to ensure that the UI is updated on the JavaFX Application Thread.
     */
    public void clearLogArea() {
        Platform.runLater(() -> logArea.clear());
    }

    /**
     * ClearResultsArea method clears the result area.
     * This method is called by the controller to clear the results.
     *
     * Use Platform.runLater to ensure that the UI is updated on the JavaFX Application Thread.
     */
    public void clearResultsArea() {
        Platform.runLater(() -> resultArea.clear());
    }

    /**
     * GetArrivalSlider method returns the arrival slider.
     * This method is called by the controller to get the arrival slider.
     *
     * @return The arrival slider.
     */
    public Slider getArrivalSlider() {
        return arrivalSlider;
    }

    /**
     * GetCurrentConfigs method returns the current service point configurations.
     * This method is called by the controller to get the current configurations.
     *
     * @return The current service point configurations.
     */
    // Add a getter for the selected configs if needed
    public List<ServicePointConfig> getCurrentConfigs() {
        return currentConfigs;
    }

    /**
     * GetEUFlightPercentageSlider method returns the EU flight percentage slider.
     * This method is called by the controller to get the EU flight percentage slider.
     *
     * @return The EU flight percentage slider.
     */
    public Slider getEUFlightPercentageSlider() {
        return euPercentSlider; // Ensure `euPercentSlider` is properly initialized in the GUI
    }

    /**
     * UpdateServicePointSummaryLabel method updates the service point summary label.
     * This method is called by the controller to update the service point summary.
     *
     * @param configs The list of service point configurations.
     */
    // Removed servicePointSummaryLabel and UI label update. Only print to console.
    private void updateServicePointSummaryLabel(List<ServicePointConfig> configs) {
        if (configs == null || configs.isEmpty()) {
            System.out.println("No service point configs found for this airport.");
            return;
        }
        StringBuilder sb = new StringBuilder("Service points for selected airport:\n");
        for (ServicePointConfig config : configs) {
            sb.append(config.getPointType())
              .append(": ")
              .append(config.getNumberOfServers())
              .append("\n");
        }
        System.out.println(sb.toString());
    }

    /**
     * PrintServicePointSummaryToLog method prints the service point summary to the log area.
     * This method is called by the controller to log the service point summary.
     *
     * @param configs The list of service point configurations.
     */
    private void printServicePointSummaryToLog(List<ServicePointConfig> configs) {
        if (configs == null || configs.isEmpty()) {
            logEvent("No service point configs found for this airport.");
            return;
        }
        StringBuilder sb = new StringBuilder("Service points for selected airport:\n");
        for (ServicePointConfig config : configs) {
            sb.append(config.getPointType())
              .append(": ")
              .append(config.getNumberOfServers())
              .append("\n");
        }
        logEvent(sb.toString());
    }

    /**
     * GetStartButton method returns the start button.
     * These methods return the corresponding UI components in Node format.
     *
     * Controller calls this method to get the start button.
     */
    public Node getStartButton() {
        return startButton; // Return the start button in Node format
    }

    /**
     * GetStopButton method returns the stop button.
     * These methods return the corresponding UI components in Node format.
     *
     * Controller calls this method to get the stop button.
     */
    public Node getStopButton() {
        return stopButton; // Return the stop button in Node format
    }

    /**
     * GetTimeSpinner method returns the time spinner.
     * These methods return the corresponding UI components in Node format.
     *
     * Controller calls this method to get the time spinner.
     */
    public Node getTimeSpinner() {
        return timeSpinner; // Return the slow button in Node format
    }

    /**
     * GetDelaySpinner method returns the delay spinner.
     * These methods return the corresponding UI components in Node format.
     *
     * Controller calls this method to get the delay spinner.
     */
    public Node getDelaySpinner() {
        return delay; // Return the delay button in Node format
    }

    /**
     * GetSlowButton method returns the slow button.
     * These methods return the corresponding UI components in Node format.
     *
     * Controller calls this method to get the slow button.
     */
    public Node getSlowDownButton() {
        return slowButton; // Return the slow-down button in Node format
    }

    /**
     * GetSpeedUpButton method returns the speed-up button.
     * These methods return the corresponding UI components in Node format.
     *
     * Controller calls this method to get the speed-up button.
     */
    public Node getSpeedUpButton() {
        return speedUpButton; // Return the speed-up button in Node format
    }

    /**
     * GetResetButton method returns the reset button.
     * These methods return the corresponding UI components in Node format.
     *
     * Controller calls this method to get the reset button.
     */
    public Node getResetButton() {
        return resetButton; // Return the reset button in Node format
    }

    /**
     * GetAirportComboBox method returns the airport ComboBox.
     * These methods return the corresponding UI components in Node format.
     *
     * Controller calls this method to get the airport ComboBox.
     */
    public Node getAirportComboBox() {
        return airportComboBox; // Return the airport ComboBox in Node format
    }

    /**
     * GetPlayPauseButton method returns the play/pause button.
     * These methods return the corresponding UI components in Node format.
     *
     * Controller calls this method to get the play/pause button.
     */
    public Node getPlayPauseButton() {
        return playPauseButton; // Return the play/pause button in Node format
    }

    /**
     * GetExternalViewButton method returns the external view button.
     * These methods return the corresponding UI components in Node format.
     *
     * Controller calls this method to get the external view button.
     */
    public Node getExternalViewButton(){ return externalViewButton; }

    /**
     * SetResetButtonDisabled method sets the reset button to be disabled or enabled.
     * This method is called by the controller to enable or disable the reset button.
     *
     * @param disabled true to disable the button, false to enable it.
     */
    public void setResetButtonDisabled(boolean disabled) {
        Platform.runLater(() -> resetButton.setDisable(disabled));
    }

    /**
     * Sets graph data for the view using the provided HashMap.
     * This method extracts usage ratio and waiting time data
     * from the model and stores them in corresponding fields
     * for visualization.
     *
     * @param data A HashMap containing the graph data, where
     *             "usageRatio" represents service usage ratios
     *             and "waitingTimes" represents service point's average waiting times.
     */
    public void setGraphData(HashMap<String, HashMap<String, Double>> data) {
        graphDataUsageRatio = data.get("usageRatio");
        graphDataAverageTimes = data.get("averageServiceTime");
        // Enable the external view button after data is set
        openExternalView();
    }

    /**
     * Opens an external window to display graphical data.
     * This method creates two bar charts:
     * - Usage Ratio per service point
     * - Average Service Time per service point
     * Both graphs share the same order of service points for consistency.
     */
    private void openExternalView() {
        Stage externalStage = new Stage();
        externalStage.setTitle("Graphical View");

        // Common X-axis order for both graphs
        String[] serviceOrder = {"Check-in", "Security", "Passport", "EU Gate", "Non-EU Gate"};

        // X-axis for a usage ratio
        CategoryAxis xAxisUsage = new CategoryAxis();
        xAxisUsage.setLabel("Service Points");

        // X-axis for average service time
        CategoryAxis xAxisServiceTime = new CategoryAxis();
        xAxisServiceTime.setLabel("Service Points");

        // Y-axis for a usage ratio
        NumberAxis yAxisUsage = new NumberAxis();
        yAxisUsage.setLabel("Usage Ratio (%)");

        // Y-axis for average service time
        NumberAxis yAxisServiceTime = new NumberAxis();
        yAxisServiceTime.setLabel("Average Service Time (time units)");

        /* BarChart for Usage Ratio */
        BarChart<String, Number> usageChart = new BarChart<>(xAxisUsage, yAxisUsage);
        usageChart.setTitle("Service Usage Ratio");

        XYChart.Series<String, Number> usageSeries = new XYChart.Series<>();
        usageSeries.setName("Usage Ratio");

        for (String service : serviceOrder) {
            if (graphDataUsageRatio.containsKey(service)) {
                usageSeries.getData().add(new XYChart.Data<>(service, graphDataUsageRatio.get(service)));
            }
        }

        usageChart.getData().add(usageSeries);

        /* BarChart for Average Service Time */
        BarChart<String, Number> serviceTimeChart = new BarChart<>(xAxisServiceTime, yAxisServiceTime);
        serviceTimeChart.setTitle("Average Service Time per Service Point");

        XYChart.Series<String, Number> serviceTimeSeries = new XYChart.Series<>();
        serviceTimeSeries.setName("Average Service Time");

        for (String service : serviceOrder) {
            if (graphDataAverageTimes.containsKey(service)) {
                serviceTimeSeries.getData().add(new XYChart.Data<>(service, graphDataAverageTimes.get(service)));
            }
        }

        serviceTimeChart.getData().add(serviceTimeSeries);

        // Adjust chart sizes for better readability
        usageChart.setPrefSize(600, 300);
        serviceTimeChart.setPrefSize(600, 350);

        /* Set up layout */
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(usageChart, serviceTimeChart);

        Scene scene = new Scene(layout, 600, 700); // Adjust the window size for both graphs
        externalStage.setScene(scene);

        // Display the window
        externalStage.show();
    }

    /**
     * SetSlowDownButtonDisabled method sets the slow-down button to be disabled or enabled.
     * This usingRunLater to ensure that the UI is updated on the JavaFX Application Thread.
     *
     * This method is called by the controller to enable or disable the slow-down button.
     */
    public void setSlowDownButtonDisabled(boolean b) {
        Platform.runLater(() -> slowButton.setDisable(b));
    }

    /**
     * SetSpeedUpButtonDisabled method sets the speed-up button to be disabled or enabled.
     * This usingRunLater to ensure that the UI is updated on the JavaFX Application Thread.
     *
     * This method is called by the controller to enable or disable the speed-up button.
     */
    public void setSpeedUpButtonDisabled(boolean b) {
        Platform.runLater(() -> speedUpButton.setDisable(b));
    }

    /**
     * SetArrivalSliderDisabled method sets the arrival slider to be disabled or enabled.
     * This usingRunLater to ensure that the UI is updated on the JavaFX Application Thread.
     *
     * This method is called by the controller to enable or disable the arrival slider.
     */
    public void setArrivalSliderDisabled(boolean b) {
        Platform.runLater(() -> arrivalSlider.setDisable(b));
    }

    /**
     * SetEUFlightPercentageSliderDisabled method sets the EU flight percentage slider to be disabled or enabled.
     * This usingRunLater to ensure that the UI is updated on the JavaFX Application Thread.
     *
     * This method is called by the controller to enable or disable the EU flight percentage slider.
     */
    public void setEUFlightPercentageSliderDisabled(boolean b) {
        Platform.runLater(() -> euPercentSlider.setDisable(b));
    }

    /* JavaFX-application (UI) start-up */
    public static void main(String[] args) {
        launch(args);
    }
}
