package be.ac.ulb.infof307.g06;


import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
    private static Stage primaryStage;
    private static AnchorPane mainLayout;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Main.primaryStage = primaryStage;
        Main.primaryStage.setTitle("Projet génie logiciel");
        // SetUp the main page. Should try to change that.
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(Main.class.getResource("JavaUI/sample/Connection.fxml"));
        mainLayout = loader.load();
        Scene scene = new Scene(mainLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
        /*
            If u want to show another window as the first one u need to get rif of
            the "setup" part and the "delay" part and replace them by:
            mainLayout = loader.load();
            Scene scene = new Scene(mainLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
         */
    }

    public static void showConnectionScene() throws Exception{
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(Main.class.getResource("JavaUI/sample/Connection.fxml"));
        // Setup the new page.
        AnchorPane connection = loader.load();
        mainLayout.getChildren().setAll(connection);
    }

    public static void showRegisterScene() throws Exception{
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(Main.class.getResource("JavaUI/sample/Register.fxml"));
        // Setup the new page.
        AnchorPane register = loader.load();
        mainLayout.getChildren().setAll(register);
    }

    public static void showStatisticsScene() throws Exception{
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(Main.class.getResource("JavaUI/sample/Stats.fxml"));
        // Setup the new page.
        AnchorPane register = loader.load();
        // Delay part.
        PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
        delay.setOnFinished( actionEvent ->mainLayout.getChildren().setAll(register));
        delay.play();
    }

    public static void showConditionsScene() throws Exception{
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(Main.class.getResource("JavaUI/sample/Terms.fxml"));
        AnchorPane conditions = loader.load();
        Stage stage = new Stage();
        stage.setTitle("User terms and conditions");
        stage.setScene(new Scene(conditions, 750, 900));
        stage.show();
    }

    public static void showVisualiseScene() throws Exception{
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(Main.class.getResource("JavaUI/sample/Visual.fxml"));
        // Setup the new page.
        AnchorPane visual = loader.load();
        mainLayout.getChildren().setAll(visual);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
