package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Global;
import be.ac.ulb.infof307.g06.models.Invitation;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.MenuViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MainController {
    //-------------- ATTRIBUTES ----------------
    private MenuViewController view;
    private static Stage primaryStage;
    private static AnchorPane mainLayout;
    private static Stage stage;
    private static boolean isFirstBoot;
    //--------------- METHODS ----------------

    /**
     * Starts the main window
     *
     * @param primaryStage2 Main Stage
     * @throws IOException e
     */
    /*
    @Override
    public void start(Stage primaryStage2) throws IOException {
        // Set main stage
        Global.userID = 0;
        try {
            new UserDB("Database.db");
            isFirstBoot = UserDB.isFirstBoot();
        } catch (SQLException throwables) {
            alertWindow(Alert.AlertType.ERROR, "Database error", "Could not access the database");
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        primaryStage = primaryStage2;
        primaryStage.setTitle("Projet génie logiciel");
        // Disconnect user before closing
        primaryStage.setOnCloseRequest(e -> {
            try {
                if (Global.userID != 0) UserDB.disconnectUser();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            Platform.exit();
            System.exit(0);
        });
        // Load the fxml
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(LoginViewController.class.getResource("LoginView.fxml"));
        mainLayout = loader.load();
        // Display scene
        Scene scene = new Scene(mainLayout);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //public static void main(String[] args) { launch(args); }
    */
    /**
     * Sets the loader to show the Main menu scene.
     */
    public static void showMainMenu() {
        if (isFirstBoot) {
            try {
                UserDB.setAdmin(256 * 1000000);
            } catch (SQLException throwables) {
                new AlertWindow("Database error", "Could not access the database").errorWindow();
            }
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MenuViewController.class.getResource("MenuView.fxml"));
        load(loader, 940, 1515);
    }

    /**
     * Sets the loader to show the project menu scene.
     */
    public static void showProjectMenu()  {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MenuViewController.class.getResource("ProjectMenu.fxml"));
        load(loader, 940, 1515);
    }

    /**
     * Sets the loader to show the stage with an invitation to join a project.
     */
    public static void showInvitationStage() {
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(MenuViewController.class.getResource("InvitationView.fxml"));
        MainController.showStage("Invitation", 571, 473, Modality.APPLICATION_MODAL, loader);
    }

    /**
     * Initializes the main view.
     *
     * @param view MenuViewController
     */
    public void init(MenuViewController view){ this.view = view; }

    /**
     * Checks the invitation requests.
     *
     * @throws SQLException        throws SQLException
     * @throws java.lang.Exception throws Exception
     */
    public void checkInvites() throws SQLException, java.lang.Exception {
        List<Invitation> invitations = UserDB.getInvitations(Global.userID);
        for (Invitation invitation : invitations) {
            view.showInvitation(invitation.getProject_id(), invitation.getSender_id());
        }
    }

    /**
     * Sets the primary stage with the new scene.
     *
     * @param loader FXMLLoader
     * @param height Integer
     * @param width Integer
     */
    public static void load(FXMLLoader loader,Integer height,Integer width) {
        try {
            // Set main stage
            primaryStage.setResizable(true);
            primaryStage.setHeight(height);
            primaryStage.setWidth(width);
            primaryStage.centerOnScreen();
            primaryStage.setResizable(false);

            // Setup the new page.
            AnchorPane connectionAnchor = loader.load();
            mainLayout.getChildren().setAll(connectionAnchor);
        } catch (IOException e) {
            new AlertWindow("Error", "Could not load the window").errorWindow();
        }
    }

    /**
     * Sets a new stage.
     *
     * @param title String
     * @param width Integer
     * @param height Integer
     * @param modality Modality
     * @param loader FXMLLoader
     */
    public static void showStage(String title, Integer width, Integer height, Modality modality, FXMLLoader loader ){
        try {
            AnchorPane conditionsAnchor = loader.load();
            stage = new Stage();
            stage.initModality(modality);
            stage.setTitle(title);
            stage.setScene(new Scene(conditionsAnchor, width, height));
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.show();
        }catch(IOException e){
            new AlertWindow("Error", "Could not load the window").errorWindow();
        }

    }

    /**
     * Closes a stage.
     */
    public static void closeStage(){ stage.close(); }

    /**
     * Handles the request to join a project.
     *
     * @param accept Boolean
     * @param project String
     * @param user int
     */
    public static void invitation(Boolean accept,String project, int user){
        try {
            if (accept) { ProjectDB.addCollaborator(ProjectDB.getProjectID(project), user); }
            UserDB.removeInvitation(ProjectDB.getProjectID(project), user);
        }catch(SQLException e){
            new AlertWindow("Database error", "Could not access the database").errorWindow();
        }
    }

}
