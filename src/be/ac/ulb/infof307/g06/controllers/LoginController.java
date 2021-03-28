package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.database.UserDB;
import be.ac.ulb.infof307.g06.views.ConnectionsViews.LoginViewController;
import java.sql.SQLException;

public class LoginController {
    private LoginViewController logInView;

    /**
     * Initializes databases and the view.
     *
     */
    public void init() {
        try{
            new ProjectDB("Database.db");
            new UserDB("Database.db");
        }
        catch(SQLException | ClassNotFoundException e){
            logInView.showAlert("An error has occurred with the database.");
        }

    }

    /**
     * Returns the UserID if the user exists and if the user is not already connected.
     *
     * @param passwd String
     * @param user String
     * @return int
     */
    public int validateUserID(String passwd, String user) {
        try{
            return UserDB.validateData(user, passwd);
        } catch(SQLException e){
            logInView.showAlert("An error has occurred with the database.");
            return 0;
        }
    }

}
