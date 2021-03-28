package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.database.UserDB;
import be.ac.ulb.infof307.g06.models.Global;
import be.ac.ulb.infof307.g06.models.Invitation;
import be.ac.ulb.infof307.g06.views.ConnectionsViews.LoginViewController;
import be.ac.ulb.infof307.g06.views.MenuViewController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MainController extends Application {
    //-------------- ATTRIBUTES ----------------
    private MenuViewController view;
    private static Stage primaryStage;
    private static AnchorPane mainLayout;
    private static Stage stage;
    //--------------- METHODS ----------------

    /**
     * Starts the main window
     *
     * @param primaryStage2 Main Stage
     * @throws Exception;
     */
    @Override
    public void start(Stage primaryStage2) throws Exception {
        // Set main stage
        Global.userID = 0;
        primaryStage = primaryStage2;
        primaryStage.setTitle("Projet génie logiciel");
        // Disconnect user before closing

        primaryStage.setOnCloseRequest(e -> {
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
        loader.setLocation(LoginViewController.class.getResource("LoginView.fxml"));
        mainLayout = loader.load();
        // Display scene
        Scene scene = new Scene(mainLayout);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showMainMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MenuViewController.class.getResource("MenuView.fxml"));
        load(loader, 940, 1515);
    }

    public static void showProjectMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MenuViewController.class.getResource("ProjectMenu.fxml"));
        load(loader, 940, 1515);
    }

    public static void showInvitationStage() {
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(MenuViewController.class.getResource("InvitationView.fxml"));
        MainController.showStage("Invitation", 571, 473, Modality.APPLICATION_MODAL, loader );
    }

    /**
     * Initializes the main view.
     *
     * @param view MenuViewController
     */
    public void init(MenuViewController view){
        this.view = view;
    }

    /**
     * Checks the invitation requests.
     *
     * @throws SQLException throws SQLException
     * @throws java.lang.Exception throws Exception
     */
    public void checkInvites() throws SQLException, java.lang.Exception{
        List<Invitation> invitations = UserDB.getInvitations(Global.userID);
        for (Invitation invitation : invitations) {
            view.showInvitation(invitation.getProject_id(), invitation.getSender_id());
        }
    }

    public static void load(FXMLLoader loader,Integer height,Integer width) throws IOException {
        // Set main stage
        primaryStage.setResizable(true);
        primaryStage.setHeight(height);
        primaryStage.setWidth(width);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);

        // Setup the new page.
        AnchorPane connectionAnchor = loader.load();
        mainLayout.getChildren().setAll(connectionAnchor);
    }

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
            alertWindow(Alert.AlertType.ERROR,"Error", "An error has occurred when loading the window.",400 );
        }

    }

    public static void closeStage(){ stage.close(); }
    public static void main(String[] args) { launch(args); }

    public static void alertWindow(Alert.AlertType type, String title,String message,Integer minWidth){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.getDialogPane().setMinWidth(minWidth);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
