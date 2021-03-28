package be.ac.ulb.infof307.g06.views.ProjectViews;

import be.ac.ulb.infof307.g06.controllers.ProjectController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.controlsfx.control.CheckComboBox;
import javafx.scene.text.Text;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ProjectInputViewController implements Initializable {
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
    protected ProjectController controller;

    //--------------- METHODS ----------------
    /**
     * Initializes the controller and launchs the initComboBox method.
     *
     * @param url URL
     * @param resourceBundle ResourceBundle
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        controller = new ProjectController();
        controller.initComboBox(this);

    }
    /**
     * The main method for button's events.
     *
     * @param event ActionEvent
     * @throws SQLException throws SQLException
     */
    @FXML
    protected void events(ActionEvent event)throws SQLException{}

    /**
     * Adds tags in the check combo box.
     *
     * @param tags ObservableList<String>
     */
    @FXML
    public void addTags(ObservableList<String> tags){ tagsProject.getItems().addAll(tags); }
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
