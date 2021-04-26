package be.ac.ulb.infof307.g06.controllers.projectControllers;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.cloudModels.DropBox.DropBoxAPI;
import be.ac.ulb.infof307.g06.models.cloudModels.DropBox.DropBoxAuthorization;
import be.ac.ulb.infof307.g06.models.cloudModels.GoogleDrive.GoogleDriveAPI;
import be.ac.ulb.infof307.g06.models.cloudModels.GoogleDrive.GoogleDriveAuthorization;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.projectViews.CloudSelectionViewController;
import be.ac.ulb.infof307.g06.views.projectViews.CloudViewController;
import com.dropbox.core.DbxException;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CloudServiceController implements CloudSelectionViewController.ViewListener, CloudViewController.ViewListener {
    private final ProjectController projectController;
    private final UserDB userDB;
    private DropBoxAPI dbxClient;
    private GoogleDriveAPI gDriveClient;
    private boolean isDBox;
    private List<Metadata> dboxFiles;
    private List<com.google.api.services.drive.model.File> gDriveFiles;
    private DropBoxAuthorization authorization;

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
        isDBox = false;
        try {
            if (gDriveClient == null) {
                GoogleDriveAuthorization authorization = new GoogleDriveAuthorization(userDB.getCurrentUser().getUserName());
                NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
                gDriveClient = new GoogleDriveAPI(authorization.getCredentials(httpTransport), httpTransport);
            }
            gDriveFiles = gDriveClient.getFiles();
        } catch (GeneralSecurityException | IOException e) {
            new AlertWindow("Error", "Access denied").errorWindow();
        }
    }

    /**
     * Selects and initializes the DropBox API as the service provider
     */
    @Override
    public void selectDropBox() {
        isDBox = true;
        try {
            DbxCredential credential = userDB.getDropBoxCredentials();
            if (credential == null) {
                throw new DbxException("Credentials not found. Make sure that you granted access in settings");
            } else if (dbxClient == null) {
                dbxClient = new DropBoxAPI(credential.getAccessToken(), credential.getAppKey());
            }
            dboxFiles = dbxClient.getFiles();
        } catch (DbxException | SQLException e) {
            new AlertWindow("Credential error", e.getMessage()).errorWindow();
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
        if (localPath.isBlank()) {
            return;
        }
        if (isDBox) {
            downloadDropBox(cloudPath, localPath);
        } else {
            downloadGoogleDrive(cloudPath, localPath);
        }
    }

    /**
     * Downloads a file from the user's Google Drive account
     *
     * @param cloudPath The list of paths to the target files in the cloud storage
     * @param localPath The directory where to save the files on disk
     */
    private void downloadGoogleDrive(List<String> cloudPath, String localPath) {
        List<com.google.api.services.drive.model.File> fileMetas = getGoogleDriveFiles(cloudPath);
        try {
            for (com.google.api.services.drive.model.File fileMeta : fileMetas) {
                boolean download = false;
                String localFilePath = localPath + "/" + fileMeta.getName();
                File localFile = new File(localFilePath);
                if (!localFile.exists()) {
                    download = true;
                } else if (isGoogleDriveFileIdentical(localFile, Objects.requireNonNull(fileMeta))) {
                    new AlertWindow("Identical files", "The file " + fileMeta.getName() + " already exists").informationWindow();
                } else {
                    download = true;
                }
                if (!download) {
                    continue;
                }
                gDriveClient.downloadFile(localFilePath, Objects.requireNonNull(fileMeta).getId());
                projectController.importProject(localFilePath);
            }
        } catch (IOException e) {
            new AlertWindow("Error", "An error occurred: " + e.getMessage());
        }
        gDriveFiles = null;
    }

    /**
     * Retrieves the metadata of the selected files
     *
     * @param cloudPaths The list of paths to the target files in the cloud storage
     * @return A list of the target files' metadata
     */
    private List<com.google.api.services.drive.model.File> getGoogleDriveFiles(List<String> cloudPaths) {
        List<com.google.api.services.drive.model.File> files = new ArrayList<>();
        for (String cloudPath : cloudPaths) {
            for (com.google.api.services.drive.model.File file : gDriveFiles) {
                if (file.getName().equals(cloudPath)) {
                    files.add(file);
                }
            }
        }
        return files;
    }

    /**
     * Downloads a file from the user's DropBox account
     *
     * @param cloudPaths The list of paths to the target files in the cloud storage
     * @param localPath  The directory where to save the files on disk
     */
    private void downloadDropBox(List<String> cloudPaths, String localPath) {
        List<Metadata> fileMetas = getDropBoxFiles(cloudPaths);

        for (Metadata fileMeta : fileMetas) {
            String localFilePath = localPath + "/" + fileMeta.getName();
            File localFile = new File(localFilePath);
            boolean download = false;
            if (!localFile.exists()) {
                download = true;
            } else if (isDropBoxFileIdentical(localFilePath, (FileMetadata) fileMeta)) {
                new AlertWindow("Identical files", "The file " + fileMeta.getName() + " already exists").informationWindow();
            } else {
                download = true;
            }
            if (!download) {
                continue;
            }
            try {
                dbxClient.downloadFile(localFilePath, fileMeta.getPathDisplay());
            } catch (IOException | DbxException | NoSuchAlgorithmException e) {
                new AlertWindow("Error", "An error occurred: " + e.getMessage());
            }
            projectController.importProject(localFilePath);
        }
        dboxFiles = null;
    }

    /**
     * Retrieves the metadata of the selected files
     *
     * @param cloudPaths The list of paths to the target files in the cloud storage
     * @return A list of the target files' metadata
     */
    private List<Metadata> getDropBoxFiles(List<String> cloudPaths) {
        List<Metadata> fileMetas = new ArrayList<>();
        for (String cloudPath : cloudPaths) {
            for (Metadata metadata : dboxFiles) {
                if (metadata.getPathDisplay().equals(cloudPath)) {
                    fileMetas.add(metadata);
                    break;
                }
            }
        }
        return fileMetas;
    }

    /**
     * Compares files using the Drop Box hashing algorithm
     *
     * @param localPath Path to the local file to be compared
     * @param fileMeta  Metadata of the file in the cloud storage
     * @return true if identical, false otherwise
     */
    private boolean isDropBoxFileIdentical(String localPath, FileMetadata fileMeta) {
        String hash = dbxClient.getHash(localPath);
        if (hash == null){
            return false;
        }
        return fileMeta.getContentHash().equals(hash);
    }

    /**
     * Compares files using the Google Drive hashing algorithm
     *
     * @param localFile Path to the local file to be compared
     * @param cloudFile Metadata of the file in the cloud storage
     * @return true if identical, false otherwise
     */
    private boolean isGoogleDriveFileIdentical(File localFile, com.google.api.services.drive.model.File cloudFile) throws IOException {
        String localChecksum = gDriveClient.getHash(localFile);
        return localChecksum.equals(cloudFile.getAppProperties().get("hash"));
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
            List<String> files;
            if (isDBox) {
                if (dboxFiles == null) {
                    return;
                }
                files = dBoxToStrings(dboxFiles);
            } else {
                if (gDriveFiles == null) {
                    return;
                }
                files = gDriveToStrings(gDriveFiles);
            }
            controller.show(files);
        } catch (IOException e) {
            new AlertWindow("Error", "An error has occurred : " + e).errorWindow();
        }
    }

    /**
     * Extract the names of the files from the Google Drive metadata
     *
     * @param gDriveFiles The metadata of the files
     * @return A list containing the names of the files
     */
    private List<String> gDriveToStrings(List<com.google.api.services.drive.model.File> gDriveFiles) {
        List<String> res = new ArrayList<>();
        for (com.google.api.services.drive.model.File entry : gDriveFiles) {
            res.add(entry.getName());
        }
        return res;
    }

    /**
     * Extract the paths of the files from the DropBox metadata
     *
     * @param dboxFiles The metadata of the files
     * @return A list containing the paths of the files
     */
    private List<String> dBoxToStrings(List<Metadata> dboxFiles) {
        List<String> res = new ArrayList<>();
        for (Metadata entry : dboxFiles) {
            res.add(entry.getPathDisplay());
        }
        return res;
    }

    /**
     * Prompts the user for their cloud service of choice
     *
     * @param isDownload true if the operation to be performed is a download to show the files contained in the cloud storage
     */
    public void showSelectionStage(boolean isDownload) {
        FXMLLoader loader = new FXMLLoader(CloudSelectionViewController.class.getResource("CloudSelectionView.fxml"));
        try {
            AnchorPane selectionPane = loader.load();
            CloudSelectionViewController controller = loader.getController();
            controller.setListener(this);
            controller.show(selectionPane);
            if (isDownload) {
                showCloudDownloadStage();
            }
        } catch (IOException e) {
            new AlertWindow("Error", "Unable to load window").errorWindow();
        }
    }

    /**
     * Uploads a project using the appropriate api
     *
     * @param fileName      The file name of the file to be stored in the cloud
     * @param localFilePath The path to the file to be uploaded
     * @return true on success, false otherwise
     */
    public boolean uploadProject(String fileName, String localFilePath) {
        try {
            if (isDBox) {
                dbxClient.uploadFile(localFilePath, fileName);
            } else {
                gDriveClient.uploadFile(localFilePath, fileName.substring(1));
            }
        } catch (IOException | DbxException e) {
            return false;
        }
        return true;
    }

}



