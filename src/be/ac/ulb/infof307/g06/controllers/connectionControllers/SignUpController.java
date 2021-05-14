package be.ac.ulb.infof307.g06.controllers.connectionControllers;

import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.exceptions.WindowLoadException;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.views.connectionViews.SignUpViewController;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller for the sign up view.
 */
public class SignUpController extends ConnectionController implements SignUpViewController.ViewListener {

    /**
     * Constructor
     *
     * @param stage Stage, a stage
     */
    public SignUpController(Stage stage) throws DatabaseException {
        super(stage);
    }

    /**
     * Sets the loader to show the Sign Up scene.
     */
    @Override
    public void show() {
        try {
            SignUpViewController controller = (SignUpViewController) loadView(SignUpViewController.class, "SignUpView.fxml");
            controller.setListener(this);
            controller.show(stage);
        } catch (IOException e) {
            new WindowLoadException(e).show();
        }
    }

    /**
     * Shows the conditions stage if the informations are valid.
     *
     * @param firstName            String, the user's first name
     * @param lastName             String, the user's last name
     * @param userName             String, the user's username
     * @param email                String, the user's email
     * @param password             String the user's password
     * @param passwordConfirmation String, confirmation of the user's password
     */
    @Override
    public void signup(String firstName, String lastName, String userName, String email, String password, String passwordConfirmation) {
        try {
            if (validCredentials(firstName, lastName, userName, email, password, passwordConfirmation)) {
                if (!userExists(userName) && showConditionStage(firstName, lastName, userName, email, password)) {
                    createUser(firstName, lastName, userName, email, password);
                    showMainMenu();
                }
            } else {
                showInvalidCredentialsPrompt();
            }
        } catch (DatabaseException error) {
            error.show();
        } catch (WindowLoadException e) {
            e.show();
        }
    }

    /**
     * Checks the validity of the credentials
     *
     * @param firstName            String, the user's first name
     * @param lastName             String, the user's last name
     * @param userName             String, the user's username
     * @param email                String, the user's email
     * @param password             String the user's password
     * @param passwordConfirmation String, confirmation of the user's password
     * @return true if all of them are valid, false otherwise
     */
    private boolean validCredentials(String firstName, String lastName, String userName, String email, String password, String passwordConfirmation) {
        final String namePattern = "^[^±!@£$%^&*_+§¡€#¢§¶•ªº«\\/<>?:;|=.,]{1,64}$";
        final String usernamePattern = "^[^±!@£$%^&*+§¡€#¢§¶•ª º«\\/<>?:;|=.,]{6,16}$";
        final String passwordPattern = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#_$%!]).{6,16})";
        final String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return validateTextField(lastName, namePattern) && validateTextField(firstName, namePattern)
                && validateTextField(userName, usernamePattern) && validateTextField(password, passwordPattern)
                && validateTextField(email, emailPattern) && passwordConfirmation.equals(password);
    }

    /**
     * Displays an error message
     */
    private void showInvalidCredentialsPrompt() {
        String contextText = """
                One of the sign up options is wrong:
                   - The last name and the first name must not contain any special characters
                   - The email Address must be a valid one: 'an.example@gmail.com'
                   - The username must not contain any special characters or spaces (8 to 16 chars)
                   - The password must have at least one lowercase character, one uppercase character and one special character from '@#_$%!' (6 to 16 chars)
                """;
        new AlertWindow("Invalid input", contextText).showWarningWindow();
    }

    /**
     * Go back to the previous scene.
     */
    @Override
    public void onBack() {
        LoginController controller;
        try {
            controller = new LoginController(stage);
            controller.show();
        } catch (DatabaseException e) {
            e.show(); // Nothing to do
        } catch (WindowLoadException e) {
            e.show(); // Nothing to do
        }
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
    public boolean showConditionStage(String firstName, String lastName, String userName, String email, String password) throws WindowLoadException {
        Stage termsStage = new Stage();
        UserAgreement controller = new UserAgreement(termsStage);
        controller.show();
        termsStage.setOnCloseRequest(e -> {
            if (controller.termsAccepted()) {
                try {
                    createUser(firstName, lastName, userName, email, password);
                } catch (DatabaseException databaseException) {
                    databaseException.show();
                    stage.close();
                }
            } else {
                stage.close();
            }
        });
        return controller.termsAccepted();
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
     * Creates a user in the database.
     *
     * @param firstName User's first name
     * @param lastName  User's last name
     * @param userName  User's username
     * @param email     User's email
     * @param password  User's password
     */
    public void createUser(String firstName, String lastName, String userName, String email, String password) throws DatabaseException {
        try {
            loginSetup(userDB.addUser(firstName, lastName, userName, email, password));
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    /**
     * Checks if a user already exists in the database.
     *
     * @return true if the user exists, false otherwise
     */
    public boolean userExists(String username) throws DatabaseException {
        try {
            if (userDB.userExists(username)) {
                new AlertWindow("Error", "This user already exists").showInformationWindow();
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
