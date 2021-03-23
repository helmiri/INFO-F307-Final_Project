package be.ac.ulb.infof307.g06.views.ProjectViews;

import be.ac.ulb.infof307.g06.Main;
import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.Global;
import be.ac.ulb.infof307.g06.models.Project;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class EditProjectViewController extends ProjectInputViewController{

    @FXML
    private Button editProjectBtn;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        super.initialize(url, resourceBundle);
        try {
            initFields();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    protected void events(ActionEvent event) throws SQLException {
        if (event.getSource() == editProjectBtn) {
            controller.editProject(this);
            Main.closeStage();
        }
    }

    @FXML
    public void initFields() throws SQLException {
        int id = ProjectDB.getProjectID(Global.currentProject);
        Project project= ProjectDB.getProject(id);
        nameProject.setText(project.getTitle());
        descriptionProject.setText(project.getDescription());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String date = controller.dateToString(project.getDate());
        dateProject.setValue(LocalDate.parse(date, formatter));
    }

}