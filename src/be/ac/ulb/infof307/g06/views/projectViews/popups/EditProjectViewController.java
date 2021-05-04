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
     * @param event ActionEvent, the event.
     */
    @FXML
    protected void events(ActionEvent event) {
        if (event.getSource() == editProjectBtn) {
            listener.onEditProject(project, nameProject.getText(), descriptionProject.getText(), dateProject.getValue(), endDateProject.getValue(), tagsProject.getCheckModel().getCheckedItems());
            close();
        }
    }

    /**
     * Sets values in  date fields and description field when editing a project.
     *
     * @param descriptionProject TextField where we put the description.
     * @param description String of the selected project description.
     * @param dateProject DatePicker where we put the start date.
     * @param startDate Long, the start date of the selected project.
     * @param endDateProject DatePicker where we put the end date.
     * @param endDate Long, the end date of the selected project.
     */
    static void setDescriptionTest(TextField descriptionProject, String description, DatePicker dateProject, Long startDate, DatePicker endDateProject, Long endDate) {
        descriptionProject.setText(description);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateProject.setValue(LocalDate.parse(dateFormat.format(startDate * 86400000L), formatter));
        endDateProject.setValue(LocalDate.parse(dateFormat.format(endDate * 86400000L), formatter));
    }

    /**
     * Initializes fields(name of the project and tags) related to the edition of a project.
     *
     * @param tags List of the selected project tags.
     */
    @FXML
    public void initFields(List<Tag> tags) {
        nameProject.setText(project.getTitle());
        setDescriptionTest(descriptionProject, project.getDescription(), dateProject, project.getStartDate(), endDateProject, project.getEndDate());
        for (Tag tag : tags) {
            tagsProject.getCheckModel().check(tag.getDescription());
        }
    }

    /**
     * Inits the edit stage.
     *
     * @param project Project, the selected project
     * @param listener ViewListener, the listener to the view.
     * @param stage Stage, the edit stage.
     * @param tags List of the selected project tags.
     */
    public void init(Project project, ProjectsViewController.ViewListener listener, Stage stage, List<Tag> tags) {
        init(listener, stage);
        this.project = project;
        initFields(tags);
    }
}