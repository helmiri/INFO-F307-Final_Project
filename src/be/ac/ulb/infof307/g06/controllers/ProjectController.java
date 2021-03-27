package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.Main;
import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.database.UserDB;
import be.ac.ulb.infof307.g06.models.Global;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.views.ProjectViews.ProjectInputViewController;
import be.ac.ulb.infof307.g06.views.ProjectViews.ProjectsViewController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import org.rauschig.jarchivelib.*;

import java.io.File;
import java.io.FileWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ProjectController{
    /**
     * Initializes the view, the root, trees and clears the projects table+ the map to "reload" them.
     *
     * @param view ProjectsViewController
     * @param root TreeItem<Project>
     */
    public void init(ProjectsViewController view, TreeItem<Project> root) {
        Global.projectsView = view;
        Global.root = root;
        view.initTree();
        try {
            ProjectDB.createTag("tag1","#4287f5");
            ProjectDB.createTag("tag2","#ffffff");
            ProjectDB.createTag("tag3","#000000");
            view.clearProjects();
            Global.TreeMap.clear();
            List<Integer> projectsArray = ProjectDB.getUserProjects(Global.userID);
            getProjects(projectsArray);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void getProjectInfo(ProjectsViewController view,  TreeItem<Project> selectedProject) throws SQLException{
        try{
        String description = selectedProject.getValue().getDescription();
        String title = selectedProject.getValue().getTitle();
        Long date = selectedProject.getValue().getDate();
        int id = selectedProject.getValue().getId();
        List<Tag> tags = ProjectDB.getTags(id);
        ObservableList<String> tagsName = FXCollections.observableArrayList();
        for (Tag tag : tags) { tagsName.add(tag.getDescription()); System.out.println("Add tag " + tag.getDescription()); }
        view.displayProject(title, description, date, tagsName);
        }catch(NullPointerException throwables){
            // TODO Show error
        }
    }

    /**
     *
     *
     * @param view ProjectsViewController
     * @throws SQLException
     */
    public void initCollaborators (ProjectsViewController view) throws SQLException{
        ObservableList<String> names = FXCollections.observableArrayList();
        List<Integer> collaborators = ProjectDB.getCollaborators(view.getSelectedProject().getValue().getId());
        for (Integer collaborator : collaborators) {
            names.add((UserDB.getUserInfo(collaborator).get("uName")));
        }
        view.insertCollaborator(names);
    }

    /**
     *
     * @param task Task
     * @param view ProjectsViewController
     * @throws SQLException
     */
    public void initTaskCollaborators (ProjectsViewController view, Task task) throws SQLException{
        if (task != null) {
            ObservableList<String> names = FXCollections.observableArrayList();
            List<Integer> collaborators = ProjectDB.getTaskCollaborator(task.getId());
            for (Integer collaborator : collaborators) {
                names.add((UserDB.getUserInfo(collaborator).get("uName")));
            }
            view.insertTaskCollaborators(names);
        }
    }

    /**
     * @param view ProjectsViewController
     * @param task Task
     * @return List<String>
     * @throws SQLException
     */
    public List<String> getCheckedCollaborators (ProjectsViewController view, Task task) throws SQLException{
        ObservableList<String> checked = FXCollections.observableArrayList();
        List<Integer> collaborators = ProjectDB.getTaskCollaborator(task.getId());
        for (Integer collaborator : collaborators) {
            checked.add((UserDB.getUserInfo(collaborator).get("uName")));
        }
        return checked;
    }

    /**
     *
     *
     * @param collaborators ObservableList<String>
     * @param selectedTask Task
     * @param projectId int
     * @throws SQLException
     */
    public void assignCollaborators(ObservableList<String> collaborators, Task selectedTask, int projectId) throws SQLException{
        for (String collaborator : collaborators){
            ProjectDB.addTaskCollaborator(selectedTask.getId(), Integer.parseInt(UserDB.getUserInfo(collaborator).get("id")));
        }
    }

    public void deleteTaskCollaborator(String collaborator, Task selectedTask) throws SQLException{
        ProjectDB.deleteTaskCollaborator(selectedTask.getId(), Integer.parseInt(UserDB.getUserInfo(collaborator).get("id")));
    }

    /**
     * Initializes the tags combobox.
     *
     * @param inputView ProjectInputViewController
     * @throws SQLException
     */
    public void initComboBox(ProjectInputViewController inputView) throws SQLException{
        final ObservableList<String> tags = FXCollections.observableArrayList();
        List<Tag> tagsList = ProjectDB.getAllTags();
        for (Tag tag : tagsList) {
            tags.add(tag.getDescription());
        }
        inputView.addTags(tags);
    }

    /**
     *
     *
     * @param inputView ProjectInputViewController
     * @throws SQLException
     */
    public void initProjectExport(ProjectInputViewController inputView) throws SQLException{
        final ObservableList<String> projectsTitleList = FXCollections.observableArrayList();
        List<Integer> ProjectIDList = ProjectDB.getUserProjects(Global.userID);
        for (Integer projectID : ProjectIDList) {
            projectsTitleList.add(ProjectDB.getProject(projectID).getTitle());
        }
        //inputView.addProjectTitle(projectsTitleList);//i
    }

    /**
     * Initializes the map and displays projects on the tree table view.
     *
     * @param projects List<Integer>;
     * @throws SQLException;
     */
    public void getProjects(List<Integer> projects) throws SQLException{
        Global.projectsView.hideRoot();
        for(Integer project : projects){
            Project childProject= ProjectDB.getProject(project);
            int parentID= childProject.getParent_id();
            String title= childProject.getTitle();
            int childID= ProjectDB.getProjectID(title);
            TreeItem<Project> child = new TreeItem<Project>(childProject);
            Global.TreeMap.put(childID, child);
            if (parentID== 0){ Global.projectsView.addChild(Global.root, child); }
            else { Global.projectsView.addChild(Global.TreeMap.get(parentID), child); }
        }
        Global.projectsView.refresh();

    }

    /**
     * Adds a project to the tree, the map and the database.
     *
     * @param addView ProjectInputViewController
     * @throws SQLException
     */
    public void addProject(ProjectInputViewController addView) throws SQLException{
        //TODO: add conditions to projects creation

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
        Main.closeStage();
    }

    /**
     * Changes a project's informations with the new ones.
     *
     * @param inputView ProjectInputViewController
     * @throws SQLException
     */
    public void editProject(ProjectInputViewController inputView) throws SQLException{
        int projectID = ProjectDB.getProjectID(inputView.getNameProject());
        if (projectID != 0 && projectID != ProjectDB.getProjectID(Global.currentProject)){
            inputView.setError("Cannot edit the project with such a title.");}
        else if (inputView.getNameProject().equals("")){
            inputView.setError("Cannot edit a project with an empty name.");}
        else {
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
    }

    /**
     * Changes the description of a task and displays it
     *
     * @param description String
     * @param newDescription String
     * @param task Task
     * @throws SQLException
     */
    public void editTask(String description, String newDescription, Task task) throws SQLException {
        List<Task> tasks = ProjectDB.getTasks(task.getProjectID());
        List<String> taskNames = new ArrayList<>();
        for (Task task2 : tasks) {
            taskNames.add(task2.getDescription());
        }
        if (taskNames.contains(newDescription)){Global.projectsView.showAlert("Task already exists");return;}
        if (newDescription.equals("")){deleteTask(task);}
        else if (validateDescription(newDescription)) { ProjectDB.editTask(description,newDescription,task.getProjectID());}
        Global.projectsView.displayTask();
        UserDB.updateDiskUsage(ProjectDB.getSizeOnDisk());
    }

    /**
     * Adds a task to the parent project, adds it to the database.
     *
     * @throws Exception;
     * @throws SQLException;
     */
    public void addTask(String taskDescription, String taskParent) throws Exception, SQLException {
        //taskColumn.setCellValueFactory(new PropertyValueFactory<ProjectDB.Task, String>("description"));
        List<Task> tasks = ProjectDB.getTasks(ProjectDB.getProjectID(taskParent));
        List<String> taskNames = new ArrayList<>();
        for (Task task : tasks) {
            taskNames.add(task.getDescription());
        }
        if (taskNames.contains(taskDescription)){Global.projectsView.showAlert("Task already exists");return;}
        if (!taskParent.equals("") || ProjectDB.getProjectID(taskParent) != 0) {
            int projectID = ProjectDB.getProjectID(taskParent);
            ProjectDB.createTask(taskDescription, projectID);
        }
        UserDB.updateDiskUsage(ProjectDB.getSizeOnDisk());
    }

    /**
     * Deletes a task from the database and the table.
     *
     * @param task Task
     * @throws SQLException
     */
    public void deleteTask(Task task) throws SQLException {
        ProjectDB.deleteTask(task.getDescription(), task.getProjectID());
        UserDB.updateDiskUsage(ProjectDB.getSizeOnDisk());
    }

    /**
     * Displays it in the table view
     *
     * @throws SQLException;
     */
    public ObservableList<Task> getTasks(TreeItem<Project> selectedProject) throws SQLException {
        if (selectedProject != null && selectedProject.getValue() != null) {
            String projectTitle = selectedProject.getValue().getTitle();
            int projectID = ProjectDB.getProjectID(projectTitle);
            List<Task> taskList = ProjectDB.getTasks(projectID);
            return FXCollections.observableArrayList(taskList);
        }
        return null;
    }

    /**
     *  Checks if the string has at least one alphabet character and as 1 to 126 characters
     * @param text;
     * @return boolean;
     */
    @FXML
    private boolean validateDescription(String text){
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
    public String dateToString(Long date){
        System.out.println("long date dateToString " + date);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date * 86400000L);
    }

    /**
     * Returns collaborators' id of a project.
     *
     * @param project TreeItem<Project>
     * @return ObservableList<String>
     * @throws SQLException
     */
    public ObservableList<String> getCollaborators(TreeItem<Project> project) throws SQLException{
        List<Integer> collaborators_id = ProjectDB.getCollaborators(project.getValue().getId());
        List<String> collaboratorsList = new ArrayList<>();
        for(Integer integer : collaborators_id) {
            collaboratorsList.add(UserDB.getUserInfo(integer).get("uName"));
        }
        return FXCollections.observableArrayList(collaboratorsList);
    }

    /**
     * Deletes a collaborator linked to a project from the database.
     *
     * @param username String
     * @param project int
     * @throws SQLException
     */
    public void deleteCollaborator(String username,int project) throws SQLException{
        System.out.println(project + " " + Integer.parseInt(UserDB.getUserInfo(username).get("id")));
        ProjectDB.deleteCollaborator(project, Integer.parseInt(UserDB.getUserInfo(username).get("id")));
        UserDB.updateDiskUsage(ProjectDB.getSizeOnDisk());
    }

    /**
     * Adds a collaborator to a project and in the database.
     *
     * @param username String
     * @param project int
     * @return Boolean
     * @throws SQLException
     */
    public Boolean addCollaborator(String username, int project)throws SQLException{
        if (!UserDB.userExists(username)){return false;}
        int receiverID = Integer.parseInt(UserDB.getUserInfo(username).get("id"));
        if (ProjectDB.getCollaborators(project).contains(receiverID)){return true;}
        UserDB.sendInvitation(project, Global.userID, receiverID);
        return true;

    }

    /**
     * Returns the string of a list without brackets.
     *
     * @param list ObservableList<String>
     * @return String
     */
    public String listToString(ObservableList<String> list){ return list.toString().replaceAll("(^\\[|\\]$)",""); }

    /**
     *
     *
     * @param project Project
     * @param archivePath String
     * @param JsonFIle String
     * @param rootID int
     * @return
     */
    public boolean exportProject(Project project, String archivePath, String JsonFIle, int rootID) {
        try {
            final int ID = project.getId();
            saveProject(project, JsonFIle);
            saveTasks(ProjectDB.getTasks(ID), JsonFIle);
            saveTags(ProjectDB.getTags(ID), JsonFIle);
            for (Integer subProject : ProjectDB.getSubProjects(ID)) {
                exportProject(ProjectDB.getProject(subProject), archivePath, JsonFIle, rootID);
            }
            if (ID == rootID) {
                System.out.println("ajaja");
                zip(project.getTitle(), JsonFIle, archivePath);
                deleteFile(JsonFIle);
            }
            return true;
        } catch (Exception ignored) {return false;}
    }

    public boolean exportProject1(Project project, String archivePath, String jsonFile) {
        try {
            final int ID = project.getId();
            FileWriter fw = new FileWriter(jsonFile, true);
            saveProject69(project, ProjectDB.getTasks(ID), ProjectDB.getTags(ID), jsonFile);
            for (Integer subProject : ProjectDB.getSubProjects(ID)) {
                exportProject1(ProjectDB.getProject(subProject), archivePath, jsonFile);
                fw.write(",");
            }
            fw.close();
            return true;
        } catch (Exception ignored) {return false;}
    }

    public boolean exportProject2(Project project, String archivePath, String jsonFile) {
        try {
            final int ID = project.getId();
            FileWriter fw = new FileWriter(jsonFile, true);
            fw.write("[");
            exportProject1(project, archivePath, jsonFile);
            fw.write("]");
            fw.close();
            zip(project.getTitle(), jsonFile, archivePath);
            deleteFile(jsonFile);
            return true;
        } catch (Exception ignored) {return false;}
    }

    /**
     *
     *
     * @param filetxt String
     * @return boolean
     */
    public boolean importProject(String filetxt) {
        boolean a =valideArchive(filetxt);
        System.out.println(a);
        boolean b= unzip(filetxt,"C:\\Users\\hodai\\Download");
        boolean c= isProjectInDb("C:\\Users\\hodai\\Download\\file.txt");
        boolean d= isValidFile("C:\\Users\\hodai\\Download\\file.txt");
        // ajouter dans la db
        return d;
        //verif c'est un zip , si oui on dezipe ta braillette
        //on verif la base de donnée si il y est as déja
    }

    /**
     *
     *
     * @param archivePath String
     * @return boolean
     */
    public static boolean valideArchive(final String archivePath) {
        try {
            Archiver archiver =
                    ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
            File archive = new File(archivePath);
            ArchiveStream stream = archiver.stream(archive);
            ArchiveEntry entry;
            int count = 0;
            while((entry = stream.getNextEntry()) != null) {
                ++count;
                String name = entry.getName();
                if (count > 1 || name.substring(name.length()-4, name.length()-1).equals(".txt")) {
                    return false;
                }
            }
            stream.close();
            return true;
        }
        catch(Exception ignored) {return false;}
    }

    /**
     *
     *
     * @param archiveName String
     * @param source String
     * @param destination String
     * @return boolean
     */
    public static boolean zip(String archiveName, String source, String destination) {
        try {
            File src = new File(source);
            File dest = new File(destination);
            Archiver archiver =
                    ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
            archiver.create(archiveName, dest, src);
            System.out.println("test");
            return true;
        }catch (Exception ignored) {return false;}
    }

    /**
     *
     *
     * @param source String
     * @param destination String
     * @return boolean
     */
    public static boolean unzip(final String source, final String destination){
        try {
            Archiver archiver =
                    ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
            File archive = new File(source);
            File dest = new File(destination);
            System.out.println("WARNINGS are normal");
            archiver.extract(archive, dest); // WARNING OK
            return true;
        }catch (Exception ignored) {return false;}
    }

    /**
     * Saves project.
     *
     * @param project Project
     * @param filePath String
     * @return boolean
     */
    public static boolean saveProject(final Project project, String filePath) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter fw = new FileWriter(filePath, true);
            fw.write("[");
            gson.toJson(project, fw);
            fw.write(",");
            fw.close();
            return true;
        }
        catch(Exception ignored) {return false;}
    }

    public static boolean saveProject69(Project project, List<Task> tasks, List<Tag> tags, String jsonPath) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter fw = new FileWriter(jsonPath, true);
            fw.write("[");
            gson.toJson(project, fw);
            fw.write(",");
            gson.toJson(tasks, fw);
            fw.write(",");
            gson.toJson(tags, fw);
            fw.write("]");
            fw.close();
            return true;
        }
        catch(Exception ignored) {return false;}
    }

    /**
     * Saves task.
     *
     * @param task Project
     * @param filePath String
     * @return boolean
     */
    public static boolean saveTasks(final List<Task> task, final String filePath) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter fw = new FileWriter(filePath, true);
            gson.toJson(task, fw);
            fw.write(",");
            fw.close();
            return true;
        }
        catch(Exception ignored) {return false;}
    }

    /**
     * Saves tag.
     *
     * @param tag Project
     * @param filePath String
     * @return boolean
     */
    public static boolean saveTags(final List<Tag> tag, final String filePath) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter fw = new FileWriter(filePath, true);
            gson.toJson(tag, fw);
            fw.write("]");
            fw.close();
            return true;
        }
        catch(Exception ignored) {return false;}
    }

    /**
     *
     *
     * @param fileTxt String
     * @return boolean
     */
    public static boolean isProjectInDb(String fileTxt){
        try {
            File file = new File(fileTxt);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.substring(0, 7).equals("Project")) {
                    System.out.println("c'est un projet: " + line.substring(8));
                    Gson gson = new Gson();
                    Project project = gson.fromJson(line.substring(8), Project.class);
                    if(ProjectDB.getProjectID(project.getTitle())!=0){
                        reader.close();
                        return true;
                    }
                }
            }
            reader.close();
            return false;
        } catch (Exception e) {return false;}

    }

    /**
     * Checks if the file is valid.
     *
     * @param fileTxt String
     * @return boolean : true if the file is valid or false if is not.
     */
    public static boolean isValidFile( String fileTxt){
        try {
            File file = new File(fileTxt);
            Scanner reader = new Scanner(file);
            Gson gson = new Gson();
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.substring(0, 7).equals("Project")) {
                    System.out.println("c'est un projet: " + line.substring(8));
                    Project project = gson.fromJson(line.substring(8), Project.class);
                }
                else if(line.substring(0, 4).equals("Task")) {
                    System.out.println("c'est un projet: " + line.substring(5));
                    Task task = gson.fromJson(line.substring(5), Task.class);
                }
                else if(line.substring(0, 3).equals("Tag")) {
                    System.out.println("c'est un projet: " + line.substring(4));
                    Tag tag = gson.fromJson(line.substring(4), Tag.class);
                }
                else{
                    reader.close();
                    return false;
                }

            }
            reader.close();
            return true;
        } catch (Exception e) {return false;}

    }

    /**
     *
     *
     * @param fileName String
     * @return boolean
     */
    public static boolean deleteFile(final String fileName) {
        try {
            File myObj = new File(fileName);
            if (myObj.delete()) {return true;}
            else {return false;}
        }
        catch(Exception ignored) {return false;}
    }
}
