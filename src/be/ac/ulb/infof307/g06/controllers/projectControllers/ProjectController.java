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
        //ioController.setViewController(viewController);
    }


    /**
     * Sets the loader to show the Project scene.
     */
    @Override
    public void show() {
        try {
            calendar_db = new CalendarDB("database.db");
            project_db.createTag("tag1", "#ff55ff");
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
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
     * Sets the loader to show the stage to add a project.
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
     * Sets the loader to show the stage to edit a task.
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

    @Override
    public void back() {
        super.back();
    }

    @Override
    public void addProject() {
        if (storageLimitReached()) {
            return;
        }
        showAddProjectStage(this);
    }

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
            // TODO SQLEXception getColor resultset closed
            e.printStackTrace();
            new AlertWindow("Error", "" + e).errorWindow();
        }
    }

    @Override
    public void editProject(Project project) {
        if (storageLimitReached()) {
            return;
        }
        showEditProjectStage(project, this);
    }

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

    @Override
    public Tag getTag(String name) {
        Tag res = null;
        try {
            res = project_db.getTag(project_db.getTagID(name));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return res;
    }

    @Override
    public void addTask(String description, int project_id, Long startDate, Long endDate) {
        if (storageLimitReached()) {
            return;
        }
        onAddTask(description, project_id, startDate, endDate);
    }

    @Override
    public void editTask(Task task) {
        if (storageLimitReached()) {
            return;
        }
        showEditTaskStage(task, this);
    }

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

    @Override
    public void deleteTask(Task task) {
        try {
            project_db.deleteTask(task.getDescription(), task.getProjectID());
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }
    }

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

    @Override
    public void assignTaskCollaborator(ObservableList<String> collaborators, Task task) {
        if (storageLimitReached()) {
            return;
        }
        try {
            if (task != null){
                for (String collaborator : collaborators) {
                    project_db.addTaskCollaborator(task.getId(), user_db.getUserInfo(collaborator).getId());
                }
            }
            else{
                new AlertWindow("Warning", "Please select a task before assigning a collaborator.").warningWindow();
            }

        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }
    }

    @Override
    public ObservableList<String> getTaskCollaborators(Task task) {
        ObservableList<String> names = FXCollections.observableArrayList();
        try {
            if (task != null) {
                List<Integer> collaborators = project_db.getTaskCollaborator(task.getId());
                for (Integer collaborator : collaborators) {
                    names.add((user_db.getUserInfo(collaborator).getUserName()));
                }
            }
            else{
                new AlertWindow("Warning", "Please select a task.").warningWindow();
            }
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }
        return names;
    }

    @Override
    public void deleteTaskCollaborator(String collaborator, Task task) {
        try {
            project_db.deleteTaskCollaborator(task.getId(), user_db.getUserInfo(collaborator).getId());
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }
    }

    @Override
    public boolean isCollaboratorInTask(Task task) {
        return getTaskCollaborators(task).contains(user_db.getCurrentUser().getId());
    }

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

    @Override
    public void deleteCollaborator(String collaboratorName, int project_id) {
        try {
            project_db.deleteCollaborator(project_id, user_db.getUserInfo(collaboratorName).getId());
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }
    }

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

    @Override
    public boolean isUserInTask(String user, Task task) {
        try {
            return project_db.getTaskCollaborator(task.getId()).contains((user_db.getUserInfo(user).getId()));
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }
        return false;
    }

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

    @Override
    public void exportProject(Project project, String path) {
        boolean res = ioController.onExportProject(project, path);
        if (res) {
            new AlertWindow("Success", "Exportation successful").informationWindow();
        } else {
            new AlertWindow("Error", "An error occurred while exporting").errorWindow();
        }
    }

    @Override
    public void uploadProject(List<Project> projects) {
        if (setServiceProvider()) return;
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

    @Override
    public void downloadProject() {
        if (setServiceProvider()) return;
        if (storageLimitReached()) {
            return;
        }
        cloudServiceController.showSelectionStage(true);
    }

    private boolean setServiceProvider() {
        if (cloudServiceController == null) {
            try {
                cloudServiceController = new CloudServiceController(this, user_db);
            } catch (SQLException throwables) {
                new AlertWindow("Error", "A database error has occurred").errorWindow();
                cloudServiceController = null;
                return true;
            }
        }
        return false;
    }


    /**
     * Adds a project to the tree, the map and the database.
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
     *
     * @param taskDescription String
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
