package be.ac.ulb.infof307.g06.controllers.connectionControllers;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.views.connectionViews.ConditionsViewController;
import be.ac.ulb.infof307.g06.views.connectionViews.SignUpViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller for the sign up view.
 */
public class SignUpController implements SignUpViewController.ViewListener {
    //--------------- ATTRIBUTES ----------------
    private final Listener listener;
    private final Stage stage;
    private final FXMLLoader loader;
    private final Scene previousScene;

    /**
     * Constructor.
     *
     * @param stage Stage, a stage
     * @param listener Listener, a listener
     * @param loginScene Scene, the scene of the login.
     */
    //--------------- METHODS ----------------
    public SignUpController(Stage stage, Listener listener, Scene loginScene) {
        this.stage = stage;
        this.listener = listener;
        loader = new FXMLLoader(SignUpViewController.class.getResource("SignUpView.fxml"));
        previousScene = loginScene;
    }

    /**
     * Sets the loader to show the Sign Up scene.
     */
    public void show() {
        Scene scene;
        try {
            scene = new Scene(loader.load());
        } catch (IOException error) {
            new AlertWindow("Error", "Could not load the scene", error.getMessage());
            return;
        }
        stage.setScene(scene);
        stage.sizeToScene();
        SignUpViewController controller = loader.getController();
        controller.setListener(this);
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Shows the conditions stage if the informations are valid.
     *
     * @param firstName String, the user's first name
     * @param lastName String, the user's last name
     * @param userName String, the user's username
     * @param email String, the user's email
     * @param password String the user's password
     * @param passwordConfirmation String, confirmation of the user's password
     */
    @Override
    public void signup(String firstName, String lastName, String userName, String email, String password, String passwordConfirmation) {
        if (validateTextField(lastName, "^[^±!@£$%^&*_+§¡€#¢§¶•ªº«\\/<>?:;|=.,]{1,64}$")
                && validateTextField(firstName, "^[^±!@£$%^&*_+§¡€#¢§¶•ªº«\\/<>?:;|=.,]{1,64}$")
                && validateTextField(userName, "^[^±!@£$%^&*+§¡€#¢§¶•ª º«\\/<>?:;|=.,]{6,16}$")
                && validateTextField(password, "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#_$%!]).{6,16})")
                && validateTextField(email, "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
                && passwordConfirmation.equals(password)) {
            if (!(listener.doesUserExist(userName))) {
                showConditionStage(firstName, lastName, userName, email, password);
            } else {
                new AlertWindow("Error", "This user already exists").showInformationWindow();
            }
        } else {
            String contextText = """
                    One of the sign up options is wrong:
                       - The last name and the first name must not contain any special characters
                       - The email Address must be a valid one: 'an.example@gmail.com'
                       - The username must not contain any special characters or spaces (8 to 16 chars)
                       - The password must have at least one lowercase character, one uppercase character and one special character from '@#_$%!' (6 to 16 chars)
                    """;
            new AlertWindow("Wrong enters", contextText).showWarningWindow();
        }
    }

    /**
     * Go back to the previous scene.
     */
    @Override
    public void back() {
        stage.setScene(previousScene);
    }

    /**
     * Sets the loader to show the condition stage.
     *
     * @param firstName The user's first name
     * @param lastName  The user's last name
     * @param userName  The user's username
     * @param email     The user's email
     * @param password  The password
     */
    public void showConditionStage(String firstName, String lastName, String userName, String email, String password) {
        FXMLLoader loader = new FXMLLoader(ConditionsViewController.class.getResource("Conditions.fxml"));
        Stage termsStage = new Stage();
        Scene termsScene;
        try {
            termsScene = new Scene(loader.load());
        } catch (IOException error) {
            new AlertWindow("Error", "Could not load the window", error.getMessage());
            return;
        }
        termsStage.setScene(termsScene);
        termsStage.show();
        ConditionsViewController controller = loader.getController();
        controller.setListener(new ConditionsViewController.ViewListener() {
            @Override
            public void onConditionsAccepted() {
                termsStage.close();
                listener.createUser(firstName, lastName, userName, email, password);
                listener.showMainMenu();
            }

            @Override
            public void onConditionsDeclined() {
                termsStage.hide();
                listener.onSignup();
            }

        });
    }

    /**
     * Validates the user's inputs according to a pattern given.
     *
     * @param field   TextField
     * @param pattern The regex pattern
     * @return true if the field is valid, false otherwise
     */
    public boolean validateTextField(String field, String pattern) {
        Pattern pattern1 = Pattern.compile(pattern);
        Matcher matcher1 = pattern1.matcher(field);
        return matcher1.matches();
    }

    /**
     * The listener and his methods.
     */
    //--------------- LISTENER ----------------
    // Hand over control to the ConnectionHandler
    public interface Listener {
        /**
         * creates a user.
         *
         * @param firstName String, the first name
         * @param lastName String, the last name
         * @param userName String, the username
         * @param email String, the email
         * @param password String, the password
         */
        void createUser(String firstName, String lastName, String userName, String email, String password);

        /**
         * Show the main menu.
         */
        void showMainMenu();

        /**
         * When we sign up.
         */
        void onSignup();

        /**
         * checks if the user exists.
         * @param username String, the username
         * @return boolean
         */
        boolean doesUserExist(String username);

        /**
         * Show the login scene.
         */
        void showLogin();
    }

}
