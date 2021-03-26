package be.ac.ulb.infof307.g06.views.ProjectViews;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;

public class CloudTableController {
    //    public TableView<String> cloudTable;
    public TableColumn<String, String> cloudTable;
    public Button downloadBtn;

    @FXML
    public void initTree() {
//        cloudColumn.setCellValueFactory(new PropertyValueFactory<String, String>("description"));
//        cloudColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    public void events() {

    }
}
