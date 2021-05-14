package be.ac.ulb.infof307.g06.controllers.connectionControllers;

import be.ac.ulb.infof307.g06.controllers.Controller;
import be.ac.ulb.infof307.g06.controllers.MainMenuController;
import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.models.User;
import be.ac.ulb.infof307.g06.models.database.ActiveUser;
import be.ac.ulb.infof307.g06.models.database.DatabaseConnection;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Class that serves as a base for LoginController and SignupController
 */
public abstract class ConnectionController extends Controller {
    //-------------- ATTRIBUTES ----------------
    protected final UserDB userDB;

    /**
     * Handles login/signup
     *
     * @param newStage The application newStage
     * @throws DatabaseException On error accessing the database
     */
    public ConnectionController(Stage newStage) throws DatabaseException {
        super(newStage);
        try {
            userDB = new UserDB();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        setStageCloseRequest();
    }

    //-------------- METHODS ----------------

    /**
     * Shows the main menu.
     */
    protected final void showMainMenu() {
        setStageCloseRequest();
        try {
            setAdminIfFirstBoot();
        } catch (DatabaseException e) {
            e.show();
            stage.close(); // Close the app and return to main
            return;
        }
        MainMenuController controller = new MainMenuController(stage);
        controller.show();
    }

    /**
     * Sets admin to first boot
     */
    private void setAdminIfFirstBoot() throws DatabaseException {
        try {
            if (userDB.isFirstBoot()) {
                userDB.setAdmin(256 * 1000000);
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    /**
     * Sets the application cleanup method on close
     */
    protected final void setStageCloseRequest() {
        stage.setOnCloseRequest(e -> {
            try {
                userDB.disconnectUser();
                DatabaseConnection.getInstance().closeConnection();
            } catch (SQLException error) {
                new DatabaseException(error).show();
            }
            Platform.exit();
        });
    }

    /**
     * Sets up the user information to be used across the application
     *
     * @param id The ID of the user
     * @throws SQLException on error accessing the database
     */
    protected final void loginSetup(int id) throws SQLException {
        User currentUser = userDB.getUserInfo(id);
        // Initialize the singleton to be used for user this user's specific queries
        ActiveUser.initializeInstance(currentUser);
        userDB.updateCurrentUser();
        showMainMenu();
    }
}
