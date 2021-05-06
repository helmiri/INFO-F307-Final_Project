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
    private final Long TO_DAY = 86400000L;

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
        setDescriptionTest(taskName, task.getDescription(), newStartDateTask, task.getStartDate(), newEndDateTask, task.getEndDate());
    }

    /**
     * Sets values in  date fields and description field when editing a project.
     *
     * @param descriptionProject TextField where we put the description.
     * @param description        String of the selected project description.
     * @param dateProject        DatePicker where we put the start date.
     * @param startDate          Long, the start date of the selected project.
     * @param endDateProject     DatePicker where we put the end date.
     * @param endDate            Long, the end date of the selected project.
     */
    private void setDescriptionTest(TextField descriptionProject, String description, DatePicker dateProject, Long startDate, DatePicker endDateProject, Long endDate) {
        descriptionProject.setText(description);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateProject.setValue(LocalDate.parse(dateFormat.format(startDate * TO_DAY), formatter));
        endDateProject.setValue(LocalDate.parse(dateFormat.format(endDate * TO_DAY), formatter));
    }
}
