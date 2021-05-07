package be.ac.ulb.infof307.g06.controllers.settingsControllers;

import be.ac.ulb.infof307.g06.controllers.Controller;
import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.User;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.settingsViews.ProfileViewController;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileController extends Controller implements ProfileViewController.ViewListener {
    ProfileViewController viewController;
    User user;

    public ProfileController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene, ProfileViewController controller) {
        super(user_db, project_db, stage, scene);
        viewController = controller;
    }

    /**
     * Initializes the scene
     */
    @Override
    public void show() {
        user = user_db.getCurrentUser();
        viewController.setListener(this);
        viewController.initialize(user.getUserName(), user.getFirstName(), user.getLastName(), user.getEmail());
    }

    /**
     * Validates and saves the new first name
     *
     * @param field The new first name
     * @return true on success, false otherwise
     */
    @Override
    public boolean saveFirstName(String field) {
        if (validateTextField(field, "^[^±!@£$%^&*_+§¡€#¢§¶•ªº«\\/<>?:;|=.,]{1,64}$")) {
            return false;
        }
        user.setFirstName(field);
        return !syncSettingsFailed();
    }

    /**
     * Save the new last name
     *
     * @param field The last name
     * @return true on success, false otherwise
     */
    @Override
    public boolean saveLastName(String field) {
        if (validateTextField(field, "^[^±!@£$%^&*_+§¡€#¢§¶•ªº«\\/<>?:;|=.,]{1,64}$")) {
            return false;
        }
        user.setLastName(field);
        return !syncSettingsFailed();
    }


    /**
     * Validates the user's inputs according to a pattern given.
     *
     * @param field   TextField
     * @param pattern String
     * @return true on fail, false on success
     */
    public boolean validateTextField(String field, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(field);
        if (!m.matches()) {
            showWarning();
            return true;
        }
        return false;
    }

    /**
     * Validates and saved the new email
     *
     * @param field The new emil
     * @return true on success, false otherwise
     */
    @Override
    public boolean saveEmail(String field) {
        if (validateTextField(field, "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            return false;
        }
        user.setEmail(field);
        syncSettingsFailed();
        return false;
    }

    /**
     * Updates the user's information
     *
     * @return true on fail, false on success
     */
    private boolean syncSettingsFailed() {
        try {
            user_db.userSettingsSync(user, "");
        } catch (SQLException e) {
            new DatabaseException(e).show();
            return true;
        }
        return false;
    }

    /**
     * Validates and saves the new password
     *
     * @param newPassword          The new password
     * @param confirmationPassword The same password for confirmation
     */
    @Override
    public void savePassword(String newPassword, String confirmationPassword) {
        if (validateTextField(newPassword, "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#_$%!]).{6,16})")) {
            return;
        }
        if (!newPassword.equals(confirmationPassword)) {
            new AlertWindow("Error", "The passwords don't match").errorWindow();
            return;
        }
        try {
            user_db.userSettingsSync(null, newPassword);
            new AlertWindow("Success", "Password updated successfully").informationWindow();
        } catch (SQLException e) {
            new DatabaseException(e).show();
        }
    }

    /**
     * Alert shown to inform the user of malformed
     */
    private void showWarning() {
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
