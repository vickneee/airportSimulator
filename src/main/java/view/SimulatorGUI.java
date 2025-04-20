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
	private TextField time;
	private TextField delay;
	private Label results;
	private Label timeLabel;
	private Label delayLabel;
	private Label resultLabel;

	private Button startButton;
	private Button slowButton;
	private Button speedUpButton;

	private IVisualisation display;

    private Label mainTitle;

    private List<Customer> customers = new ArrayList<>();

	@Override
	public void init(){
		Trace.setTraceLevel(Level.INFO);
		controller = new Controller(this);
	}

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

			primaryStage.setTitle("Airport Simulator");

            // Initialize mainTitle here
            mainTitle = new Label("Airport Simulator");
            mainTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

			startButton = new Button();
			startButton.setText("Start simulation");
			startButton.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	                controller.startSimulation();
	                startButton.setDisable(true);

                    // Example: Add a customer when the simulation starts
                    // Use 0 or 1 for isEUFlight
                    Customer customer = new Customer(0);
                    customers.add(customer);
                    display.addCustomer(customer);
	            }
	        });

			slowButton = new Button();
			slowButton.setText("Slow down");
			slowButton.setOnAction(e -> controller.decreaseSpeed());

			speedUpButton = new Button();
			speedUpButton.setText("Speed up");
			speedUpButton.setOnAction(e -> controller.increaseSpeed());

			timeLabel = new Label("Simulation time:");
			timeLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
	        time = new TextField("Give time");
	        time.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
	        time.setPrefWidth(100);

	        delayLabel = new Label("Delay:");
			delayLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
	        delay = new TextField("Give delay");
	        delay.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
	        delay.setPrefWidth(100);

	        resultLabel = new Label("Total time:");
			resultLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
	        results = new Label();
	        results.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
	        results.setPrefWidth(100);

            // Create a VBox layout
            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.setPadding(new Insets(10));
            vBox.setSpacing(10); // Set spacing between elements
            vBox.getChildren().add(mainTitle);

            // Create a hBox layout
	        HBox hBox = new HBox();
	        hBox.setPadding(new Insets(15, 12, 15, 12)); // margins up, right, bottom, left
	        hBox.setSpacing(10);   // Node distance 10 pixels

            GridPane grid = new GridPane();
	        grid.setAlignment(Pos.CENTER);
	        grid.setVgap(10);
	        grid.setHgap(5);

	        grid.add(timeLabel, 0, 0);   // column, row
	        grid.add(time, 1, 0);
	        grid.add(delayLabel, 0, 1);
	        grid.add(delay, 1, 1);
	        grid.add(resultLabel, 0, 2);
	        grid.add(results, 1, 2);
	        grid.add(startButton,0, 3);
	        grid.add(speedUpButton, 0, 4);
	        grid.add(slowButton, 1, 4);

	        display = new Visualisation(600,600);

	        // Fill the box:
	        hBox.getChildren().addAll(grid, (Canvas) display);

            // Create a root BorderPane and set the VBox and HBox
            BorderPane root = new BorderPane();
            root.setTop(vBox); // Place the title at the top
            root.setCenter(hBox); // Place the grid and canvas in the center

	        Scene scene = new Scene(root, 1000, 800);
	        primaryStage.setScene(scene);
	        primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/* UI interface methods (controller calls) */
	@Override
	public double getTime(){
		return Double.parseDouble(time.getText());
	}

	@Override
	public long getDelay(){
		return Long.parseLong(delay.getText());
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
}
