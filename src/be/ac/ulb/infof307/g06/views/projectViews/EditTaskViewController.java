package be.ac.ulb.infof307.g06.views.projectViews;

import be.ac.ulb.infof307.g06.controllers.MainController;
import be.ac.ulb.infof307.g06.models.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class EditTaskViewController {
    //--------------- ATTRIBUTES ----------------
    @FXML
    private Button editTaskBtn;
    @FXML
    private TextField taskName;
    @FXML
    protected DatePicker newStartDateTask;
    @FXML
    protected DatePicker newEndDateTask;
    private Task task;
    private ProjectsViewController.ViewListener listener;

    //--------------- METHODS ----------------

    @FXML
    private void taskEvents(ActionEvent event) {
        if (event.getSource() == editTaskBtn) {
            listener.onEditTask(task.getDescription(), taskName.getText(), newStartDateTask.getValue().toEpochDay(), newEndDateTask.getValue().toEpochDay(), task);
            MainController.closeStage();
        }
    }

    public void init(Task task, ProjectsViewController.ViewListener listener) {
        this.task = task;
        this.listener = listener;
        taskName.setText(task.getDescription());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        newStartDateTask.setValue(LocalDate.parse(dateFormat.format(task.getStartDate() * 86400000L), formatter));
        newEndDateTask.setValue(LocalDate.parse(dateFormat.format(task.getEndDate() * 86400000L), formatter));
    }
}
