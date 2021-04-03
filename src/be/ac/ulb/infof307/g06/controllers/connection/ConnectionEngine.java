package be.ac.ulb.infof307.g06.controllers.connection;

import be.ac.ulb.infof307.g06.controllers.MainMenuController;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class ConnectionEngine extends Application implements SignUpController.Listener, LoginController.Listener, MainMenuController.Listener {
    public final String DB_PATH = "Database.db";
    public UserDB user_db;
    public ProjectDB project_db;
    public Stage stage;
    public boolean isFirstBoot;
    private int userID;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Set main stage
        this.stage = stage;
        userID = 0;
        try {
            user_db = new UserDB(DB_PATH);
            project_db = new ProjectDB(DB_PATH);
            isFirstBoot = user_db.isFirstBoot();
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
            res = user_db.validateData(user, passwd);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        userID = res;
        switch (res) {
//            case 0 -> loginErrMsg.setText("This user does not exist or the password/username is wrong")
//
//            case -1 -> loginErrMsg.setText("This user is already connected");

            default -> showMainMenu();
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
        MainMenuController controller = new MainMenuController(userID, user_db, project_db, stage);
        controller.setListener(this);
        controller.show();
    }

    @Override
    public void createUser(String firstName, String lastName, String userName, String email, String password) {
        try {
            userID = user_db.addUser(firstName, lastName, userName, email, password);
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
            return user_db.userExists(username);
        } catch (SQLException e) {
            new AlertWindow("Error", "An error has occurred with the database when checking if the user already exists: " + e).errorWindow();
            return true;
        }
    }

    @Override
    public void logout() {
        try {
            user_db.disconnectUser();
        } catch (SQLException e) {
            new AlertWindow("Error", "Couldn't disconnect the user: " + e).errorWindow();
        }
    }
}
