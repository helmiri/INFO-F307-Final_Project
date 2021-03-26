package be.ac.ulb.infof307.g06.views;
import be.ac.ulb.infof307.g06.controllers.ProjectController;
import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.database.UserDB;
import be.ac.ulb.infof307.g06.models.Global;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.views.ProjectViews.ProjectInputViewController;
import be.ac.ulb.infof307.g06.views.ProjectViews.ProjectsViewController;
import com.google.gson.Gson;
import com.sun.source.tree.Tree;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import be.ac.ulb.infof307.g06.Main;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import java.util.ArrayList;


public class EditTaskViewController implements Initializable {
    @FXML
    private TextField taskName;
    @FXML
    private Button editTaskBtn;
    private ProjectController controller;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        controller = new ProjectController();
        taskName.setText(Global.selectedTask.getDescription());
    }

    @FXML
    private void taskEvents(ActionEvent event) throws SQLException {
        if (event.getSource() == editTaskBtn) {
            controller.editTask(Global.selectedTask.getDescription(), taskName.getText(), Global.selectedTask);
            Main.closeStage();
        }
    }
}
