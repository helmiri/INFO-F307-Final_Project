package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.statisticsViews.StatsViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The statistics screen controller
 */
public class StatsController extends Controller implements StatsViewController.ViewListener  {
    //--------------- ATTRIBUTE ----------------
    private StatsViewController statsView;

    //--------------- METHODS ----------------
    public StatsController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene, String DB_PATH) {
        super(user_db, project_db, stage, scene, DB_PATH);
    }

    /**
     * Shows the main scene of statistics (overall statistics view).
     */
    @Override
    public void show() {
        String fxmlName = "OverallStatsView.fxml";
        load(fxmlName);
        statsView.initOverallStats();
    }

    /**
     * Shows the individual statistics view.
     */
    @Override
    public void showIndividualStats() {
        String fxmlName = "IndividualStatsView.fxml";
        load(fxmlName);
        statsView.initTables();
    }

    /**
     * Sets the loader to show the appropriate statistics scene.
     *
     * @param fxmlFilename name of the xml file to be loaded
     */
    private void load(String fxmlFilename) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(StatsViewController.class.getResource(fxmlFilename));
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            new AlertWindow("Error", "An error has occurred. Try to restart the application.", e.getMessage()).showErrorWindow();
        }
        statsView = loader.getController();
        statsView.setListener(this);
        stage.setScene(scene);
        stage.sizeToScene();
    }

    /**
     * Returns a projects list of the actual user.
     *
     * @return The IDs of the current user's projects
     *
     */
    @Override
    public List<Integer> getProjects()  {
        try {
            return project_db.getUserProjects(user_db.getCurrentUser().getId());
        } catch(SQLException error) {
            new AlertWindow("Database error","Unable to get user's projects from the database: ",error.getMessage()).showErrorWindow();
            return null;
        }
    }

    /**
     * Returns a project thanks to its id.
     *
     * @param id The identifier of the project
     * @return The project
     */
    @Override
    public Project getProjectsFromID(int id){
        try {
            return project_db.getProject(id);
        } catch(SQLException error) {
            new AlertWindow("Database error","Unable to get a project from its ID in the database: ",error.getMessage()).showErrorWindow();
            return null;
        }
    }

    /**
     * Changes a "Long" date to a string.
     *
     * @param date current date
     * @return date in string format
     */
    @Override
    public String dateToString(Long date){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Long TO_DAY = 86400000L;
        return dateFormat.format(date * TO_DAY);
    }

    /**
     * Displays projects on the tree table view
     *
     * @param projects The IDs of the projects
     * @param root     The Project item from the table tree
     */
    @Override
    public void setProjectsTable(List<Integer> projects,TreeItem<Project> root)  {
        try{
            Map<Integer, TreeItem<Project>> statsTreeMap = new HashMap<>();
            for(Integer project : projects){
                Project childProject = project_db.getProject(project);
                int parentID = childProject.getParentId();
                String title = childProject.getTitle();
                int childID = project_db.getProjectID(title);
                TreeItem<Project> projectTreeItem = new TreeItem<>(childProject);
                statsTreeMap.put(childID, projectTreeItem);
                if (parentID== 0){ statsView.addChild(root,projectTreeItem); } else { statsView.addChild(statsTreeMap.get(parentID),projectTreeItem); }
            }
            statsView.expandRoot(root);
        }catch(SQLException error){
            new AlertWindow("Database error","Unable to get a project from the database: ",error.getMessage()).showErrorWindow();
        }
    }

    /**
     * Displays tasks of the selected project in the tasks table.
     *
     * @param selectedProject Project, the selected project.
     * @return ObservableList of tasks, that contains tasks of the selected project.
     */
    @Override
    public ObservableList<Task> setTasksTable(Project selectedProject) {
        try{
            List<Task> projectTasks = project_db.getTasks(selectedProject.getId());
            return FXCollections.observableArrayList(projectTasks);
        }catch(SQLException error){
            new AlertWindow("Database error","Unable to get tasks from the database: ",error.getMessage()).showErrorWindow();
        }
        return FXCollections.observableArrayList();
    }

    /**
     * Counts the global statistics.
     *
     * @return A list containing the number of projects, tasks and collaborators across all projects
     */
    @Override
    public List<Integer> countOverallStats() {
        List<Integer> res = new ArrayList<Integer>(Collections.nCopies(3, 0));
        try {
            List<Integer> projects = project_db.getUserProjects(user_db.getCurrentUser().getId());
            if (projects.size() != 0) {
                int tasks = 0;
                List<Integer> collaborators = new ArrayList<>();
                for (Integer project : projects) {
                    tasks += project_db.countTasks(project);
                    List<Integer> projectsCollaborators = project_db.getCollaborators(project);
                    for (Integer collaborator : projectsCollaborators) {
                        if (!collaborators.contains(collaborator)) { collaborators.add(collaborator); }
                    }
                }

                res.set(0,projects.size());
                res.set(1,tasks);
                res.set(2,collaborators.size());
            }
        }catch(SQLException error){
            new AlertWindow("Database error", "Unable to load data due to a database access error: ", error.getMessage()).showErrorWindow();
            return res;
        }
        return res;
    }

    /**
     * Counts the statistics of a specific project.
     *
     * @param selectedProject Project
     * @return a list with the number of sub-projects, tasks and collaborators of selectedProject
     */
    @Override
    public List<Integer> countIndividualProjectStats(Project selectedProject){
        List<Integer> res = new ArrayList<Integer>(Collections.nCopies(3, 0));
        try{
            int sub = 0;
            if(selectedProject != null) {
                int projectId = selectedProject.getId();
                res.set(0,countSubProject(projectId, sub));
                res.set(1,project_db.countTasks(projectId));
                res.set(2,project_db.countCollaborators(projectId));
            }
            return res;
        } catch (SQLException error){
            new AlertWindow("Error", "An error has occurred with the database while trying to load counts: ", error.getMessage()).showErrorWindow();
            return res;
        }
    }

    /**
     * Counts the tasks of a project.
     *
     * @param projectId The identifier of the project
     * @return The number of tasks in the project
     */
    @Override
    public Integer countTasksOfAProject(int projectId)  {
        try {
            return project_db.countTasks(projectId);
        } catch (SQLException error) {
            new AlertWindow("Database error", "Couldn't count the number of tasks from the database: ", error.getMessage()).showErrorWindow();
            return 0;
        }
    }

    /**
     * Transforms the tree table view into strings and writes it down in a file.
     *
     * @param fileName The name of the file to be written
     */
    @Override
    public void exportStatsAsJson(String fileName, String path) {
        try {
            StringBuilder finalString = new StringBuilder("{\n");
            List<Integer> projectsID = project_db.getUserProjects(user_db.getCurrentUser().getId());
            for (Integer project : projectsID) {
                Project child = project_db.getProject(project);
                boolean isMainProject = child.getParentId() == 0;
                if (isMainProject){
                    String treeBranchString = "";
                    treeBranchString = statToJsonString(project, treeBranchString );
                    String informationRelatedToStats = generateJSONFormat(child);
                    String gotChild = "{" + informationRelatedToStats.replaceAll("(^\\{|}$)", "");
                    treeBranchString = ("'" + child.getTitle() + "'" + " :" + gotChild + "," + treeBranchString + "\n");
                    finalString.append(treeBranchString);
                }
            } write(finalString + "}", fileName, path);
        }catch(DatabaseException  error ) {
            error.show();
        }catch(SQLException error){
            new AlertWindow("Database error", "An error has occurred with the database while trying to get a project: ", error.getMessage()).showErrorWindow();
        }
    }

    /**
     * Transforms a stat into a json string with his children recursively.
     *
     * @param id               The identifier of the project
     * @param treeBranchString String
     * @return The new treeBranchString
     */
    public String statToJsonString(Integer id, String treeBranchString) throws DatabaseException {
        try {
            List<Integer> projectsID = project_db.getSubProjects(id);
            for (int k = 0; k < projectsID.size(); k++) {
                Project currentProject = project_db.getProject(projectsID.get(k));

                String informationRelatedToStats = generateJSONFormat(currentProject);
                if (project_db.getSubProjects(projectsID.get(k)).size() == 0) {
                    //Has no children so we can let the brackets closed --> 'name':{info}.
                    treeBranchString = (treeBranchString + '"' + currentProject.getTitle() + '"' + " :" + informationRelatedToStats + ",");
                } else {
                    //Has a child so we let it open -->'Name':{info,... and check the child's children.
                    String gotChild = "{" + informationRelatedToStats.replaceAll("(^\\{|}$)", "");
                    treeBranchString += '"' + currentProject.getTitle() + '"' + " :" + gotChild + ",";
                    treeBranchString = statToJsonString(project_db.getSubProjects(id).get(k), treeBranchString);
                }
            }
            treeBranchString += "},";
        } catch (SQLException error) {
            throw new DatabaseException(error);
        }
        return treeBranchString;
    }

    /**
     * Generates the JSON format.
     *
     * @param currentProject Project
     * @return JSON string
     */
    private String generateJSONFormat(Project currentProject) {
        List<Integer> counts = countIndividualProjectStats(currentProject);
        String startDate = dateToString(currentProject.getStartDate()), endDate = dateToString(currentProject.getEndDate());
        return "{" + '"' + "Collaborators" + '"' + ":" + counts.get(2) +
                "," + '"' + "Tasks" + '"' + ":" + counts.get(1) +
                "," + '"' + "Sub projects" + '"' + ":" + counts.get(0) +
                "," + '"' + "Start date" + '"' + ":" + startDate +
                "," + '"' + "Estimated date" + '"' + ":" + endDate + "}";
    }

    /**
     * Exports the statistics in CSV file.
     *
     * @param fileName name given to the file
     * @param path     path given for the destination of the file exported
     */
    @Override
    public void exportStatsAsCSV(String fileName, String path) {
        try (PrintWriter csv = new PrintWriter(path + fileName)) {
            // Name of columns
            String content = "ID" + "," + "Title" + "," + "Collaborators" + "," + "Tasks" + "," + "Sub projects" + "," + "Parent ID" + "," + "Start date" + "," + "Estimated date" + "\r\n";
            List<Integer> projectsID = project_db.getUserProjects(user_db.getCurrentUser().getId());
            for (Integer project : projectsID) {
                Project child = project_db.getProject(project);
                boolean isMainProject = child.getParentId() == 0;
                if (isMainProject){
                    content = statsToCSVString(project, child, content);
                }}
            csv.write(content);
            new AlertWindow("Success", "Exported successfully").showInformationWindow();
        } catch (FileNotFoundException error) {
            new AlertWindow("Error", "Couldn't find or access to this file: ", error.getMessage()).showErrorWindow();
        } catch (SQLException error) {
            new AlertWindow("Database Error", "Could not access the database: ", error.getMessage()).showErrorWindow();
        } catch (DatabaseException error) {
            error.show();
        }
    }

    /**
     * Returns CSV format.
     *
     * @param currentProjectID ID of the current object statistics
     * @param currentProject   the information to add in the content
     * @param content          CSV format
     * @return the content of the file with CSV format
     * @throws DatabaseException throws SQL exceptions
     */
    public String statsToCSVString(int currentProjectID, Project currentProject, String content) throws DatabaseException {
        List<Integer> counter = countIndividualProjectStats(currentProject);
        int numberOfCollaborators = counter.get(2), numberOfTasks = counter.get(1), numberOfSubProjects = counter.get(0);
        String startDate = dateToString(currentProject.getStartDate()), endDate = dateToString(currentProject.getEndDate());
        try {
            if (project_db.getSubProjects(currentProjectID).size() == 0) {
                content += currentProjectID + "," + currentProject.getTitle() + "," + '"' + numberOfCollaborators + '"' + "," + '"' + numberOfTasks + '"' + "," + numberOfSubProjects + "," + project_db.getProject(currentProjectID).getParentId() + "," + startDate + "," + endDate + "\r\n";
            } else {
                content += currentProjectID + "," + currentProject.getTitle() + "," + '"' + numberOfCollaborators + '"' + "," + '"' + numberOfTasks + '"' + "," + numberOfSubProjects + "," + project_db.getProject(currentProjectID).getParentId() + "," + startDate + "," + endDate + "\r\n";
                for (int k = 0; k < project_db.getSubProjects(currentProjectID).size(); k++) {
                    content = statsToCSVString(project_db.getSubProjects(currentProjectID).get(k), project_db.getProject(project_db.getSubProjects(currentProjectID).get(k)), content);
                }
            }
        } catch (SQLException error) {
            throw new DatabaseException(error);
        }
        return content;
    }

    /**
     * Writes information in a file for a json format.
     *
     * @param chosenString String
     * @param fileName     String
     * @param path         String
     */
    public void write(String chosenString, String fileName,String path) {
        try {
            FileWriter writer = new FileWriter(path + fileName, false);
            writer.write(chosenString + "\n");
            writer.close();
            new AlertWindow("Success", "Success in exporting statistics.").showInformationWindow();
        } catch (IOException error) {
            new AlertWindow("Error", "Couldn't write in this file (" + path + fileName + ")", error.getMessage()).showErrorWindow();
        }
    }

    /**
     * @param projectID int
     * @param counter int
     * @return int
     * @throws SQLException e
     */
    public int countSubProject(int projectID, int counter) throws SQLException {
        for (int k = 0; k < project_db.getSubProjects(projectID).size(); k++) {
            counter += 1;
            counter = countSubProject(project_db.getSubProjects(projectID).get(k), counter);
        }
        return counter;
    }

    /**
     * Return to previous scene
     */
    @Override
    public void onBackButtonClicked() {
        back();
    }
}