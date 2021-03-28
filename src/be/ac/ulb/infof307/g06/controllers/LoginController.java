package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.database.UserDB;
import be.ac.ulb.infof307.g06.views.ConnectionsViews.LoginViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    /**
     * Initializes databases and the view.
     */
    public void init(){
        try {
            new ProjectDB("Database.db");
            new UserDB("Database.db");
        } catch (SQLException | ClassNotFoundException e) {
            MainController.alertWindow(Alert.AlertType.ERROR,"Error","An error has occurred to load with the database files: " + e);
        }
    }
    /**
     * Sets the loader to show the Log In scene.
     */
    public static void show(){
        // Load the fxml
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(LoginViewController.class.getResource("LoginView.fxml"));
        MainController.load(loader, 465, 715);
    }

    /**
     * Returns the UserID if the user exists and if the user is not already connected.
     *
     * @param passwd String
     * @param user String
     * @return int
     */
    public int validateUserID(String passwd, String user) {
        try {
            return UserDB.validateData(user, passwd);
        } catch (SQLException e) {
            MainController.alertWindow(Alert.AlertType.ERROR,"Error","An error has occurred with the database: "+ e);
            return 0;
        }
    }
}
