package be.ac.ulb.infof307.g06;

import be.ac.ulb.infof307.g06.controllers.connectionControllers.ConnectionHandler;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.SQLException;

public class Main extends Application {
    /**
     * Launches the main application
     *
     * @param args String[] Arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * Starts the main stage
     *
     * @param stage Stage
     */
    @Override
    public void start(Stage stage) {
        // Set main stage
        try {
            String DB_PATH = "Database.db";
            UserDB userDB = new UserDB(DB_PATH);
            ProjectDB projectDB = new ProjectDB(DB_PATH);
            boolean isFirstBoot = userDB.isFirstBoot();
            ConnectionHandler engine = new ConnectionHandler(userDB, projectDB, stage, isFirstBoot);
            engine.showLogin();
        } catch (SQLException | ClassNotFoundException throwables) {
            new AlertWindow("Database error", "Could not access the database").errorWindow();
        }

    }
}
