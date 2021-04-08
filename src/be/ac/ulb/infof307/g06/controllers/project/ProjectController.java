package be.ac.ulb.infof307.g06.controllers.project;

import be.ac.ulb.infof307.g06.controllers.Controller;
import be.ac.ulb.infof307.g06.controllers.MainController;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.projectViews.*;
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
    private ProjectsViewController viewController;
    private final IOController ioController;

    //--------------- METHODS ----------------
    public ProjectController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene) {
        super(user_db, project_db, stage, scene);
        ioController = new IOController(user_db, project_db, stage, scene);
        ioController.setViewController(viewController);
    }


    /**
     * Sets the loader to show the Project scene.
     */
    @Override
    public void show() {
        FXMLLoader loader = new FXMLLoader(ProjectsViewController.class.getResource("ProjectsViewV2.fxml"));
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        viewController = loader.getController();
        viewController.setListener(this);
        stage.setScene(scene);
        viewController.init();
    }

    // ----------------------------- STAGES -----------------------------------

    /**
     * Sets the loader to show the stage to add a project.
     */
    public void showAddProjectStage(ProjectsViewController.ViewListener listener) {
        try {
            FXMLLoader loader = new FXMLLoader(ProjectsViewController.class.getResource("AddProjectView.fxml"));
            AnchorPane projectPane = loader.load();
            AddProjectViewController controller = loader.getController();
            controller.init(listener);
            MainController.showStage("Add project", 541, 473, Modality.APPLICATION_MODAL, projectPane);
        } catch (IOException e) {
            // TODO Exception
        }
    }

    /**
     * Sets the loader to show the stage to edit a project.
     */
    public void showCloudDownloadStage() {
        //FXMLLoader loader = new FXMLLoader();
        //loader.setLocation(CloudViewController.class.getResource("CloudView.fxml"));

        try {
            FXMLLoader loader = new FXMLLoader(CloudViewController.class.getResource("CloudView.fxml"));
            AnchorPane cloudPane = loader.load();
            CloudViewController controller = loader.getController();
            controller.initialize();

            MainController.showStage("Download", 541, 473, Modality.APPLICATION_MODAL, cloudPane);

        } catch (IOException e) {
            // TODO Exception
        }
        // TODO Download Stage
//        MainController.showStage("Add project", 750, 400, Modality.APPLICATION_MODAL, loader);
    }


    public void showEditProjectStage(Project project, ProjectsViewController.ViewListener listener) {
        try {
            FXMLLoader loader = new FXMLLoader(ProjectsViewController.class.getResource("EditProjectView.fxml"));
            AnchorPane editPane = loader.load();
            EditProjectViewController controller = loader.getController();
            controller.init(project, listener);
            MainController.showStage("Edit Project", 541, 473, Modality.APPLICATION_MODAL, editPane);
        } catch (IOException e) {
            // TODO Exception
        }
    }

    /**
     * Sets the loader to show the stage to edit a task.
     */
    public void showEditTaskStage(Task task, ProjectsViewController.ViewListener listener) {
        try {
            FXMLLoader loader = new FXMLLoader(ProjectsViewController.class.getResource("TaskEditView.fxml"));
            AnchorPane taskPane = loader.load();
            EditTaskViewController controller = loader.getController();
            controller.init(task, listener);
            MainController.showStage("Edit Task", 435, 256, Modality.APPLICATION_MODAL, taskPane);
        } catch (IOException e) {
            // TODO Exception
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
        stage.hide();
        stage.setScene(prevScene);
        stage.show();
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
            // TODO refresh
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
        } catch (SQLException e) {
            // TODO Exception
        }
    }

    @Override
    public void onEditProject(Project project, String title, String description, LocalDate date, ObservableList<String> newTags) {
        if (storageLimitReached()) {
            return;
        }
        try {
            int projectID = project.getId();
            if (title == "") {
                // TODO Cannot edit a project with an empty name.
            } else {
                project_db.editProject(
                        projectID,
                        title,
                        description,
                        date.toEpochDay()
                );
                List<Integer> tags = new ArrayList<>();
                for (String newTag : newTags) {
                    tags.add(project_db.getTagID(newTag));
                }
                project_db.editTags(projectID, tags);
                // TODO refresh
            }
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
        } catch (SQLException e) {
            // TODO Exception
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
            // TODO Exception
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
            // TODO Exception
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
            // TODO Exception
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
    public void addTask(String description, int project_id) {
        if (storageLimitReached()) {
            return;
        }
        onAddTask(description, project_id);
    }

    @Override
    public void editTask(Task task) {
        if (storageLimitReached()) {
            return;
        }
        showEditTaskStage(task, this);
    }

    @Override
    public void onEditTask(String prev_description, String new_description, Task task) {
        if (storageLimitReached()) {
            return;
        }
        try {
            List<Task> tasks = project_db.getTasks(task.getProjectID());
            List<String> taskNames = new ArrayList<>();
            for (Task task2 : tasks) {
                taskNames.add(task2.getDescription());
            }
            if (taskNames.contains(new_description)) {
                // TODO Exception task already exists
            }
            if (new_description.equals("")) {
                deleteTask(task);
            } else if (validateDescription(new_description)) {
                project_db.editTask(prev_description, new_description, task.getProjectID());
            }
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
        } catch (SQLException e) {
            // TODO Exception
        }
    }

    @Override
    public void deleteTask(Task task) {
        try {
            project_db.deleteTask(task.getDescription(), task.getProjectID());
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
        } catch (SQLException e) {
            // TODO Exception
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
            // TODO Exception
        }
        return FXCollections.observableArrayList();
    }

    @Override
    public void assignTaskCollaborator(ObservableList<String> collaborators, Task task) {
        if (storageLimitReached()) {
            return;
        }
        try {
            for (String collaborator : collaborators) {
                project_db.addTaskCollaborator(task.getId(), user_db.getUserInfo(collaborator).getId());
            }
        } catch (SQLException e) {
            // TODO Exception
            e.printStackTrace();
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
        } catch (SQLException e) {
            // TODO Exception
            e.printStackTrace();
        }
        return names;
    }

    @Override
    public void deleteTaskCollaborator(String collaborator, Task task) {
        try {
            project_db.deleteTaskCollaborator(task.getId(), user_db.getUserInfo(collaborator).getId());
        } catch (SQLException e) {
            // TODO Exeption
            e.printStackTrace();
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
                // TODO message user doesnt exist
            }
            int receiverID = user_db.getUserInfo(username).getId();
            if (project_db.getCollaborators(project_id).contains(receiverID)) {
                // TODO message colalborator already in project
            }
            user_db.sendInvitation(project_id, user_db.getCurrentUser().getId(), receiverID);
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
        } catch (SQLException e) {
            // TODO exception
        }
    }

    @Override
    public void deleteCollaborator(String collaboratorName, int project_id) {
        try {
            project_db.deleteCollaborator(project_id, user_db.getUserInfo(collaboratorName).getId());
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
        } catch (SQLException e) {
            // TODO Exception
            e.printStackTrace();
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
            // TODO Exception
            e.printStackTrace();
        }
        return names;
    }

    @Override
    public boolean isUserInTask(String user, Task task) {
        try {
            return project_db.getTaskCollaborator(task.getId()).contains((user_db.getUserInfo(user).getId()));
        } catch (SQLException e) {
            // TODO Exception
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void importProject(String path) {
        if (storageLimitReached()) {
            return;
        }
        ioController.onImportProject(path);
    }

    @Override
    public void exportProject(Project project, String path) {
        ioController.onExportProject(project, path);
    }

    @Override
    public void uploadProject() {
    }

    @Override
    public void downloadProject() {
        if (storageLimitReached()) {
            return;
        }
        showCloudDownloadStage();
    }


    /**
     * Adds a project to the tree, the map and the database.
     */
    public void onAddProject(String title, String description, LocalDate date, ObservableList<String> tags, String parent) {
        if (storageLimitReached()) {
            return;
        }
        try {
            int parentID = 0;

            if (title.equals("")) {
                // TODO Cannot add a project with an empty title
            } else if (project_db.getProjectID(title) != 0) {
                // TODO A project with the same title already exists.
            } else if (date == null) {
                // TODO Cannot create a project without a date.
            } else if (parent.equals("") || project_db.getProjectID(parent) != 0) {

                if (!parent.equals("")) {
                    parentID = project_db.getProjectID(parent);
                }
                int newProjectID = project_db.createProject(title, description, date.toEpochDay(), parentID);
                for (String tag : tags) {
                    project_db.addTag(project_db.getTagID(tag), newProjectID);
                }

                project_db.addCollaborator(newProjectID, user_db.getCurrentUser().getId());
                TreeItem<Project> child = new TreeItem<>(project_db.getProject(newProjectID));
                viewController.insertProject(newProjectID, child, parentID);
            }
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
        } catch (SQLException e) {
            // TODO Exception
        }
    }


    /**
     * Adds a task to the parent project, adds it to the database.
     *
     * @param taskDescription String
     */
    public void onAddTask(String taskDescription, int project_id) {
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
                // TODO Task already exists
                return;
            }
            if (project_id != 0) {
                project_db.createTask(taskDescription, project_id);
            }
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
        } catch (SQLException e) {
            // TODO Exception
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