package be.ac.ulb.infof307.g06.views.projectViews.popups;

import be.ac.ulb.infof307.g06.views.projectViews.ProjectsViewController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

import java.time.LocalDate;

@SuppressWarnings("rawtypes")
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

    //--------------- METHODS ----------------
    public void init(ProjectsViewController.ViewListener listener, Stage stage) {
        this.stage = stage;
        this.listener = listener;
        ObservableList<String> tags = listener.getAllTags();
        tagsProject.getItems().addAll(tags);
    }

    @FXML
    public void setError(String txt) {
        errorText.setText(txt);
    }

    @FXML
    public ObservableList<String> getSelectedTags() {
        return tagsProject.getCheckModel().getCheckedItems();
    }

    @FXML
    public LocalDate getDateProject() {
        return dateProject.getValue();
    }

    @FXML
    public LocalDate getEndDateProject() {
        return endDateProject.getValue();
    }

    @FXML
    public String getNameProject() {
        return nameProject.getText();
    }

    @FXML
    public String getDescriptionProject() {
        return descriptionProject.getText();
    }

    @FXML
    public String getParentProjectName() {
        return parentProject.getText();
    }

    public void close() {
        stage.close();
    }
}
