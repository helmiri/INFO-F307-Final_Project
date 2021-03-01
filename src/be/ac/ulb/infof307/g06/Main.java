package be.ac.ulb.infof307.g06;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("JavaUI/sample/statistics1.fxml")); // Test
        primaryStage.setTitle("ALINE");
        primaryStage.setScene(new Scene(root,1500, 900));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
