package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Base class for controllers. Controllers should extend this class to ensure proper functionality
 */
public abstract class Controller {
    //--------------- ATTRIBUTES ----------------
    protected Stage stage;
    protected Scene scene;
    protected UserDB user_db;
    protected ProjectDB project_db;
    protected Scene prevScene;
    protected String DB_PATH;

    /**
     * Constructor
     *
     * @param user_db UserDB, the user database
     * @param project_db ProjectDB, the project database
     * @param stage Stage, a stage
     * @param scene Scene, a scene
     * @param DB_PATH String, the path to the database
     */
    //--------------- METHODS ----------------
    public Controller(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene, String DB_PATH) {
        this.user_db = user_db;
        this.project_db = project_db;
        this.stage = stage;
        prevScene = scene;
        this.DB_PATH = DB_PATH;
    }

    /**
     * Returns to previous stage
     */
    public void back() {
        stage.setScene(prevScene);
        stage.sizeToScene();
    }

    /**
     * Shows the controller's screen
     *
     * @throws SQLException on error loading the view
     */
    public abstract void show() throws SQLException;
}
