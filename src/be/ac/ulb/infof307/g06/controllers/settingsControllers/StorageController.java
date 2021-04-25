package be.ac.ulb.infof307.g06.controllers.settingsControllers;

import be.ac.ulb.infof307.g06.controllers.Controller;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.User;
import be.ac.ulb.infof307.g06.models.cloudModels.DropBox.DropBoxAuthorization;
import be.ac.ulb.infof307.g06.models.cloudModels.GoogleDrive.GoogleDriveAuthorization;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.projectViews.CodePromptViewController;
import be.ac.ulb.infof307.g06.views.settingsViews.StorageViewController;
import com.dropbox.core.DbxException;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.oauth.DbxCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;

public class StorageController extends Controller implements StorageViewController.ViewListener, CodePromptViewController.ViewListener {
    User currentUser;
    StorageViewController storageViewController;
    private DropBoxAuthorization authorization;

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
    public boolean saveSettings(String limit, StorageViewController storageViewController) throws SQLException {
        boolean res = false;
//        res = saveCredentials(clientID, accessToken);
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

    @Override
    public void authenticateGoogleDrive() {
        GoogleDriveAuthorization authorization = new GoogleDriveAuthorization(user_db.getCurrentUser().getUserName());
        try {
            authorization.getCredentials(GoogleNetHttpTransport.newTrustedTransport());
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void authenticateDropBox() {
        try {
            authorization = new DropBoxAuthorization();
            String url = authorization.getUrl();
            FXMLLoader loader = new FXMLLoader(CodePromptViewController.class.getResource("CodePromptView.fxml"));
            AnchorPane pane = loader.load();
            CodePromptViewController controller = loader.getController();
            controller.setListener(this);
            controller.initialize(url, pane);
        } catch (JsonReader.FileLoadException | IOException e) {
            e.printStackTrace();
        }

    }

    private void setLimit(String limit) throws SQLException {
        int newLimit = Integer.parseInt(limit);
        if (newLimit == 0) {
            throw new NumberFormatException("Invalid number");
        }
        user_db.setLimit(Integer.parseInt(limit) * 1000L * 1000L);
    }

    @Override
    public void onOKClicked(String code) {
        try {
            DbxCredential credential = user_db.getDropBoxCredentials();
            if (credential == null) {
                credential = authorization.getAuthorization(code);
            }
            user_db.addDropBoxCredentials(credential);
        } catch (IOException | DbxException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHyperlinkClicked(String url) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(url);
        clipboard.setContent(content);
    }

//    private boolean saveCredentials(String clientID, String accessToken) throws SQLException {
//        boolean res = false;
//        if (!clientID.isBlank() && !accessToken.isBlank()) {
//            user_db.addCloudCredentials(accessToken, clientID);
//            res = true;
//        } else {
//            if (!accessToken.isBlank()) {
//                user_db.addAccessToken(accessToken);
//                res = true;
//            }
//            if (!clientID.isBlank()) {
//                user_db.addClientID(clientID);
//                res = true;
//            }
//        }
//        return res;
//    }

}
