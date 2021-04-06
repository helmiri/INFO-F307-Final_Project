package be.ac.ulb.infof307.g06.controllers.connection;

import be.ac.ulb.infof307.g06.controllers.MainMenuController;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.User;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class ConnectionEngine extends Application implements SignUpController.Listener, LoginController.Listener, MainMenuController.Listener {
    private UserDB userDB;
    private ProjectDB projectDB;
    private Stage stage;
    private boolean isFirstBoot;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Set main stage
        this.stage = stage;

        try {
            String DB_PATH = "Database.db";
            userDB = new UserDB(DB_PATH);
            projectDB = new ProjectDB(DB_PATH);
            isFirstBoot = userDB.isFirstBoot();
        } catch (SQLException | ClassNotFoundException throwables) {
            //alertWindow(Alert.AlertType.ERROR, "Database error", "Could not access the database");
            return;
        }
        showLogin();
    }

    @Override
    public void showLogin() {
        LoginController controller = new LoginController(stage, this);
        try {
            controller.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLogin(String passwd, String user) {
        int res = 0;
        try {
            res = userDB.validateData(passwd, user);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        switch (res) {
            case 0 -> new AlertWindow("Login Error", "This user does not exist or the password/username is wrong").errorWindow();
            case -1 -> new AlertWindow("Login Error", "This user is already connected").errorWindow();
            default -> {
                try {
                    User current = userDB.getUserInfo(res);
                    userDB.setCurrentUser(current);
                    projectDB.setCurrentUser(current);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                showMainMenu();
            }
        }
    }

    @Override
    public void onSignup() {
        SignUpController controller = new SignUpController(stage, this, stage.getScene());
        try {
            controller.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showMainMenu() {
        stage.setOnCloseRequest(e -> {
            logout();
            Platform.exit();
            System.exit(0);
        });
        setAdminIfFirstBoot();
        MainMenuController controller = new MainMenuController(userDB, projectDB, stage, stage.getScene());
        controller.setListener(this);
        controller.show();
    }

    private void setAdminIfFirstBoot() {
        if (isFirstBoot) {
            try {
                userDB.setAdmin(256 * 1000000);
            } catch (SQLException throwables) {
                new AlertWindow("Database error", "Could not access the database").errorWindow();
            }
        }
    }

    @Override
    public void createUser(String firstName, String lastName, String userName, String email, String password) {
        try {
            User current = userDB.getUserInfo(userDB.addUser(firstName, lastName, userName, email, password));
            userDB.setCurrentUser(current);
            projectDB.setCurrentUser(current);
        } catch (SQLException e) {
            new AlertWindow("Error", "An error has occurred when adding the user to the database: " + e).errorWindow();
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
            new AlertWindow("Error", "An error has occurred with the database when checking if the user already exists: " + e).errorWindow();
            return true;
        }
    }

    @Override
    public void logout() {
        try {
            userDB.disconnectUser();
        } catch (SQLException e) {
            new AlertWindow("Error", "Couldn't disconnect the user: " + e).errorWindow();
        }
    }

}
