package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AirportView extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/airport-view.fxml"));
        Parent root = fxmlLoader.load();
        // Set the scene
        primaryStage.setScene(new Scene(root));
        // Initialize your GUI components here
        primaryStage.setTitle("Simulator");
        // Add your scene and layout setup here
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
