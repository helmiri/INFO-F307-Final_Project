package be.ac.ulb.infof307.g06.controllers.projectControllers;

import be.ac.ulb.infof307.g06.controllers.Controller;
import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.exceptions.WindowLoadException;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.models.database.CalendarDB;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.projectViews.ProjectsViewController;
import be.ac.ulb.infof307.g06.views.projectViews.popups.AddProjectViewController;
import be.ac.ulb.infof307.g06.views.projectViews.popups.EditProjectViewController;
import be.ac.ulb.infof307.g06.views.projectViews.popups.EditTaskViewController;
import com.dropbox.core.DbxException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Handles the main page for projects management and only that.
 */
public class ProjectController extends Controller implements ProjectsViewController.ViewListener {
    private final IOController ioController;
    //--------------- ATTRIBUTES ----------------
    private CalendarDB calendarDB;
    private UserDB userDB;
    private ProjectDB projectDB;
    private ProjectsViewController viewController;
    private CloudServiceController cloudServiceController;

    //--------------- METHODS ----------------

    /**
     * Constructor.
     *
     * @param stage   Stage, a stage
     * @param scene   Scene, a scene
     * @param DB_PATH String, the path to the database.
     */
    public ProjectController(Stage stage, Scene scene, String DB_PATH) throws SQLException, ClassNotFoundException {
        super(stage, scene, DB_PATH);
        projectDB = new ProjectDB(DB_PATH);
        userDB = new UserDB(DB_PATH);
        calendarDB = new CalendarDB(DB_PATH);
        ioController = new IOController(userDB, projectDB);
        cloudServiceController = new CloudServiceController(this, userDB);
    }


    /**
     * Sets the loader to show the Project scene.
     */
    @Override
    public void show() {
        try {
            projectDB.createTag("tag1", "#ff55ff");
            viewController = (ProjectsViewController) loadView(ProjectsViewController.class, "ProjectsView.fxml");
            ioController.setViewController(viewController);
            viewController.setListener(this);
            viewController.show(stage);
        } catch (SQLException error) {
            new DatabaseException(error).show();
        } catch (IOException error) {
            new WindowLoadException(error).show();
        }
    }

    // ----------------------------- STAGES -----------------------------------

    /**
     * Shows the project add stage
     *
     * @param listener listener
     */

    public void showAddProjectStage(ProjectsViewController.ViewListener listener) {
        try {
            FXMLLoader loader = new FXMLLoader(ProjectsViewController.class.getResource("popups/AddProjectView.fxml"));
            AnchorPane projectPane = loader.load();
            AddProjectViewController controller = loader.getController();
            Stage addStage = new Stage();
            addStage.initModality(Modality.APPLICATION_MODAL);
            addStage.setTitle("Add project");
            addStage.setScene(new Scene(projectPane));
            addStage.centerOnScreen();
            addStage.setResizable(false);
            addStage.show();
            controller.init(listener, addStage);
        } catch (IOException error) {
            new WindowLoadException(error).show();
        }
    }

    /**
     * Shows the project edition stage
     *
     * @param project  project to edit
     * @param listener listener
     */
    
    public void showEditProjectStage(Project project, ProjectsViewController.ViewListener listener) throws DatabaseException {
        final String stageTitle = "Edit project";
        final String view = "popups/EditProjectView.fxml";
        Stage stage = new Stage();
        Object controller;
        try {
            controller = loadStage(stageTitle, view, stage);
        } catch (IOException error) {
            new AlertWindow("Error", "Could not load the window", error.getMessage());
            return;
        }
        try {
            ((EditProjectViewController) controller).init(project, listener, stage, projectDB.getTags(project.getId()));
        } catch (SQLException error) {
            throw new DatabaseException(error);
        }
    }

    /**
     * Load a stage.
     *
     * @param stageTitle String, the title to the stage
     * @param view String, the view to load
     * @param stage Stage, the stage to load
     * @return Object, a controller
     * @throws IOException exception
     */
    private Object loadStage(String stageTitle, String view, Stage stage) throws IOException {
        FXMLLoader loader;
        AnchorPane layout;
        loader = new FXMLLoader(ProjectsViewController.class.getResource(view));
        layout = loader.load();
        Object controller = loader.getController();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(stageTitle);
        stage.setScene(new Scene(layout));
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
        return controller;
    }


