package be.ac.ulb.infof307.g06.views;
import be.ac.ulb.infof307.g06.controllers.ProjectController;
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
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class TagsViewController {
    //---------DEFAULT TAGS---------
    @FXML
    private ColorPicker tagsColorPicker;
    @FXML
    private TextField defaultTagNameTextField;
    @FXML
    private Button backBtn;
    @FXML
    private TableView<Tag> defaultTagsTableView;
    @FXML
    private TableColumn<Tag, String> defaultTagsColumn;

}
