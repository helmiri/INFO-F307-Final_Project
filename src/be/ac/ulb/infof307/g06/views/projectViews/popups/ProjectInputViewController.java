package be.ac.ulb.infof307.g06.views.projectViews.popups;

import be.ac.ulb.infof307.g06.views.projectViews.ProjectsViewController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import java.time.LocalDate;

public class ProjectInputViewController {
    //---------- ATTRIBUTES ----------------
    @FXML
    protected CheckComboBox<String> tagsProject;
    @FXML
    protected DatePicker dateProject;
    @FXML
    protected DatePicker endDateProject;
    @FXML
    protected TextField descriptionProject;
    @FXML
    protected TextField nameProject;
    @FXML
    protected TextField parentProject;
    @FXML
    protected Text errorText;
    protected ProjectsViewController.ViewListener listener;
    protected Stage stage;

    /**
     * Initializes the project view.
     *
     * @param listener Listener, the listener to the view.
     * @param stage Stage, the stage to the project view.
     */
    //--------------- METHODS ----------------
    public void init(ProjectsViewController.ViewListener listener, Stage stage) {
        this.stage = stage;
        this.listener = listener;
        ObservableList<String> tags = listener.getAllTags();
        tagsProject.getItems().addAll(tags);
    }

    /**
     * Sets the error text field.
     *
     * @param txt String, the text to show.
     */
    @FXML
    public void setError(String txt) {
        errorText.setText(txt);
    }

    /**
     * @return the selected project tags.
     */
    @FXML
    public ObservableList<String> getSelectedTags() {
        return tagsProject.getCheckModel().getCheckedItems();
    }

    /**
     * @return the selected project date.
     */
    @FXML
    public LocalDate getDateProject() {
        return dateProject.getValue();
    }

    /**
     * @return the selected project end date.
     */
    @FXML
    public LocalDate getEndDateProject() {
        return endDateProject.getValue();
    }

    /**
     * @return the selected project name.
     */
    @FXML
    public String getNameProject() {
        return nameProject.getText();
    }

    /**
     * @return the selected project description.
     */
    @FXML
    public String getDescriptionProject() {
        return descriptionProject.getText();
    }

    /**
     * @return the selected project parent's name.
     */
    @FXML
    public String getParentProjectName() {
        return parentProject.getText();
    }

    /**
     * Close the project stage.
     */
    public void close() {
        stage.close();
    }
}