    /**
     * Shows the task edition stage
     *
     * @param task     task to edit
     * @param listener listener
     */
    public void showEditTaskStage(Task task, ProjectsViewController.ViewListener listener) {
        final String stageTitle = "Edit task";
        final String view = "popups/EditTaskView.fxml";
        Stage stage = new Stage();

        Object controller;
        try {
            controller = loadStage(stageTitle, view, stage);
            ((EditTaskViewController) controller).init(task, listener, stage);
        } catch (IOException error) {
            new AlertWindow("Error", "Could not load the window", error.getMessage());
        }
    }

    /**
     * shows if storage limit has been reached
     *
     * @return boolean
     */
    public boolean storageLimitReached() {
        try {
            if (userDB.availableDisk() <= 0) {
                new AlertWindow("Insufficient storage", "You've reached your maximum storage quota").showInformationWindow();
                return true;
            }
        } catch (SQLException error) {
            new DatabaseException(error).show();
            return true;
        }
        return false;
    }
    // ------------------------------------- CODE --------------------------------------

    /**
     * on add project button pressed
     */
    @Override
    public void addProject() {
        if (storageLimitReached()) {
            return;
        }
        showAddProjectStage(this);
    }

    /**
     * deletes a project
     *
     * @param name title of project to delete
     */
    @Override
    public void deleteProject(String name) {
        try {
            int projectID = projectDB.getProjectID(name);
            projectDB.deleteProject(projectID);
            userDB.updateDiskUsage(projectDB.getSizeOnDisk());
            calendarDB.removeProject(name);
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
    }

    /**
     * Edits a project
     *
     * @param project     project
     * @param title       project title
     * @param description project description
     * @param startDate   project start date
     * @param endDate     project end date
     * @param newTags     project's new tags
     */
    @Override
    public void onEditProject(Project project, String title, String description, LocalDate startDate, LocalDate endDate, ObservableList<String> newTags) {
        if (storageLimitReached()) {
            return;
        }
        try {
            int projectID = project.getId();
            if (title.equals("")) {
                new AlertWindow("Alert", "Project title cannot be empty").showErrorWindow();
            } else {
                projectDB.editProject(
                        projectID,
                        title,
                        description,
                        startDate.toEpochDay(),
                        endDate.toEpochDay()
                );
                calendarDB.replaceProject(project.getTitle(), title);
                List<Integer> tags = new ArrayList<>();
                for (String newTag : newTags) {
                    tags.add(projectDB.getTagID(newTag));
                }
                projectDB.editTags(projectID, tags);
            }
            viewController.refreshTree(projectDB.getProject(projectID));
            viewController.displayProject(projectDB.getProject(projectID), newTags);
            userDB.updateDiskUsage(projectDB.getSizeOnDisk());
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
    }

    /**
     * on edit project button pressed
     *
     * @param project project to edit
     */
    @Override
    public void editProject(Project project) {
        if (storageLimitReached()) {
            return;
        }
        try {
            showEditProjectStage(project, this);
        } catch (DatabaseException error) {
            error.show();
        }
    }

    /**
     * returns all projects
     *
     * @return list of projects
     */
    @Override
    public List<Project> getProjects() {
        List<Project> res = new ArrayList<>();
        try {
            List<Integer> projectsID = projectDB.getUserProjects(userDB.getCurrentUser().getId());
            for (Integer project : projectsID) {
                res.add(projectDB.getProject(project));
            }
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
        // res will be empty if there are no projects/an error occurs
        return res;
    }

    /**
     * returns tags of a project
     *
     * @param project project to get tags
     * @return tags of project
     */
    @Override
    public ObservableList<String> getProjectTags(Project project) {
        ObservableList<String> tagsName = FXCollections.observableArrayList();
        try {
            List<Tag> tags = projectDB.getTags(project.getId());
            for (Tag tag : tags) {
                tagsName.add(tag.getDescription());
            }
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
        // Will return an empty list on error
        return tagsName;
    }

    /**
     * returns all the existing tags
     *
     * @return all tags
     */
    @Override
    public ObservableList<String> getAllTags() {
        ObservableList<String> tagsName = FXCollections.observableArrayList();
        try {
            List<Tag> tags = projectDB.getAllTags();
            for (Tag tag : tags) {
                tagsName.add(tag.getDescription());
            }
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
        // Empty list on error
        return tagsName;
    }

    /**
     * returns a tag from the database
     *
     * @param name tag name
     * @return tag
     */
    @Override
    public Tag getTag(String name) {
        Tag res = null;
        try {
            res = projectDB.getTag(projectDB.getTagID(name));
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
        return res;
    }

    /**
     * Adds task to project
     *
     * @param description task description
     * @param project_id  project id
     * @param startDate   task start date
     * @param endDate     task end date
     */
    @Override
    public void addTask(String description, int project_id, Long startDate, Long endDate) {
        if (storageLimitReached()) {
            return;
        }
        onAddTask(description, project_id, startDate, endDate);
    }

    /**
     * On task edit button clicked
     *
     * @param task task to edit
     */
    @Override
    public void editTask(Task task) {
        if (storageLimitReached()) {
            return;
        }
        showEditTaskStage(task, this);
    }

    /**
     * Edits a task
     *
     * @param prev_description previous task description
     * @param new_description  new task description
     * @param startDate        new task start date
     * @param endDate          new task end date
     * @param task             task
     */
    @Override
    public void onEditTask(String prev_description, String new_description, Long startDate, Long endDate, Task task) {
        if (storageLimitReached()) {
            return;
        }
        try {
            List<Task> tasks = projectDB.getTasks(task.getProjectID());
            if (!new_description.equals(prev_description)) {
                List<String> taskNames = new ArrayList<>();
                for (Task task2 : tasks) {
                    taskNames.add(task2.getDescription());
                }
                if (taskNames.contains(new_description)) {
                    new AlertWindow("Warning", "This task already exists.").showWarningWindow();
                }
            }
            if (new_description.equals("")) {
                deleteTask(task);
            } else if (validateDescription(new_description)) {
                projectDB.editTask(prev_description, new_description, task.getProjectID(), startDate, endDate);
            }
            viewController.displayTask();
            userDB.updateDiskUsage(projectDB.getSizeOnDisk());
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
    }

    /**
     * Deletes a task
     *
     * @param task task to delete
     */
    @Override
    public void deleteTask(Task task) {
        try {
            projectDB.deleteTask(task.getDescription(), task.getProjectID());
            userDB.updateDiskUsage(projectDB.getSizeOnDisk());
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
    }

    /**
     * returns all tasks assigned to a project
     *
     * @param project project to get tasks
     * @return all tasks assigned to a project
     */
    @Override
    public ObservableList<Task> getTasks(Project project) {
        try {
            if (project != null) {
                String projectTitle = project.getTitle();
                int projectID = projectDB.getProjectID(projectTitle);
                List<Task> taskList = projectDB.getTasks(projectID);
                return FXCollections.observableArrayList(taskList);
            }
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
        return FXCollections.observableArrayList();
    }

    /**
     * Assigns collaborators to a task
     *
     * @param collaborators collaborators to assign to task
     * @param task          task to assign collaborators to
     */
    @Override
    public void assignTaskCollaborator(ObservableList<String> collaborators, Task task) {
        if (storageLimitReached()) {
            return;
        }
        try {
            if (task != null) {
                for (String collaborator : collaborators) {
                    projectDB.addTaskCollaborator(task.getId(), userDB.getUserInfo(collaborator).getId());
                }
            } else {
                new AlertWindow("Warning", "Please select a task before assigning a collaborator.").showWarningWindow();
            }

        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
    }

    /**
     * Returns all collaborators assigned to a task
     *
     * @param task task to get collaborators
     * @return all collaborators of a task
     */
    @Override
    public ObservableList<String> getTaskCollaborators(Task task) {
        ObservableList<String> names = FXCollections.observableArrayList();
        try {
            if (task != null) {
                List<Integer> collaborators = projectDB.getTaskCollaborator(task.getId());
                for (Integer collaborator : collaborators) {
                    names.add((userDB.getUserInfo(collaborator).getUserName()));
                }
            } else {
                new AlertWindow("Warning", "Please select a task.").showWarningWindow();
            }
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
        // Empty list on error
        return names;
    }

    /**
     * deletes a collaborator from a task
     *
     * @param collaborator collaborator to delete
     * @param task         task assigned to collaborator
     */
    @Override
    public void deleteTaskCollaborator(String collaborator, Task task) {
        try {
            projectDB.deleteTaskCollaborator(task.getId(), userDB.getUserInfo(collaborator).getId());
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
    }

    /**
     * Returns true if collaborator is in task
     *
     * @param task task to check
     * @return true if collaborator is in task
     */
    @Override
    public boolean isCollaboratorInTask(Task task) {
        return getTaskCollaborators(task).contains(Integer.toString(userDB.getCurrentUser().getId()));
    }

    /**
     * Adds a collaborator to a project
     *
     * @param username   collaborator's username
     * @param project_id project to add collaborator to
     */
    @Override
    public void addCollaborator(String username, int project_id) {
        if (storageLimitReached()) {
            return;
        }
        try {
            if (!userDB.userExists(username)) {
                new AlertWindow("Alert", "User '" + username + "' doesn't exist").showErrorWindow();
                return;
            }
            int receiverID = userDB.getUserInfo(username).getId();
            if (projectDB.getCollaborators(project_id).contains(receiverID)) {
                new AlertWindow("Alert", "User '" + username + "' is already a collaborator in this project").showErrorWindow();
                return;
            }
            userDB.sendInvitation(project_id, userDB.getCurrentUser().getId(), receiverID);
            new AlertWindow("Alert", "Invitation sent to '" + username + "'").showInformationWindow();
            userDB.updateDiskUsage(projectDB.getSizeOnDisk());
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
    }

    /**
     * Deletes a collaborator from a project
     *
     * @param collaboratorName Name of collaborator to delete
     * @param project_id       project
     */
    @Override
    public void deleteCollaborator(String collaboratorName, int project_id) {
        try {
            projectDB.deleteCollaborator(project_id, userDB.getUserInfo(collaboratorName).getId());
            userDB.updateDiskUsage(projectDB.getSizeOnDisk());
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
    }

    /**
     * Returns all collaborators on a project
     *
     * @param project project to get collaborators
     * @return List of collaborators
     */
    @Override
    public ObservableList<String> getCollaborators(Project project) {
        ObservableList<String> names = FXCollections.observableArrayList();
        try {
            List<Integer> collaborators;
            collaborators = projectDB.getCollaborators(project.getId());
            for (Integer collaborator : collaborators) {
                names.add((userDB.getUserInfo(collaborator).getUserName()));
            }
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
        return names;
    }

    /**
     * Returns true if user is in task
     *
     * @param user user
     * @param task task
     * @return true if user is in task
     */
    @Override
    public boolean isUserInTask(String user, Task task) {
        try {
            return projectDB.getTaskCollaborator(task.getId()).contains((userDB.getUserInfo(user).getId()));
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
        return false;
    }

    /**
     * on import button pressed
     *
     * @param path file path
     */
    @Override
    public void importProject(String path) {
        if (storageLimitReached()) {
            return;
        }

        String password = promptPasswordInput(path);
        if (password.isBlank()) {
            return;
        }

        try {
            if (ioController.onImportProject(password, path)) {
                new AlertWindow("Success", "The project has been imported").showInformationWindow();
            } else {
                new AlertWindow("Failure", "This project already exists in the database").showErrorWindow();
            }
        } catch (SQLException error) {
            new DatabaseException(error).show();
        } catch (IOException error) {
            new AlertWindow("Error", "An error reading the file", error.getMessage()).showErrorWindow();
        }
    }

    /**
     * On export button pressed
     *
     * @param project project to export
     * @param path    path to save
     */
    @Override
    public void exportProject(Project project, String path) {
        String password = promptPasswordInput(project.getTitle());
        if (password.isBlank()) {
            return;
        }
        try {
            ioController.onExportProject(password, project, path);
            new AlertWindow("Success", "Exportation successful").showInformationWindow();
        } catch (IOException error) {
            new AlertWindow("Error", "An error occurred while exporting", error.getMessage()).showErrorWindow();
        } catch (DatabaseException error) {
            error.show();
        }
    }

    /**
     * On upload project button pressed
     *
     * @param projects projects to upload
     */
    @Override
    public void uploadProject(List<Project> projects) {
        cloudServiceController.showSelectionStage(false);
        try {
            for (Project project : projects) {
                String password = promptPasswordInput(project.getTitle());
                // Export the project
                String localFilePath = System.getProperty("user.dir");
                ioController.onExportProject(password, project, localFilePath);
                // Upload the exported file
                String fileName = "/" + project.getTitle() + ".tar.gz";
                cloudServiceController.uploadProject(fileName, localFilePath + fileName);
                // Delete temporary file
                new File(localFilePath + fileName).delete();
            }
            new AlertWindow("Success", "Upload successful").showInformationWindow();
        } catch (IOException error) {
            new AlertWindow("Error", "An error occurred while exporting the project file", error.getMessage()).showErrorWindow();
        } catch (DbxException error) {
            new AlertWindow("Connection Error", "Could not connect to DropBox", error.getMessage()).showErrorWindow();
        } catch (DatabaseException error) {
            error.show();
        }
    }


    /**
     * On download project button pressed
     */
    @Override
    public void downloadProject() {
        if (storageLimitReached()) {
            return;
        }
        cloudServiceController.showSelectionStage(true);
    }

    /**
     * Adds a project to the database
     *
     * @param title       project title
     * @param description project description
     * @param startDate   project start date
     * @param endDate     project end date
     * @param tags        project tags
     * @param parent      project parent
     */
    public void onAddProject(String title, String description, LocalDate startDate, LocalDate endDate, ObservableList<String> tags, String parent) {
        if (storageLimitReached()) {
            return;
        }
        try {
            int parentID = 0;

            if (title.equals("")) {
                new AlertWindow("Alert", "Title cannot be empty").showErrorWindow();
            } else if (projectDB.getProjectID(title) != 0) {
                new AlertWindow("Alert", "Project '" + title + "' Already exists").showErrorWindow();
            } else if (startDate == null) {
                new AlertWindow("Alert", "Project needs a start date").showErrorWindow();
            } else if (endDate == null) {
                new AlertWindow("Alert", "Project needs an end date").showErrorWindow();
            } else if (parent.equals("") || projectDB.getProjectID(parent) != 0) {
                insertNewProject(title, description, startDate, endDate, tags, parent, parentID);
            }
            userDB.updateDiskUsage(projectDB.getSizeOnDisk());
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
    }

    /**
     * Inserts the newly created project into the database
     *
     * @param title       The title
     * @param description The description
     * @param startDate   The starting date
     * @param endDate     The ending date
     * @param tags        The tags
     * @param parent      The project's parent (if it's a sub-project)
     * @param parentID    The parent project's ID
     * @throws SQLException When a database access error occurs
     */
    private void insertNewProject(String title, String description, LocalDate startDate, LocalDate endDate, ObservableList<String> tags, String parent, int parentID) throws SQLException {
        if (!parent.equals("")) {
            parentID = projectDB.getProjectID(parent);
        }
        int newProjectID = projectDB.createProject(title, description, startDate.toEpochDay(), endDate.toEpochDay(), parentID);
        for (String tag : tags) {
            projectDB.addTag(projectDB.getTagID(tag), newProjectID);
        }

        projectDB.addCollaborator(newProjectID, userDB.getCurrentUser().getId());
        TreeItem<Project> child = new TreeItem<>(projectDB.getProject(newProjectID));
        viewController.insertProject(newProjectID, child, parentID);
    }


    /**
     * Adds a task to the parent project, adds it to the database.
     *
     * @param taskDescription task description
     * @param project_id      project id
     * @param startDate       task start date
     * @param endDate         task end date
     */
    public void onAddTask(String taskDescription, int project_id, Long startDate, Long endDate) {
        if (storageLimitReached()) {
            return;
        }
        if (taskDescription.isBlank()) {
            return;
        }
        try {
            List<Task> tasks = projectDB.getTasks(project_id);
            List<String> taskNames = new ArrayList<>();
            for (Task task : tasks) {
                taskNames.add(task.getDescription());
            }
            if (taskNames.contains(taskDescription)) {
                new AlertWindow("Warning", "This task already exists.").showWarningWindow();
                return;
            }
            if (project_id != 0) {
                projectDB.createTask(taskDescription, project_id, startDate, endDate);
            }
            userDB.updateDiskUsage(projectDB.getSizeOnDisk());
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
    }

    /**
     * Checks if the string has at least one alphabet character and as 1 to 126 characters
     *
     * @param text;
     * @return boolean;
     */
    @FXML
    private boolean validateDescription(String text) {
        Pattern pattern1 = Pattern.compile("^.*[a-zA-Z0-9]{1,126}$");
        Matcher matcher1 = pattern1.matcher(text);
        return matcher1.matches();
    }

    /**
     * Creates a text input dialog that will prompt the user for a password
     *
     * @param projectName The name of the project to be protected
     * @return The password entered by the user
     */
    private String promptPasswordInput(String projectName) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("File protection: " + projectName);
        dialog.setContentText("Enter a password used to protect the file.");
        dialog.showAndWait();
        return dialog.getEditor().getText();
    }
}
