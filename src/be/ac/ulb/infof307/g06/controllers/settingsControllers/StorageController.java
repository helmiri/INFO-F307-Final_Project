package be.ac.ulb.infof307.g06.controllers.settingsControllers;

import be.ac.ulb.infof307.g06.controllers.Controller;
import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.models.AlertWindow;
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

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.sql.SQLException;

public class StorageController extends Controller implements StorageViewController.ViewListener, CodePromptViewController.ViewListener {
    private final StorageViewController storageViewController;
    private DropBoxAuthorization authorization;

    //--------------- METHODS ----------------
    public StorageController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene, StorageViewController storageViewController) {
        super(user_db, project_db, stage, scene);
        this.storageViewController = storageViewController;
    }

    @Override
    public void show() {
        try {
            storageViewController.setListener(this);
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
            storageViewController.initialize(user_db.getDiskLimit(), user_db.getDiskUsage(), user_db.getCurrentUser().isAdmin());
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
    }


    /**
     * Saves the storage limit to the database. (only available to admin users).
     *
     * @param limit                 The new storage limit
     * @param storageViewController the view controller (needed to refresh the scene)
     * @return true on success, false otherwise
     */
    @Override
    public boolean saveSettings(String limit, StorageViewController storageViewController) {
        boolean res = false;
        try {
            if (user_db.isAdmin() && !limit.isBlank()) {
                try {
                    setLimit(limit);
                    res = true;
                } catch (NumberFormatException error) {
                    new AlertWindow("Invalid parameter", "The disk usage limit must be a valid integer number: "+error).showErrorWindow();
                }
            }
            storageViewController.refresh(user_db.getDiskLimit(), user_db.getDiskUsage(), user_db.getCurrentUser().isAdmin());
        } catch (SQLException error) {
            new DatabaseException(error).show();
            return false;
        }
        return res;
    }

    /**
     * Establishes the connection with the GoogleDrive account.
     */
    @Override
    public void authenticateGoogleDrive() {
        AlertWindow alert = new AlertWindow("Authorization request", "Requesting authorization...\nDo not close the app. Click 'OK' to continue");
        alert.showInformationWindow();
        GoogleDriveAuthorization authorization = new GoogleDriveAuthorization(user_db.getCurrentUser().getUserName());
        try {
            authorization.getCredentials(GoogleNetHttpTransport.newTrustedTransport());
        } catch (IOException | GeneralSecurityException error) {
            new AlertWindow("Authorization request", "Access denied").showErrorWindow();
        }
    }

    /**
     * Establishes a connection with the Dropbox account.
     */
    @Override
    public void authenticateDropBox() {
        try {
            authorization = new DropBoxAuthorization();
            String url = authorization.getUrl();
            FXMLLoader loader = new FXMLLoader(CodePromptViewController.class.getResource("CodePromptView.fxml"));
            AnchorPane pane = loader.load();
            CodePromptViewController controller = loader.getController();
            controller.setListener(this);
            openBrowser(url);
            controller.initialize(url, pane);
        } catch (IOException error) {
            new AlertWindow("Error", "Could not load the client configuration", error.getMessage()).showErrorWindow();
        } catch (JsonReader.FileLoadException error) {
            new AlertWindow("Error", "An error occurred while setting up the connection", error.getMessage()).showErrorWindow();
        }
    }

    /**
     * Saves the storage limit on the disk on the database.
     */
    private void setLimit(String limit) throws SQLException {
        int newLimit = Integer.parseInt(limit);
        if (newLimit == 0) {
            throw new NumberFormatException("Invalid number");
        }
        user_db.setLimit(Integer.parseInt(limit) * 1000L * 1000L);
    }

    /**
     * Opens the browser to establish the connection to the cloud services.
     *
     * @param url Url to the cloud service website.
     */
    private void openBrowser(String url) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        Desktop desktop;
        if (Desktop.isDesktopSupported() && (desktop = Desktop.getDesktop()).isSupported(Desktop.Action.BROWSE)) {
            desktop.browse(URI.create(url));
        } else if (os.contains("mac")) {
            Runtime.getRuntime().exec("open " + url);
        } else if (os.contains("linux")) {
            Runtime.getRuntime().exec("xdg-open " + url);
        }
    }

    /**
     * Validates the code and stores the credentials
     *
     * @param code The authentication code
     */
    @Override
    public void onOKClicked(String code) {
        try {
            DbxCredential credential = user_db.getDropBoxCredentials();
            if (credential == null) {
                // Insert if no credentials found
                credential = authorization.getAuthorization(code);
                user_db.addDropBoxCredentials(credential);
            } else {
                // Update existing credentials if found
                credential = authorization.getAuthorization(code);
                user_db.updateDropBoxCredentials(credential);
            }
            new AlertWindow("Credentials saved", "Settings saved").showInformationWindow();
        } catch (DbxException error) {
            new AlertWindow("Error", "Could not complete the request", error.getMessage());
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
    }

    /**
     * On link clicked
     *
     * @param url link
     */
    @Override
    public void onHyperlinkClicked(String url) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(url);
        clipboard.setContent(content);
    }
}
