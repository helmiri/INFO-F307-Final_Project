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

    /**
     * Transforms the tree table view into strings and writes it down in a file.
     *
     * @param fileName String
     * @param root TreeItem<Statistics>
     * @throws SQLException
     */
    public void exportStatsAsJson(String fileName,String path,TreeItem<Statistics> root) throws SQLException{
        Gson gson = new Gson();
        String finalString="{\n";
        for(int i=0;i<root.getChildren().size();i++) {
            Statistics child = root.getChildren().get(i).getValue();
            String treeBranchString = "";
            String title=child.getTitle();
            int id=ProjectDB.getProjectID(title);
            treeBranchString=statToJsonString(id, gson, treeBranchString, root.getChildren().get(i));
            String infoStat = gson.toJson(child);
            String gotChild = "{" + infoStat.replaceAll("(^\\{|}$)", "");
            treeBranchString=("'" +child.getTitle() + "'" + " :" + gotChild + ","+treeBranchString+"\n");
            finalString+=treeBranchString;
        }
        write(finalString+"}",fileName,path);
    }

    /**
     * Transforms a stat into a json string with his children recursively.
     *
     * @param id Integer
     * @param gson Gson
     * @param treeBranchString String
     * @param root TreeItem<Statistics>
     * @return String
     * @throws SQLException
     */
    public String statToJsonString(Integer id,Gson gson,String treeBranchString,TreeItem<Statistics> root) throws SQLException {
        List<Integer> projectsID = ProjectDB.getSubProjects(id);
        for (int k = 0; k < projectsID.size(); k++) {
            Statistics stat =  root.getChildren().get(k).getValue();
            if (ProjectDB.getSubProjects(projectsID.get(k)).size() == 0) {
                //Has no children so we can let the brackets closed --> 'name':{info}.
                String infoStat = gson.toJson(root.getChildren().get(k).getValue());
                treeBranchString = (treeBranchString + "'" + stat.getTitle() + "'" + " :" + infoStat + ",");
            } else {
                //Has a child so we let it open -->'Name':{info,... and check the child's children.
                String infoStat = gson.toJson(stat);
                String gotChild = "{" + infoStat.replaceAll("(^\\{|}$)", "");
                treeBranchString += "'" + stat.getTitle() + "'" + " :" + gotChild + ",";
                treeBranchString = statToJsonString(ProjectDB.getSubProjects(id).get(k), gson, treeBranchString, root.getChildren().get(k));
            }
        }
        treeBranchString += "},";
        return treeBranchString;
    }

    /**
     * Writes informations in a file.
     *
     * @param chosenString String
     * @param fileName String
     * @param path String
     * @throws IOException
     */
    public static void write(String chosenString, final String fileName,String path) {
        try {
            FileWriter fw = new FileWriter(path + fileName, true);
            fw.write(chosenString + "\n");
            fw.close();
        }
        catch (IOException throwables){
            System.out.println("The exportation failed.");
        }
    }


    /**
     * Exports the statistics in CSV file.
     *
     * @param fileName String : name given to the file
     * @param path String : path given for the destination of the file exported
     * @param root TreeItem<Statistics> : root of the TreeTableView
     */
    public void exportStatsAsCSV(final String fileName, String path, TreeItem<Statistics> root) throws FileNotFoundException {
        PrintWriter csv = new PrintWriter(path+fileName);
        // Name of columns
        String content = "ID" + "," + "Title" + "," + "Collaborators" + "," + "Tasks" + "," + "EstimatedDate"+ "," + "Parent ID" + "\r\n";
        try {
            for(int i=0;i<root.getChildren().size();i++) {
                String mainProjectTitle = root.getChildren().get(i).getValue().getTitle();
                int mainProjectID = ProjectDB.getProjectID(mainProjectTitle);
                content = toCSVFormat(mainProjectID, root.getChildren().get(i).getValue(),content, root.getChildren().get(i));
            }
            csv.write(content);
            csv.close();
            System.out.println("Finished (csv)!");
        }
        catch(Exception ignored) {
            System.out.println("The exportation failed.");
        }
    }

    /**
     * Returns CSV format.
     *
     * @param currentStatID Integer : ID of the current object statistics
     * @param currentStat Statistics : the information to add in the content
     * @param content String : CSV format
     * @param root TreeItem<Statistics> : root of the TreeTableView
     * @return the content of the file with CSV format
     * @throws SQLException throws SQL exceptions
     */
    public static String toCSVFormat(int currentStatID, Statistics currentStat, String content, TreeItem<Statistics> root) throws SQLException {
        if(ProjectDB.getSubProjects(currentStatID).size()==0){
            content += currentStatID+ ","+ currentStat.getTitle() + "," + '"' + currentStat.getCollaborators() + '"' + "," +  '"' + currentStat.getTasks() +  '"' + "," + currentStat.getEstimatedDate()+ "," + ProjectDB.getProject(currentStatID).getParent_id()+ "\r\n";
        }
        else{
            content += currentStatID + ","+ currentStat.getTitle() + "," + '"' + currentStat.getCollaborators()+ '"' + "," +  '"' + currentStat.getTasks()+  '"'+ "," + currentStat.getEstimatedDate()+ "," + ProjectDB.getProject(currentStatID).getParent_id()+ "\r\n";
            for(int k = 0; k<ProjectDB.getSubProjects(currentStatID).size(); k++ ) {
                content = toCSVFormat(ProjectDB.getSubProjects(currentStatID).get(k),root.getChildren().get(k).getValue(), content,root.getChildren().get(k));
            }
        }
        return content;
    }
}