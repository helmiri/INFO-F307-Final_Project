package be.ac.ulb.infof307.g06.controllers;
import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.database.UserDB;
import be.ac.ulb.infof307.g06.models.Global;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Statistics;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.views.StatsViewController;
import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import java.io.*;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsController {
    private StatsViewController statsView;

    /**
     * Initializes the main view and the tree. Sets values.
     *
     * @param view StatsViewController
     * @param root TreeItem<Statistics>
     */
    public void init(StatsViewController view,TreeItem<Statistics> root){
        statsView = view;
        statsView.initTree();
        try {
            List<Integer> projectsArray = getProjects();
            setStats(projectsArray,root);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Returns a projects list of the actual user.
     *
     * @return List<Integer>
     * @throws SQLException;
     */
    public List<Integer> getProjects() throws SQLException { return ProjectDB.getUserProjects(Global.userID);}

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
        List<Integer> collaborators = ProjectDB.getCollaborators(project);
        ObservableList<String> collaboratorsNames = FXCollections.observableArrayList();
        for(Integer collaborator : collaborators){
            collaboratorsNames.add(UserDB.getUserInfo(collaborator).get("uName"));
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
        List<Task> tasks = ProjectDB.getTasks(project);
        ObservableList<String> tasksDescriptions = FXCollections.observableArrayList();
        for(Task task : tasks){
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
     * @throws SQLException;
     */
    public void setStats(List<Integer> projects,TreeItem<Statistics> root) throws SQLException {
        Map<Integer, TreeItem<Statistics>> statsTreeMap = new HashMap<>();
        for(Integer project : projects){
            Project childProject= ProjectDB.getProject(project);
            int parentID= childProject.getParent_id();
            String title= childProject.getTitle();
            int childID= ProjectDB.getProjectID(title);
            Statistics stat= createStat(childProject,project,title,childID);
            TreeItem<Statistics> statsItem = new TreeItem<>(stat);
            statsTreeMap.put(childID, statsItem);
            if (parentID== 0){ statsView.addChild(root,statsItem); }
            else { statsView.addChild(statsTreeMap.get(parentID),statsItem); }
        }
        statsView.expandRoot(root);
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
        String tasksList =  listToString(tasksDescriptions);
        return (new Statistics(childID,title,collaboratorsList,tasksList,date));
    }
}
