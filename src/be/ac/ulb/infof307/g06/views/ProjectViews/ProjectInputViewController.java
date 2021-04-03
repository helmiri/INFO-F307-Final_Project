package be.ac.ulb.infof307.g06.views.ProjectViews;

import be.ac.ulb.infof307.g06.controllers.project.ProjectController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.controlsfx.control.CheckComboBox;
import javafx.scene.text.Text;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ProjectInputViewController {
    //---------- ATTRIBUTES ----------------
    @FXML
    protected CheckComboBox tagsProject;
    @FXML
    protected DatePicker dateProject;
    @FXML
    protected TextField descriptionProject;
    @FXML
    protected TextField nameProject;
    @FXML
    protected TextField parentProject;
    @FXML
    protected Text errorText;
    protected ProjectsViewController.ViewListener listener;

    //--------------- METHODS ----------------
    public void init(ProjectsViewController.ViewListener listener) {
        this.listener = listener;
        ObservableList<String> tags = listener.getAllTags();
        tagsProject.getItems().addAll(tags);
    }

    /**
     * The main method for button's events.
     *
     * @param event ActionEvent
     */
    @FXML
    protected void events(ActionEvent event){}
    @FXML
    public void setError(String txt){ errorText.setText(txt); }
    @FXML
    public ObservableList<String> getSelectedTags(){ return tagsProject.getCheckModel().getCheckedItems(); }
    @FXML
    public LocalDate getDateProject(){return dateProject.getValue();}
    @FXML
    public String getNameProject(){return nameProject.getText();}
    @FXML
    public String getDescriptionProject(){return descriptionProject.getText();}
    @FXML
    public String getParentProjectName(){return parentProject.getText();}
}
