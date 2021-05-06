package be.ac.ulb.infof307.g06.controllers.projectControllers;

import be.ac.ulb.infof307.g06.controllers.Controller;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ProjectController extends Controller implements ProjectsViewController.ViewListener {
    //--------------- ATTRIBUTES ----------------
    private CalendarDB calendar_db;
    private ProjectsViewController viewController;
    private final IOController ioController;
    private CloudServiceController cloudServiceController;

    //--------------- METHODS ----------------
    public ProjectController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene) {
        super(user_db, project_db, stage, scene);
        ioController = new IOController(user_db, project_db, stage, scene);
    }


    /**
     * Sets the loader to show the Project scene.
     */
    @Override
    public void show() {
        try {
            calendar_db = new CalendarDB("database.db");
            project_db.createTag("tag1", "#ff55ff");
        } catch (SQLException | ClassNotFoundException e) {
            new AlertWindow("Error", " " + e).errorWindow();
        }
        FXMLLoader loader = new FXMLLoader(ProjectsViewController.class.getResource("ProjectsView.fxml"));
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }
        viewController = loader.getController();
        ioController.setViewController(viewController);

        viewController.setListener(this);
        stage.setScene(scene);
        stage.sizeToScene();
        viewController.init();
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
        } catch (IOException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }
    }

    /**
     * Shows the project edition stage
     *
     * @param project  project to edit
     * @param listener listener
     */
    public void showEditProjectStage(Project project, ProjectsViewController.ViewListener listener) {
        try {
            FXMLLoader loader = new FXMLLoader(ProjectsViewController.class.getResource("popups/EditProjectView.fxml"));
            AnchorPane editPane = loader.load();
            EditProjectViewController controller = loader.getController();
            Stage editStage = new Stage();
            editStage.initModality(Modality.APPLICATION_MODAL);
            editStage.setTitle("Edit project");
            editStage.setScene(new Scene(editPane));
            editStage.centerOnScreen();
            editStage.setResizable(false);
            editStage.show();
            controller.init(project, listener, editStage, project_db.getTags(project.getId()));
        } catch (IOException | SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }
    }


    /**
     *  Shows the task edition stage
     * @param task task to edit
     * @param listener listener
     */
    public void showEditTaskStage(Task task, ProjectsViewController.ViewListener listener) {
        try {
            FXMLLoader loader = new FXMLLoader(ProjectsViewController.class.getResource("popups/EditTaskView.fxml"));
            AnchorPane taskPane = loader.load();
            EditTaskViewController controller = loader.getController();
            Stage editTaskStage = new Stage();
            editTaskStage.initModality(Modality.APPLICATION_MODAL);
            editTaskStage.setTitle("Edit task");
            editTaskStage.setScene(new Scene(taskPane));
            editTaskStage.centerOnScreen();
            editTaskStage.setResizable(false);
            editTaskStage.show();
            controller.init(task, listener, editTaskStage);
        } catch (IOException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }
    }

    /**
     * shows if storage limit has been reached
     *
     * @return boolean
     */
    public boolean storageLimitReached() {
        try {
            if (user_db.availableDisk() <= 0) {
                new AlertWindow("Insufficient storage", "You've reached your maximum storage quota").informationWindow();
                return true;
            }
        } catch (SQLException throwables) {
            new AlertWindow("Database error", "Could not access the database").errorWindow();
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
            int projectID = project_db.getProjectID(name);
            project_db.deleteProject(projectID);
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
            calendar_db.removeProject(name);
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
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
                new AlertWindow("Alert", "Project title cannot be empty").errorWindow();
            } else {
                project_db.editProject(
                        projectID,
                        title,
                        description,
                        startDate.toEpochDay(),
                        endDate.toEpochDay()
                );
                calendar_db.replaceProject(project.getTitle(), title);
                List<Integer> tags = new ArrayList<>();
                for (String newTag : newTags) {
                    tags.add(project_db.getTagID(newTag));
                }
                project_db.editTags(projectID, tags);
            }
            viewController.refreshTree(project_db.getProject(projectID));
            viewController.displayProject(project_db.getProject(projectID), newTags);
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
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
        showEditProjectStage(project, this);
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
            List<Integer> projectsID = project_db.getUserProjects(user_db.getCurrentUser().getId());
            for (Integer project : projectsID) {
                res.add(project_db.getProject(project));
            }
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }
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
            List<Tag> tags = project_db.getTags(project.getId());
            for (Tag tag : tags) {
                tagsName.add(tag.getDescription());
            }
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }
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
            List<Tag> tags = project_db.getAllTags();
            for (Tag tag : tags) {
                tagsName.add(tag.getDescription());
            }
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }
        return tagsName;
    }

    /**
     * returns a tag from the database
     *
     * @param name tag naùe
     * @return tag
     */
    @Override
    public Tag getTag(String name) {
        Tag res = null;
        try {
            res = project_db.getTag(project_db.getTagID(name));
        } catch (SQLException e) {
            new AlertWindow("Error", " " + e).errorWindow();
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
            List<Task> tasks = project_db.getTasks(task.getProjectID());
            if (!new_description.equals(prev_description)) {
                List<String> taskNames = new ArrayList<>();
                for (Task task2 : tasks) {
                    taskNames.add(task2.getDescription());
                }
                if (taskNames.contains(new_description)) {
                    new AlertWindow("Warning", "This task already exists.").warningWindow();
                }
            }
            if (new_description.equals("")) {
                deleteTask(task);
            } else if (validateDescription(new_description)) {
                project_db.editTask(prev_description, new_description, task.getProjectID(), startDate, endDate);
            }
            viewController.displayTask();
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
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
            project_db.deleteTask(task.getDescription(), task.getProjectID());
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
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
                int projectID = project_db.getProjectID(projectTitle);
                List<Task> taskList = project_db.getTasks(projectID);
                return FXCollections.observableArrayList(taskList);
            }
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
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
                    project_db.addTaskCollaborator(task.getId(), user_db.getUserInfo(collaborator).getId());
                }
            } else {
                new AlertWindow("Warning", "Please select a task before assigning a collaborator.").warningWindow();
            }

        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
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
                List<Integer> collaborators = project_db.getTaskCollaborator(task.getId());
                for (Integer collaborator : collaborators) {
                    names.add((user_db.getUserInfo(collaborator).getUserName()));
                }
            } else {
                new AlertWindow("Warning", "Please select a task.").warningWindow();
            }
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }
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
            project_db.deleteTaskCollaborator(task.getId(), user_db.getUserInfo(collaborator).getId());
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
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
        return getTaskCollaborators(task).contains(user_db.getCurrentUser().getId());
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
            if (!user_db.userExists(username)) {
                new AlertWindow("Alert", "User '" + username + "' doesn't exist").errorWindow();
                return;
            }
            int receiverID = user_db.getUserInfo(username).getId();
            if (project_db.getCollaborators(project_id).contains(receiverID)) {
                new AlertWindow("Alert", "User '" + username + "' is already a collaborator in this project").errorWindow();
                return;
            }
            user_db.sendInvitation(project_id, user_db.getCurrentUser().getId(), receiverID);

            user_db.updateDiskUsage(project_db.getSizeOnDisk());
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
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
            project_db.deleteCollaborator(project_id, user_db.getUserInfo(collaboratorName).getId());
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
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
            collaborators = project_db.getCollaborators(project.getId());
            for (Integer collaborator : collaborators) {
                names.add((user_db.getUserInfo(collaborator).getUserName()));
            }
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
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
            return project_db.getTaskCollaborator(task.getId()).contains((user_db.getUserInfo(user).getId()));
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
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
        int res = ioController.onImportProject(path);
        if (res == 1) {
            new AlertWindow("Success", "The project has been imported").informationWindow();
        } else if (res == -1) {
            new AlertWindow("Error", "An error occurred").errorWindow();
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
        boolean res = ioController.onExportProject(project, path);
        if (res) {
            new AlertWindow("Success", "Exportation successful").informationWindow();
        } else {
            new AlertWindow("Error", "An error occurred while exporting").errorWindow();
        }
    }

    /**
     * On upload project button pressed
     *
     * @param projects projects to upload
     */
    @Override
    public void uploadProject(List<Project> projects) {
        if (setServiceProvider()) {
            return;
        }
        cloudServiceController.showSelectionStage(false);
        for (Project project : projects) {
            String localFilePath = System.getProperty("user.dir");
            ioController.onExportProject(project, localFilePath);
            String fileName = "/" + project.getTitle() + ".tar.gz";
            if (cloudServiceController.uploadProject(fileName, localFilePath + fileName)) {
                new AlertWindow("Success", "Upload successful").informationWindow();
            } else {
                new AlertWindow("Error", "Could not upload project: " + project.getTitle()).errorWindow();
            }
        }
    }

    /**
     * On download project button pressed
     */
    @Override
    public void downloadProject() {
        if (setServiceProvider()) {
            return;
        }
        if (storageLimitReached()) {
            return;
        }
        cloudServiceController.showSelectionStage(true);
    }

    /**
     * @return Sets service provider
     */
    private boolean setServiceProvider() {
        if (cloudServiceController == null) {
            cloudServiceController = new CloudServiceController(this, user_db);
        }
        return false;
    }


    /**
     * Adds a project to the database
     * @param title project title
     * @param description project description
     * @param startDate project start date
     * @param endDate project end date
     * @param tags project tags
     * @param parent project parent
     */
    public void onAddProject(String title, String description, LocalDate startDate, LocalDate endDate, ObservableList<String> tags, String parent) {
        if (storageLimitReached()) {
            return;
        }
        try {
            int parentID = 0;

            if (title.equals("")) {
                new AlertWindow("Alert", "Title cannot be empty").errorWindow();
            } else if (project_db.getProjectID(title) != 0) {
                new AlertWindow("Alert", "Project '" + title + "' Already exists").errorWindow();
            } else if (startDate == null) {
                new AlertWindow("Alert", "Project needs a start date").errorWindow();
            } else if (endDate == null) {
                new AlertWindow("Alert", "Project needs an end date").errorWindow();
            } else if (parent.equals("") || project_db.getProjectID(parent) != 0) {

                if (!parent.equals("")) {
                    parentID = project_db.getProjectID(parent);
                }
                int newProjectID = project_db.createProject(title, description, startDate.toEpochDay(), endDate.toEpochDay(), parentID);
                for (String tag : tags) {
                    project_db.addTag(project_db.getTagID(tag), newProjectID);
                }

                project_db.addCollaborator(newProjectID, user_db.getCurrentUser().getId());
                TreeItem<Project> child = new TreeItem<>(project_db.getProject(newProjectID));
                viewController.insertProject(newProjectID, child, parentID);
            }
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }
    }


    /**
     * Adds a task to the parent project, adds it to the database.
     * @param taskDescription task description
     * @param project_id project id
     * @param startDate task start date
     * @param endDate task end date
     */
    public void onAddTask(String taskDescription, int project_id, Long startDate, Long endDate) {
        if (storageLimitReached()) {
            return;
        }
        if (taskDescription.isBlank()) {
            return;
        }
        try {
            List<Task> tasks = project_db.getTasks(project_id);
            List<String> taskNames = new ArrayList<>();
            for (Task task : tasks) {
                taskNames.add(task.getDescription());
            }
            if (taskNames.contains(taskDescription)) {
                new AlertWindow("Warning", "This task already exists.").warningWindow();
                return;
            }
            if (project_id != 0) {
                project_db.createTask(taskDescription, project_id, startDate, endDate);
            }
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
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
        Pattern p = Pattern.compile("^.*[a-zA-Z0-9]{1,126}$");
        Matcher m = p.matcher(text);
        return m.matches();
    }

}
