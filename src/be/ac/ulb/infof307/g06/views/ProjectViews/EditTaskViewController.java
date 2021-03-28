package be.ac.ulb.infof307.g06.views.ProjectViews;
import be.ac.ulb.infof307.g06.controllers.MainController;
import be.ac.ulb.infof307.g06.controllers.ProjectController;
import be.ac.ulb.infof307.g06.models.Global;
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
    private ProjectController controller;
    //--------------- METHODS ----------------
    public void initialize(URL url, ResourceBundle resourceBundle) {
        controller = new ProjectController();
        taskName.setText(Global.selectedTask.getDescription());
    }

    @FXML
    private void taskEvents(ActionEvent event) {
        if (event.getSource() == editTaskBtn) {
            controller.editTask(Global.selectedTask.getDescription(), taskName.getText(), Global.selectedTask);
            MainController.closeStage();
        }
    }
}
