package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.database.UserDB;
import be.ac.ulb.infof307.g06.models.UserInformations;
import be.ac.ulb.infof307.g06.views.ConnectionsViews.SignUpViewController;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController {
    private SignUpViewController signUpView;

    /**
     * Sets an ID for the user added in the database.
     *
     * @return int UserID
     * @throws SQLException
     */
    public int setUserID() {
        try {
            return UserDB.addUser(UserInformations.getFirstName(), UserInformations.getLastName(), UserInformations.getUsername(), UserInformations.getEmail(), UserInformations.getPasswd());
        } catch (SQLException e) {
            signUpView.alertWindow(Alert.AlertType.ERROR,"Error Dialog","An error has occurred when adding the user to the database.",600);
            return 0;
        }
    }

    public boolean doesUserExist() {
        try {
            return UserDB.userExists(UserInformations.getUsername());
        } catch (SQLException e) {
            signUpView.alertWindow(Alert.AlertType.ERROR,"Error Dialog","An error has occurred while checking the database.",500);
            return true;
        }
    }

    /**
     * Sets informations of the user
     *
     * @param firstName String
     * @param lastName String
     * @param email String
     * @param username String
     * @param password String
     */
    public void setInformations(String firstName,String lastName,String email,String username,String password){
        UserInformations.setFirstName(firstName);
        UserInformations.setLastName(lastName);
        UserInformations.setEmail(email);
        UserInformations.setUsername(username);
        UserInformations.setPasswd(password);
    }

    /**
     * Validates the user's inputs according to a pattern given.
     *
     * @param field TextField
     * @param pattern String
     * @return boolean
     */
    public boolean validateTextField(TextField field, String pattern){
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(field.getText());
        return m.matches();
    }
}
