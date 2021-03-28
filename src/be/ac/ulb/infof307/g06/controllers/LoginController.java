package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.database.UserDB;
import be.ac.ulb.infof307.g06.views.ConnectionsViews.LoginViewController;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    /**
     * Initializes databases and the view.
     *
     * @throws SQLException throws database error
     * @throws ClassNotFoundException throws class not found error
     */
    public void init() throws SQLException, ClassNotFoundException {
        new ProjectDB("Database.db");
        new UserDB("Database.db");
    }

    public static void show() throws IOException {
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
     * @throws SQLException throws database error
     */
    public int validateUserID(String passwd, String user) throws SQLException { return UserDB.validateData(user, passwd); }
}
