package be.ac.ulb.infof307.g06.controllers.project;

import be.ac.ulb.infof307.g06.controllers.MainController;
import be.ac.ulb.infof307.g06.models.*;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.ProjectViews.CloudViewController;
import be.ac.ulb.infof307.g06.views.ProjectViews.ProjectInputViewController;
import be.ac.ulb.infof307.g06.views.ProjectViews.ProjectsViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;
import javafx.stage.Modality;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ProjectController implements ProjectsViewController.ViewListener {
    private final TasksController tasksController = new TasksController(this);
    private final CollaboratorsController collaboratorsController = new CollaboratorsController();

    /**
     * Initializes the view, the root, trees and clears the projects table+ the map to "reload" them.
     *
     * @param view ProjectsViewController
     * @param root TreeItem<Project>
     */
    public static void init(ProjectsViewController view, TreeItem<Project> root) {
        Global.projectsView = view;
        Global.root = root;
        view.initTree();
        try {
            ProjectDB.createTag("tag1", "#4287f5");
            ProjectDB.createTag("tag2", "#ffffff");
            ProjectDB.createTag("tag3", "#000000");
            Global.TreeMap.clear();
            List<Integer> projectsArray = ProjectDB.getUserProjects(Global.userID);
            getProjects(projectsArray);
        } catch (SQLException e) {
            new AlertWindow("Error", "Could not initialize the window").errorWindow();
        }
    }

    /**
     * Sets the loader to show the Project scene.
     */
    public void showProjects() throws IOException {
        FXMLLoader loader = new FXMLLoader(ProjectsViewController.class.getResource("ProjectsViewV2.fxml"));
        loader.load();
        ProjectsViewController controller = loader.getController();
        controller.setListener(this);
        MainController.load(loader, 940, 1515);
    }

    /**
     * Sets the loader to show the stage to add a project.
     */
    public static void showAddProjectStage() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ProjectsViewController.class.getResource("AddProjectView.fxml"));
        MainController.showStage("Add project", 541, 473, Modality.APPLICATION_MODAL, loader);
    }

    /**
     * Sets the loader to show the stage to edit a project.
     */
    public static void showCloudDownloadStage() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(CloudViewController.class.getResource("CloudView.fxml"));
        MainController.showStage("Add project", 750, 400, Modality.APPLICATION_MODAL, loader);
    }


    public static void showEditProjectStage() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ProjectsViewController.class.getResource("EditProjectView.fxml"));
        MainController.showStage("Edit Project", 541, 473, Modality.APPLICATION_MODAL, loader);
    }

    /**
     * Initializes the map and displays projects on the tree table view.
     *
     * @param projects List<Integer>;
     */
    public static void getProjects(List<Integer> projects) {
        try {
            Global.projectsView.hideRoot();
            for (Integer project : projects) {
                Project childProject = ProjectDB.getProject(project);
                int parentID = childProject.getParent_id();
                String title = childProject.getTitle();
                int childID = ProjectDB.getProjectID(title);
                TreeItem<Project> child = new TreeItem<Project>(childProject);
                Global.TreeMap.put(childID, child);
                if (parentID == 0) {
                    Global.projectsView.addChild(Global.root, child);
                } else {
                    Global.projectsView.addChild(Global.TreeMap.get(parentID), child);
                }
            }
            Global.projectsView.refresh();
        } catch (SQLException e) {
            new AlertWindow("Database error", "Could not access the database").errorWindow();
        }
    }

    /**
     * @param collaborators ObservableList<String>
     * @param selectedTask  Task
     */
    public void assignCollaborators(ObservableList<String> collaborators, Task selectedTask) {
        collaboratorsController.assignCollaborators(collaborators, selectedTask);
    }

    public static Project getProject(String currentProject) {
        Project project = new Project();
        try {
            project = ProjectDB.getProject(ProjectDB.getProjectID(currentProject));
        } catch (SQLException e) {
            new AlertWindow("Database error", "Could not access the database").errorWindow();
        }
        return project;

    }

    /**
     * gets a project information and displays it
     *
     * @param view            ProjectsViewController
     * @param selectedProject TreeItem<Project>
     */
    public Project getProjectInfo(ProjectsViewController view, TreeItem<Project> selectedProject) {
        try {
            String description = selectedProject.getValue().getDescription();
            String title = selectedProject.getValue().getTitle();
            Long date = selectedProject.getValue().getDate();
            int id = selectedProject.getValue().getId();
            List<Tag> tags = ProjectDB.getTags(id);
            ObservableList<String> tagsName = FXCollections.observableArrayList();
            for (Tag tag : tags) {
                tagsName.add(tag.getDescription());
                System.out.println("Add tag " + tag.getDescription());
            }
            return new Project(id, title, description, date, tagsName);
        } catch (NullPointerException | SQLException e) {
            new AlertWindow("Database error", "Could not access the database").errorWindow();
        }
        return null;
    }

    /**
     * Initializes the tags combobox.
     *
     * @param inputView ProjectInputViewController
     */
    public void initComboBox(ProjectInputViewController inputView){
        try{
            final ObservableList<String> tags = FXCollections.observableArrayList();
            List<Tag> tagsList = ProjectDB.getAllTags();
            for (Tag tag : tagsList) {
                tags.add(tag.getDescription());
            }
            inputView.addTags(tags);
        }catch(SQLException e){
            new AlertWindow("Database error", "Could not initialize the input window").errorWindow();
        }
    }

    /**
     *
     *
     * @param inputView ProjectInputViewController
     */
    public void initProjectExport(ProjectInputViewController inputView){
        try{
            final ObservableList<String> projectsTitleList = FXCollections.observableArrayList();
            List<Integer> ProjectIDList = ProjectDB.getUserProjects(Global.userID);
            for (Integer projectID : ProjectIDList) {
                projectsTitleList.add(ProjectDB.getProject(projectID).getTitle());
            }
            //inputView.addProjectTitle(projectsTitleList);//i
        }catch(SQLException e){
            new AlertWindow("Database error", "Could not export the project").errorWindow();
        }
    }

    /**
     * Adds a project to the tree, the map and the database.
     *
     * @param addView ProjectInputViewController
     */
    public void addProject(ProjectInputViewController addView){
        try{
            int parentID=0;
            String nameProject = addView.getNameProject();
            String descriptionProject = addView.getDescriptionProject();
            LocalDate dateProject = addView.getDateProject();
            String parentProject = addView.getParentProjectName();

            if(nameProject.equals("")) { addView.setError("Cannot add a project with an empty title.");}
            else if (ProjectDB.getProjectID(nameProject) != 0){ addView.setError("A project with the same title already exists.");}
            else if(dateProject == null){ addView.setError("Cannot create a project without a date.");}
            else if (parentProject.equals("") || ProjectDB.getProjectID(parentProject)!=0){

                if(!parentProject.equals("")){ parentID= ProjectDB.getProjectID(parentProject);}
                System.out.println("addProject " + dateProject.toEpochDay());
                int newProjectID = ProjectDB.createProject(nameProject,descriptionProject,dateProject.toEpochDay(),parentID);

                ObservableList<String> tags = addView.getSelectedTags();//
                for (String tag : tags) {
                    ProjectDB.addTag(ProjectDB.getTagID(tag), newProjectID);
                }

                ProjectDB.addCollaborator(newProjectID, Global.userID);
                TreeItem<Project> child = new TreeItem<Project>(ProjectDB.getProject(newProjectID));
                Global.TreeMap.put(newProjectID, child);
                addView.setError("");

                if (parentID == 0) { Global.projectsView.addChild(Global.root,child); }
                else { Global.projectsView.addChild(Global.TreeMap.get(parentID), child); }
            }
            UserDB.updateDiskUsage(ProjectDB.getSizeOnDisk());
            MainController.closeStage();
        }catch(SQLException e){
            new AlertWindow("Database error", "Could not access the database").errorWindow();
        }
    }

    /**
     * Changes the description of a task and displays it
     *
     * @param description String
     * @param newDescription String
     * @param task Task
     */
    public void editTask(String description, String newDescription, Task task){
        tasksController.editTask(description, newDescription, task);
    }

    /**
     * Adds a task to the parent project, adds it to the database.
     *
     * @param taskDescription String
     */
    public void onAddTask(String taskDescription, int project_id) {
        tasksController.onAddTask(taskDescription, project_id);
    }

    /**
     * Deletes a task from the database and the table.
     *
     * @param task Task
     */
    public void deleteTask(Task task){
        tasksController.deleteTask(task);
    }


    /**
     * Checks if the string has at least one alphabet character and as 1 to 126 characters
     *
     * @param text;
     * @return boolean;
     */
    @FXML
    protected boolean validateDescription(String text) {
        Pattern p = Pattern.compile("^.*[a-zA-Z0-9]{1,126}$");
        Matcher m = p.matcher(text);
        return m.matches();
    }

    /**
     * Changes a Long format date to a string date.
     *
     * @param date Long
     * @return
     */
    public String dateToString(Long date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date * 86400000L);
    }

    /**
     * Changes a project's informations with the new ones.
     *
     * @param inputView ProjectInputViewController
     */
    public void editProject(ProjectInputViewController inputView) {
        try {
            int projectID = ProjectDB.getProjectID(inputView.getNameProject());
            if (projectID != 0 && projectID != ProjectDB.getProjectID(Global.currentProject)) {
                inputView.setError("Cannot edit the project with such a title.");
            } else if (inputView.getNameProject().equals("")) {
                inputView.setError("Cannot edit a project with an empty name.");
            } else {
                ProjectDB.editProject(
                        ProjectDB.getProjectID(Global.currentProject),
                        inputView.getNameProject(),
                        inputView.getDescriptionProject(),
                        inputView.getDateProject().toEpochDay()
                );
                List<Integer> tags = new ArrayList<>();
                ObservableList<String> newTags = inputView.getSelectedTags();
                for (String newTag : newTags) {
                    tags.add(ProjectDB.getTagID(newTag));
                }
                ProjectDB.editTags(projectID, tags);
                inputView.setError("");
                init(Global.projectsView, Global.root);
            }
            UserDB.updateDiskUsage(ProjectDB.getSizeOnDisk());
        }catch(SQLException e){
            new AlertWindow("Database error", "Could not access the database").errorWindow();
        }
    }

    /**
     * Returns collaborators' id of a project.
     *
     * @param project TreeItem<Project>
     * @return ObservableList<String>
     */
    public ObservableList<String> getCollaborators(TreeItem<Project> project){
        return collaboratorsController.getCollaborators(project);
    }

    /**
     * Adds a collaborator to a project and in the database.
     *
     * @param username String
     * @param project  int
     * @return Boolean
     */

    @Override
    public void addCollaborator(String username, int project) {
        //MainController.alertWindow(Alert.AlertType.INFORMATION,"Alert","User " + collaboratorsName.getText() + " doesn't exist.");
        collaboratorsController.addCollaborator(username, project);
    }

    /**
     * Returns the string of a list without brackets.
     *
     * @param list ObservableList<String>
     * @return String
     */
    public String listToString(ObservableList<String> list) {
        return list.toString().replaceAll("(^\\[|\\]$)", "");
    }

    @Override
    public void back() {

    }

    @Override
    public void addProject() {
        showAddProjectStage();
    }

    @Override
    public void deleteProject(String name) {
        try {
            int projectID = ProjectDB.getProjectID(name);
            ProjectDB.deleteProject(projectID);
            init(Global.projectsView, Global.root);
            UserDB.updateDiskUsage(ProjectDB.getSizeOnDisk());
        } catch (SQLException e) {
            new AlertWindow("Database error", "Could not access the database").errorWindow();
        }
    }

    @Override
    public void editProject() {
        showEditProjectStage();
    }

    @Override
    public ObservableList<String> getProjectTags(Project project) {
        List<Tag> tags = null;
        try {
            tags = ProjectDB.getTags(project.getId());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ObservableList<String> tagsName = FXCollections.observableArrayList();
        assert tags != null;
        for (Tag tag : tags) {
            tagsName.add(tag.getDescription());
        }
        return tagsName;
    }

    @Override
    public void addTask(String description, int project_id) {
        tasksController.onAddTask(description, project_id);
    }

    @Override
    public void editTask(Task task) {
        TasksController.showEditTaskStage();
    }

    @Override
    public ObservableList<Task> getTasks(Project project) {
        try {
            if (project != null) {
                String projectTitle = project.getTitle();
                int projectID = ProjectDB.getProjectID(projectTitle);
                List<Task> taskList = ProjectDB.getTasks(projectID);
                return FXCollections.observableArrayList(taskList);
            }
        } catch (SQLException e) {
            new AlertWindow("Database error", "Could not access the database").errorWindow();
        }
        return FXCollections.observableArrayList();
    }

    @Override
    public void assignTaskCollaborator(ObservableList<String> collaborators, Task task) {
        collaboratorsController.assignCollaborators(collaborators, task);
    }

    @Override
    public ObservableList<String> getTaskCollaborators(Task task) {
        try {
            if (task != null) {
                ObservableList<String> names = FXCollections.observableArrayList();
                List<Integer> collaborators = ProjectDB.getTaskCollaborator(task.getId());
                for (Integer collaborator : collaborators) {
                    names.add((UserDB.getUserInfo(collaborator).get("uName")));
                }
                return names;
            }
        } catch (SQLException e) {
            new AlertWindow("Database error", "Could not access the database").errorWindow();
        }
        return null;
    }

    @Override
    public void deleteTaskCollaborator(String collaborator, Task task) {

    }

    @Override
    public void importProject() {

    }

    @Override
    public boolean exportProject(Project selectedProject, String absolutePath, String s) {
        return false;
    }

    @Override
    public void uploadProject() {

    }

    @Override
    public void downloadProject() {

    }


    @Override
    public void deleteCollaborator(String collaboratorName, int project_id) {
        try {
            ProjectDB.deleteCollaborator(project_id, Integer.parseInt(UserDB.getUserInfo(collaboratorName).get("id")));
            UserDB.updateDiskUsage(ProjectDB.getSizeOnDisk());
        } catch (SQLException e) {
            new AlertWindow("Error", "The collaborator could not be deleted: \n").errorWindow();
        }
    }

    @Override
    public ObservableList<String> getCollaborators(Project project) {
        try {
            ObservableList<String> names = FXCollections.observableArrayList();
            List<Integer> collaborators;
            collaborators = ProjectDB.getCollaborators(project.getId());
            for (Integer collaborator : collaborators) {
                names.add((UserDB.getUserInfo(collaborator).get("uName")));
            }
            return names;
        } catch (SQLException e) {
            new AlertWindow("Error", "Error initializing collaborators").errorWindow();
        }
        return null;
    }

    @Override
    public boolean isUserInTask(String user, Task task) {
        try {
            return ProjectDB.getTaskCollaborator(task.getId()).contains(Integer.parseInt(UserDB.getUserInfo(user).get("id")));
        } catch (SQLException e) {
            new AlertWindow("Database access error", "Could not access the database").errorWindow();
        }
        return false;
    }

}

