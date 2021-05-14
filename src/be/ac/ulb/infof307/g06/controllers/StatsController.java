package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.exceptions.WindowLoadException;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.models.database.ActiveUser;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
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
 * Controller for the statistics.
 */
public class StatsController extends Controller implements StatsViewController.ViewListener {
    //--------------- ATTRIBUTE ----------------
    private StatsViewController statsView;
    private ProjectDB projectDB;
    private ActiveUser activeUser;

    /**
     * Constructor
     *
     * @param stage Stage, a stage
     * @throws DatabaseException    On database access error
     * @throws NullPointerException When the CurrentUser instance has not been initialized
     */
    //--------------- METHODS ----------------
    public StatsController(Stage stage) throws DatabaseException, NullPointerException {
        super(stage);
        try {
            projectDB = new ProjectDB();
        } catch (SQLException error) {
            throw new DatabaseException(error);
        }
        activeUser = ActiveUser.getInstance();
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
        FXMLLoader loader = new FXMLLoader(StatsViewController.class.getResource(fxmlFilename));
        try {
            Scene statsScene = new Scene(loader.load());
            statsView = loader.getController();
            statsView.setListener(this);
            stage.setScene(statsScene);
        } catch (IOException error) {
            new WindowLoadException(error).show();
        }
    }

