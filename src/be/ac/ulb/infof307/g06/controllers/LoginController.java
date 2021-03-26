package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.database.UserDB;

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
