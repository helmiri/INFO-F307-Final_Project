package be.ac.ulb.infof307.g06.controllers.projectControllers;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.cloudModels.DropBox.DropBoxAPI;
import be.ac.ulb.infof307.g06.models.cloudModels.GoogleDriveAPI;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.projectViews.CloudSelectionViewController;
import be.ac.ulb.infof307.g06.views.projectViews.CloudViewController;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class CloudServiceController implements CloudSelectionViewController.ViewListener, CloudViewController.ViewListener {
    private final ProjectController projectController;
    private String accessToken;
    private String clientID;
    private DropBoxAPI dbxClient;
    private GoogleDriveAPI gDriveClient;
    private boolean isDBox;

    private List<Metadata> dboxFiles;

    public CloudServiceController(ProjectController projectController, UserDB userDB) throws SQLException {
        this.projectController = projectController;
        HashMap<String, String> credentials = userDB.getCloudCredentials();
        accessToken = credentials.get("accessToken");
        clientID = credentials.get("clientID");
    }

    /**
     * Save a file by choosing it's path with a filechooser.
     *
     * @return
     */
    public String saveFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(new Stage());
        if (file == null) {
            return "";
        }
        return file.getAbsolutePath();
    }

    /**
     * Download a file from the cloud
     *
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws DbxException
     */
    private void downloadFiles() throws NoSuchAlgorithmException, IOException, DbxException {


    }

    @Override
    public void selectGoogleDrive() {
        isDBox = false;
    }

    @Override
    public void selectDropBox() {
        isDBox = true;
        try {
            if (dbxClient == null) {
                dbxClient = new DropBoxAPI(accessToken, clientID);
            }
            dboxFiles = dbxClient.getFiles();
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void downloadFile(String cloudPath) {
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

    private void downloadGoogleDrive(String cloudPath, String localPath) {

    }

    private void downloadDropBox(String cloudPath, String localPath) {
        Metadata fileMeta = getFile(cloudPath);
        File localFile = new File(localPath);
        boolean download = false;
        if (!localFile.exists()) {
            download = true;
        } else if (isFileIdentical(cloudPath, localPath, (FileMetadata) fileMeta)) {
            new AlertWindow("Identical files", "The file already exists").informationWindow();
        } else {
            download = true;
        }

        if (download) {
            try {
                dbxClient.downloadFile(localPath, cloudPath);
            } catch (IOException | DbxException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
//        projectController.importProject(localPath);
    }

    private Metadata getFile(String cloudPath) {
        Metadata fileMeta = null;
        for (Metadata metadata : dboxFiles) {
            if (metadata.getPathDisplay().equals(cloudPath)) {
                fileMeta = metadata;
                break;
            }
        }
        return fileMeta;
    }

    private boolean isFileIdentical(String cloudPath, String localPath, FileMetadata fileMeta) {
        return fileMeta.getContentHash().equals(dbxClient.dropBoxHash(localPath));
    }

    /**
     * Sets the loader to show the stage to edit a project.
     */
    public void showCloudDownloadStage() {

        try {
            FXMLLoader loader = new FXMLLoader(CloudViewController.class.getResource("CloudView.fxml"));
            AnchorPane cloudPane = loader.load();
            CloudViewController controller = loader.getController();
            controller.setListener(this);
            controller.show(dboxFiles, cloudPane);
        } catch (IOException e) {
            // TODO Exception
        }
        // TODO Download Stage
//        MainController.showStage("Add project", 750, 400, Modality.APPLICATION_MODAL, loader);
    }

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
            e.printStackTrace();
        }
    }

    public void uploadProject(String fileName, String localFilePath) {
        try {
            dbxClient.uploadFile(localFilePath, fileName);
        } catch (IOException | DbxException e) {
            e.printStackTrace();
        }

    }
}



