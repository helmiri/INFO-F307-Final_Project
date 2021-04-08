package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.StorageViewController;
import be.ac.ulb.infof307.g06.views.TagsViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class StorageController extends Controller implements StorageViewController.ViewListener {
    //--------------- METHODS ----------------
    public StorageController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene) {
        super(user_db, project_db, stage, scene);
    }

    @Override
    public void show() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(TagsViewController.class.getResource("StorageView.fxml"));
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        StorageViewController storageViewController = loader.getController();
        storageViewController.setListener(this);
        stage.setScene(scene);

        try {
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
            storageViewController.initialize(user_db.getDiskLimit(), user_db.getDiskUsage(), user_db.isAdmin());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public boolean saveSettings(String clientID, String accessToken, String limit) throws SQLException {
        boolean res = false;
        if (!clientID.isBlank() && !accessToken.isBlank()) {
            user_db.addCloudCredentials(accessToken, clientID);
            res = true;
        } else if (clientID.isBlank() && accessToken.isBlank()) {
            res = false;
        } else if (clientID.isBlank() || accessToken.isBlank()) {
            new AlertWindow("Settings error", "Missing credentials").errorWindow();
            res = false;
        }

        if (user_db.isAdmin()) {
            String value = limit;
            if (!value.isBlank()) {
                user_db.setLimit(Integer.parseInt(limit));
                res = true;
            }

        }
        return res;
    }

    @Override
    public void onBackButtonClicked() {
        stage.hide();
        stage.setScene(prevScene);
        stage.show();
    }
}
