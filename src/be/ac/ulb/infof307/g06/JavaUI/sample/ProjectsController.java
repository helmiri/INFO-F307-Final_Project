package be.ac.ulb.infof307.g06.JavaUI.sample;

import be.ac.ulb.infof307.g06.database.ProjectDB.Project;
import be.ac.ulb.infof307.g06.database.ProjectDB.Task;

import be.ac.ulb.infof307.g06.Main;
import be.ac.ulb.infof307.g06.database.ProjectDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    //---------------EDIT PROJECTS----------

    @FXML
    private Button EditProjectBtn;

    @FXML
    private ComboBox<String> projectSelection;

    @FXML
    private TextField newNameProject;

    @FXML
    private TextField newdescription;

    @FXML
    private DatePicker newDateProject;

    @FXML
    private TextField newTagsProject;


    //---------------METHODE----------------

    /**
     *
     * @param url
     * @param resourceBundle
     */
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
        else if( event.getSource()== EditProjectBtn) {editProject();}
        else if( event.getSource()== backBtn) { Main.ShowMainMenu(); }

    }

    @FXML
    private void initTree(){
        treeProjectColumn.setCellValueFactory(new TreeItemPropertyValueFactory<ProjectDB.Project, String>("title"));
        taskColumn.setCellValueFactory(new PropertyValueFactory<ProjectDB.Task, String>("description"));
        taskTable.setEditable(true);
        taskColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        treeProjects.setRoot(root);
    }

    private void loadProjects() throws SQLException {
        treeProjects.getRoot().getChildren().clear();
        Global.TreeMap.clear();
        projectSelection.getItems().clear();
        projectSelection.setPromptText("Select project");
        List<Integer> projectsArray = ProjectDB.getUserProjects(Global.userID);
        getProjects(projectsArray, root);
    }

    public void getProjects(List<Integer> projects, TreeItem<ProjectDB.Project> parent) throws SQLException{
        treeProjects.setShowRoot(false);
        for(Integer project : projects){
            Project childProject= ProjectDB.getProject(project);
            int parentID= childProject.getParent_id();
            String title= childProject.getTitle();
            projectSelection.getItems().add(title);
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
            projectSelection.getItems().add(nameProject.getText());
            ProjectDB.addCollaborator(newProjectID, Global.userID);

            TreeItem<ProjectDB.Project> child = new TreeItem<ProjectDB.Project>(ProjectDB.getProject(newProjectID));
            Global.TreeMap.put(newProjectID, child);

            if (parentID == 0){
                root.getChildren().add(child);
            } else {
                System.out.println(Global.TreeMap.get(parentID));
                Global.TreeMap.get(parentID).getChildren().add(child);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @FXML
    private void deleteProject(ActionEvent event) throws SQLException{
        if(treeProjects.getSelectionModel().getSelectedItem().getValue()!=null) {
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
    private void editProject() throws SQLException{
        //Vérification de l'existence
        String selection = projectSelection.getSelectionModel().getSelectedItem().toString();
        int projectID= ProjectDB.getProjectID(selection);
        System.out.println(newNameProject.getText());
        ProjectDB.editProject(projectID, newNameProject.getText(), newdescription.getText(), newTagsProject.getText(),newDateProject.getValue().toEpochDay());
        loadProjects();
    }

    @FXML
    private void Select(ActionEvent event) throws Exception{
        if(projectSelection.getSelectionModel().getSelectedItem()!=null) {
            String selected = projectSelection.getSelectionModel().getSelectedItem().toString();
            int projectID = ProjectDB.getProjectID(selected);
            ProjectDB.Project project = ProjectDB.getProject(projectID);
            String description = project.getDescription();
            String tags = project.getTags();
            LocalDate date = LocalDate.ofEpochDay(project.getDate());

            newdescription.setText(description);
            newDateProject.setValue(date);
            newNameProject.setText(selected);
            newTagsProject.setText(tags);
        }
    }


    @FXML
    private void addTask() throws Exception, SQLException {
        //taskColumn.setCellValueFactory(new PropertyValueFactory<ProjectDB.Task, String>("description"));

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

    @FXML
    private void displayTask() throws SQLException {
        if( treeProjects.getSelectionModel().getSelectedItem()!=null && treeProjects.getSelectionModel().getSelectedItem().getValue() !=null) {
            String projectTitle = treeProjects.getSelectionModel().getSelectedItem().getValue().getTitle();
            int projectID = ProjectDB.getProjectID(projectTitle);
            List<ProjectDB.Task> taskList = ProjectDB.getTasks(projectID);
            ObservableList<ProjectDB.Task> oTaskList = FXCollections.observableArrayList(taskList);
            taskTable.setItems(oTaskList);
        }
    }

    @FXML
    private void deleteTask(ActionEvent event) throws SQLException{
        String taskDescription = taskTable.getSelectionModel().getSelectedItem().getDescription();
        int projectID = taskTable.getSelectionModel().getSelectedItem().getProjectID();
        ProjectDB.deleteTask(taskDescription,projectID);
        taskTable.getItems().removeAll(taskTable.getSelectionModel().getSelectedItem());
    }

    /**
     *  Give the opportunity to edit a cell with a double mouse click
     */
    @FXML
    private void editTask(TableColumn.CellEditEvent event) throws SQLException {
        Task task = (Task) event.getRowValue();
        int projectID = task.getProjectID();
        String description = task.getDescription();
        String newDescription = (String) event.getNewValue();
        if(validateTask(newDescription)) { ProjectDB.editTask(description,newDescription,projectID);}
        displayTask();
    }

    /**
     *
     * @param text
     * @return boolean
     */
    @FXML
    private boolean validateTask(String text){
        Pattern p = Pattern.compile("^.*[a-zA-Z0-9]{1,126}$");
        Matcher m = p.matcher(text);
        return m.matches();
    }
}
