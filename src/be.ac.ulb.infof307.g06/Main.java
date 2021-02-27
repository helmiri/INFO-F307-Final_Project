package be.ac.ulb.infof307.g06;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        // JE NE SAIS PAS PQ MAIS CA CREE UN BUG (ALINE)!! Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("JavaUI/sample/test.fxml"));
        primaryStage.setTitle("Terms & conditions");
        //primaryStage.setScene(new Scene(root,600, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
