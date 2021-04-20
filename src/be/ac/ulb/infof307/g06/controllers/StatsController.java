package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.statisticsViews.StatsViewController;
import com.google.gson.Gson;
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
     * Shows the main scene of statistics (overall statistics view).
     */
    @Override
    public void show() {
        String fxmlName = "OverallStatsView.fxml";
        toShow(fxmlName);
        statsView.initOverallStats();
    }

    /**
     * Shows the individual statistics view.
     */
    @Override
    public void showIndividualStats() {
        String fxmlName = "IndividualStatsView.fxml";
        toShow(fxmlName);
        statsView.initTreeTableView();
    }

    /**
     * Sets the loader to show the appropriate statistics scene.
     *
     * @param fxmlFilename String
     */
    private void toShow(String fxmlFilename) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(StatsViewController.class.getResource(fxmlFilename));
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            new AlertWindow("Error", "An error has occured. Try to restart the application." +e).errorWindow();
        }
        statsView = loader.getController();
        statsView.setListener(this);
        stage.setScene(scene);
        stage.sizeToScene();
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
            new AlertWindow("Error", "An error has occurred with the database: " + e).errorWindow();
            throw new DatabaseException(e);
        }
    }

    @Override
    public Project getProjectsFromID(int id) throws DatabaseException{
        try {
            return project_db.getProject(id);
        }
        catch(SQLException e) {
            new AlertWindow("Error", "An error has occurred with the database: " + e).errorWindow();
            throw new DatabaseException(e);
        }
    }

    /**
     * Changes a "Long" date to a string.
     *
     * @param date Long
     * @return String
     */
    @Override
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
            new AlertWindow("Error", "An error has occurred with the database: " + e).errorWindow();
            throw new DatabaseException(e);
        }
    }

    /**
     * Counts the global statistics.
     *
     * @return List<Integer>
     */
    @Override
    public List<Integer> countOverallStats() {
        List<Integer> res = new ArrayList<>();
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
                res.add(projects.size());
                res.add(tasks);
                res.add(collaborators.size());
            } else {
                emptyStats(res);
            }
        }catch(SQLException e){
            new AlertWindow("Error", "An error has occurred with the database while trying to load counts: "+e).errorWindow();
            emptyStats(res);
            return res;
        }
        return res;
    }

    /**
     * Counts the statistics of a specific project.
     *
     * @param selectedProject Project
     * @return List<Integer>
     */
    @Override
    public List<Integer> countIndividualProjectStats(Project selectedProject){
        List<Integer> res = new ArrayList<>();
        try{
            int sub = 0;
            if(selectedProject != null) {
                int projectId = selectedProject.getId();
                res.add(countSubProject(projectId, sub));
                res.add(project_db.countTasks(projectId));
                res.add(project_db.countCollaborators(projectId));
            }
            return res;
        }
        catch (SQLException e){
            new AlertWindow("Error", "An error has occurred with the database while trying to load counts: "+e).errorWindow();
            emptyStats(res);
            return res;
        }
    }

    /**
     * Counts the tasks of a project.
     *
     * @param project_id int
     * @return Integer
     * @throws DatabaseException e
     */
    @Override
    public Integer countTasksOfAProject(int project_id) throws DatabaseException {
        try{
            return project_db.countTasks(project_id);
        }catch(SQLException e){
            new AlertWindow("Error", "An error has occurred with the database while trying to load counts: "+e).errorWindow();
            throw new DatabaseException(e);
        }
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
                String informationRelatedToStats = generateJSONFormat(child);
                String gotChild = "{" + informationRelatedToStats.replaceAll("(^\\{|}$)", "");
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
            Project currentProject = root.getChildren().get(k).getValue();

            String informationRelatedToStats  = generateJSONFormat(currentProject);
            if (project_db.getSubProjects(projectsID.get(k)).size() == 0) {
                //Has no children so we can let the brackets closed --> 'name':{info}.
                treeBranchString = (treeBranchString + "'" + currentProject.getTitle() + "'" + " :" + informationRelatedToStats  + ",");
            } else {
                //Has a child so we let it open -->'Name':{info,... and check the child's children.
                String gotChild = "{" + informationRelatedToStats .replaceAll("(^\\{|}$)", "");
                treeBranchString += "'" + currentProject.getTitle() + "'" + " :" + gotChild + ",";
                treeBranchString = statToJsonString(project_db.getSubProjects(id).get(k), gson, treeBranchString, root.getChildren().get(k));
            }
        }
        treeBranchString += "},";
        return treeBranchString;
    }

    /**
     * @param currentProject
     * @return
     */
    private String generateJSONFormat(Project currentProject) {
        List<Integer> counts = countIndividualProjectStats(currentProject);
        String startDate     = dateToString(currentProject.getStartDate()), endDate = dateToString(currentProject.getEndDate());
        return "{"+"'Collaborators':"+ counts.get(2) +
                ",'Tasks':"+counts.get(1)+
                ",'Sub projects':"+counts.get(0) +
                ",'Start date':"+ startDate +
                ",'Estimated date:"+endDate+"}";
    }

    /**
     * Exports the statistics in CSV file.
     *
     * @param fileName String : name given to the file
     * @param path     String : path given for the destination of the file exported
     * @param root     TreeItem<Statistics> : root of the TreeTableView
     */
    @Override
    public void exportStatsAsCSV(String fileName, String path, TreeItem<Project> root) {
        try {
            PrintWriter csv = new PrintWriter(path + fileName);
            // Name of columns
            String content = "ID" + "," + "Title" + "," + "Collaborators" + "," + "Tasks" + "," + "Sub projects" + "," + "Parent ID" + "," + "Start date" + "," + "Estimated date" + "\r\n";
            for (int i = 0; i < root.getChildren().size(); i++) {
                String mainProjectTitle = root.getChildren().get(i).getValue().getTitle();
                int mainProjectID = project_db.getProjectID(mainProjectTitle);
                content = statsToCSVString(mainProjectID, root.getChildren().get(i).getValue(), content, root.getChildren().get(i));
            }
            csv.write(content);
            csv.close();
            new AlertWindow("Success", "The export succeeded !").informationWindow();
        }catch(FileNotFoundException e){
            new AlertWindow("Error", "Couldn't find or access to this file: "+e).errorWindow();
        }catch (SQLException e) {
            new AlertWindow("Error", "An error has occurred with the database while trying to export stats: "+e).errorWindow();
        }
    }

    /**
     * Returns CSV format.
     *
     * @param currentProjectID Integer : ID of the current object statistics
     * @param currentProject   Statistics : the information to add in the content
     * @param content          String : CSV format
     * @param root              TreeItem<Statistics> : root of the TreeTableView
     * @return the content of the file with CSV format
     * @throws SQLException throws SQL exceptions
     */
    public String statsToCSVString(int currentProjectID, Project currentProject, String content, TreeItem<Project> root) throws SQLException {
        List<Integer> counter = countIndividualProjectStats(currentProject);
        int numberOfCollaborators = counter.get(2), numberOfTasks = counter.get(1), numberOfSubProjects = counter.get(0);
        String startDate = dateToString(currentProject.getStartDate()), endDate = dateToString(currentProject.getEndDate());
        if (project_db.getSubProjects(currentProjectID).size() == 0) {
            content += currentProjectID + "," + currentProject.getTitle() + "," + '"' + numberOfCollaborators+ '"' + "," + '"' + numberOfTasks+ '"' + "," + numberOfSubProjects + "," + project_db.getProject(currentProjectID).getParent_id() + "," + startDate+"," +  endDate + "\r\n";
        } else {
            content += currentProjectID + "," + currentProject.getTitle() + "," + '"' + numberOfCollaborators+ '"' + "," + '"' + numberOfTasks + '"' + "," + numberOfSubProjects + "," + project_db.getProject(currentProjectID).getParent_id() + "," + startDate+"," +  endDate + "\r\n";
            for (int k = 0; k < project_db.getSubProjects(currentProjectID).size(); k++) {
                content = statsToCSVString(project_db.getSubProjects(currentProjectID).get(k), root.getChildren().get(k).getValue(), content, root.getChildren().get(k));
            }
        }
        return content;
    }

    @Override
    public void exportAsCSVOverallView(String fileName, String path){
        try {
            PrintWriter csv = new PrintWriter(path + fileName);
            // Name of columns
            String content = "User"+","+"Collaborators" + "," + "Tasks" + "," + "projects" + ","+"\r\n";
            List<Integer> counts = countOverallStats();
            content += user_db.getCurrentUser().getUserName()+","+counts.get(2) +","+ counts.get(1)+","+counts.get(0);
            csv.write(content);
            csv.close();
            new AlertWindow("Success", "The export succeeded !").informationWindow();
        }catch(FileNotFoundException e){
            new AlertWindow("Error", "Couldn't find or access to this file.").errorWindow();
        }
    }

    @Override
    public void exportAsJSONOverallView(String fileName, String path){
        List<Integer> counts = countOverallStats();
        String json= "{\n"+user_db.getCurrentUser().getUserName()+":{"+"'Collaborators':"+ counts.get(2) + ",'Tasks':"+counts.get(1)+",'Projects':"+counts.get(0)+"}\n}";
        write(json, fileName, path);
    }

    /**
     * Writes informations in a file for a json format.
     *
     * @param chosenString String
     * @param fileName     String
     * @param path         String
     */
    public void write(String chosenString, String fileName,String path) {
        try {
            FileWriter fw = new FileWriter(path + fileName, false);
            fw.write(chosenString + "\n");
            fw.close();
            new AlertWindow("Success", "The exportation succeeded !").informationWindow();
        } catch (IOException e) {
            new AlertWindow("Error", "Couldn't write in this file (" +path + fileName+") : "+ e).errorWindow();
        }
    }

    /**
     * ??
     *
     * @param counts
     */
    public void emptyStats(List<Integer> counts){
        for(int k=0;k<3;k++)
            counts.add(0);
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
    public void onBackButtonClicked() {
        super.back();
    }

}