package be.ac.ulb.infof307.g06.views.ProjectViews;

import be.ac.ulb.infof307.g06.controllers.ProjectController;
import be.ac.ulb.infof307.g06.database.UserDB;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class ProjectsViewController implements Initializable {
    //----------ATTRIBUTES---------
    @FXML
    private Button exportProjectBtn;
    @FXML
    private Button importProjectBtn;
    @FXML
    private Button addBtn;
    @FXML
    private Button editBtn;
    @FXML
    private Button addTaskbtn;
    @FXML
    private Button backBtn;
    @FXML
    private Button addCollaboratorsBtn;
    @FXML
    private Label projectsDate;
    @FXML
    private Text projectsTitle;
    @FXML
    private TextArea projectsTags;
    @FXML
    private TextArea projectsDescription;
    @FXML
    private TextArea descriptionTask;
    @FXML
    private TextArea collaboratorsName;
    @FXML
    private TextField taskParent;
    @FXML
    private CheckComboBox<String> collabComboBox;
    @FXML
    private TableView<Task> taskTable;
    @FXML
    private TableView<String> collaboratorsTable;
    @FXML
    private TableColumn<Task,String> taskColumn;
    @FXML
    private TableColumn<String, String> collaboratorsColumn;
    @FXML
    private TreeTableView<Project> treeProjects;
    @FXML
    private TreeTableColumn<Project, String> treeProjectColumn;
    private TreeItem<Project> root = new TreeItem<Project>();
    private ProjectController controller;
    //---------------METHODES----------------

    /**
     * Initializes the controller and launchs the init method.
     *
     * @param url URL
     * @param resourceBundle ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        controller = new ProjectController();
        controller.init(this, root);
    }


    /**
     * Hides the tree table root.
     */
    @FXML
    public void hideRoot(){ treeProjects.setShowRoot(false); }

    /**
     * Sho<s the tree table root.
     */
    @FXML
    public void refresh(){ treeProjects.setShowRoot(true); }

    /**
     * The main method for button's events.
     *
     * @param event ActionEvent
     * @throws Exception
     */
    @FXML
    private void events(ActionEvent event) throws Exception {
        if( event.getSource()== addTaskbtn){ addTask();}
        else if( event.getSource()== addBtn ) {Main.showAddProjectStage(); }
        else if( event.getSource()== editBtn ) {
            if (!projectsTitle.getText().equals("")){
                Global.currentProject = projectsTitle.getText();
                Main.showEditProjectStage();}
        }
        else if( event.getSource()== backBtn){ Main.showMainMenuScene(); }
        else if (event.getSource()==exportProjectBtn){exportProject();}
        else if(event.getSource()==importProjectBtn){importProject();}
    }

    /**
     * Returns the selected item in the tree table.
     *
     * @return TreeItem<Project>
     */
    //public String getProjectExport(){return String.valueOf(projectExportList.getValue()); }
    public TreeItem<Project> getSelectedProject(){return treeProjects.getSelectionModel().getSelectedItem();}

    /**
     * Initializes all the columns and some tables.
     */
    @FXML
    public void initTree(){
        treeProjectColumn.setCellValueFactory(new TreeItemPropertyValueFactory<Project, String>("title"));
        taskColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("description"));
        collaboratorsColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        taskTable.setEditable(true);
        collaboratorsTable.setEditable(true);
        taskColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        collaboratorsColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        treeProjects.setRoot(root);
        taskTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                try {
                    if (task == null)
                        setStyle("");
                    else if (ProjectDB.getTaskCollaborator(task.getId()).contains(Global.userID))
                        setStyle("-fx-background-color: #baffba;");
                    else if (!ProjectDB.getTaskCollaborator(task.getId()).contains(Global.userID))
                        setStyle("-fx-background-color: #ffd7d1;");
                    else
                        setStyle("");
                } catch (SQLException e) {
                }
            }
        });
    }

    /**
     * Clears the project's table.
     *
     */
    public void clearProjects(){
        treeProjects.getRoot().getChildren().clear();
        /*
        projectSelection.getItems().clear();
        projectSelection.setPromptText("Select project")*/
    }

    /**
     * Adds a child to a parent in the tree.
     *
     * @param parent TreeItem<Project>
     * @param child TreeItem<Project>
     */
    @FXML
    public void addChild(TreeItem<Project> parent, TreeItem<Project> child){ parent.getChildren().add(child); }


    /**
     * Adds collaborators to the combobox with the collaborators in it.
     *
     * @param names ObservableList<String>
     */
    public void insertCollaborator(ObservableList<String> names){ collabComboBox.getItems().addAll(names); }

    /**
     * Deletes a project in the Database and in the tree table view.
     *
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    @FXML
    private void deleteProject() throws SQLException{
        if(treeProjects.getSelectionModel().getSelectedItem()!= null && treeProjects.getSelectionModel().getSelectedItem().getValue()!=null) {
            Project child= treeProjects.getSelectionModel().getSelectedItem().getValue();
            String projectName = child.getTitle();
            int projectID = ProjectDB.getProjectID(projectName);
            ProjectDB.deleteProject(projectID);
            int parentID = child.getParent_id();
            if (parentID == 0) { root.getChildren().removeAll(treeProjects.getSelectionModel().getSelectedItem()); }
            else { Global.TreeMap.get(parentID).getChildren().removeAll(treeProjects.getSelectionModel().getSelectedItem()); }
        }
    }

    /**
     * Deletes a collaborator from the database and the table.
     *
     * @throws SQLException
     */
    public void deleteCollaborator() throws SQLException{
        String collaborator = getSelectedUser();
        controller.deleteCollaborator(collaborator, getSelectedProject().getValue().getId());
        collaboratorsTable.getItems().removeAll(collaborator);
    }

    /**
     * Displays tasks on the table.
     *
     * @throws SQLException
     */
    @FXML
    public void displayTask() throws SQLException {
        TreeItem<Project> selectedProject = getSelectedProject();
        ObservableList<Task> oTaskList = controller.getTasks(selectedProject);
        taskTable.setItems(oTaskList);
    }

    /**
     * Displays collaborators on the table.
     *
     * @throws SQLException
     */
    @FXML
    public void displayCollaborators() throws SQLException {
        TreeItem<Project> selectedProject = getSelectedProject();
        ObservableList<String> oCollaboratorsList = controller.getCollaborators(selectedProject);
        collaboratorsTable.setItems(oCollaboratorsList);
    }

    /**
     * Sets the row to the new value when we double click on it.
     *
     * @param event TableColumn.CellEditEvent
     * @throws SQLException
     */
    public void editTask(TableColumn.CellEditEvent event) throws SQLException {
        Task task = (Task) event.getRowValue();
        String newDescription = (String) event.getNewValue();
        String description = task.getDescription();//
        controller.editTask(description, newDescription, task);
    }

    /**
     * Adds a task to the table and displays the table to refresh it.
     *
     * @throws Exception
     */
    public void addTask() throws Exception{
        controller.addTask(descriptionTask.getText(),getSelectedProject().getValue().getTitle());
        displayTask();
    }

    /**
     * Deletes the selected task in the table and in the database.
     *
     * @throws SQLException
     */
    public void deleteTask() throws SQLException{
        Task task = getSelectedTask();
        controller.deleteTask(task);
        taskTable.getItems().removeAll(task);
    }

    /**
     * Shows a new directory chooser stage to choose where we want to save our selected project then exports it.
     */
    public void exportProject(){
        TreeItem<Project> selectedProject = getSelectedProject();
        if(selectedProject!= null && selectedProject.getValue()!=null) {
            System.out.println(selectedProject.getValue().getTitle());
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(new File("src"));
            File selectedDirectory = directoryChooser.showDialog(new Stage());
            if (selectedDirectory != null) {
                System.out.println(selectedDirectory.getAbsolutePath());
                boolean succeed = controller.exportProject(selectedProject.getValue(),
                        selectedDirectory.getAbsolutePath(),
                        selectedDirectory.getAbsolutePath() + "/file.txt",
                        selectedProject.getValue().getId());
                Main.alertExport(succeed);
            }
        }
    }

    /**
     * Imports the file selected from a file chooser stage.
     */
    public void importProject() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("archive","*.tar.gz"));
        File selectedArchive = fileChooser.showOpenDialog(new Stage());
        if (selectedArchive != null) {
            boolean succeed =controller.importProject(selectedArchive.getAbsolutePath());
            Main.alertImport(succeed);
        }
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

    /**
     *
     *
     * @throws SQLException
     */
    @FXML
    public void assignCollaborators() throws SQLException{
        ObservableList<String> selectedCollaborators = collabComboBox.getCheckModel().getCheckedItems();
        Task selectedTask = getSelectedTask();
        controller.assignCollaborators(selectedCollaborators, selectedTask, getSelectedProject().getValue().getId());
        displayTask();
    }

    /**
     *
     *
     * @throws SQLException
     */
    @FXML
    public void onTaskSelected() throws SQLException{
        ObservableList<String> items = collabComboBox.getItems();
        for(String item : items){
            collabComboBox.getItemBooleanProperty(item).set(false);
            if (ProjectDB.getTaskCollaborator(getSelectedTask().getId()).contains(Integer.parseInt(UserDB.getUserInfo(item.toString()).get("id")))){
                System.out.println("in " + item);
                collabComboBox.getItemBooleanProperty(item).set(true);
            }
        }
    }

    /**
     *
     *
     * @param event ActionEvent
     * @throws SQLException
     */
    @FXML
    public void onTaskCollaboratorSelected(ActionEvent event) throws SQLException{ }

    /**
     * Returns the selected task.
     *
     * @return Task
     */
    @FXML
    public Task getSelectedTask(){ return taskTable.getSelectionModel().getSelectedItem(); }

    /**
     * Returns the selected user in the collaborators table.
     *
     * @return String
     */
    @FXML
    public String getSelectedUser(){ return collaboratorsTable.getSelectionModel().getSelectedItem(); }

    /**
     * Displays informations of the selected project.
     *
     * @throws SQLException
     */
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
            String tagsList = controller.listToString(tagsName);
            projectsTags.setText(tagsList);
            displayTask();
            displayCollaborators();
            controller.initTaskCollaborators(this);
        }catch(NullPointerException throwables){
            projectsDescription.setText("");
            projectsDate.setText("");
            projectsTitle.setText("");
        }
    }
}
