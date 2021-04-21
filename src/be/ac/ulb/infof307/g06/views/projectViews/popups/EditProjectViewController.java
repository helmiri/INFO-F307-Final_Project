package be.ac.ulb.infof307.g06.views.projectViews.popups;

import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.views.projectViews.ProjectsViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
            listener.onEditProject(project, nameProject.getText(), descriptionProject.getText(), dateProject.getValue(), endDateProject.getValue(), tagsProject.getCheckModel().getCheckedItems());
            close();
        }
    }

    /**
     * Initializes the fields related to the edition of a project.
     */
    @FXML
    public void initFields(List<Tag> tags) {
        nameProject.setText(project.getTitle());
        descriptionProject.setText(project.getDescription());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateProject.setValue(LocalDate.parse(dateFormat.format(project.getStartDate() * 86400000L), formatter));
        endDateProject.setValue(LocalDate.parse(dateFormat.format(project.getEndDate() * 86400000L), formatter));
        for (Tag tag : tags) {
            tagsProject.getCheckModel().check(tag.getDescription());
        }
    }

    public void init(Project project, ProjectsViewController.ViewListener listener, Stage stage, List<Tag> tags) {
        init(listener, stage);
        this.project = project;
        initFields(tags);
    }
}