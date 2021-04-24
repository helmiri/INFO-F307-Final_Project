package be.ac.ulb.infof307.g06.controllers.settingsControllers;

import be.ac.ulb.infof307.g06.controllers.Controller;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.User;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.settingsViews.StorageViewController;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;

public class StorageController extends Controller implements StorageViewController.ViewListener {
    User currentUser;
    StorageViewController storageViewController;

    //--------------- METHODS ----------------
    public StorageController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene, StorageViewController storageViewController) {
        super(user_db, project_db, stage, scene);
        this.storageViewController = storageViewController;
    }

    @Override
    public void show() {
        currentUser = user_db.getCurrentUser();
        try {
            storageViewController.setListener(this);
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
            storageViewController.initialize(user_db.getDiskLimit(), user_db.getDiskUsage(), user_db.getCurrentUser().isAdmin(),
                    currentUser.getAccessToken(), currentUser.getClientID());
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        storageViewController.refresh(user_db.getDiskLimit(), user_db.getDiskUsage(), user_db.getCurrentUser().isAdmin(),
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

}
