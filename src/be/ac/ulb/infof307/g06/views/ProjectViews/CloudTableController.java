package be.ac.ulb.infof307.g06.views.ProjectViews;

import be.ac.ulb.infof307.g06.models.Cloud;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.Metadata;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class CloudTableController implements Initializable {
    @FXML
    private AnchorPane downloadAnchor;
    @FXML
    private TableView<String> cloudTable;
    @FXML
    private TableColumn<String, String> filesColumn;
    @FXML
    private Button downloadBtn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        filesColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        filesColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        HashMap<String, String> usrCreds = null;
        List<Metadata> files = null;
        try {
            usrCreds = UserDB.getCloudCredentials();
            Cloud.init(usrCreds.get("accToken"), usrCreds.get("clientID"));
            files = Cloud.getFiles();
        } catch (SQLException | DbxException | IOException throwables) {
            throwables.printStackTrace();
        }

        Cloud.filterFolders(files);
        for (Metadata metadata : files) {
            cloudTable.getItems().add(metadata.getPathDisplay());
        }

    }


    private void downloadFiles() throws NoSuchAlgorithmException, IOException, DbxException {
        Cloud.downloadFile("test.txt", cloudTable.getSelectionModel().getSelectedItem());
        System.out.println("Downloaded");
    }

    public void events(javafx.event.ActionEvent event) throws NoSuchAlgorithmException, IOException, DbxException {
        if (event.getSource() == downloadBtn) {
            downloadFiles();
        }
    }
}
