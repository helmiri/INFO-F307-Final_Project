package be.ac.ulb.infof307.g06.views.projectViews.popups;

import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.views.projectViews.ProjectsViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

    /**
     * The main method for button's events.
     *
     * @param event ActionEvent, the event.
     */
    @FXML
    private void taskEvents(ActionEvent event) {
        if (event.getSource() == editTaskBtn) {
            listener.onEditTask(task.getDescription(), taskName.getText(), newStartDateTask.getValue().toEpochDay(), newEndDateTask.getValue().toEpochDay(), task);
            stage.close();
        }
    }

    /**
     * Initializes the task edition.
     *
     * @param task Task, the selected task.
     * @param listener ViewListener, listener to the view.
     * @param stage Stage, the edit task stage.
     */
    public void init(Task task, ProjectsViewController.ViewListener listener, Stage stage) {
        this.task = task;
        this.listener = listener;
        this.stage = stage;
        EditProjectViewController.setDescriptionTest(taskName, task.getDescription(), newStartDateTask, task.getStartDate(), newEndDateTask, task.getEndDate());
    }
}
