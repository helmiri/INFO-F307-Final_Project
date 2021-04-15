package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.User;
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
    User currentUser;

    //--------------- METHODS ----------------
    public StorageController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene) {
        super(user_db, project_db, stage, scene);
    }

    @Override
    public void show() {
        StorageViewController storageViewController;
        currentUser = user_db.getCurrentUser();
        try {
            storageViewController = getStorageViewController();
            storageViewController.setListener(this);
            stage.setScene(scene);
            stage.sizeToScene();
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
            storageViewController.initialize(user_db.getDiskLimit(), user_db.getDiskUsage(), user_db.isAdmin(),
                    currentUser.getAccessToken(), currentUser.getClientID());
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private StorageViewController getStorageViewController() throws IOException {
        FXMLLoader loader = new FXMLLoader(TagsViewController.class.getResource("StorageView.fxml"));
        scene = new Scene(loader.load());
        return loader.getController();
    }

    @Override
    public boolean saveSettings(String clientID, String accessToken, String limit, StorageViewController storageViewController) throws SQLException {
        boolean res;
        res = saveCredentials(clientID, accessToken);
        if (user_db.isAdmin()) {
            if (!limit.isBlank()) {
                try {
                    setLimit(limit);
                    res = true;
                } catch (NumberFormatException e) {
                    new AlertWindow("Invalid parameter", "The disk usage limit must be a valid integer number").errorWindow();
                }
            }
        }
        storageViewController.refresh(user_db.getDiskLimit(), user_db.getDiskUsage(), user_db.isAdmin(),
                currentUser.getAccessToken(), currentUser.getClientID());
        return res;
    }

    private void setLimit(String limit) throws SQLException {
        int newLimit = Integer.parseInt(limit);
        if (newLimit == 0) {
            throw new NumberFormatException("Invalid number");
        }
        user_db.setLimit(Integer.parseInt(limit) * 1000L * 1000L);
    }

    private boolean saveCredentials(String clientID, String accessToken) throws SQLException {
        boolean res = false;
        if (!clientID.isBlank() && !accessToken.isBlank()) {
            user_db.addCloudCredentials(accessToken, clientID);
            res = true;
        } else {
            if (!accessToken.isBlank()) {
                user_db.addAccessToken(accessToken);
                res = true;
            }
            if (!clientID.isBlank()) {
                user_db.addClientID(clientID);
                res = true;
            }
        }
        return res;
    }

    @Override
    public void onBackButtonClicked() {
        stage.setScene(prevScene);
        stage.sizeToScene();
    }
}
