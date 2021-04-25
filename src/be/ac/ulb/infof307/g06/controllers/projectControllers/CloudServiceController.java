package be.ac.ulb.infof307.g06.controllers.projectControllers;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.cloudModels.DropBox.DropBoxAPI;
import be.ac.ulb.infof307.g06.models.cloudModels.DropBox.DropBoxAuthorization;
import be.ac.ulb.infof307.g06.models.cloudModels.GoogleDrive.GoogleDriveAPI;
import be.ac.ulb.infof307.g06.models.cloudModels.GoogleDrive.GoogleDriveAuthorization;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.projectViews.CloudSelectionViewController;
import be.ac.ulb.infof307.g06.views.projectViews.CloudViewController;
import be.ac.ulb.infof307.g06.views.projectViews.CodePromptViewController;
import com.dropbox.core.DbxException;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CloudServiceController implements CloudSelectionViewController.ViewListener, CloudViewController.ViewListener, CodePromptViewController.ViewListener {
    private final ProjectController projectController;
    private final UserDB userDB;
    private final String accessToken;
    private final String clientID;
    private DropBoxAPI dbxClient;
    private GoogleDriveAPI gDriveClient;
    private boolean isDBox;
    private List<Metadata> dboxFiles;
    private List<com.google.api.services.drive.model.File> gDriveFiles;
    private DropBoxAuthorization authorization;

    public CloudServiceController(ProjectController projectController, UserDB userDB) throws SQLException {
        this.projectController = projectController;
        this.userDB = userDB;
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
        try {
            if (gDriveClient == null) {
                GoogleDriveAuthorization authorization = new GoogleDriveAuthorization(userDB.getCurrentUser().getUserName());
                NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
                gDriveClient = new GoogleDriveAPI(authorization.getCredentials(httpTransport), httpTransport);
            }
            gDriveFiles = gDriveClient.getFiles();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void selectDropBox() {
        isDBox = true;
        String url;
        boolean expired = true;
        try {
            DbxCredential credential = userDB.getDropBoxCredentials();
            if (credential != null) {
                Long expiration = credential.getExpiresAt();
                expired = Instant.now().getEpochSecond() * 1000 > expiration;
            }
            if (dbxClient == null && expired) {
                authorization = new DropBoxAuthorization();
                url = authorization.getUrl();
                FXMLLoader loader = new FXMLLoader(CodePromptViewController.class.getResource("CodePromptView.fxml"));
                AnchorPane pane = loader.load();
                CodePromptViewController controller = loader.getController();
                controller.setListener(this);
                controller.initialize(url, pane);
            } else {
                dbxClient = new DropBoxAPI(credential.getAccessToken(), credential.getAppKey());
            }
            dboxFiles = dbxClient.getFiles();
        } catch (JsonReader.FileLoadException | IOException | DbxException | SQLException e) {
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
        com.google.api.services.drive.model.File fileMeta = getGDriveFile(cloudPath);
        try {
            boolean download = false;
            File localFile = new File(localPath);
            if (!localFile.exists()) {
                download = true;
            } else if (isGFileIdentical(localFile, fileMeta)) {
                new AlertWindow("Identical files", "The file already exists").informationWindow();
            } else {
                download = true;
            }
            if (!download) {
                return;
            }
            gDriveClient.downloadFile(localPath, fileMeta.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private com.google.api.services.drive.model.File getGDriveFile(String cloudPath) {
        for (com.google.api.services.drive.model.File file : gDriveFiles) {
            if (file.getName().equals(cloudPath)) {
                return file;
            }
        }
        return null;
    }

    private void downloadDropBox(String cloudPath, String localPath) {
        Metadata fileMeta = getFile(cloudPath);
        File localFile = new File(localPath);
        boolean download = false;
        if (!localFile.exists()) {
            download = true;
        } else if (isFileIdentical(localPath, (FileMetadata) fileMeta)) {
            new AlertWindow("Identical files", "The file already exists").informationWindow();
        } else {
            download = true;
        }
        if (!download) {
            return;
        }
        try {
            dbxClient.downloadFile(localPath, cloudPath);
        } catch (IOException | DbxException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        projectController.importProject(localPath);
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

    private boolean isFileIdentical(String localPath, FileMetadata fileMeta) {
        return fileMeta.getContentHash().equals(dbxClient.getHash(localPath));
    }

    private boolean isGFileIdentical(File localFile, com.google.api.services.drive.model.File cloudFile) throws IOException {
        String localChecksum = gDriveClient.getHash(localFile);
        return localChecksum.equals(cloudFile.getAppProperties().get("hash"));
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
            List<String> files;
            if (isDBox) {
                files = dBoxToStrings(dboxFiles);
            } else {
                files = gDriveToStrings(gDriveFiles);
            }
            controller.show(files, cloudPane);
        } catch (IOException e) {
            // TODO Exception
        }
        // TODO Download Stage
//        MainController.showStage("Add project", 750, 400, Modality.APPLICATION_MODAL, loader);
    }

    private List<String> gDriveToStrings(List<com.google.api.services.drive.model.File> gDriveFiles) {
        List<String> res = new ArrayList<>();
        for (com.google.api.services.drive.model.File entry : gDriveFiles) {
            res.add(entry.getName());
        }
        return res;
    }

    private List<String> dBoxToStrings(List<Metadata> list) {
        List<String> res = new ArrayList<>();
        for (Metadata entry : list) {
            res.add(entry.getPathDisplay());
        }
        return res;
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
            if (isDBox) {
                dbxClient.uploadFile(localFilePath, fileName);
            } else {
                gDriveClient.uploadFile(localFilePath, fileName.substring(1));
            }
        } catch (IOException | DbxException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onOKClicked(String code) {
        try {
            DbxCredential credential = userDB.getDropBoxCredentials();
            if (credential == null) {
                credential = authorization.getAuthorization(code);
            }
            dbxClient = new DropBoxAPI(credential.getAccessToken(), credential.getAppKey());
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
}



