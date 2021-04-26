package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;

public abstract class Controller {
    //--------------- ATTRIBUTES ----------------
    protected Stage stage;
    protected Scene scene;
    protected UserDB user_db;
    protected ProjectDB project_db;
    protected Scene prevScene;

    //--------------- METHODS ----------------
    public Controller(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene) {
        this.user_db = user_db;
        this.project_db = project_db;
        this.stage = stage;
        prevScene = scene;
    }

    public void back() {
        stage.setScene(prevScene);
        stage.sizeToScene();
    }

    public abstract void show() throws SQLException;
}
