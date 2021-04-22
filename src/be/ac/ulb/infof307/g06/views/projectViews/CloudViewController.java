package be.ac.ulb.infof307.g06.views.projectViews;

import com.dropbox.core.v2.files.Metadata;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class CloudViewController {
    //--------------- ATTRIBUTES ----------------
    @FXML
    private TableView<String> cloudTable;
    @FXML
    private TableColumn<String, String> filesColumn;
    private List<Metadata> files;
    private ViewListener listener;
    private Stage stage;
    //--------------- METHODS ----------------

    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    public void show(List<String> files, AnchorPane cloudPane) {
        stage = new Stage();
        stage.setScene(new Scene(cloudPane));
        stage.setTitle("Dropbox Files");
        stage.show();

        filesColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        filesColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        try {
            for (String name : filterValidFiles(files)) {
                cloudTable.getItems().add(name);
            }
        } finally {
            cloudTable.setPlaceholder(new Label("Could not retrieve the content"));
        }
    }


    /**
     * Returns the valid files contained in the dropbox.
     *
     * @param entries List of all the files contained in the cloud.
     * @return
     */
    public List<String> filterValidFiles(List<String> entries) {
        List<String> lst = new ArrayList<>();
        for (String entry : entries) {
            if (entry.contains(".tar.gz")) {
                lst.add(entry);
            }
        }
        return lst;
    }

    /**
     * button handling
     **/
    public void events() {
        listener.downloadFile(getSelectedItem());
        stage.close();
    }

    private String getSelectedItem() {
        return cloudTable.getSelectionModel().getSelectedItem();
    }

    public interface ViewListener {
        void downloadFile(String item);
    }
}
