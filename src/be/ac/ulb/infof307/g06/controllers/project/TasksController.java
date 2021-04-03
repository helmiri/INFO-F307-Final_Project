package be.ac.ulb.infof307.g06.controllers.project;

import be.ac.ulb.infof307.g06.controllers.MainController;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.ProjectViews.ProjectsViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TasksController {
    private final ProjectController projectController;

    public TasksController(ProjectController projectController) {
        this.projectController = projectController;
    }

    /**
     * Sets the loader to show the stage to edit a task.
     */
    public static void showEditTaskStage() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ProjectsViewController.class.getResource("TaskEditView.fxml"));
        MainController.showStage("Edit Task", 435, 256, Modality.APPLICATION_MODAL, loader);
    }

    /**
     * @param task Task
     * @param view ProjectsViewController
     */
    public void initTaskCollaborators(ProjectsViewController view, Task task) {
        try {
            if (task != null) {
                ObservableList<String> names = FXCollections.observableArrayList();
                List<Integer> collaborators = ProjectDB.getTaskCollaborator(task.getId());
                for (Integer collaborator : collaborators) {
                    names.add((UserDB.getUserInfo(collaborator).get("uName")));
                }
                view.insertTaskCollaborators(names);
            }
        } catch (SQLException e) {
            new AlertWindow("Database error", "Could not access the databse").errorWindow();
        }
    }

    public void deleteTaskCollaborator(String collaborator, Task selectedTask) {
        try {
            ProjectDB.deleteTaskCollaborator(selectedTask.getId(), Integer.parseInt(UserDB.getUserInfo(collaborator).get("id")));
        } catch (SQLException e) {
            new AlertWindow("Database error", "Could not access the databse").errorWindow();
        }
    }

    /**
     * returns if a user is in a task's collaborators
     *
     * @param taskId int
     * @param user   String
     * @return boolean
     */
    public boolean isUserInTask(int taskId, String user) {
        return true;
    }

    /**
     * Changes the description of a task and displays it
     *
     * @param description    String
     * @param newDescription String
     * @param task           Task
     */
    public void editTask(String description, String newDescription, Task task) {
        try {
            List<Task> tasks = ProjectDB.getTasks(task.getProjectID());
            if (taskExists(newDescription, tasks)) return;
            if (newDescription.equals("")) {
                deleteTask(task);
            } else if (projectController.validateDescription(newDescription)) {
                ProjectDB.editTask(description, newDescription, task.getProjectID());
            }
            UserDB.updateDiskUsage(ProjectDB.getSizeOnDisk());
        } catch (SQLException e) {
            new AlertWindow("Error", "Failed to save changes").errorWindow();
        }
    }

    private boolean taskExists(String newDescription, List<Task> tasks) {
        List<String> taskNames = new ArrayList<String>();
        for (Task task2 : tasks) {
            taskNames.add(task2.getDescription());
        }
        if (taskNames.contains(newDescription)) {
            new AlertWindow("Error", "The task already exists").errorWindow();
            return true;
        }
        return false;
    }

    /**
     * Adds a task to the parent project, adds it to the database.
     *
     * @param taskDescription String
     */
    public void onAddTask(String taskDescription, int project_id) {
        if (taskDescription.isBlank()) {
            return;
        }
        try {
            List<Task> tasks = ProjectDB.getTasks(project_id);
            if (taskExists(taskDescription, tasks)) return;
            if (project_id != 0) {
                ProjectDB.createTask(taskDescription, project_id);
            }
            UserDB.updateDiskUsage(ProjectDB.getSizeOnDisk());
        } catch (SQLException e) {
            new AlertWindow("Database error", "Could not access the ").errorWindow();
        }
    }

    /**
     * Deletes a task from the database and the table.
     *
     * @param task Task
     */
    public void deleteTask(Task task) {
        try {
            ProjectDB.deleteTask(task.getDescription(), task.getProjectID());
            UserDB.updateDiskUsage(ProjectDB.getSizeOnDisk());
        } catch (SQLException e) {
            new AlertWindow("Database error", "Could not access the databse").errorWindow();
        }
    }
}