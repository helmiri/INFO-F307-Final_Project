package be.ac.ulb.infof307.g06.views.ProjectViews;

import be.ac.ulb.infof307.g06.controllers.MainController;
import be.ac.ulb.infof307.g06.controllers.project.ProjectController;
import be.ac.ulb.infof307.g06.models.Cloud;
import be.ac.ulb.infof307.g06.models.Global;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import com.dropbox.core.DbxException;
import com.sun.source.tree.Tree;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ProjectsViewController implements Initializable {
    public Button cloudDownloadBtn;
    public Button cloudUploadBtn;
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
    private TableColumn<Task, String> taskColumn;
    @FXML
    private TableColumn<String, String> collaboratorsColumn;
    @FXML
    private TreeTableView<Project> treeProjects;
    @FXML
    private TreeTableColumn<Project, String> treeProjectColumn;
    private final TreeItem<Project> root = new TreeItem<Project>();
    public ViewListener listener;
    private Task selectedTask;
    private Project selectedProject;
    private Map<Integer, TreeItem<Project>> TreeMap = new HashMap<>();
    //---------------METHODES----------------

    /**
     * Initializes the controller and launchs the init method.
     *
     * @param url            URL
     * @param resourceBundle ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTree();
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

        if (event.getSource() == backBtn) {
            listener.back();
        } else if (event.getSource() == cloudUploadBtn) {
            listener.uploadProject();
            uploadFiles();
        } else {
            try {
                if (UserDB.availableDisk() <= 0) {
                    MainController.alertWindow(Alert.AlertType.INFORMATION, "Insufficient storage", "You've reached your storage quota.");
                    return;
                }
            } catch (SQLException throwables) {
                MainController.alertWindow(Alert.AlertType.ERROR, "Database error", "Error accessing the database");
                return;
            }
            if (event.getSource() == addTaskbtn) {
                listener.addTask(descriptionTask.getText(), getSelectedProject().getId());
                displayTask();
            } else if (event.getSource() == addBtn) {
                listener.addProject();
            } else if (event.getSource() == assignTaskCollaboratorBtn) {
                listener.assignTaskCollaborator(collabComboBox.getCheckModel().getCheckedItems(), getSelectedTask());
            } else if (event.getSource() == editBtn) {
                listener.editProject();
                if (!projectsTitle.getText().equals("")) {
                    Global.currentProject = projectsTitle.getText();
                    ProjectController.showEditProjectStage();
                }
            } else if (event.getSource() == exportProjectBtn) {
                listener.exportProject();
                exportProject();
            } else if (event.getSource() == importProjectBtn) {
                listener.importProject();
                importProject();
            } else if (event.getSource() == cloudDownloadBtn) {
                listener.downloadProject();
                ProjectController.showCloudDownloadStage();
            }
        }
    }

    // --------------------------------------- EVENTS -----------------------------------

    /**
     * Deletes a project in the Database and in the tree table view.
     */
    @SuppressWarnings("unchecked")
    @FXML
    private void deleteProject(ActionEvent event) {
        if (treeProjects.getSelectionModel().getSelectedItem() != null && treeProjects.getSelectionModel().getSelectedItem().getValue() != null) {
            Project child = treeProjects.getSelectionModel().getSelectedItem().getValue();
            listener.deleteProject(child.getTitle());
            int parentID = child.getParent_id();
            if (parentID == 0) {
                root.getChildren().removeAll(treeProjects.getSelectionModel().getSelectedItem());
            } else {
                TreeMap.get(parentID).getChildren().removeAll(treeProjects.getSelectionModel().getSelectedItem());
            }
        }
    }


    /**
     * Deletes the selected task in the table and in the database.
     */
    public void deleteTask() {
        Task task = getSelectedTask();
        listener.deleteTask(task);
        taskTable.getItems().removeAll(task);
    }


    /**
     * Shows the edit task stage for the selected task.
     */
    public void showTaskEdition() {
        Task task = getSelectedTask();
        listener.editTask(task);
        displayTask();
    }

    /**
     * Deletes a collaborator from the database and the table.
     */
    public void deleteCollaborator() {
        String collaborator = getSelectedUser();
        listener.deleteCollaborator(collaborator, getSelectedProject().getId());
        collaboratorsTable.getItems().removeAll(collaborator);
    }

    public void deleteTaskCollaborator() {
        listener.deleteTaskCollaborator(getSelectedTaskCollaborator(), selectedTask);
    }

    @FXML
    public void onTaskSelected() {
        if (getSelectedTask() != null) {
            selectedTask = getSelectedTask();
            ObservableList<String> names = listener.getTaskCollaborators(selectedTask);
            taskCollaboratorTable.setItems(names);

            ObservableList<String> items = collabComboBox.getItems();
            for (String item : items) {
                collabComboBox.getItemBooleanProperty(item).set(false);
                if (listener.isUserInTask(item, selectedTask)) {
                    collabComboBox.getItemBooleanProperty(item).set(true);
                }
            }
        }
    }

    @FXML
    public void onProjectSelected() {
        selectedProject = getSelectedProject();
        if (selectedProject == null) {
            return;
        }
        displayProject(selectedProject, listener.getProjectTags(selectedProject));
    }
    // --------------------------------- CODE ------------------------------------

    /**
     * Returns the selected item in the tree table.
     *
     * @return TreeItem<Project>
     */
    public Project getSelectedProject() {
        return treeProjects.getSelectionModel().getSelectedItem().getValue();
    }

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
                                setText(t);
                                setStyle("-fx-background-color: " + ProjectDB.getTag(ProjectDB.getTagID(t)).getColor() + ";"); // TODO
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
                    else if (ProjectDB.getTaskCollaborator(task.getId()).contains(Global.userID)) // TODO
                        setStyle("-fx-background-color: #8fbc8f;");
                } catch (SQLException e) {
                    // TODO Error
                }
            }
        });
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
     * Shows a new directory chooser stage to choose where we want to save our selected project then exports it.
     */
    public void exportProject(){
        Project selectedProject = getSelectedProject();
        if (selectedProject != null) {
            System.out.println(selectedProject.getTitle());
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(new File("src"));
            File selectedDirectory = directoryChooser.showDialog(new Stage());
            if (selectedDirectory != null) {
                System.out.println(selectedDirectory.getAbsolutePath());
                boolean succeed = controller.exportProject(selectedProject,
                        selectedDirectory.getAbsolutePath(),
                        selectedDirectory.getAbsolutePath() + "/file.json");
                ProjectController.alertExportImport("export", succeed);
            }
        }
    }

    /**
     * Choose the file we want to upload to the cloud via a filechooser.
     */
    public void uploadFiles() {
        Project selectedProject = getSelectedProject();
        if (selectedProject == null) {
            return;
        }
        File selectedDirectory = new File("");

        boolean succeed = controller.exportProject(selectedProject,
                selectedDirectory.getAbsolutePath(),
                selectedDirectory.getAbsolutePath() + "/file.json");
        try {
            Map<String, String> creds = UserDB.getCloudCredentials();
            if (creds.get("accToken") == null || creds.get("clientID") == null) {
                MainController.alertWindow(Alert.AlertType.ERROR, "Cloud service", "Could not connect to DropBox. Check your credentials.: ");
                return;
            }
            Cloud.init(creds.get("accToken"), creds.get("clientID"));
            String fileName = "/" + selectedProject.getTitle() + ".tar.gz";
            String localFilePath = selectedDirectory.getAbsolutePath() + fileName;

            Cloud.uploadFile(localFilePath, fileName);
            MainController.alertWindow(Alert.AlertType.INFORMATION, "Cloud service", "Uploaded successfully");
        } catch (DbxException e) {
            MainController.alertWindow(Alert.AlertType.ERROR, "Cloud service", "Error connecting to dropbox: " + e.getMessage());
        } catch (IOException e) {
            MainController.alertWindow(Alert.AlertType.ERROR, "File access", "Error reading the file" + e.getMessage());
        } catch (SQLException throwables) {
            MainController.alertWindow(Alert.AlertType.ERROR, "Database", "Error accessing the database" + throwables.getMessage());
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
            System.out.println(selectedArchive.getAbsolutePath());
            boolean succeed = ProjectController.importProject(selectedArchive.getAbsolutePath());
            ProjectController.alertExportImport("import", succeed);
        }
    }

    /**
     * Displays collaborators on the table.
     *
     */
    @FXML
    public void displayCollaborators(){
        Project selectedProject = getSelectedProject();
        ObservableList<String> oCollaboratorsList = listener.getCollaborators(selectedProject);
        collaboratorsTable.setItems(oCollaboratorsList);
    }


    /**
     * Displays informations of the selected project.
     */
    @FXML
    public void displayProject(Project project, ObservableList<String> tagsName) {
        projectsDescription.setText(project.getDescription());
        projectsDate.setText(controller.dateToString(project.getDate()));
        projectsTitle.setText(project.getTitle());
        displayTask();
        displayCollaborators();
        collabComboBox.getItems().setAll(listener.getCollaborators(selectedProject));
        projectTags.setItems(tagsName);
    }


    /**
     * Displays tasks on the table.
     */
    @FXML
    public void displayTask() {
        selectedProject = getSelectedProject();
        ObservableList<Task> oTaskList = listener.getTasks(selectedProject);
        taskTable.setItems(oTaskList);
    }


    @FXML
    public Task getSelectedTask(){
        return taskTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    public String getSelectedTaskCollaborator() {
        return taskCollaboratorTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    public String getSelectedUser() {
        return collaboratorsTable.getSelectionModel().getSelectedItem();
    }

    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    public interface ViewListener {
        void back();

        void addProject();

        void deleteProject(String name);

        void editProject();

        ObservableList<String> getProjectTags(Project project);

        void addTask(String description, int project_id);

        void editTask(Task task;);

        void deleteTask(Task task);

        ObservableList<Task> getTasks(Project project);

        void assignTaskCollaborator(ObservableList<String> collaborators, Task task);

        ObservableList<String> getTaskCollaborators(Task task);

        void deleteTaskCollaborator(String collaborator, Task task);

        void importProject();

        void exportProject();

        void uploadProject();

        void downloadProject();

        void addCollaborator(String collaboratorName, int project_id);

        void deleteCollaborator(String collaboratorName, int project_id);

        ObservableList<String> getCollaborators(Project project);

        boolean isUserInTask(String user, Task task);
    }

}
