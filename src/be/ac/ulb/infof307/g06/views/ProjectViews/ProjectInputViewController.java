package be.ac.ulb.infof307.g06.views.ProjectViews;

import be.ac.ulb.infof307.g06.controllers.ProjectController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ProjectInputViewController implements Initializable {
    @FXML
    protected TextField nameProject;
    @FXML
    protected TextField descriptionProject;
    @FXML
    protected DatePicker dateProject;
    @FXML
    protected CheckComboBox tagsProject;
    @FXML
    protected TextField parentProject;


    @FXML
    protected Text errorText;
    protected ProjectController controller;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        controller = new ProjectController();
        try {
            controller.initComboBox(this);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML
    protected void events(ActionEvent event)throws SQLException{};
    @FXML
    public void addTags(ObservableList<String> tags){
        tagsProject.getItems().addAll(tags);
    }

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