    /**
     * Returns a projects list of the actual user.
     *
     * @return The IDs of the current user's projects, null on error
     */
    @Override
    public List<Integer> getProjects() {
        try {
            return projectDB.getUserProjects(activeUser.getID());
        } catch (SQLException error) {
            new DatabaseException(error).show();
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
    public Project getProjectsFromID(int id) {
        try {
            return projectDB.getProject(id);
        } catch (SQLException error) {
            new DatabaseException(error).show();
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
    public String dateToString(Long date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Long today = 86400000L;
        return dateFormat.format(date * today);
    }

    /**
     * Displays projects on the tree table view
     *
     * @param projects The IDs of the projects
     * @param root     The Project item from the table tree
     */
    @Override
    public void setProjectsTable(List<Integer> projects, TreeItem<Project> root) {
        try {
            Map<Integer, TreeItem<Project>> statsTreeMap = new HashMap<>();
            for (Integer project : projects) {
                Project childProject = projectDB.getProject(project);
                int parentID = childProject.getParentId();
                String title = childProject.getTitle();
                int childID = projectDB.getProjectID(title);
                TreeItem<Project> projectTreeItem = new TreeItem<>(childProject);
                statsTreeMap.put(childID, projectTreeItem);
                if (parentID == 0) {
                    statsView.addChild(root, projectTreeItem);
                } else {
                    statsView.addChild(statsTreeMap.get(parentID), projectTreeItem);
                }
            }
            statsView.expandRoot(root);
        } catch (SQLException error) {
            new DatabaseException(error).show();
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
        try {
            List<Task> projectTasks = projectDB.getTasks(selectedProject.getId());
            return FXCollections.observableArrayList(projectTasks);
        } catch (SQLException error) {
            new DatabaseException(error).show();
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
        List<Integer> res = new ArrayList<>(Collections.nCopies(3, 0));
        try {
            List<Integer> projects = projectDB.getUserProjects(activeUser.getID());
            if (projects.size() != 0) {
                int tasks = 0;
                List<Integer> collaborators = new ArrayList<>();
                for (Integer project : projects) {
                    tasks += projectDB.countTasks(project);
                    List<Integer> projectsCollaborators = projectDB.getCollaborators(project);
                    for (Integer collaborator : projectsCollaborators) {
                        if (!collaborators.contains(collaborator)) {
                            collaborators.add(collaborator);
                        }
                    }
                }

                res.set(0, projects.size());
                res.set(1, tasks);
                res.set(2, collaborators.size());
            }
        } catch (SQLException error) {
            new DatabaseException(error).show();
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
    public List<Integer> countIndividualProjectStats(Project selectedProject) {
        List<Integer> res = new ArrayList<>(Collections.nCopies(3, 0));
        try {
            int sub = 0;
            if (selectedProject != null) {
                int projectId = selectedProject.getId();
                res.set(0, countSubProject(projectId, sub));
                res.set(1, projectDB.countTasks(projectId));
                res.set(2, projectDB.countCollaborators(projectId));
            }
            return res;
        } catch (SQLException error) {
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
    public Integer countTasksOfAProject(int projectId) {
        try {
            return projectDB.countTasks(projectId);
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
            List<Integer> projectsID = projectDB.getUserProjects(activeUser.getID());
            for (Integer project : projectsID) {
                Project child = projectDB.getProject(project);
                boolean isMainProject = child.getParentId() == 0;
                if (isMainProject) {
                    String treeBranchString = "";
                    treeBranchString = statToJsonString(project, treeBranchString);
                    String informationRelatedToStats = generateJSONFormat(child);
                    String gotChild = "{" + informationRelatedToStats.replaceAll("(^\\{|}$)", "");
                    treeBranchString = ("'" + child.getTitle() + "'" + " :" + gotChild + "," + treeBranchString + "\n");
                    finalString.append(treeBranchString);
                }
            }
            write(finalString + "}", fileName, path);
        } catch (DatabaseException error) {
            error.show();
        } catch (SQLException error) {
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
            List<Integer> projectsID = projectDB.getSubProjects(id);
            for (int k = 0; k < projectsID.size(); k++) {
                Project currentProject = projectDB.getProject(projectsID.get(k));

                String informationRelatedToStats = generateJSONFormat(currentProject);
                if (projectDB.getSubProjects(projectsID.get(k)).size() == 0) {
                    //Has no children so we can let the brackets closed --> 'name':{info}.
                    treeBranchString = (treeBranchString + '"' + currentProject.getTitle() + '"' + " :" + informationRelatedToStats + ",");
                } else {
                    //Has a child so we let it open -->'Name':{info,... and check the child's children.
                    String gotChild = "{" + informationRelatedToStats.replaceAll("(^\\{|}$)", "");
                    treeBranchString += '"' + currentProject.getTitle() + '"' + " :" + gotChild + ",";
                    treeBranchString = statToJsonString(projectDB.getSubProjects(id).get(k), treeBranchString);
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
            List<Integer> projectsID = projectDB.getUserProjects(activeUser.getID());
            for (Integer project : projectsID) {
                Project child = projectDB.getProject(project);
                boolean isMainProject = child.getParentId() == 0;
                if (isMainProject) {
                    content = statsToCSVString(project, child, content);
                }
            }
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
            if (projectDB.getSubProjects(currentProjectID).size() == 0) {
                content += currentProjectID + "," + currentProject.getTitle() + "," + '"' + numberOfCollaborators + '"' + "," + '"' + numberOfTasks + '"' + "," + numberOfSubProjects + "," + projectDB.getProject(currentProjectID).getParentId() + "," + startDate + "," + endDate + "\r\n";
            } else {
                content += currentProjectID + "," + currentProject.getTitle() + "," + '"' + numberOfCollaborators + '"' + "," + '"' + numberOfTasks + '"' + "," + numberOfSubProjects + "," + projectDB.getProject(currentProjectID).getParentId() + "," + startDate + "," + endDate + "\r\n";
                for (int k = 0; k < projectDB.getSubProjects(currentProjectID).size(); k++) {
                    content = statsToCSVString(projectDB.getSubProjects(currentProjectID).get(k), projectDB.getProject(projectDB.getSubProjects(currentProjectID).get(k)), content);
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
     * @param chosenString String to be written
     * @param fileName     Name of the file
     * @param path         Path to file
     */
    public void write(String chosenString, String fileName, String path) {
        try (FileWriter writer = new FileWriter(path + fileName, false)) {
            writer.write(chosenString + "\n");
            new AlertWindow("Success", "Success in exporting statistics.").showInformationWindow();
        } catch (IOException error) {
            new AlertWindow("Error", "Couldn't write in this file (" + path + fileName + ")", error.getMessage()).showErrorWindow();
        }
    }

    /**
     * @param projectID int
     * @param counter   int
     * @return int
     * @throws SQLException e
     */
    public int countSubProject(int projectID, int counter) throws SQLException {
        for (int k = 0; k < projectDB.getSubProjects(projectID).size(); k++) {
            counter += 1;
            counter = countSubProject(projectDB.getSubProjects(projectID).get(k), counter);
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