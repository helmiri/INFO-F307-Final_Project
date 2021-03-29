package be.ac.ulb.infof307.g06.views.ProjectViews;

import be.ac.ulb.infof307.g06.controllers.MainController;
import be.ac.ulb.infof307.g06.controllers.ProjectController;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.Global;
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
import javafx.util.Callback;
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
    private Button assignTaskCollaboratorBtn;
    @FXML
    private Label projectsDate;
    @FXML
    private Text projectsTitle;
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
    private ListView<String> projectTags;
    @FXML
    private TableView<Task> taskTable;
    @FXML
    private TableView<String> collaboratorsTable;
    @FXML
    private TableView<String> taskCollaboratorTable;
    @FXML
    private TableColumn<String, String> taskCollaboratorColumn;
    @FXML
    private TableColumn<Task,String> taskColumn;
    @FXML
    private TableColumn<String, String> collaboratorsColumn;
    @FXML
    private TreeTableView<Project> treeProjects;
    @FXML
    private TreeTableColumn<Project, String> treeProjectColumn;
    private final TreeItem<Project> root = new TreeItem<Project>();
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
    public void hideRoot(){
        treeProjects.setShowRoot(false);
    }

    /**
     * Shows the tree table root.
     */
    @FXML
    public void refresh(){
        treeProjects.setShowRoot(true);
    }

    /**
     * The main method for button's events.
     *
     * @param event ActionEvent
     */
    @FXML
    private void events(ActionEvent event) {
        if( event.getSource()== addTaskbtn){ addTask();}
        else if( event.getSource()== addBtn ) {ProjectController.showAddProjectStage();}
        else if( event.getSource() == assignTaskCollaboratorBtn){assignCollaborators();}
        else if( event.getSource()== editBtn ) {
            if (!projectsTitle.getText().equals("")){
                Global.currentProject = projectsTitle.getText();
                ProjectController.showEditProjectStage();}
        }
        else if( event.getSource()== backBtn){ MainController.showMainMenu(); }
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
        treeProjects.setRoot(root);
        treeProjectColumn.setCellValueFactory(new TreeItemPropertyValueFactory<Project, String>("title"));

        collaboratorsTable.setEditable(false);
        collaboratorsTable.setStyle("-fx-selection-bar: lightgray; -fx-text-background-color:black; -fx-selection-bar-non-focused:white;");
        collaboratorsColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        collaboratorsColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        taskCollaboratorTable.setStyle("-fx-selection-bar: lightgray; -fx-text-background-color:black; -fx-selection-bar-non-focused:white;");
        taskCollaboratorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        taskCollaboratorColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        projectTags.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> p) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String t, boolean bln) {
                        super.updateItem(t, bln);
                        try {
                            if (t != null){
                                System.out.println("load tags " + t);
                                setText(t);
                                setStyle("-fx-background-color: "+ ProjectDB.getTag(ProjectDB.getTagID(t)).getColor()+";");
                            } else {
                                setText(null);
                                setGraphic(null);
                                setStyle(null);
                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                };
            }
        });
        taskColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("description"));
        taskColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        taskTable.setEditable(false);
        taskTable.setStyle("-fx-selection-bar: lightgray; -fx-text-background-color:black; -fx-selection-bar-non-focused:white;");
        taskTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                try {
                    if (task == null)
                        setStyle("");
                    else if (ProjectDB.getTaskCollaborator(task.getId()).contains(Global.userID))
                        setStyle("-fx-background-color: #8fbc8f;");
                } catch (SQLException e) {
                    // TODO Error
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
    public void addChild(TreeItem<Project> parent, TreeItem<Project> child){
        parent.getChildren().add(child);
    }

    /**
     * Adds collaborators to the combobox with the collaborators in it.
     *
     * @param names ObservableList<String>
     */
    public void insertCollaborator(ObservableList<String> names){
        collabComboBox.getItems().setAll(names);
    }

    public void insertTaskCollaborators(ObservableList<String> names){
        taskCollaboratorTable.setItems(names);
    }

    /**
     * Deletes a project in the Database and in the tree table view.
     *
     */
    @SuppressWarnings("unchecked")
    @FXML
    private void deleteProject(ActionEvent event){
        if(treeProjects.getSelectionModel().getSelectedItem()!= null && treeProjects.getSelectionModel().getSelectedItem().getValue()!=null) {
            Project child= treeProjects.getSelectionModel().getSelectedItem().getValue();
            String projectName = child.getTitle();
            controller.deleteProject(projectName);
            int parentID = child.getParent_id();

            if (parentID == 0) {
                root.getChildren().removeAll(treeProjects.getSelectionModel().getSelectedItem());
            } else {
                Global.TreeMap.get(parentID).getChildren().removeAll(treeProjects.getSelectionModel().getSelectedItem());
            }
        }
    }

    /**
     * Displays tasks on the table.
     */
    @FXML
    public void displayTask(){
        TreeItem<Project> selectedProject = getSelectedProject();
        ObservableList<Task> oTaskList = controller.getTasks(selectedProject);
        taskTable.setItems(oTaskList);
    }

    /**
     * Shows the edit task stage for the selected task.
     */
    public void showTaskEdition() {
        Global.selectedTask = getSelectedTask();
        ProjectController.showEditTaskStage();
    }

    /**
     * Adds a task to the table and displays the table to refresh it.
     */
    public void addTask(){
        if (getSelectedProject().getValue() != null) {
            controller.addTask(descriptionTask.getText(),getSelectedProject().getValue().getTitle());
            displayTask();
        }
    }

    /**
     * Deletes the selected task in the table and in the database.
     */
    public void deleteTask(){
        Task task = getSelectedTask();
        controller.deleteTask(task);
        taskTable.getItems().removeAll(task);
    }

    /**
     * Shows a new directory chooser stage to choose where we want to save our selected project then exports it.
     */
    public void exportProject(){
        TreeItem<Project> selectedProject = getSelectedProject();
        if(selectedProject != null && selectedProject.getValue() != null) {
            System.out.println(selectedProject.getValue().getTitle());
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(new File("src"));
            File selectedDirectory = directoryChooser.showDialog(new Stage());
            if (selectedDirectory != null) {
                System.out.println(selectedDirectory.getAbsolutePath());
                boolean succeed = controller.exportProject2(selectedProject.getValue(),
                        selectedDirectory.getAbsolutePath(),
                        selectedDirectory.getAbsolutePath() + "/file.json");
                ProjectController.alertExportImport("export", succeed);
            }
        }
    }

    /**
     * Imports the file selected from a file chooser stage.
     */
    public void importProject() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src"));
        //fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("archive","*.tar.gz"));
        File selectedArchive = fileChooser.showOpenDialog(new Stage());
        if (selectedArchive != null) {
            System.out.println(selectedArchive.getAbsolutePath());
            boolean succeed = controller.importProject(selectedArchive.getAbsolutePath());
            ProjectController.alertExportImport("import", succeed);
        }
    }

    /**
     * Deletes a collaborator from the database and the table.
     *
     */
    public void deleteCollaborator(){
        String collaborator = getSelectedUser();
        controller.deleteCollaborator(collaborator, getSelectedProject().getValue().getId());
        collaboratorsTable.getItems().removeAll(collaborator);
    }

    /**
     * Displays collaborators on the table.
     *
     */
    @FXML
    public void displayCollaborators(){
        TreeItem<Project> selectedProject = getSelectedProject();
        ObservableList<String> oCollaboratorsList = controller.getCollaborators(selectedProject);
        collaboratorsTable.setItems(oCollaboratorsList);
    }

    public void addCollaborator(){
        if(!collaboratorsName.getText().equals("")){
            if(!controller.addCollaborator(collaboratorsName.getText(), getSelectedProject().getValue().getId())){
                MainController.alertWindow(Alert.AlertType.INFORMATION,"Alert","User " + collaboratorsName.getText() + " doesn't exist.");
            } else {
                MainController.alertWindow(Alert.AlertType.INFORMATION,"Alert","Invitation sent to " + collaboratorsName.getText() + ".");
            }
        }
    }

    /**
     *
     *
     */
    @FXML
    public void assignCollaborators(){
        ObservableList<String> selectedCollaborators = collabComboBox.getCheckModel().getCheckedItems();
        Task selectedTask = Global.selectedTask;
        controller.assignCollaborators(selectedCollaborators, selectedTask, getSelectedProject().getValue().getId());
        displayTask();
    }

    @FXML
    public void deleteTaskCollaborator(){
        controller.deleteTaskCollaborator(getSelectedTaskCollaborator(),Global.selectedTask);
    }

    /**
     *
     *
     */
    @FXML
    public void onTaskSelected(){
        if (getSelectedTask() != null) {
            Global.selectedTask = getSelectedTask();
            controller.initTaskCollaborators(this, Global.selectedTask);
            controller.initTaskCollaborators(this, getSelectedTask());
            ObservableList<String> items = collabComboBox.getItems();
            for (String item : items) {
                collabComboBox.getItemBooleanProperty(item).set(false);
                if (controller.isUserInTask(getSelectedTask().getId(), item)) {
                    collabComboBox.getItemBooleanProperty(item).set(true);
                }
            }
        }
    }

    /**
     * Returns the selected task.
     *
     * @return Task
     */
    @FXML
    public Task getSelectedTask(){
        return taskTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    public String getSelectedTaskCollaborator(){
        return taskCollaboratorTable.getSelectionModel().getSelectedItem();
    }

    /**
     * Returns the selected user in the collaborators table.
     *
     * @return String
     */
    @FXML
    public String getSelectedUser(){ return collaboratorsTable.getSelectionModel().getSelectedItem(); }

    /**
     * Displays informations of the selected project.
     */
    @FXML
    public void displayProject(String title, String description, Long date, ObservableList<String> tagsName){
        projectsDescription.setText(description);
        projectsDate.setText(controller.dateToString(date));
        projectsTitle.setText(title);
        displayTask();
        displayCollaborators();
        controller.initCollaborators(this);
        projectTags.setItems(tagsName);
    }

    @FXML
    public void onProjectSelected(){
        TreeItem<Project> selectedProject = getSelectedProject();
        if (selectedProject == null || selectedProject.getValue() == null){return;}
        controller.getProjectInfo(this, selectedProject);
    }

}
