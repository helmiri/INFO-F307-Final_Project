package be.ac.ulb.infof307.g06.views;

import be.ac.ulb.infof307.g06.controllers.ProjectController;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.Main;
import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.Global;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.text.Text;
import org.controlsfx.control.CheckComboBox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProjectsViewController {
    //----------ATTRIBUTES---------
    // ---------PROJECTS MENU------
    @FXML
    private TextField nameProject;
    @FXML
    private TextField descriptionProject;
    @FXML
    private DatePicker dateProject;
    @FXML
    private CheckComboBox tagsProject;
    @FXML
    private TextField parentProject;
    @FXML
    private TreeTableView<Project> treeProjects;
    @FXML
    private TreeTableColumn<Project, String> treeProjectColumn;

    private TreeItem<Project> root = new TreeItem<Project>();
    @FXML
    private Button addProjectBtn;
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
    //---------------EDIT PROJECTS----------
    @FXML
    private Button EditProjectBtn;
    @FXML
    private TextField newNameProject;
    @FXML
    private TextField newdescription;
    @FXML
    private DatePicker newDateProject;
    @FXML
    private TextField newTagsProject;
    @FXML
    private Text errorText;
    //----------------CONTROLLER--------------
    private ProjectController controller;
    //---------------METHODES----------------

    /**
     * The main method for button's events
     * @param event;
     * @throws Exception;
     */
    @FXML
    private void events(ActionEvent event) throws Exception {
        //if( event.getSource()== addTaskbtn){ controller.addTask(); }
        if( event.getSource()== addProjectBtn){ controller.addProject(); }
        //else if( event.getSource()== EditProjectBtn){editProject();}
        else if( event.getSource()== backBtn){ Main.showMainMenuScene(); }
    }
    public TreeItem<Project> getSelectedProject(){return treeProjects.getSelectionModel().getSelectedItem();}
    @FXML
    public void initTree() {
        treeProjectColumn.setCellValueFactory(new TreeItemPropertyValueFactory<Project, String>("title"));
        taskColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("description"));
        taskTable.setEditable(true);
        taskColumn.setCellFactory(TextFieldTableCell.forTableColumn());
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
        parent.getChildren().add(child);//
    }

    @FXML
    public void addTags(ObservableList<String> tags){
        tagsProject.getItems().addAll(tags);//
    }

    /**
     * Creates a project and add it to the Database and the map + displays it in the tree table view
     * @throws SQLException;
     */

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
    @FXML
    public void setError(String txt){ errorText.setText(txt); }

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
            clearProjects();
        }
    }

    /**
     * Creates a pop up window to show details of a project; its description and tags
     * @param event;
     * @throws SQLException;
     */
    @FXML
    private void showDetailsProject(ActionEvent event) throws SQLException{
        String projectName = treeProjects.getSelectionModel().getSelectedItem().getValue().getTitle();
        int projectID= ProjectDB.getProjectID(projectName);
        Project showProject= ProjectDB.getProject(projectID);
        String description= showProject.getDescription();

        List<Tag> tags = ProjectDB.getTags(projectID);
        List<String> tagStrings = new ArrayList<>();
        for (int i = 0; i<tags.size(); i++){
            tagStrings.add(tags.get(i).getDescription());
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Details");
        alert.setHeaderText(null);
        alert.getDialogPane().setMinWidth(600);
        alert.setResizable(false);
        alert.setContentText("DESCRIPTION:\n" +description+"\n\n"+"TAGS:\n"+tagStrings);
        alert.showAndWait();
    }

    /**
     * Edits a project
     * @throws SQLException;
     */
    /*
    @FXML
    private void editProject() throws SQLException{
        if (projectSelection.getSelectionModel().getSelectedItem()==null){
            errorText.setText("Please select a project.");}
        else if (ProjectDB.getProjectID(newNameProject.getText()) != 0 ) {
            errorText.setText("Cannot edit the project with such a title.");}
        else if (newNameProject.getText() == ""){
            errorText.setText("Cannot edit a project with an empty name.");}
        else {
            String selection = projectSelection.getSelectionModel().getSelectedItem().toString();
            int projectID= ProjectDB.getProjectID(selection);
            ProjectDB.editProject(projectID, newNameProject.getText(), newdescription.getText(), newTagsProject.getText(), newDateProject.getValue().toEpochDay());
            errorText.setText("");
            clearProjects();
        }
    }
    */

    @FXML
    public void displayTask() throws SQLException {
        TreeItem<Project> selectedProject = getSelectedProject();
        ObservableList<Task> oTaskList = controller.getTasks(selectedProject);
        taskTable.setItems(oTaskList);
    }

    public void editTask(TableColumn.CellEditEvent event) throws SQLException {
        Task task = (Task) event.getRowValue();
        String newDescription = (String) event.getNewValue();
        String description = task.getDescription();//
        controller.editTask(description, newDescription, task);
    }
    public void addTask() throws Exception{
        //taskColumn.setCellValueFactory(new PropertyValueFactory<ProjectDB.Task, String>("description"));
        controller.addTask(descriptionTask.getText(), taskParent.getText());
        displayTask();
    }

    public void deleteTask() throws SQLException{
        Task task = getSelectedTask();
        controller.deleteTask(task);
    }

    @FXML
    public Task getSelectedTask(){
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        taskTable.getItems().removeAll(selectedTask);
        return selectedTask;
    }


}
