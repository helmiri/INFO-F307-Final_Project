package be.ac.ulb.infof307.g06.controllers.projectControllers;

import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.projectViews.CloudSelectionViewController;
import be.ac.ulb.infof307.g06.views.projectViews.CloudViewController;
import com.dropbox.core.DbxException;
import javafx.fxml.FXMLLoader;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the cloud services used by the application. Here we have DropBox and Google Drive
 */
public class CloudServiceController implements CloudSelectionViewController.ViewListener, CloudViewController.ViewListener {
    private final ProjectController projectController;
    private final UserDB userDB;

    private DropBoxController dropBoxController;
    private GoogleDriveController googleDriveController;
    private boolean dropBoxSelected;
    private boolean googleDriveSelected;

    public CloudServiceController(ProjectController projectController, UserDB userDB) {
        this.projectController = projectController;
        this.userDB = userDB;
    }

    /**
     * Save a file by choosing it's path with a file chooser.
     *
     * @return The absolute path of the selected directory
     */
    public String saveFile() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File selectedDirectory = directoryChooser.showDialog(new Stage());
        if (selectedDirectory == null) {
            return "";
        }
        return selectedDirectory.getAbsolutePath();
    }

    /**
     * Selects and initializes the Google Drive API as the service provider
     */
    @Override
    public void selectGoogleDrive() {
        try {
            dropBoxSelected = false;
            googleDriveController = new GoogleDriveController(userDB.getCurrentUser().getUserName());
            googleDriveSelected = true;
        } catch (IOException error) {
            new AlertWindow("Error", "Could not retrieve the files ", error.getMessage()).showErrorWindow();
        } catch (GeneralSecurityException error) {
            new AlertWindow("Cloud service error", "Could not connect to google drive ", error.getMessage()).showErrorWindow();
        }
    }

    /**
     * Selects and initializes the DropBox API as the service provider
     */
    @Override
    public void selectDropBox() {
        try {
            googleDriveSelected = false;
            dropBoxController = new DropBoxController(userDB.getDropBoxCredentials());
            dropBoxSelected = true;
        } catch (SQLException error) {
            new DatabaseException(error).show();
        } catch (DbxException error) {
            new AlertWindow("Cloud service error", "An error occurred while trying to connect to DropBox", error.getMessage()).showErrorWindow();
        }
    }

    /**
     * Downloads the files in the user's cloud storage using the appropriate API
     *
     * @param cloudPath The path to the file in the cloud storage
     */
    @Override
    public void downloadFiles(List<String> cloudPath) {
        String localPath = saveFile();
        List<String> downloadedFiles = new ArrayList<>();
        if (localPath.isBlank()) {
            return;
        }
        try {
            if (dropBoxSelected) {
                downloadedFiles = dropBoxController.downloadFiles(cloudPath, localPath);
            } else if (googleDriveSelected) {
                downloadedFiles = googleDriveController.downloadFiles(cloudPath, localPath);
            }
        } catch (IOException error) {
            new AlertWindow("IOError", "An error occurred while saving the files", error.getMessage()).showErrorWindow();
        } catch (NoSuchAlgorithmException | DbxException error) {
            new AlertWindow("Connection Error", "An error occurred while fetching the files", error.getMessage()).showErrorWindow();
        }
        importFiles(downloadedFiles);
    }

    private void importFiles(List<String> downloadedFiles) {
        for (String file : downloadedFiles) {
            projectController.importProject(file);
        }
    }


    /**
     * Shows a table from which the user can select items
     */
    public void showCloudDownloadStage() {
        try {
            FXMLLoader loader = new FXMLLoader(CloudViewController.class.getResource("CloudView.fxml"));
            loader.load();
            CloudViewController controller = loader.getController();
            controller.setListener(this);
            List<String> files = new ArrayList<>();
            if (dropBoxSelected) {
                files = dropBoxController.fetchFiles();
            } else if (googleDriveSelected) {
                files = googleDriveController.fetchFiles();
            }
            controller.show(files);
        } catch (IOException error) {
            new AlertWindow("Error", "Could not read the file ", error.getMessage()).showErrorWindow();
        } catch (DbxException error) {
            new AlertWindow("DropBox Error", "An error has occurred. Make sure that your credentials are properly configured", error.getMessage()).showErrorWindow();
        }
    }


    /**
     * Prompts the user for their cloud service of choice
     *
     * @param isDownload true if the operation to be performed is a download to show the files contained in the cloud storage
     */
    public void showSelectionStage(boolean isDownload) {
        FXMLLoader loader = new FXMLLoader(CloudSelectionViewController.class.getResource("CloudSelectionView.fxml"));
        try {
            loader.load();
            CloudSelectionViewController controller = loader.getController();
            controller.setListener(this);
            controller.show();
            if (isDownload) {
                showCloudDownloadStage();
            }
        } catch (IOException error) {
            new AlertWindow("Error", "Unable to load window", error.getMessage()).showErrorWindow();
        }
    }

    /**
     * Uploads a project using the appropriate api
     *
     * @param fileName      The file name of the file to be stored in the cloud
     * @param localFilePath The path to the file to be uploaded
     */
    public void uploadProject(String fileName, String localFilePath) throws IOException, DbxException {

        if (dropBoxSelected) {
            dropBoxController.uploadFile(localFilePath, fileName);
        } else {
            googleDriveController.uploadFile(localFilePath, fileName.substring(1));
        }
    }
}
