package be.ac.ulb.infof307.g06.views;
import be.ac.ulb.infof307.g06.controllers.ProjectController;
import be.ac.ulb.infof307.g06.controllers.SettingsController;
import be.ac.ulb.infof307.g06.database.UserDB;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.Main;
import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.Global;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class TagsViewController implements Initializable{
    //-------------- ATTRIBUTES ----------------
    @FXML
    private Button backBtn;
    @FXML
    private Button addBtn;
    @FXML
    private ColorPicker tagsColorPicker;
    @FXML
    private TextField defaultTagNameTextField;
    @FXML
    private TableView<Tag> defaultTagsTableView;
    @FXML
    private TableColumn<Tag, String> defaultTagsColumn;
    private SettingsController controller;
    //--------------- METHODS ----------------
    @FXML
    private void events(ActionEvent event) throws Exception{
        if(event.getSource() == backBtn) {
            Main.showSettingsMenuScene();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        controller = new SettingsController();
        defaultTagsColumn.setCellValueFactory(new PropertyValueFactory<Tag, String>("description"));
        defaultTagsColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        defaultTagsTableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Tag tag, boolean empty) {
                if (tag != null) {
                    super.updateItem(tag, empty);
                    setText(tag.getDescription());
                    System.out.println(tag.getDescription());
                    setStyle("-fx-background-color: " + tag.getColor() + ";");
                }
            }
        });
        try {
            defaultTagsTableView.setItems(FXCollections.observableArrayList(ProjectDB.getAllTags()));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
