package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import javafx.stage.Stage;

public abstract class Controller {
    protected Stage stage;
    protected UserDB user_db;
    protected ProjectDB project_db;
    protected int userID;

    public Controller(int userID, UserDB user_db, ProjectDB project_db, Stage stage) {
        this.userID = userID;
        this.user_db = user_db;
        this.project_db = project_db;
        this.stage = stage;
    }

    public abstract void show();

}
