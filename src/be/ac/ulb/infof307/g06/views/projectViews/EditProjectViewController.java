package be.ac.ulb.infof307.g06.views.projectViews;

import be.ac.ulb.infof307.g06.controllers.MainController;
import be.ac.ulb.infof307.g06.models.Project;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditProjectViewController extends ProjectInputViewController{
    //---------- ATTRIBUTE ----------------
    @FXML
    private Button editProjectBtn;
    private Project project;
    //--------------- METHODS ----------------

    /**
     * The main method for button's events.
     *
     * @param event ActionEvent
     */
    @Override
    protected void events(ActionEvent event){
        if (event.getSource() == editProjectBtn) {
            listener.onEditProject(project, nameProject.getText(), descriptionProject.getText(), dateProject.getValue(), tagsProject.getCheckModel().getCheckedItems());
            MainController.closeStage();
        }
    }

    /**
     * Initializes the fields related to the edition of a project.
     */
    @FXML
    public void initFields() {
        nameProject.setText(project.getTitle());
        descriptionProject.setText(project.getDescription());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateProject.setValue(LocalDate.parse(dateFormat.format(project.getDate() * 86400000L), formatter));
    }

    public void init(Project project, ProjectsViewController.ViewListener listener) {
        init(listener);
        this.project = project;
        initFields();
    }
}