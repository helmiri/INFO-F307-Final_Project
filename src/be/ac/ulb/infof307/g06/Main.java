package be.ac.ulb.infof307.g06;
import be.ac.ulb.infof307.g06.database.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application {
    private static Stage primaryStage;
    private static AnchorPane mainLayout;
    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Main.primaryStage = primaryStage;
        Main.primaryStage.setTitle("Projet génie logiciel");
        // SetUp the main page. Should try to change that.
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(Main.class.getResource("JavaUI/sample/LoginV2.fxml"));
        mainLayout = loader.load();
        Scene scene = new Scene(mainLayout);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showConnectionScene() throws Exception{
        primaryStage.setHeight(465);
        primaryStage.setWidth(715);
        primaryStage.centerOnScreen();
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(Main.class.getResource("JavaUI/sample/LoginV2.fxml"));
        // Setup the new page.
        AnchorPane connection = loader.load();
        mainLayout.getChildren().setAll(connection);
        //getUserProjects() choppes l'id de tous les projets de l'User
    }

    public static void showRegisterScene() throws Exception{
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(Main.class.getResource("JavaUI/sample/SignUpV2.fxml"));
        // Setup the new page.
        AnchorPane register = loader.load();
        mainLayout.getChildren().setAll(register);
    }

    public static void showStatisticsScene() throws Exception{
        primaryStage.setHeight(940);
        primaryStage.setWidth(1515);
        primaryStage.centerOnScreen();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("JavaUI/sample/StatsV2.fxml"));
        // Setup the new page.
        AnchorPane register = loader.load();
        mainLayout.getChildren().setAll(register);

    }

    public static void showProjectsScene() throws Exception{
        primaryStage.setHeight(940);
        primaryStage.setWidth(1515);
        primaryStage.centerOnScreen();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("JavaUI/sample/Projects.fxml"));
        // Setup the new page.
        AnchorPane register = loader.load();
        mainLayout.getChildren().setAll(register);

    }

    public static void showConditionsScene() throws Exception{
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(Main.class.getResource("JavaUI/sample/TermsV2.fxml"));
        AnchorPane conditions = loader.load();
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("User terms and conditions");
        stage.setScene(new Scene(conditions, 900, 768));
        stage.centerOnScreen();
        stage.show();
    }

    public static void closeConditionsStage() {stage.close();}

    public static void showVisualScene() throws Exception{
        primaryStage.setHeight(940);
        primaryStage.setWidth(1515);
        primaryStage.centerOnScreen();
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(Main.class.getResource("JavaUI/sample/VisualV2.fxml"));
        // Setup the new page.
        AnchorPane connection = loader.load();
        mainLayout.getChildren().setAll(connection);
    }

    public static void showTaskScene() throws Exception{
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(Main.class.getResource("JavaUI/sample/Task.fxml"));
        AnchorPane conditions = loader.load();
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Task management");
        stage.setScene(new Scene(conditions, 600, 400));
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    public static void ShowMainMenu() throws Exception {
        primaryStage.setHeight(940);
        primaryStage.setWidth(1515);
        primaryStage.centerOnScreen();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("JavaUI/sample/MainMenu.fxml"));
        AnchorPane menu = loader.load();
        mainLayout.getChildren().setAll(menu);
    }

    public static void ShowProjectsMenu() throws Exception {
        primaryStage.setHeight(940);
        primaryStage.setWidth(1515);
        primaryStage.centerOnScreen();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("JavaUI/sample/ProjectMenu.fxml"));
        AnchorPane projectMenu = loader.load();
        mainLayout.getChildren().setAll(projectMenu);
    }

    public static void closeTaskStage() {stage.close();}

    public static void main(String[] args) { launch(args); }
}
