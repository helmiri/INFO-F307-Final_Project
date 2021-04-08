package be.ac.ulb.infof307.g06.controllers.connection;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.views.ConnectionsViews.ConditionsViewController;
import be.ac.ulb.infof307.g06.views.ConnectionsViews.SignUpViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController {
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
    public void show() throws IOException {
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.sizeToScene();
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
                stage.hide();
                stage.setScene(previousScene);
                stage.show();
            }
        });
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Sets the loader to show the condition stage.
     */
    public void showConditionStage(String firstName, String lastName, String userName, String email, String password) throws IOException {
        FXMLLoader loader = new FXMLLoader(ConditionsViewController.class.getResource("Conditions.fxml"));
        Stage termsStage = new Stage();
        Scene termsScene = new Scene(loader.load());
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
     * @param pattern String
     * @return boolean
     */
    public boolean validateTextField(String field, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(field);
        return m.matches();
    }

    //--------------- LISTENER ----------------
    public interface Listener {
        void createUser(String firstName, String lastName, String userName, String email, String password);

        void showMainMenu();

        void onSignup();

        boolean doesUserExist(String username);

        void showLogin();
    }

}
