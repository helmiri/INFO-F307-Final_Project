package be.ac.ulb.infof307.g06.JavaUI.sample;

import be.ac.ulb.infof307.g06.database.*;
import be.ac.ulb.infof307.g06.database.ProjectDB.Project;
import be.ac.ulb.infof307.g06.database.ProjectDB.Task;

import be.ac.ulb.infof307.g06.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import be.ac.ulb.infof307.g06.database.ProjectDB;
import javafx.fxml.Initializable;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ProjectsController implements Initializable {
    // ---------PROJECTS MENU------
    @FXML
    private Button addBtn;

    @FXML
    private TextField nameProject;

    @FXML
    private TextField descriptionProject;

    @FXML
    private DatePicker dateProject;

    @FXML
    private TextField tagsProject;

    @FXML
    private TextField parentProject;

    @FXML
    private TreeTableView<ProjectDB.Project> treeProjects;

    @FXML
    private TreeTableColumn<ProjectDB.Project, String> treeProjectColumn;

    private TreeItem<ProjectDB.Project> root = new TreeItem<ProjectDB.Project>();

    @FXML
    private Button addProjectBtn;

    // ----------------TASK---------------

    @FXML
    private TableView<ProjectDB.Task> taskTable;

    @FXML
    private TextArea taskDescription;

    @FXML
    private TableColumn<ProjectDB.Task,String> taskColumn;

    @FXML
    private MenuItem addTaskMenu;

    @FXML
    private Button addTaskbtn;

    @FXML
    private Button backBtn;

    @FXML
    private TextArea descriptionTask;

    @FXML
    private TextField taskParent;

    //---------------METHODE----------------
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTree();
        try {
            loadProjects();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML
    private void Events(ActionEvent event) throws Exception {
        if( event.getSource()== addTaskMenu)        { Main.showTaskScene(); }
        else if( event.getSource()== addTaskbtn)    { addTask(); }
        else if( event.getSource()== addProjectBtn) { addProject(); }
        else if( event.getSource()== backBtn) { Main.ShowMainMenu(); }
    }

    @FXML
    private void initTree(){
        treeProjectColumn.setCellValueFactory(new TreeItemPropertyValueFactory<ProjectDB.Project, String>("title"));
        taskColumn.setCellValueFactory(new PropertyValueFactory<ProjectDB.Task, String>("description"));
        treeProjects.setRoot(root);
    }

    private void loadProjects() throws SQLException {
        List<Integer> projectsArray = ProjectDB.getUserProjects(Global.userID);
        getProjects(projectsArray, root);
    }

    public void getProjects(List<Integer> projects, TreeItem<ProjectDB.Project> parent) throws SQLException{
        treeProjects.setShowRoot(false);
        for(Integer project : projects){
            Project childProject= ProjectDB.getProject(project);
            int parentID= childProject.getParent_id();
            String title= childProject.getTitle();
            int childID= ProjectDB.getProjectID(title);

            System.out.println("Project= "+childProject+" parentID= "+parentID+ " childID= "+childID+ " description= "+title);

            TreeItem<ProjectDB.Project> child = new TreeItem<ProjectDB.Project>(childProject);
            Global.TreeMap.put(childID, child);
            if (parentID== 0){
                root.getChildren().add(child);
            } else {
                Global.TreeMap.get(parentID).getChildren().add(child);
            }
        }
        treeProjects.setShowRoot(true);
    }

    @FXML
    private void addProject() throws SQLException{
        int parentID=0;
        //TODO:Créer une condition si le parent n'existe pas
        if (parentProject.getText() == "" || ProjectDB.getProjectID(parentProject.getText())!=0){
            if(parentProject.getText() != ""){ parentID= ProjectDB.getProjectID(parentProject.getText());}

            int newProjectID = ProjectDB.createProject(nameProject.getText(),descriptionProject.getText(),tagsProject.getText(),dateProject.getValue().toEpochDay(),parentID);
            ProjectDB.addCollaborator(newProjectID, Global.userID);

            TreeItem<ProjectDB.Project> child = new TreeItem<ProjectDB.Project>(ProjectDB.getProject(newProjectID));
            Global.TreeMap.put(newProjectID, child);

            //TODO:Marche pas après un relancement
            if (parentID == 0){
                root.getChildren().add(child);
            } else {
                System.out.println(Global.TreeMap.get(parentID));
                Global.TreeMap.get(parentID).getChildren().add(child);
            }
        }
    }

    @FXML
    private void deleteProject(ActionEvent event) throws SQLException{
        //régler le prob si aucun item est sélectionné
        //TODO: attention au delete de la root
        String projectName = treeProjects.getSelectionModel().getSelectedItem().getValue().getTitle();
        int projectID = ProjectDB.getProjectID(projectName);
        ProjectDB.deleteProject(projectID);
        root.getChildren().removeAll(treeProjects.getSelectionModel().getSelectedItem());
    }
    /*
    @FXML
    private void showSubProjects(ActionEvent event) throws SQLException{
        treeProjectColumn.setCellValueFactory(new TreeItemPropertyValueFactory<ProjectDB.Project, String>("title"));

        String projectName = treeProjects.getSelectionModel().getSelectedItem().getValue().getTitle();
        int projectID = ProjectDB.getProjectID(projectName);
        List<Integer> subProjects = ProjectDB.getSubProjects(projectID);
        getProjects(subProjects,treeProjects.getSelectionModel().getSelectedItem());
    }*/

    @FXML
    private void showDetailsProject(ActionEvent event) throws SQLException{
        String projectName = treeProjects.getSelectionModel().getSelectedItem().getValue().getTitle();
        int projectID= ProjectDB.getProjectID(projectName);
        ProjectDB.Project showProject= ProjectDB.getProject(projectID);
        String description= showProject.getDescription();
        String tags=showProject.getTags();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Details");
        alert.setHeaderText(null);
        alert.getDialogPane().setMinWidth(600);
        alert.setResizable(false);
        alert.setContentText("DESCRIPTION:\n" +description+"\n\n"+"TAGS:\n"+tags);
        alert.showAndWait();
    }

    @FXML
    private void addTask() throws Exception, SQLException {
        taskColumn.setCellValueFactory(new PropertyValueFactory<ProjectDB.Task, String>("description"));

        int parentID = 0;
        if (!taskParent.getText().equals("") || ProjectDB.getProjectID(taskParent.getText()) != 0) {
            String projectName = taskParent.getText();
            int projectID = ProjectDB.getProjectID(projectName);
            int task = ProjectDB.createTask(descriptionTask.getText(), projectID);
            List<ProjectDB.Task> taskList =  ProjectDB.getTasks(projectID);
            ObservableList<ProjectDB.Task> otaskList = FXCollections.observableArrayList(taskList);
            taskTable.setItems(otaskList);
        }
    }
}
