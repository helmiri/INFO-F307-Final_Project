package be.ac.ulb.infof307.g06.views.projectViews;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Cloud;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CloudViewController{
    //--------------- ATTRIBUTES ----------------
    @FXML
    private AnchorPane downloadAnchor;
    @FXML
    private TableView<String> cloudTable;
    @FXML
    private TableColumn<String, String> filesColumn;
    @FXML
    private Button downloadBtn;

    private static List<Metadata> files;

    //--------------- METHODS ----------------
    public void initialize() {
        filesColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        filesColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        HashMap<String, String> usrCreds = null;
        files = null;
        try {
//            usrCreds = UserDB.getCloudCredentials();
            Cloud.init(usrCreds.get("accToken"), usrCreds.get("clientID"));
            files = Cloud.getFiles();
            for (Metadata metadata : filterValidFiles(files)) {
                cloudTable.getItems().add(metadata.getPathDisplay());
            }
        } catch (IOException throwables) {
            new AlertWindow("Error", "An error occurred").errorWindow();
        } catch (DbxException e) {

            new AlertWindow("Cloud service", "Error connecting to the service. Check your cloud service configuration.").errorWindow();
        } finally {
            cloudTable.setPlaceholder(new Label("Could not retrieve the content"));
        }
    }

    /*
    public List<Metadata> filterFolders(List<Metadata> entries) {
        List<Metadata> folders = new ArrayList<>();

        for (int i = 0; i < entries.size(); i++) {
            for (int j = 0; j < entries.size(); j++) {
                if (entries.get(j).getPathDisplay().contains(entries.get(i).getPathDisplay())
                        && !entries.get(j).getPathDisplay().equals(entries.get(i).getPathDisplay())) {
                    folders.add(entries.get(i));
                    entries.remove(j);
                }

            }
        }
        return folders;
    }*/

    /**
     * Returns the valid files contained in the dropbox.
     * @param entries List of all the files contained in the cloud.
     * @return
     */
    public List<Metadata> filterValidFiles(List<Metadata> entries) {
        List<Metadata> lst = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            String pathDisplay = entries.get(i).getPathDisplay();
            if (pathDisplay.contains(".tar.gz")) {
                lst.add(entries.get(i));
            }
        }
        return lst;
    }

    /**
     * Download a file from the cloud
     *
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws DbxException
     */
    private void downloadFiles() throws NoSuchAlgorithmException, IOException, DbxException {
        String localPath = saveFile();
        if (localPath.isBlank()) {
            return;
        }
        String cloudPath = cloudTable.getSelectionModel().getSelectedItem();
        Metadata fileMeta = null;
        for (Metadata metadata : files) {
            if (metadata.getPathDisplay().equals(cloudPath)) {
                fileMeta = metadata;
                break;
            }
        }

        File localFile = new File(localPath);

        if (!localFile.exists()) {
            Cloud.downloadFile(localPath, cloudPath);
            return;
        }


        if (((FileMetadata) fileMeta).getContentHash().equals(Cloud.dropBoxHash(localPath))) {
            new AlertWindow("Identical files", "The file already exists").informationWindow();
        } else {
            Cloud.downloadFile(localPath, cloudPath);
//            IOController.importProject(localPath);
        }
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
     * button handling
     * @param event
     */
    public void events(javafx.event.ActionEvent event) {
        if (event.getSource() == downloadBtn) {
            try {
                downloadFiles();
            } catch (NoSuchAlgorithmException | IOException e) {
                new AlertWindow("Error", "An error occurred").errorWindow();
            } catch (DbxException e) {
                new AlertWindow("Cloud service", "Error connecting to the service. Check your cloud service configuration.").errorWindow();
            }
        }
    }

}
