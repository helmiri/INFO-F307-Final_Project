package be.ac.ulb.infof307.g06.controllers.connectionControllers;

import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.exceptions.WindowLoadException;
import be.ac.ulb.infof307.g06.views.connectionViews.LoginViewController;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller for the login view.
 */
public class LoginController extends ConnectionController implements LoginViewController.ViewListener {

    /**
     * Constructor
     *
     * @param stage Stage, a stage
     */
    public LoginController(Stage stage) throws DatabaseException {
        super(stage);
    }

    /**
     * Shows the login screen
     */
    @Override
    public void show() throws WindowLoadException {
        try {
            LoginViewController controller = (LoginViewController) loadView(LoginViewController.class, "LoginView.fxml");
            controller.setListener(this);
            controller.show(stage);
        } catch (IOException e) {
            throw new WindowLoadException(e);
        }
    }

    /**
     * Validates user data for the log in.
     *
     * @param username Username (String)
     * @param password User's password (String)
     */
    @Override
    public String login(String username, String password) {
        int res;
        String errorMessage = "";
        try {
            res = userDB.validateData(username, password);
            if (res == 0) {
                errorMessage = "This user does not exist or the password/username is wrong";
            } else if (res == -1) {
                errorMessage = "This user is already connected";
            } else {
                loginSetup(res);
            }
        } catch (SQLException e) {
            new DatabaseException(e, "An error occurred retrieving the user data").show();
        }
        return errorMessage;
    }

    @Override
    public void signupClicked() {
        try {
            SignUpController controller = new SignUpController(stage);
            controller.show();
        } catch (DatabaseException e) {
            e.show();
        }
    }
}
