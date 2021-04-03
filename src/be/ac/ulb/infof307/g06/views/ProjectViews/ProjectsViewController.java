package be.ac.ulb.infof307.g06.views.ProjectViews;

import be.ac.ulb.infof307.g06.controllers.MainController;
import be.ac.ulb.infof307.g06.models.Global;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.controllers.project.IOController;
import be.ac.ulb.infof307.g06.controllers.project.ProjectController;
import be.ac.ulb.infof307.g06.models.*;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import com.dropbox.core.DbxException;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
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
        List<Project> projectsArray = listener.getProjects();
        hideRoot();
        for (Project project : projectsArray) {
            TreeItem<Project> child = new TreeItem<Project>(project);
            TreeMap.put(project.getId(), child);
            if (project.getParent_id() == 0) {
                root.getChildren().add(child);
            } else {
                TreeMap.get(project.getParent_id()).getChildren().add(child);
            }
        }
        refresh();
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
    public void refresh() {
        treeProjects.setShowRoot(true);
    }

    // --------------------------------------- EVENTS -----------------------------------

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
                    new AlertWindow("Insufficient storage", "You've reached your maximum storage quota").informationWindow();
                    return;
                }
            } catch (SQLException throwables) {
                new AlertWindow("Database error", "Could not access the database").errorWindow();
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
                if (!projectsTitle.getText().equals("")) {
                    listener.editProject(getSelectedProject());
                }
            } else if (event.getSource() == exportProjectBtn) {
                exportProject();
            } else if (event.getSource() == importProjectBtn) {
                importProject();
            } else if (event.getSource() == cloudDownloadBtn) {
                listener.downloadProject();
            }
        }
    }


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

    public void insertProject(int newProjectID, TreeItem<Project> project, int parentID) {
        TreeMap.put(newProjectID, project);
        if (parentID == 0) {
            root.getChildren().add(project);
        } else {
            TreeMap.get(parentID).getChildren().add(project);
        }
    }

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
                        if (t != null) {
                            setText(t);
                            setStyle("-fx-background-color: " + listener.getTag(t).getColor() + ";"); // TODO
                        } else {
                            setText(null);
                            setGraphic(null);
                            setStyle(null);
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
                if (task == null)
                    setStyle("");
                else if (listener.getTaskCollaborators(task).contains(userID)) // TODO
                    setStyle("-fx-background-color: #8fbc8f;");
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
    // TODO Yassine déplace le code dans controller stp
    public void exportProject(){
        Project selectedProject = getSelectedProject();
        if (selectedProject != null) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(new File("src"));
            File selectedDirectory = directoryChooser.showDialog(new Stage());
            if (selectedDirectory != null) {
                listener.exportProject(selectedProject,
                        selectedDirectory.getAbsolutePath());
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

        listener.exportProject(selectedProject,
                selectedDirectory.getAbsolutePath());
        try {
            Map<String, String> creds = UserDB.getCloudCredentials();
            if (creds.get("accToken") == null || creds.get("clientID") == null) {
                new AlertWindow("Cloud service", "Could not connect to DropBox. Check that your credentials are valid").errorWindow();
                return;
            }
            Cloud.init(creds.get("accToken"), creds.get("clientID"));
            String fileName = "/" + selectedProject.getTitle() + ".tar.gz";
            String localFilePath = selectedDirectory.getAbsolutePath() + fileName;

            Cloud.uploadFile(localFilePath, fileName);
            new AlertWindow("Upload", "Uploaded successfully").informationWindow();
        } catch (DbxException e) {
            new AlertWindow("Cloud service error", "Error connecting to DropBox").errorWindow();
        } catch (IOException e) {
            new AlertWindow("IO Error", "Error reading the file").errorWindow();
        } catch (SQLException throwables) {
            new AlertWindow("Database error", "Could not access the database").errorWindow();
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
            listener.importProject(selectedArchive.getAbsolutePath());
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
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        projectsDate.setText(dateFormat.format(project.getDate() * 86400000L));
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

        void onAddProject(String title, String description, LocalDate date, ObservableList<String> tags, String parent);

        void deleteProject(String name);

        void editProject(Project project);

        void onEditProject(Project project, String title, String description, LocalDate date, ObservableList<String> tags);

        List<Project> getProjects();

        ObservableList<String> getProjectTags(Project project);

        ObservableList<String> getAllTags();

        Tag getTag(String name);

        void addTask(String description, int project_id);

        void editTask(Task task);

        void onEditTask(String prev_description, String new_description, Task task);

        void deleteTask(Task task);

        ObservableList<Task> getTasks(Project project);

        void assignTaskCollaborator(ObservableList<String> collaborators, Task task);

        ObservableList<String> getTaskCollaborators(Task task);

        void deleteTaskCollaborator(String collaborator, Task task);

        void importProject(String path);

        void exportProject(Project project, String path);

        void uploadProject();

        void downloadProject();

        void addCollaborator(String collaboratorName, int project_id);

        void deleteCollaborator(String collaboratorName, int project_id);

        ObservableList<String> getCollaborators(Project project);

        boolean isUserInTask(String user, Task task);
    }

}
