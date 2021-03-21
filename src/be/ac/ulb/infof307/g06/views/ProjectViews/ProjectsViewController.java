package be.ac.ulb.infof307.g06.views.ProjectViews;

import be.ac.ulb.infof307.g06.controllers.ProjectController;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.Main;
import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.Global;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.text.Text;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class ProjectsViewController implements Initializable {
    //----------ATTRIBUTES---------
    // ---------PROJECTS MENU------
    @FXML
    private TreeTableView<Project> treeProjects;
    @FXML
    private TreeTableColumn<Project, String> treeProjectColumn;

    private TreeItem<Project> root = new TreeItem<Project>();
    @FXML
    private Button addProjectBtn;
    @FXML
    private TextArea projectsTags;

    @FXML
    private TextArea projectsDescription;

    @FXML
    private Text projectsTitle;

    @FXML
    private Label projectsDate;

    @FXML
    private Button addBtn;
    @FXML
    private Button editBtn;
    // ----------------TASK---------------
    @FXML
    private TableView<Task> taskTable;
    @FXML
    private TableColumn<Task,String> taskColumn;
    @FXML
    private Button addTaskbtn;
    @FXML
    private Button backBtn;
    @FXML
    private TextArea descriptionTask;
    @FXML
    private TextField taskParent;

    //--------------COLLABORATORS----------

    @FXML
    private TableView<String> collaboratorsTable;
    @FXML
    private TableColumn<String, String> collaboratorsColumn;
    @FXML
    private Button addCollaboratorsBtn;
    @FXML
    private TextArea collaboratorsName;

    //---------------EDIT PROJECTS----------

    //----------------CONTROLLER--------------
    private ProjectController controller;
    //---------------METHODES----------------

    /**
     * The main method for button's events
     * @param ;
     * @throws Exception;
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        controller = new ProjectController();
        controller.init(this, root);
    }

    @FXML
    public void showRoot(){
        treeProjects.setShowRoot(false);
    }
    @FXML
    public void refresh(){
        treeProjects.setShowRoot(true);
    }

    /**
     * The main method for button's events
     * @param event ;
     * @throws Exception;
     */
    @FXML
    private void events(ActionEvent event) throws Exception {
        if( event.getSource()== addTaskbtn){ addTask(); }
        //else if( event.getSource()== EditProjectBtn){editProject();}
        else if( event.getSource()== addBtn ) {Main.showAddProjectStage(); }
        else if( event.getSource()== editBtn ) {
            if (projectsTitle.getText()!= ""){
                Global.currentProject = projectsTitle.getText();
                Main.showEditProjectStage();}
        }
        else if( event.getSource()== backBtn){ Main.showMainMenuScene(); }
    }
    public TreeItem<Project> getSelectedProject(){return treeProjects.getSelectionModel().getSelectedItem();}
    @FXML
    public void initTree() {
        treeProjectColumn.setCellValueFactory(new TreeItemPropertyValueFactory<Project, String>("title"));
        taskColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("description"));
        collaboratorsColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        taskTable.setEditable(true);
        collaboratorsTable.setEditable(true);
        taskColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        collaboratorsColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        treeProjects.setRoot(root);
    }

    /**
     * Clears the tables and the map(to refresh when needed)
     * @throws SQLException;
     */
    public void clearProjects(){
        treeProjects.getRoot().getChildren().clear();
        /*
        projectSelection.getItems().clear();
        projectSelection.setPromptText("Select project")*/
    }

    @FXML
    public void addChild(TreeItem<Project> parent, TreeItem<Project> child){
        parent.getChildren().add(child);
    }



    /**
     * Creates a project and add it to the Database and the map + displays it in the tree table view
     * @throws SQLException;
     */


    /**
     * Deletes a project in the Database and in the tree table view
     * @param event;
     * @throws SQLException;
     */
    @SuppressWarnings("unchecked")
    @FXML
    private void deleteProject(ActionEvent event) throws SQLException{
        if(treeProjects.getSelectionModel().getSelectedItem()!= null && treeProjects.getSelectionModel().getSelectedItem().getValue()!=null) {
            Project child= treeProjects.getSelectionModel().getSelectedItem().getValue();
            String projectName = child.getTitle();
            int projectID = ProjectDB.getProjectID(projectName);
            ProjectDB.deleteProject(projectID);
            int parentID = child.getParent_id();

            if (parentID == 0) {
                root.getChildren().removeAll(treeProjects.getSelectionModel().getSelectedItem());
            } else {
                Global.TreeMap.get(parentID).getChildren().removeAll(treeProjects.getSelectionModel().getSelectedItem());
            }
        }
    }


    public void deleteCollaborator() throws SQLException{
        String collaborator = getSelectedUser();
        controller.deleteCollaborator(collaborator, getSelectedProject().getValue().getId());
        collaboratorsTable.getItems().removeAll(collaborator);
    }

    @FXML
    public void displayTask() throws SQLException {
        TreeItem<Project> selectedProject = getSelectedProject();
        ObservableList<Task> oTaskList = controller.getTasks(selectedProject);
        taskTable.setItems(oTaskList);
    }

    @FXML
    public void displayCollaborators() throws SQLException {
        TreeItem<Project> selectedProject = getSelectedProject();
        ObservableList<String> oCollaboratorsList = controller.getCollaborators(selectedProject);
        collaboratorsTable.setItems(oCollaboratorsList);
    }

    public void editTask(TableColumn.CellEditEvent event) throws SQLException {
        Task task = (Task) event.getRowValue();
        String newDescription = (String) event.getNewValue();
        String description = task.getDescription();//
        controller.editTask(description, newDescription, task);
    }
    public void addTask() throws Exception{
        controller.addTask(descriptionTask.getText(),getSelectedProject().getValue().getTitle());
        displayTask();
    }

    public void deleteTask() throws SQLException{
        Task task = getSelectedTask();
        controller.deleteTask(task);
        taskTable.getItems().removeAll(task);
    }

    public void addCollaborator() throws SQLException{
        if(collaboratorsName.getText() != ""){
            if(!controller.addCollaborator(collaboratorsName.getText(), getSelectedProject().getValue().getId())){
                // TODO Show "error user doesn't exist" message
            } else {
                // TODO Show "invitation sent" message
            }
        }
    }
    @FXML
    public Task getSelectedTask(){
        return taskTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    public String getSelectedUser(){
        return collaboratorsTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    public void displayProject() throws SQLException {
        try {
            TreeItem<Project> selectedProject = getSelectedProject();
            String description = selectedProject.getValue().getDescription();
            String title = selectedProject.getValue().getTitle();
            Long date = selectedProject.getValue().getDate();
            int id = selectedProject.getValue().getId();
            List<Tag> tags = ProjectDB.getTags(id);
            ObservableList<String> tagsName = FXCollections.observableArrayList();
            for (Tag tag : tags) { tagsName.add(tag.getDescription()); }
            projectsDescription.setText(description);
            projectsDate.setText(controller.dateToString(date));
            projectsTitle.setText(title);
            projectsTags.setText(String.valueOf(tagsName));
            displayTask();
            displayCollaborators();
        }catch(NullPointerException throwables){
            projectsDescription.setText("");
            projectsDate.setText("");
            projectsTitle.setText("");
        }

    }

}
