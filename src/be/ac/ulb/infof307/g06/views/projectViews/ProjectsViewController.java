package be.ac.ulb.infof307.g06.views.projectViews;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.Task;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectsViewController {
    //----------ATTRIBUTES---------
    @FXML
    private AnchorPane layout;
    @FXML
    private Button helpBtn;
    @FXML
    private Tooltip taskCollaboratorsToolTip;
    @FXML
    private Tooltip  projectCollaboratorsToolTip;
    @FXML
    private Tooltip tagToolTip;
    @FXML
    private Tooltip taskTableToolTip;
    @FXML
    private Button addCollaboratorsBtn;
    @FXML
    private Button exportProjectBtn;
    @FXML
    private Button importProjectBtn;
    @FXML
    private Button addBtn;
    @FXML
    private Button editBtn;
    @FXML
    private Button addTaskButton;
    @FXML
    private Button backBtn;
    @FXML
    private Button assignTaskCollaboratorBtn;
    @FXML
    private Button cloudDownloadBtn;
    @FXML
    private Button cloudUploadBtn;
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
    private TextArea collaboratorsName;
    @FXML
    protected DatePicker endDateTask;
    @FXML
    private TableColumn<Task, String> taskTime;
    @FXML
    protected DatePicker startDateTask;

    @FXML
    private TreeTableColumn<Project, String> treeProjectColumn;
    private final TreeItem<Project> root = new TreeItem<>();
    private ViewListener listener;
    private Task selectedTask;
    private Project selectedProject;
    private final Map<Integer, TreeItem<Project>> TreeMap = new HashMap<>();

    //---------------METHODS----------------
    public void show(Stage stage) {
        stage.setScene(new Scene(layout));
        refreshTree(null);
        refresh();
    }

    /**
     * The main method for button's events.
     *
     * @param event ActionEvent, the event.
     */
    @FXML
    private void events(ActionEvent event) {
        if (event.getSource() == backBtn) {
            listener.back();
        } else if (event.getSource() == helpBtn) {
            helpPopUp();
        } else if (event.getSource() == cloudUploadBtn && selectedProject != null) {
            listener.uploadProject(getMultipleSelectedProjects());
        } else if (event.getSource() == addBtn) {
            listener.addProject();
        } else if (event.getSource() == assignTaskCollaboratorBtn) {
            listener.assignTaskCollaborator(collabComboBox.getCheckModel().getCheckedItems(), getSelectedTask());
        } else if (event.getSource() == editBtn && selectedProject != null) {
            listener.editProject(selectedProject);
        } else if (event.getSource() == exportProjectBtn) {
            exportProject();
        } else if (event.getSource() == importProjectBtn) {
            importProject();
        } else if (event.getSource() == cloudDownloadBtn) {
            listener.downloadProject();
        } else if (event.getSource() == addCollaboratorsBtn) {
            listener.addCollaborator(collaboratorsName.getText(), selectedProject.getId());
        } else if (event.getSource() == addTaskButton && selectedProject != null) {
            if (startDateTask.getValue() != null && endDateTask.getValue() != null) {
                listener.addTask(descriptionTask.getText(), selectedProject.getId(), startDateTask.getValue().toEpochDay(), endDateTask.getValue().toEpochDay());
                displayTask();
            } else {
                new AlertWindow("Warning", "Task needs a start and end date.").showWarningWindow();
            }
        }
    }

    public void helpPopUp(){
        new AlertWindow("Help", "Go to Settings > Help > Projects management.").showInformationWindow();
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

    /**
     * Refresh the projects tree.
     *
     * @param selected Project, the selected project.
     */
    public void refreshTree(Project selected) {
        selectedProject = selected;
        initTree();
        root.getChildren().clear();
        List<Project> projectsArray = listener.getProjects();
        hideRoot();
        for (Project project : projectsArray) {
            TreeItem<Project> child = new TreeItem<>(project);
            TreeMap.put(project.getId(), child);
            if (project.getParentId() == 0) {
                root.getChildren().add(child);
            } else {
                TreeMap.get(project.getParentId()).getChildren().add(child);
            }
        }
    }

    private List<Project> getMultipleSelectedProjects() {
        List<Project> projectsList = new ArrayList<>();
        ObservableList<TreeItem<Project>> selectedProjects = treeProjects.getSelectionModel().getSelectedItems();
        for (TreeItem<Project> selected : selectedProjects) {
            projectsList.add(selected.getValue());
        }
        return projectsList;
    }

    /**
     * Deletes a project in the Database and in the tree table view.
     */
    @FXML
    private void deleteProject() {
        if (treeProjects.getSelectionModel().getSelectedItem() != null && treeProjects.getSelectionModel().getSelectedItem().getValue() != null) {
            Project child = treeProjects.getSelectionModel().getSelectedItem().getValue();
            listener.deleteProject(child.getTitle());
            int parentID = child.getParentId();
            if (parentID == 0) {
                root.getChildren().remove(treeProjects.getSelectionModel().getSelectedItem());
            } else {
                TreeMap.get(parentID).getChildren().remove(treeProjects.getSelectionModel().getSelectedItem());
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
        if (selectedProject != null) {
            listener.deleteCollaborator(collaborator, selectedProject.getId());
            collaboratorsTable.getItems().removeAll(collaborator);
        } else {
            new AlertWindow("Warning", "Please select a project before deleting a collaborator.").showWarningWindow();
        }

    }

    /**
     * Deletes a task to a collaborator.
     */
    public void deleteTaskCollaborator() {
        listener.deleteTaskCollaborator(getSelectedTaskCollaborator(), selectedTask);
    }

    /**
     * The event when a task is selected
     */
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

    /**
     * The event when a project is selected.
     */
    @FXML
    public void onProjectSelected() {
        selectedProject = getSelectedProject();
        if (selectedProject == null) {
            return;
        }
        displayProject(selectedProject, listener.getProjectTags(selectedProject));
    }

    /**
     * Inserts a project in a map to keep them updated.
     *
     * @param newProjectID int, ID of the current project.
     * @param project TreeItem, the tree item of the project.
     * @param parentID int, the parent ID of the current project.
     */
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
     * @return The selected project
     */
    public Project getSelectedProject() {
        ObservableList<TreeItem<Project>> items = treeProjects.getSelectionModel().getSelectedItems();
        if (items.size() == 0) {
            return null;
        }
        return items.get(0).getValue();
    }

    /**
     * Initializes all the columns and some tables.
     */
    @FXML
    public void initTree(){
        treeProjects.setRoot(root);
        treeProjectColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("title"));
        treeProjects.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        collaboratorsColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        collaboratorsColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        taskCollaboratorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        taskCollaboratorColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        projectTags.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> p) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t);
                            setStyle("-fx-background-color: " + listener.getTag(t).getColor() + ";");
                        } else {
                            setText(null);
                            setGraphic(null);
                            setStyle(null);
                        }
                    }
                };
            }
        });
        taskColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        taskColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        taskTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        taskTime.setCellFactory(TextFieldTableCell.forTableColumn());
        taskTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (task == null) {
                    setStyle("");
                } else if (listener.isCollaboratorInTask(task)) {
                    setStyle("-fx-background-color: #8fbc8f;");
                }
            }
        });
    }

    /**
     * Shows a new directory chooser stage to choose where we want to save our selected project then exports it.
     */
    public void exportProject(){
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
     * Imports the file selected from a file chooser stage.
     */
    public void importProject() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("archive","*.tar.gz"));
        File selectedArchive = fileChooser.showOpenDialog(new Stage());
        if (selectedArchive != null) {
            listener.importProject(selectedArchive.getAbsolutePath());
        }
    }

    /**
     * Displays collaborators on the table.
     *
     */
    @FXML
    public void displayCollaborators() {
        if (selectedProject != null) {
            ObservableList<String> oCollaboratorsList = listener.getCollaborators(selectedProject);
            collaboratorsTable.setItems(oCollaboratorsList);
        }
    }


    /**
     * Displays informations of the selected project.
     */
    @FXML
    public void displayProject(Project project, ObservableList<String> tagsName) {
        projectsDescription.setText(project.getDescription());
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        projectsDate.setText(dateFormat.format(project.getStartDate() * 86400000L) + " - " + dateFormat.format(project.getEndDate() * 86400000L));
        projectsTitle.setText(project.getTitle());
        displayTask();
        displayCollaborators();
        collabComboBox.getItems().setAll(listener.getCollaborators(project));
        if (!tagsName.isEmpty()) {
            projectTags.setItems(tagsName);
        }
    }

    /**
     * Displays tasks on the table.
     */
    @FXML
    public void displayTask() {
        if (selectedProject != null) {
            taskTable.setItems(listener.getTasks(selectedProject));
        }
    }

    /**
     * @return the selected task
     */
    @FXML
    public Task getSelectedTask(){
        return taskTable.getSelectionModel().getSelectedItem();
    }

    /**
     * @return the selected task of a collaborator.
     */
    @FXML
    public String getSelectedTaskCollaborator() {
        return taskCollaboratorTable.getSelectionModel().getSelectedItem();
    }

    /**
     * @return the selected collaborator.
     */
    @FXML
    public String getSelectedUser() {
        return collaboratorsTable.getSelectionModel().getSelectedItem();
    }

    //--------------- LISTENER ----------------

    /**
     * Sets the listener.
     *
     * @param listener ViewListener, the listener to the controller.
     */
    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    /**
     * Communicates to the controller which button has been clicked
     */
    public interface ViewListener {
        void back();

        void addProject();

        void onAddProject(String title, String description, LocalDate startDate, LocalDate endDate, ObservableList<String> tags, String parent);

        void deleteProject(String name);

        void editProject(Project project);

        void onEditProject(Project project, String title, String description, LocalDate startDate, LocalDate endDate, ObservableList<String> tags);

        List<Project> getProjects();

        ObservableList<String> getProjectTags(Project project);

        ObservableList<String> getAllTags();

        Tag getTag(String name);

        void addTask(String description, int project_id, Long startDate, Long endDate);

        void editTask(Task task);

        void onEditTask(String prev_description, String new_description, Long startDate, Long endDate, Task task);

        void deleteTask(Task task);

        ObservableList<Task> getTasks(Project project);

        void assignTaskCollaborator(ObservableList<String> collaborators, Task task);

        ObservableList<String> getTaskCollaborators(Task task);

        void deleteTaskCollaborator(String collaborator, Task task);

        boolean isCollaboratorInTask(Task task);

        void importProject(String path);

        void exportProject(Project project, String path);

        void uploadProject(List<Project> project);

        void downloadProject();

        void addCollaborator(String collaboratorName, int project_id);

        void deleteCollaborator(String collaboratorName, int project_id);

        ObservableList<String> getCollaborators(Project project);

        boolean isUserInTask(String user, Task task);
    }

}
