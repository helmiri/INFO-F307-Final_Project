package be.ac.ulb.infof307.g06.views.ProjectViews;

import be.ac.ulb.infof307.g06.controllers.MainController;
import be.ac.ulb.infof307.g06.models.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class EditTaskViewController implements Initializable {
    //--------------- ATTRIBUTES ----------------
    @FXML
    private Button editTaskBtn;
    @FXML
    private TextField taskName;
    private Task task;
    private ProjectsViewController.ViewListener listener;

    //--------------- METHODS ----------------
    public void initialize(URL url, ResourceBundle resourceBundle) {
        taskName.setText(/*Global.selectedTask.getDescription()*/ "Test");

    }

    @FXML
    private void taskEvents(ActionEvent event) {
        if (event.getSource() == editTaskBtn) {
            listener.onEditTask(/*Global.selectedTask.getDescription()*/ "Test", taskName.getText(), task);
            MainController.closeStage();
        }
    }

    public void init(Task task, ProjectsViewController.ViewListener listener) {
        this.task = task;
        this.listener = listener;
    }
}
