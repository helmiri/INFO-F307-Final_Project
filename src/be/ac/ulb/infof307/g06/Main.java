package be.ac.ulb.infof307.g06;

import be.ac.ulb.infof307.g06.database.UserDB;
import be.ac.ulb.infof307.g06.models.Global;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;

public class Main extends Application {
    private static Stage primaryStage;
    private static AnchorPane mainLayout;
    private static Stage stage;

    /**
     * Starts the main window
     *
     * @param primaryStage Main Stage
     * @throws Exception;
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Set main stage
        Global.userID = 0;
        Main.primaryStage = primaryStage;
        Main.primaryStage.setTitle("Projet génie logiciel");
        // Disconnect user before closing
        Main.primaryStage.setOnCloseRequest(e -> {
            try {
                if (Global.userID != 0) UserDB.disconnectUser(Global.userID);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            Platform.exit();
            System.exit(0);
        });

        // Load the fxml
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/LoginView.fxml"));
        mainLayout = loader.load();

        // Display scene
        Scene scene = new Scene(mainLayout);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Shows the scene to log in
     *
     * @throws Exception;
     */
    public static void showLoginScene() throws Exception{

        // Set main stage
        primaryStage.setHeight(465);
        primaryStage.setWidth(715);
        primaryStage.centerOnScreen();

        // Load the fxml
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/LoginView.fxml"));

        // Setup the new page.
        AnchorPane connectionAnchor = loader.load();
        mainLayout.getChildren().setAll(connectionAnchor);
    }

    /**
     * Shows the scene to sign up
     *
     * @throws Exception
     */
    public static void showSignUpScene() throws Exception{

        // Load the fxml
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/SignUpView.fxml"));

        // Setup the new page.
        AnchorPane registerAnchor = loader.load();
        mainLayout.getChildren().setAll(registerAnchor);
    }

    /**
     * Shows the scene to see the statistics related to a project
     *
     * @throws Exception;
     */
    public static void showStatisticsScene() throws Exception{

        // Set main stage
        primaryStage.setHeight(940);
        primaryStage.setWidth(1515);
        primaryStage.centerOnScreen();

        // Load the fxml
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/StatsView.fxml"));

        // Setup the new page.
        AnchorPane statisticsAnchor = loader.load();
        mainLayout.getChildren().setAll(statisticsAnchor);

    }

    /**
     * Show the scene for the management of a project
     *
     * @throws Exception;
     */
    public static void showProjectManagementScene() throws Exception{

        // Set main stage
        primaryStage.setHeight(940);
        primaryStage.setWidth(1515);
        primaryStage.centerOnScreen();

        // Load the fxml
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/ProjectViews/ProjectsViewV2.fxml"));

        // Setup the new page.
        AnchorPane projectManagementAnchor = loader.load();
        mainLayout.getChildren().setAll(projectManagementAnchor);

    }

    /**
     * Shows terms & conditions stage
     *
     * @throws Exception;
     */
    public static void showConditionsStage() throws Exception{

        // Load the fxml
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/TermsV2.fxml"));
        AnchorPane conditionsAnchor = loader.load();

        // Setup the new stage
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("User terms and conditions");
        stage.setScene(new Scene(conditionsAnchor, 900, 768));
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Closes terms & conditions stage
     */
    public static void closeConditionsStage() {stage.close();}


    /**
     * Shows the main menu scene
     *
     * @throws Exception;
     */
    public static void showMainMenuScene() throws Exception {

        // Set main stage
        primaryStage.setHeight(940);
        primaryStage.setWidth(1515);
        primaryStage.centerOnScreen();

        // Load the fxml
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/MenuView.fxml"));

        // Setup the new stage
        AnchorPane maineMenuAnchor = loader.load();
        mainLayout.getChildren().setAll(maineMenuAnchor);
    }

    /**
     * Shows the project menu scene
     *
     * @throws Exception;
     */
    public static void showProjectMenuScene() throws Exception {

        // Set main stage
        primaryStage.setHeight(940);
        primaryStage.setWidth(1515);
        primaryStage.centerOnScreen();

        // Load the fxml
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/ProjectMenu.fxml"));

        // Setup the new stage
        AnchorPane projectMenuAnchor = loader.load();
        mainLayout.getChildren().setAll(projectMenuAnchor);
    }

    /**
     * Shows the settings scene
     *
     * @throws Exception;
     */
    public static void showSettingsMenuScene() throws Exception {
        // Set main stage
        primaryStage.setHeight(940);
        primaryStage.setWidth(1515);
        primaryStage.centerOnScreen();

        // Load the fxml
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/SettingsMenu.fxml"));

        // Setup the new stage
        AnchorPane settingsMenuAnchor = loader.load();
        mainLayout.getChildren().setAll(settingsMenuAnchor);
    }

    public static void showAddProjectStage() throws Exception{

        // Load the fxml
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/ProjectViews/AddProjectView.fxml"));
        AnchorPane conditionsAnchor = loader.load();

        // Setup the new stage
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add project");
        stage.setScene(new Scene(conditionsAnchor, 541, 473));
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }

    public static void showEditProjectStage() throws Exception{

        // Load the fxml
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/ProjectViews/EditProjectView.fxml"));
        AnchorPane conditionsAnchor = loader.load();

        // Setup the new stage
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Edit project");
        stage.setScene(new Scene(conditionsAnchor, 541, 473));
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }

    public static void closeStage(){ stage.close(); }


    public static void main(String[] args) { launch(args); }
}
