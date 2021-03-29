package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.models.UserInformations;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.ConnectionsViews.ConditionsViewController;
import be.ac.ulb.infof307.g06.views.ConnectionsViews.SignUpViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController {
    /**
     * Sets the loader to show the Sign Up scene.
     */
    public static void show() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SignUpViewController.class.getResource("SignUpView.fxml"));
        MainController.load(loader, 465, 715);
    }

    /**
     * Sets the loader to show the condition stage.
     */
    public static void showConditionStage() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ConditionsViewController.class.getResource("TermsV2.fxml"));
        MainController.showStage("Terms", 900, 768, Modality.APPLICATION_MODAL, loader);
    }

    /**
     * Sets an ID for the user added in the database.
     *
     * @return int UserID
     */
    public int setUserID() {
        try {
            return UserDB.addUser(UserInformations.getFirstName(), UserInformations.getLastName(), UserInformations.getUsername(), UserInformations.getEmail(), UserInformations.getPasswd());
        } catch (SQLException e) {
            MainController.alertWindow(Alert.AlertType.ERROR, "Error", "An error has occurred when adding the user to the database: " + e);
            return 0;
        }
    }

    /**
     * Sets informations of the user
     *
     * @param firstName String
     * @param lastName  String
     * @param email     String
     * @param username  String
     * @param password  String
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
     * @param field   TextField
     * @param pattern String
     * @return boolean
     */
    public boolean validateTextField(TextField field, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(field.getText());
        return m.matches();
    }

    /**
     * Checks if a user already exists in the database.
     *
     * @return boolean
     */
    public boolean doesUserExist() {
        try {
            return UserDB.userExists(UserInformations.getUsername());
        } catch (SQLException e) {
            MainController.alertWindow(Alert.AlertType.ERROR, "Error", "An error has occurred with the database when checking if the user already exists: " + e);
            return true;
        }
    }

}
