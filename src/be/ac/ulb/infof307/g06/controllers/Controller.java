package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class Controller {
    protected Stage stage;
    protected Scene scene;
    protected UserDB user_db;
    protected ProjectDB project_db;
    protected int userID;
    protected Scene prevScene;

    public Controller(int userID, UserDB user_db, ProjectDB project_db, Stage stage, Scene scene) {
        this.userID = userID;
        this.user_db = user_db;
        this.project_db = project_db;
        this.stage = stage;
        this.prevScene = scene;
    }

    public abstract void show();

}
