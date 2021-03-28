package be.ac.ulb.infof307.g06;

import be.ac.ulb.infof307.g06.database.UserDB;
import be.ac.ulb.infof307.g06.models.Global;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;


import java.io.IOException;
import java.sql.SQLException;


import java.io.File;

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
        loader.setLocation(Main.class.getResource("views/ConnectionsViews/LoginView.fxml"));
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
    public static void showLoginScene() throws IOException {

        // Set main stage
        primaryStage.setResizable(true);
        primaryStage.setHeight(465);
        primaryStage.setWidth(715);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);


        // Load the fxml
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/ConnectionsViews/LoginView.fxml"));

        // Setup the new page.
        AnchorPane connectionAnchor = loader.load();
        mainLayout.getChildren().setAll(connectionAnchor);
    }

    /**
     * Shows the scene to sign up
     *
     * @throws Exception
     */
    public static void showSignUpScene() throws IOException {

        // Set main stage
        primaryStage.setResizable(true);
        primaryStage.setHeight(465);
        primaryStage.setWidth(715);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        // Load the fxml
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/ConnectionsViews/SignUpView.fxml"));

        // Setup the new page.
        AnchorPane registerAnchor = loader.load();
        mainLayout.getChildren().setAll(registerAnchor);
    }

    /**
     * Shows the scene to see the statistics related to a project
     *
     * @throws Exception;
     */
    public static void showStatisticsScene() throws IOException {

        // Set main stage
        primaryStage.setResizable(true);
        primaryStage.setHeight(940);
        primaryStage.setWidth(1515);
        primaryStage.centerOnScreen();

        // Load the fxml
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/StatsView.fxml"));

        // Setup the new page.
        AnchorPane statisticsAnchor = loader.load();
        mainLayout.getChildren().setAll(statisticsAnchor);
        primaryStage.setResizable(false);

    }

    /**
     * Show the scene for the management of a project
     *
     * @throws Exception;
     */
    public static void showProjectManagementScene() throws IOException {

        // Set main stage
        primaryStage.setResizable(true);
        primaryStage.setHeight(940);
        primaryStage.setWidth(1515);
        primaryStage.centerOnScreen();

        // Load the fxml
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/ProjectViews/ProjectsViewV2.fxml"));

        // Setup the new page.
        AnchorPane projectManagementAnchor = loader.load();
        mainLayout.getChildren().setAll(projectManagementAnchor);
        primaryStage.setResizable(false);

    }

    /**
     * Shows terms & conditions stage
     *
     * @throws Exception;
     */
    public static void showConditionsStage() throws IOException {

        // Load the fxml
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/ConnectionsViews/TermsV2.fxml"));
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
     * Shows the main menu scene
     *
     * @throws Exception;
     */
    public static void showMainMenuScene() throws IOException {

        // Set main stage
        primaryStage.setResizable(true);
        primaryStage.setHeight(940);
        primaryStage.setWidth(1515);
        primaryStage.centerOnScreen();

        // Load the fxml
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/MenuView.fxml"));

        // Setup the new stage
        AnchorPane maineMenuAnchor = loader.load();
        mainLayout.getChildren().setAll(maineMenuAnchor);
        primaryStage.setResizable(false);
    }

    /**
     * Shows the project menu scene
     *
     * @throws Exception;
     */
    public static void showProjectMenuScene() throws IOException {

        // Set main stage
        primaryStage.setResizable(true);
        primaryStage.setHeight(940);
        primaryStage.setWidth(1515);
        primaryStage.centerOnScreen();

        // Load the fxml
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/ProjectMenu.fxml"));

        // Setup the new stage
        AnchorPane projectMenuAnchor = loader.load();
        mainLayout.getChildren().setAll(projectMenuAnchor);
        primaryStage.setResizable(false);
    }

    /**
     * Shows the settings scene
     *
     * @throws Exception;
     */
    public static void showSettingsMenuScene() throws IOException {
        // Set main stage
        primaryStage.setResizable(true);
        primaryStage.setHeight(940);
        primaryStage.setWidth(1515);
        primaryStage.centerOnScreen();

        // Load the fxml
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/SettingsMenu.fxml"));

        // Setup the new stage
        AnchorPane settingsMenuAnchor = loader.load();
        mainLayout.getChildren().setAll(settingsMenuAnchor);
        primaryStage.setResizable(false);
    }

    public static void showAddProjectStage() throws IOException {

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

    public static void showEditProjectStage() throws IOException {

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
    public static void showEditTaskStage() throws IOException {

        // Load the fxml
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/TaskEditView.fxml"));
        AnchorPane conditionsAnchor = loader.load();

        // Setup the new stage
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Edit task");
        stage.setScene(new Scene(conditionsAnchor, 435, 256));
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }

    public static  void showInvitationStage() throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/InvitationView.fxml"));
        AnchorPane invitationAnchor = loader.load();

        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Invitation");
        stage.setScene(new Scene(invitationAnchor, 541, 473));
        stage.centerOnScreen();;
        stage.setResizable(false);
        stage.show();

    }

    public static void showTagsMenu() throws IOException {
        // Set main stage
        primaryStage.setResizable(true);
        primaryStage.setHeight(940);
        primaryStage.setWidth(1515);
        primaryStage.centerOnScreen();

        // Load the fxml
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/Tags.fxml"));

        // Setup the new stage
        AnchorPane tagsMenuAnchor = loader.load();
        mainLayout.getChildren().setAll(tagsMenuAnchor);
        primaryStage.setResizable(false);
    }

    @FXML
    public static void alertExport(boolean succeed){
        //TODO à mettre dans le main controller
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Export");
        alert.setHeaderText(null);
        alert.getDialogPane().setMinWidth(900);
        if(succeed){
            alert.setContentText("Congrat,your project is exported");
            alert.showAndWait();
        }

        else {
        alert.setContentText("Sorry, failed to export your project");
        alert.showAndWait();
    }
}

    @FXML
    public static void alertImport(boolean succeed){
        //TODO à mettre dans le main controller
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Import");
        alert.setHeaderText(null);
        alert.getDialogPane().setMinWidth(900);
        if(succeed){
            alert.setContentText("Congrat,your project is imported");
            alert.showAndWait();
        }

        else {
            alert.setContentText("Sorry, failed to import your project");
            alert.showAndWait();
        }
    }


    /**
     * Closes the actual stage.
     */
    public static void closeStage(){ stage.close(); }
    public static void main(String[] args) { launch(args); }
}
