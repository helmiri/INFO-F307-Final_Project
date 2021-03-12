package be.ac.ulb.infof307.g06.views;

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
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.text.Text;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectsViewController implements Initializable {
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
    private ComboBox<String> projectSelection;
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
    //---------------METHODES----------------

    /**
     * Initializes the tree table view for the projects and the table view for tasks +
     * loads user's projects and initializes the map.
     * @param url;
     * @param resourceBundle;
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTree();
        try {
            initComboBox();
            loadProjects();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * The main method for button's events
     * @param event;
     * @throws Exception;
     */
    @FXML
    private void events(ActionEvent event) throws Exception {
        if( event.getSource()== addTaskbtn)    { addTask(); }
        else if( event.getSource()== addProjectBtn) { addProject(); }
        else if( event.getSource()== EditProjectBtn) {editProject();}
        else if( event.getSource()== backBtn) { Main.showMainMenuScene(); }
    }

    private void initComboBox() throws SQLException{
        /*
        ProjectDB.createTag("tag1", 0);
        ProjectDB.createTag("tag2", 0);
        ProjectDB.createTag("tag3", 0);
        */
        final ObservableList<String> tags = FXCollections.observableArrayList();
        List<Tag> tagsList = ProjectDB.getAllTags();
        for (int i = 0; i <tagsList.size(); i++) {
            tags.add(tagsList.get(i).getDescription());
        }
        tagsProject.getItems().addAll(tags);
    }

    @FXML
    private void initTree() {
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
    private void loadProjects() throws SQLException {
        treeProjects.getRoot().getChildren().clear();
        projectSelection.getItems().clear();
        Global.TreeMap.clear();
        projectSelection.getItems().clear();
        projectSelection.setPromptText("Select project");
        List<Integer> projectsArray = ProjectDB.getUserProjects(Global.userID);
        getProjects(projectsArray, root);
    }

    /**
     * Initializes the map and display projects on the tree table view
     * @param projects;
     * @param parent;
     * @throws SQLException;
     */
    public void getProjects(List<Integer> projects, TreeItem<Project> parent) throws SQLException{
        treeProjects.setShowRoot(false);
        for(Integer project : projects){
            Project childProject= ProjectDB.getProject(project);
            int parentID= childProject.getParent_id();
            String title= childProject.getTitle();
            projectSelection.getItems().add(title);
            int childID= ProjectDB.getProjectID(title);

            TreeItem<Project> child = new TreeItem<Project>(childProject);
            Global.TreeMap.put(childID, child);
            if (parentID== 0){
                root.getChildren().add(child);
            } else {
                Global.TreeMap.get(parentID).getChildren().add(child);
            }
        }
        treeProjects.setShowRoot(true);
    }

    /**
     * Creates a project and add it to the Database and the map + displays it in the tree table view
     * @throws SQLException;
     */
    @FXML
    private void addProject() throws SQLException{
        //TODO: add conditions to projects creation
        int parentID=0;
        if(nameProject.getText() == "" ) {
            errorText.setText("Cannot add a project with an empty title.");}
        //else if(!validateName(nameProject.getText())){ErrorText.setText("Project's name is invalid (must contain 1 to 20 characters and at least one letter");}
        else if (ProjectDB.getProjectID(nameProject.getText()) != 0){
            errorText.setText("A project with the same title already exists.");}
        else if(dateProject.getValue() == null){
            errorText.setText("Cannot create a project without a date.");}
        else if (parentProject.getText() == "" || ProjectDB.getProjectID(parentProject.getText())!=0){
            if(parentProject.getText() != ""){ parentID= ProjectDB.getProjectID(parentProject.getText());}

            int newProjectID = ProjectDB.createProject(nameProject.getText(),descriptionProject.getText(),dateProject.getValue().toEpochDay(),parentID);
            projectSelection.getItems().add(nameProject.getText());
            ProjectDB.addCollaborator(newProjectID, Global.userID);

            TreeItem<Project> child = new TreeItem<Project>(ProjectDB.getProject(newProjectID));
            Global.TreeMap.put(newProjectID, child);
            errorText.setText("");

            if (parentID == 0){
                root.getChildren().add(child);
            } else {
                System.out.println(Global.TreeMap.get(parentID));
                Global.TreeMap.get(parentID).getChildren().add(child);
            }
        }
    }

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
            loadProjects();
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
        alert.setContentText("DESCRIPTION:\n" +description+"\n\n"+"TAGS:\n"+tags);
        alert.showAndWait();
    }

    /**
     * Edits a project
     * @throws SQLException;
     */
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
            loadProjects();
        }
    }

    /**
     * Displays on the edit menu the chosen project
     * @param event;
     * @throws Exception;
     */
    @FXML
    private void Select(ActionEvent event) throws Exception{
        if(projectSelection.getSelectionModel().getSelectedItem()!=null) {
            String selected = projectSelection.getSelectionModel().getSelectedItem();
            int projectID = ProjectDB.getProjectID(selected);
            Project project = ProjectDB.getProject(projectID);
            String description = project.getDescription();
            //List<Tag> tags = ProjectDB.getTags(projectID);
            LocalDate date = LocalDate.ofEpochDay(project.getDate());

            newdescription.setText(description);
            newDateProject.setValue(date);
            newNameProject.setText(selected);
            //newTagsProject.setText(tags); TODO
        }
    }

    /**
     * Adds a task to the parent project, adds it to the database
     * @throws Exception;
     * @throws SQLException;
     */
    @FXML
    private void addTask() throws Exception, SQLException {
        //taskColumn.setCellValueFactory(new PropertyValueFactory<ProjectDB.Task, String>("description"));
        int parentID = 0;
        if (!taskParent.getText().equals("") || ProjectDB.getProjectID(taskParent.getText()) != 0) {
            String projectName = taskParent.getText();
            int projectID = ProjectDB.getProjectID(projectName);
            int task = ProjectDB.createTask(descriptionTask.getText(), projectID);
            List<Task> taskList =  ProjectDB.getTasks(projectID);
            ObservableList<Task> otaskList = FXCollections.observableArrayList(taskList);
            taskTable.setItems(otaskList);
        }
    }

    /**
     * Displays it in the table view
     * @throws SQLException;
     */
    @FXML
    private void displayTask() throws SQLException {
        if( treeProjects.getSelectionModel().getSelectedItem()!=null && treeProjects.getSelectionModel().getSelectedItem().getValue() !=null) {
            String projectTitle = treeProjects.getSelectionModel().getSelectedItem().getValue().getTitle();
            int projectID = ProjectDB.getProjectID(projectTitle);
            List<Task> taskList = ProjectDB.getTasks(projectID);
            ObservableList<Task> oTaskList = FXCollections.observableArrayList(taskList);
            taskTable.setItems(oTaskList);
        }
    }

    /**
     * Deletes a task in the database and in the table view
     * @param event;
     * @throws SQLException;
     */
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
        if(validateDescription(newDescription)) { ProjectDB.editTask(description,newDescription,projectID);}
        displayTask();
    }

    /**
     *  Checks if the string has at least one alphabet character and as 1 to 126 characters
     * @param text;
     * @return boolean;
     */
    @FXML
    private boolean validateDescription(String text){
        Pattern p = Pattern.compile("^.*[a-zA-Z0-9]{1,126}$");
        Matcher m = p.matcher(text);
        return m.matches();
    }

    /**
     *  Checks if the string has at least one alphabet character and as 1 to 20 characters
     * @param text;
     * @return boolean;
     */
    @FXML
    private boolean validateName(String text){
        Pattern p = Pattern.compile("^.*[a-zA-Z0-9]{1,20}$");
        Matcher m = p.matcher(text);
        return m.matches();
    }

}
