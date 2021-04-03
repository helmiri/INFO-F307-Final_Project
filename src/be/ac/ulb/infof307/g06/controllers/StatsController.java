package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Statistics;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.StatisticsViews.StatsViewController;
import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsController extends Controller {
    private StatsViewController statsView;

    public StatsController(int userID, UserDB user_db, ProjectDB project_db, Stage stage) {
        super(userID, user_db, project_db, stage);
    }

    /**
     * Initializes the main view and the tree. Sets values.
     *
     * @param view StatsViewController
     * @param root TreeItem<Statistics>
     */
    public void init(StatsViewController view, TreeItem<Statistics> root) {
        statsView = view;
        statsView.initTree();
        List<Integer> projectsArray;
        try {
            projectsArray = getProjects();
            setStats(projectsArray, root);
        } catch (DatabaseException e) {
            new AlertWindow("Error", "An error has occurred with the database: " + e).errorWindow();
        }

    }

    /**
     * Sets the loader to show the statistics scene.
     */
    public void show() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(StatsViewController.class.getResource("StatsView.fxml"));
        MainController.load(loader, 940, 1515);
    }

    /**
     * Returns a projects list of the actual user.
     *
     * @return List<Integer>
     * @throws DatabaseException e
     */
    public List<Integer> getProjects() throws DatabaseException {
        try {
            return project_db.getUserProjects(userID);
        } catch(SQLException e) {
            throw new DatabaseException(e);
        }
    }

    /**
     * Changes a list to a string without brackets.
     *
     * @param list ObservableList<String>
     * @return String
     */
    public String listToString(ObservableList<String> list){ return list.toString().replaceAll("(^\\[|]$)",""); }

    /**
     * Returns a list of collaborators names from their id.
     *
     * @param project Integer
     * @return ObservableList<String>
     * @throws SQLException;
     */
    public ObservableList<String> collaboratorsToString(Integer project) throws SQLException {
        ObservableList<String> collaboratorsNames = FXCollections.observableArrayList();
        List<Integer> collaborators = project_db.getCollaborators(project);
        for (Integer collaborator : collaborators) {
            collaboratorsNames.add(user_db.getUserInfo(collaborator).getUserName());
        }
        return collaboratorsNames;
    }

    /**
     * Returns a list of tasks descriptions from their id.
     *
     * @param project Integer
     * @return ObservableList<String>
     * @throws SQLException;
     */
    public ObservableList<String> tasksToString(Integer project) throws SQLException {
        ObservableList<String> tasksDescriptions = FXCollections.observableArrayList();
        List<Task> tasks = project_db.getTasks(project);
        for (Task task : tasks) {
            tasksDescriptions.add(task.getDescription());
        }
        return tasksDescriptions;
    }

    /**
     * Changes a "Long" date to a string.
     *
     * @param date Long
     * @return String
     */
    public String dateToString(Long date){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date * 86400000L);
    }

    /**
     * Displays projects on the tree table view
     *
     * @param projects List<Integer>
     * @param root TreeItem<Statistics>
     * @throws DatabaseException e
     */
    public void setStats(List<Integer> projects,TreeItem<Statistics> root) throws DatabaseException {
        try{
            Map<Integer, TreeItem<Statistics>> statsTreeMap = new HashMap<>();
            for(Integer project : projects){
                Project childProject = project_db.getProject(project);
                int parentID = childProject.getParent_id();
                String title = childProject.getTitle();
                int childID = project_db.getProjectID(title);
                Statistics stat = createStat(childProject, project, title, childID);
                TreeItem<Statistics> statsItem = new TreeItem<>(stat);
                statsTreeMap.put(childID, statsItem);
                if (parentID== 0){ statsView.addChild(root,statsItem); }
                else { statsView.addChild(statsTreeMap.get(parentID),statsItem); }
            }
            statsView.expandRoot(root);
        }catch(SQLException e){
            throw new DatabaseException(e);
        }
    }

    /**
     * Creates a Statistics object
     *
     * @param childProject Project
     * @param project Integer
     * @param title String
     * @param childID String
     * @return Statistics
     * @throws SQLException;
     */
    public Statistics createStat(Project childProject, Integer project, String title, int childID) throws SQLException {
        ObservableList<String> collaboratorsNames = collaboratorsToString(project);
        ObservableList<String> tasksDescriptions = tasksToString(project);
        String date = dateToString(childProject.getDate());
        String collaboratorsList = listToString(collaboratorsNames);
        String tasksList = listToString(tasksDescriptions);
        return (new Statistics(childID,title,collaboratorsList,tasksList,date));

    }

    /**
     * Transforms the tree table view into strings and writes it down in a file.
     *
     * @param fileName String
     * @param root     TreeItem<Statistics>
     */
    public void exportStatsAsJson(String fileName, String path, TreeItem<Statistics> root) {
        try {
            Gson gson = new Gson();
            String finalString = "{\n";

            for (int i = 0; i < root.getChildren().size(); i++) {
                Statistics child = root.getChildren().get(i).getValue();
                String treeBranchString = "";
                String title = child.getTitle();
                int id = project_db.getProjectID(title);
                treeBranchString = statToJsonString(id, gson, treeBranchString, root.getChildren().get(i));
                String infoStat = gson.toJson(child);
                String gotChild = "{" + infoStat.replaceAll("(^\\{|}$)", "");
                treeBranchString = ("'" + child.getTitle() + "'" + " :" + gotChild + "," + treeBranchString + "\n");
                finalString += treeBranchString;
            }
            write(finalString + "}", fileName, path);
        }catch(SQLException e ){
            new AlertWindow("Error", "An error has occurred during the exportation. Couldn't find some informations in the database: " + e).errorWindow();
        }
    }

    /**
     * Transforms a stat into a json string with his children recursively.
     *
     * @param id Integer
     * @param gson Gson
     * @param treeBranchString String
     * @param root TreeItem<Statistics>
     * @return String
     */
    public String statToJsonString(Integer id,Gson gson,String treeBranchString,TreeItem<Statistics> root) throws SQLException {
        List<Integer> projectsID = project_db.getSubProjects(id);
        for (int k = 0; k < projectsID.size(); k++) {
            Statistics stat = root.getChildren().get(k).getValue();
            if (project_db.getSubProjects(projectsID.get(k)).size() == 0) {
                //Has no children so we can let the brackets closed --> 'name':{info}.
                String infoStat = gson.toJson(root.getChildren().get(k).getValue());
                treeBranchString = (treeBranchString + "'" + stat.getTitle() + "'" + " :" + infoStat + ",");
            } else {
                //Has a child so we let it open -->'Name':{info,... and check the child's children.
                String infoStat = gson.toJson(stat);
                String gotChild = "{" + infoStat.replaceAll("(^\\{|}$)", "");
                treeBranchString += "'" + stat.getTitle() + "'" + " :" + gotChild + ",";
                treeBranchString = statToJsonString(project_db.getSubProjects(id).get(k), gson, treeBranchString, root.getChildren().get(k));
            }
        }
        treeBranchString += "},";
        return treeBranchString;
    }

    /**
     * Writes informations in a file for a json format.
     *
     * @param chosenString String
     * @param fileName     String
     * @param path         String
     */
    public void write(String chosenString, final String fileName,String path) {
        try {
            FileWriter fw = new FileWriter(path + fileName, true);
            fw.write(chosenString + "\n");
            fw.close();
            statsView.setMsg("The exportation succeeded.");
        } catch (IOException e) {
            statsView.setMsg("The exportation failed.");
        }
    }

    /**
     * Exports the statistics in CSV file.
     *
     * @param fileName String : name given to the file
     * @param path     String : path given for the destination of the file exported
     * @param root     TreeItem<Statistics> : root of the TreeTableView
     */
    public void exportStatsAsCSV(final String fileName, String path, TreeItem<Statistics> root) {
        try {
            PrintWriter csv = new PrintWriter(path + fileName);
            // Name of columns
            String content = "ID" + "," + "Title" + "," + "Collaborators" + "," + "Tasks" + "," + "EstimatedDate" + "," + "Parent ID" + "\r\n";
            for (int i = 0; i < root.getChildren().size(); i++) {
                String mainProjectTitle = root.getChildren().get(i).getValue().getTitle();
                int mainProjectID = project_db.getProjectID(mainProjectTitle);
                content = statsToCSVString(mainProjectID, root.getChildren().get(i).getValue(), content, root.getChildren().get(i));
            }
            csv.write(content);
            csv.close();
            statsView.setMsg("The exportation succeeded.");
        }catch(FileNotFoundException e){
            new AlertWindow("Error", "Couldn't find or access to this file.").errorWindow();
        }catch (SQLException e) {
            statsView.setMsg("The exportation failed.");
        }
    }

    /**
     * Returns CSV format.
     *
     * @param currentStatID Integer : ID of the current object statistics
     * @param currentStat   Statistics : the information to add in the content
     * @param content       String : CSV format
     * @param root          TreeItem<Statistics> : root of the TreeTableView
     * @return the content of the file with CSV format
     * @throws SQLException throws SQL exceptions
     */
    public String statsToCSVString(int currentStatID, Statistics currentStat, String content, TreeItem<Statistics> root) throws SQLException {
        if (project_db.getSubProjects(currentStatID).size() == 0) {
            content += currentStatID + "," + currentStat.getTitle() + "," + '"' + currentStat.getCollaborators() + '"' + "," + '"' + currentStat.getTasks() + '"' + "," + currentStat.getEstimatedDate() + "," + project_db.getProject(currentStatID).getParent_id() + "\r\n";
        } else {
            content += currentStatID + "," + currentStat.getTitle() + "," + '"' + currentStat.getCollaborators() + '"' + "," + '"' + currentStat.getTasks() + '"' + "," + currentStat.getEstimatedDate() + "," + project_db.getProject(currentStatID).getParent_id() + "\r\n";
            for (int k = 0; k < project_db.getSubProjects(currentStatID).size(); k++) {
                content = statsToCSVString(project_db.getSubProjects(currentStatID).get(k), root.getChildren().get(k).getValue(), content, root.getChildren().get(k));
            }
        }
        return content;
    }
}