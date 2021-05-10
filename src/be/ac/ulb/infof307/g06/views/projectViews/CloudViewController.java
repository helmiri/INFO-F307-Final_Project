package be.ac.ulb.infof307.g06.views.projectViews;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
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
    private AnchorPane downloadAnchor;
    @FXML
    private TableView<String> cloudTable;
    @FXML
    private TableColumn<String, String> filesColumn;
    private ViewListener listener;
    private Stage stage;
    //--------------- METHODS ----------------

    /**
     * Shows a selection table
     *
     * @param files The list of names to be shown
     */
    public void show(List<String> files) {
        stage = new Stage();
        stage.setScene(new Scene(downloadAnchor));
        stage.setTitle("Dropbox Files");
        stage.show();

        filesColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        filesColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        cloudTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        try {
            for (String name : filterValidFiles(files)) {
                cloudTable.getItems().add(name);
            }
        } finally {
            cloudTable.setPlaceholder(new Label("No files to show"));
        }
    }


    /**
     * Returns the valid files contained in the dropbox.
     *
     * @param entries List of all the files contained in the cloud.
     * @return a list containing the names of the files that have a .tar.gz extension
     */
    public List<String> filterValidFiles(List<String> entries) {
        List<String> list = new ArrayList<>();
        for (String entry : entries) {
            if (entry.contains(".tar.gz")) {
                list.add(entry);
            }
        }
        return list;
    }

    /**
     * button handling
     **/
    public void events() {
        listener.downloadFiles(getSelectedItems());
        stage.close();
    }

    /**
     * Table selection
     *
     * @return a list containing the selected items
     */
    private List<String> getSelectedItems() {
        ObservableList<String> selected = cloudTable.getSelectionModel().getSelectedItems();
        return new ArrayList<>(selected);
    }

    //--------------- LISTENER ----------------

    /**
     * Sets the listener.
     *
     * @param listener ViewListener, the listener to the controller.
     */
    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    /**
     *
     */
    public interface ViewListener {
        void downloadFiles(List<String> item);
    }
}
