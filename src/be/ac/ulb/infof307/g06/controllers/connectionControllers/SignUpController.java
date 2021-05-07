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

public class SignUpController implements SignUpViewController.ViewListener {
    //--------------- ATTRIBUTES ----------------
    private final Listener listener;
    private final Stage stage;
    private final FXMLLoader loader;
    private final Scene previousScene;

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
        } catch (IOException e) {
            new AlertWindow("Error", "Could not load the scene", e.getMessage());
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
                new AlertWindow("Error", "This user already exists").informationWindow();
            }
        } else {
            String contextText = """
                    One of the sign up options is wrong:
                       - The last name and the first name must not contain any special characters
                       - The email Address must be a valid one: 'an.example@gmail.com'
                       - The username must not contain any special characters or spaces (8 to 16 chars)
                       - The password must have at least one lowercase character, one uppercase character and one special character from '@#_$%!' (6 to 16 chars)
                    """;
            new AlertWindow("Wrong enters", contextText).warningWindow();
        }
    }

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
        } catch (IOException e) {
            new AlertWindow("Error", "Could not load the window", e.getMessage());
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
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(field);
        return m.matches();
    }

    //--------------- LISTENER ----------------
    // Hand over control to the ConnectionHandler
    public interface Listener {
        void createUser(String firstName, String lastName, String userName, String email, String password);

        void showMainMenu();

        void onSignup();

        boolean doesUserExist(String username);

        void showLogin();
    }

}
