package be.ac.ulb.infof307.g06.controllers.connection;

import be.ac.ulb.infof307.g06.controllers.MainController;
import be.ac.ulb.infof307.g06.models.UserInformations;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.ConnectionsViews.ConditionsViewController;
import be.ac.ulb.infof307.g06.views.ConnectionsViews.SignUpViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController {
    Listener listener;
    Stage stage;

    public SignUpController(Stage stage, Listener listener) {
        this.stage = stage;
        this.listener = listener;
    }

    /**
     * Sets the loader to show the Sign Up scene.
     */
    public void show() throws IOException {
        FXMLLoader loader = new FXMLLoader(SignUpViewController.class.getResource("SignUpView.fxml"));
        loader.load();
        SignUpViewController controller = loader.getController();
        controller.setListener(new SignUpViewController.ViewListener() {

            @Override
            public void signup(String firstName, String lastName, String userName, String email, String password, String passwordConfirmation) {
                if (validateTextField(lastName, "^[^±!@£$%^&*_+§¡€#¢§¶•ªº«\\/<>?:;|=.,]{1,64}$")
                        && validateTextField(firstName, "^[^±!@£$%^&*_+§¡€#¢§¶•ªº«\\/<>?:;|=.,]{1,64}$")
                        && validateTextField(userName, "^[^±!@£$%^&*+§¡€#¢§¶•ª º«\\/<>?:;|=.,]{6,16}$")
                        && validateTextField(password, "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#_$%!]).{6,16})")
                        && validateTextField(email, "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
                        && passwordConfirmation.equals(password)) {
                    if (!(listener.doesUserExist(userName))) {
                        try {
                            showConditionStage(firstName, lastName, userName, email, password);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //signUpTxtPopup.setText("This user already exists!");
                    }
                } else {
                    String contextText = """
                            One of the sign up options is wrong:
                               - The last name and the first name must not contain any special characters
                               - The email Address must be a valid one: 'an.example@gmail.com'
                               - The username must not contain any special characters or spaces (8 to 16 chars)
                               - The password must have at least one lowercase character, one uppercase character and one special character from '@#_$%!' (6 to 16 chars)
                            """;
                    //MainController.alertWindow(Alert.AlertType.WARNING, "Wrong enters", contextText);
                }
            }
        });
        // Set main stage
        stage.setResizable(true);
        stage.setHeight(465);
        stage.setWidth(715);
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
        // Setup the new page.
        //AnchorPane connectionAnchor = loader.load();
        //mainLayout.getChildren().setAll(connectionAnchor);
    }

    /**
     * Sets the loader to show the condition stage.
     */
    public void showConditionStage(String firstName, String lastName, String userName, String email, String password) throws IOException {
        FXMLLoader loader = new FXMLLoader(ConditionsViewController.class.getResource("TermsV2.fxml"));
        loader.load();
        ConditionsViewController controller = loader.getController();
        controller.setListener(new ConditionsViewController.ViewListener() {
            @Override
            public void onConditionsAccepted() {
                listener.createUser(firstName, lastName, userName, email, password);
                listener.showMainMenu();
            }

            @Override
            public void onConditionsDeclined() {
                listener.onSignup();
            }

        });
        MainController.showStage("Terms", 900, 768, Modality.APPLICATION_MODAL, loader);
    }


    /**
     * Validates the user's inputs according to a pattern given.
     *
     * @param field   TextField
     * @param pattern String
     * @return boolean
     */
    public boolean validateTextField(String field, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(field);
        return m.matches();
    }


    public interface Listener {
        void createUser(String firstName, String lastName, String userName, String email, String password);

        void showMainMenu();

        void onSignup();

        boolean doesUserExist(String username);

        void showLogin();
    }

}
