package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Statistics;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.statisticsViews.StatsViewController;
import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

import java.io.*;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsController extends Controller implements StatsViewController.ViewListener  {
    //--------------- ATTRIBUTE ----------------
    private StatsViewController statsView;

    //--------------- METHODS ----------------
    public StatsController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene) {
        super(user_db, project_db, stage, scene);
    }

    /**
     * Sets the loader to show the statistics scene.
     */
    @Override
    public void show() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(StatsViewController.class.getResource("StatsViewV2.fxml"));
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        statsView = loader.getController();
        statsView.setListener(this);
        stage.setScene(scene);
        statsView.initOverallStats();
        //MainController.load(loader, 940, 1515);
    }

    @Override
    public void showStatsProject() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(StatsViewController.class.getResource("StatsProject.fxml"));
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        statsView = loader.getController();
        statsView.setListener(this);
        stage.setScene(scene);
        statsView.initTree();
    }

    /**
     * Returns a projects list of the actual user.
     *
     * @return List<Integer>
     * @throws DatabaseException e
     */
    @Override
    public List<Integer> getProjects() throws DatabaseException {
        try {
            return project_db.getUserProjects(user_db.getCurrentUser().getId());
        } catch(SQLException e) {
            throw new DatabaseException(e);
        }
    }
    @Override
    public Project getProjectsFromID(int id) throws DatabaseException{
        try {
            return project_db.getProject(id);} catch(SQLException e) {
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
    @Override
    public void setStats(List<Integer> projects,TreeItem<Project> root) throws DatabaseException {
        try{
            Map<Integer, TreeItem<Project>> statsTreeMap = new HashMap<>();
            for(Integer project : projects){
                Project childProject = project_db.getProject(project);
                int parentID = childProject.getParent_id();
                String title = childProject.getTitle();
                int childID = project_db.getProjectID(title);
                TreeItem<Project> projectTreeItem = new TreeItem<>(childProject);
                statsTreeMap.put(childID, projectTreeItem);
                if (parentID== 0){ statsView.addChild(root,projectTreeItem); }
                else { statsView.addChild(statsTreeMap.get(parentID),projectTreeItem); }
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
        String date = dateToString(childProject.getStartDate());
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
    @Override
    public void exportStatsAsJson(String fileName, String path, TreeItem<Project> root) {
        try {
            Gson gson = new Gson();
            String finalString = "{\n";

            for (int i = 0; i < root.getChildren().size(); i++) {
                Project child = root.getChildren().get(i).getValue();
                String treeBranchString = "";
                String title = child.getTitle();
                int id = project_db.getProjectID(title);
                treeBranchString = statToJsonString(id, gson, treeBranchString, root.getChildren().get(i));
                List<Integer> counts =countProjectStats(child);
                String startDate = dateToString(child.getStartDate()), endDate = dateToString(child.getEndDate());
                String infoStat = "{"+"'Collaborators':"+ counts.get(2) + ",'Tasks':"+counts.get(1)+",'Sub projects':"+counts.get(0)+ ",'Start date':"+ startDate+ ",'Estimated date:"+endDate+"}";
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
    public String statToJsonString(Integer id,Gson gson,String treeBranchString,TreeItem<Project> root) throws SQLException {
        List<Integer> projectsID = project_db.getSubProjects(id);
        for (int k = 0; k < projectsID.size(); k++) {
            Project stat = root.getChildren().get(k).getValue();
            List<Integer> counts =countProjectStats(stat);
            String startDate = dateToString(stat.getStartDate()), endDate = dateToString(stat.getEndDate());
            String infoStat = "{"+"'Collaborators':"+ counts.get(2) + ",'Tasks':"+counts.get(1)+",'Sub projects':"+counts.get(0)+ ",'Start date':"+ startDate+ ",'Estimated date:"+endDate+"}";
            if (project_db.getSubProjects(projectsID.get(k)).size() == 0) {
                //Has no children so we can let the brackets closed --> 'name':{info}.
                treeBranchString = (treeBranchString + "'" + stat.getTitle() + "'" + " :" + infoStat + ",");
            } else {
                //Has a child so we let it open -->'Name':{info,... and check the child's children.
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
    public void exportStatsAsCSV(String fileName, String path, TreeItem<Project> root) {
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
    public String statsToCSVString(int currentProjectID, Project currentProject, String content, TreeItem<Project> root) throws SQLException {
        List<Integer> counter = countProjectStats(currentProject);
        int numberOfCollaborators = counter.get(2), numberOfTasks = counter.get(1), numberOfSubProjects = counter.get(0);
        String startDate = dateToString(currentProject.getStartDate()), endDate = dateToString(currentProject.getEndDate());
        if (project_db.getSubProjects(currentProjectID).size() == 0) {
            content += currentProjectID + "," + currentProject.getTitle() + "," + '"' + numberOfCollaborators+ '"' + "," + '"' + numberOfTasks+ '"' + "," + numberOfSubProjects + "," + project_db.getProject(currentProjectID).getParent_id() + "," + startDate+"," +  endDate + "\r\n";
        } else {
            content += currentStatID + "," + currentStat.getTitle() + "," + '"' + currentStat.getCollaborators() + '"' + "," + '"' + currentStat.getTasks() + '"' + "," + currentStat.getEstimatedDate() + "," + project_db.getProject(currentStatID).getParent_id() + "\r\n";
            for (int k = 0; k < project_db.getSubProjects(currentStatID).size(); k++) {
                content = statsToCSVString(project_db.getSubProjects(currentStatID).get(k), root.getChildren().get(k).getValue(), content, root.getChildren().get(k));
            }
        }
        return content;
    }

    @Override
    public List<Integer> countOverallStats() throws SQLException {
        List<Integer> projects = project_db.getUserProjects(user_db.getCurrentUser().getId());
        List<Integer> res = new ArrayList<>();
        if(projects.size()!=0) {
            int tasks = 0;
            List<Integer> collaborators = new ArrayList<>();
            for (int i = 0; i < projects.size(); i++) {
                tasks += project_db.countTasks(projects.get(i));
                List<Integer> projectsCollaborators = project_db.getCollaborators(projects.get(i));
                for (Integer collaborator : projectsCollaborators) {
                    if (!collaborators.contains(collaborator)) { collaborators.add(collaborator); }
                }
            }
            res.add(projects.size());
            res.add(tasks);
            res.add(collaborators.size() - 1);
        }
        else{
            for(int k=0;k<3;k++)
                res.add(0);
        }
        return res;
    }

    @Override
    public List<Integer> countProjectStats(Project selectedProject) throws SQLException {
        List<Integer> res = new ArrayList<>();
        int sub = 0;
        if(selectedProject != null) {
            int projectId = selectedProject.getId();
            res.add(countSubProject(projectId, sub));
            res.add(project_db.countTasks(projectId));
            res.add(project_db.countCollaborators(projectId)-1);

        }
        return res;
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

    @Override
    public void exportAsCSVOverallView(String fileName, String path){
        try {
            PrintWriter csv = new PrintWriter(path + fileName);
            // Name of columns
            String content = "User"+","+"Collaborators" + "," + "Tasks" + "," + "projects" + ","+"\r\n";
            csv.write(content);
            List<Integer> counts = countOverallStats();
            content += user_db.getCurrentUser().getUserName()+","+counts.get(2) +","+ counts.get(1)+","+counts.get(0);
            csv.write(content);
            csv.close();
        }catch(FileNotFoundException | SQLException e){
            new AlertWindow("Error", "Couldn't find or access to this file.").errorWindow();
        }
    }
    @Override
    public void exportAsJSONOverallView(String fileName, String path){
        try{
        List<Integer> counts = countOverallStats();
        String json= "{"+user_db.getCurrentUser().getUserName()+":{"+"'Collaborators':"+ counts.get(2) + ",'Tasks':"+counts.get(1)+",'Projects':"+counts.get(0)+"}";
        write(json, fileName, path);
        }catch(SQLException e ){
            new AlertWindow("Error", "An error has occurred during the exportation. Couldn't find some informations in the database: " + e).errorWindow();
        }
    }

    // ------------------------------------- CODE --------------------------------------

    @Override
    public void onBackButtonClicked() {
        stage.hide();
        stage.setScene(prevScene);
        stage.show();
    }

}