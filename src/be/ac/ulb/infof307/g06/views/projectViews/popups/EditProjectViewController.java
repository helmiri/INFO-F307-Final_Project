package be.ac.ulb.infof307.g06.views.projectViews.popups;

import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Tag;
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
    protected void events(ActionEvent event) {
        if (event.getSource() == editProjectBtn) {
            listener.onEditProject(project, nameProject.getText(), descriptionProject.getText(), dateProject.getValue(), endDateProject.getValue(), tagsProject.getCheckModel().getCheckedItems());
            close();
        }
    }

    static void setDescriptionTest(TextField descriptionProject, String description, DatePicker dateProject, Long startDate, DatePicker endDateProject, Long endDate) {
        descriptionProject.setText(description);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateProject.setValue(LocalDate.parse(dateFormat.format(startDate * 86400000L), formatter));
        endDateProject.setValue(LocalDate.parse(dateFormat.format(endDate * 86400000L), formatter));
    }

    /**
     * Initializes the fields related to the edition of a project.
     */
    @FXML
    public void initFields(List<Tag> tags) {
        nameProject.setText(project.getTitle());
        setDescriptionTest(descriptionProject, project.getDescription(), dateProject, project.getStartDate(), endDateProject, project.getEndDate());
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