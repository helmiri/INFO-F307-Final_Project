package be.ac.ulb.infof307.g06.views.projectViews.popups;

import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.views.projectViews.ProjectsViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
    private Stage stage;
    //--------------- METHODS ----------------

    @FXML
    private void taskEvents(ActionEvent event) {
        if (event.getSource() == editTaskBtn) {
            listener.onEditTask(task.getDescription(), taskName.getText(), newStartDateTask.getValue().toEpochDay(), newEndDateTask.getValue().toEpochDay(), task);
            stage.close();
        }
    }

    public void init(Task task, ProjectsViewController.ViewListener listener, Stage stage) {
        this.task = task;
        this.listener = listener;
        this.stage = stage;
        taskName.setText(task.getDescription());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        newStartDateTask.setValue(LocalDate.parse(dateFormat.format(task.getStartDate() * 86400000L), formatter));
        newEndDateTask.setValue(LocalDate.parse(dateFormat.format(task.getEndDate() * 86400000L), formatter));
    }
}
