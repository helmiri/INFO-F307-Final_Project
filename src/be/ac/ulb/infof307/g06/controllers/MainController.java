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

    /**
     * Initializes the main view.
     *
     * @param view MenuViewController
     */
    //--------------- METHODS ----------------
    public void init(MenuViewController view){
        this.view = view;
    }

    /**
     * Checks the invitation requests.
     *
     * @throws SQLException throws SQLException
     * @throws java.lang.Exception throws Exception
     */
    public void checkInvites()throws SQLException, java.lang.Exception{
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
