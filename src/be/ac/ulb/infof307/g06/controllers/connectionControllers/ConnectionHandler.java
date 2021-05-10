package be.ac.ulb.infof307.g06.controllers.connectionControllers;

import be.ac.ulb.infof307.g06.controllers.MainMenuController;
import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.models.encryption.EncryptedFile;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Main controller class for the connection.
 */
public class ConnectionHandler implements SignUpController.Listener, LoginController.Listener, MainMenuController.Listener {
    //-------------- ATTRIBUTES ----------------
    private final UserDB userDB;
    private final ProjectDB projectDB;
    private final Stage stage;
    private final boolean isFirstBoot;
    private final String DECRYPTED_DB_PATH;
    private final String ENCRYPTED_DB_PATH;

    /**
     /**
     * Constructor.
     *
     * @param userDB UserDB, the user database
     * @param projectDB ProjectDB, the projects database
     * @param stage Stage, a stage
     * @param isFirstBoot boolean, to see if it's the first time launching the app
     * @param DECRYPTED_DB_PATH String, path to the decrypted database
     * @param DB_PATH String, the path to the database
     */
    public ConnectionHandler(UserDB userDB, ProjectDB projectDB, Stage stage, boolean isFirstBoot, String DECRYPTED_DB_PATH, String DB_PATH) {
        this.userDB = userDB;
        this.projectDB = projectDB;
        this.stage = stage;
        this.isFirstBoot = isFirstBoot;
        this.DECRYPTED_DB_PATH = DECRYPTED_DB_PATH;
        ENCRYPTED_DB_PATH = DB_PATH;
    }

    //-------------- METHODS ----------------


    /**
     * Manages the display of the log in window.
     */
    @Override
    public void showLogin() {
        LoginController controller = new LoginController(stage, this);
        controller.show();
    }

    /**
     * Validates user data for the log in.
     *
     * @param username Username (String)
     * @param password User's password (String)
     */
    @Override
    public void onLogin(String username, String password) {
        int res = 0;
        try {
            res = userDB.validateData(username, password);
        } catch (SQLException e) {
            new AlertWindow("error", "" + e).showErrorWindow();
        }
        switch (res) {
            case 0 -> new AlertWindow("Login Error", "This user does not exist or the password/username is wrong").showErrorWindow();
            case -1 -> new AlertWindow("Login Error", "This user is already connected").showErrorWindow();
            default -> {
                try {
                    userDB.getUserInfo(res);
                } catch (SQLException e) {
                    new AlertWindow("error", "" + e).showErrorWindow();
                }
                showMainMenu();
            }
        }
    }

    /**
     * Manages the display of the sign up window.
     */
    @Override
    public void onSignup() {
        SignUpController controller = new SignUpController(stage, this, stage.getScene());
        controller.show();
    }

    /**
     * Shows the main menu.
     */
    @Override
    public void showMainMenu() {
        stage.setOnCloseRequest(e -> {
            logout();
            EncryptedFile databaseFile = new EncryptedFile("QwAtb5wcgChC2u3@f,]/bnd\"", DECRYPTED_DB_PATH);
            try {
                databaseFile.encryptFile(ENCRYPTED_DB_PATH);
                new File(DECRYPTED_DB_PATH).delete();
            } catch (IOException ioException) {
                new AlertWindow("Error", "Could not commit the database", ioException.getMessage()).showErrorWindow();
            }
            Platform.exit();
            System.exit(0);
        });
        setAdminIfFirstBoot();
        MainMenuController controller = new MainMenuController(userDB, projectDB, stage, stage.getScene(), DECRYPTED_DB_PATH);
        controller.setListener(this);
        controller.show();
    }

    /**
     * Sets admin to first boot
     */
    private void setAdminIfFirstBoot() {
        if (isFirstBoot) {
            try {
                userDB.setAdmin(256 * 1000000);
            } catch (SQLException e) {
                new DatabaseException(e).show();
            }
        }
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
    @Override
    public void createUser(String firstName, String lastName, String userName, String email, String password) {
        try {
            userDB.getUserInfo(userDB.addUser(firstName, lastName, userName, email, password));
        } catch (SQLException e) {
            new DatabaseException(e).show();
        }
    }


    /**
     * Checks if a user already exists in the database.
     *
     * @return boolean
     */
    @Override
    public boolean doesUserExist(String username) {
        try {
            return userDB.userExists(username);
        } catch (SQLException e) {
            new DatabaseException(e).show();
            return true;
        }
    }

    /**
     * Logs the user out.
     */
    @Override
    public void logout() {
        try {
            userDB.disconnectUser();
            userDB.disconnectDB();
        } catch (SQLException e) {
            new AlertWindow("Error", "Couldn't disconnect the user: " + e).showErrorWindow();
        }
    }

}
