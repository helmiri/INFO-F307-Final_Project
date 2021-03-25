package be.ac.ulb.infof307.g06.controllers;
import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.database.UserDB;
import be.ac.ulb.infof307.g06.models.Global;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.views.ProjectViews.ProjectInputViewController;
import be.ac.ulb.infof307.g06.views.ProjectViews.ProjectsViewController;
import com.google.gson.Gson;
import com.sun.source.tree.Tree;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.io.FileWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import be.ac.ulb.infof307.g06.Main;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import java.util.ArrayList;


public class ProjectController{

    public void init(ProjectsViewController view, TreeItem<Project> root) {
        Global.projectsView = view;
        Global.root = root;
        view.initTree();
        try {
            ProjectDB.createTag("tag1",0);
            ProjectDB.createTag("tag2",0);
            ProjectDB.createTag("tag3",0);

            view.clearProjects();
            Global.TreeMap.clear();

            List<Integer> projectsArray = ProjectDB.getUserProjects(Global.userID);
            getProjects(projectsArray);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void initTaskCollaborators (ProjectsViewController view) throws SQLException{
        ObservableList<String> names = FXCollections.observableArrayList();
        List<Integer> collaborators = ProjectDB.getCollaborators(ProjectDB.getProjectID(Global.currentProject)); // TODO
        for (Integer collaborator : collaborators) {
            names.add((UserDB.getUserInfo(collaborator).get("uName")));
        }

        view.insertCollaborator(names);
    }

    public void assignCollaborators(ObservableList<String> collaborators, Task selectedTask) throws SQLException{
        for (String collaborator : collaborators) {
            ProjectDB.addTaskCollaborator(selectedTask.getId(), Integer.parseInt(UserDB.getUserInfo(collaborator).get("id")));
        }
    }

    public void initComboBox(ProjectInputViewController inputView) throws SQLException{

        final ObservableList<String> tags = FXCollections.observableArrayList();
        List<Tag> tagsList = ProjectDB.getAllTags();
        for (Tag tag : tagsList) {
            tags.add(tag.getDescription());
        }
        inputView.addTags(tags);
    }
    public void initProjectExport(ProjectInputViewController inputView) throws SQLException{
        final ObservableList<String> projectsTitleList = FXCollections.observableArrayList();
        List<Integer> ProjectIDList = ProjectDB.getUserProjects(Global.userID);
        for (Integer projectID : ProjectIDList) {
            projectsTitleList.add(ProjectDB.getProject(projectID).getTitle());
        }
        //inputView.addProjectTitle(projectsTitleList);//i
    }

    /**
     * Initializes the map and display projects on the tree table view
     * @param projects List<Integer>;
     * @throws SQLException;
     */
    public void getProjects(List<Integer> projects) throws SQLException{
        Global.projectsView.showRoot();
        for(Integer project : projects){
            Project childProject= ProjectDB.getProject(project);
            int parentID= childProject.getParent_id();

            String title= childProject.getTitle();
            //projectSelection.getItems().add(title);//
            int childID= ProjectDB.getProjectID(title);

            TreeItem<Project> child = new TreeItem<Project>(childProject);
            Global.TreeMap.put(childID, child);

            if (parentID== 0){
                Global.projectsView.addChild(Global.root, child);
            } else {
                Global.projectsView.addChild(Global.TreeMap.get(parentID), child);
            }
        }
        Global.projectsView.refresh();

    }

    public void addProject(ProjectInputViewController addView) throws SQLException{
        //TODO: add conditions to projects creation

        int parentID=0;
        String nameProject = addView.getNameProject();
        String descriptionProject = addView.getDescriptionProject();
        LocalDate dateProject = addView.getDateProject();
        String parentProject = addView.getParentProjectName();
        if(nameProject.equals("")) {
            addView.setError("Cannot add a project with an empty title.");}
        //else if(!validateName(nameProject.getText())){ErrorText.setText("Project's name is invalid (must contain 1 to 20 characters and at least one letter");}
        else if (ProjectDB.getProjectID(nameProject) != 0){
            addView.setError("A project with the same title already exists.");}
        else if(dateProject == null){
            addView.setError("Cannot create a project without a date.");}

        else if (parentProject.equals("") || ProjectDB.getProjectID(parentProject)!=0){
            if(!parentProject.equals("")){ parentID= ProjectDB.getProjectID(parentProject);}
            System.out.println("addProject " + dateProject.toEpochDay());
            int newProjectID = ProjectDB.createProject(nameProject,descriptionProject,dateProject.toEpochDay(),parentID);
            //tags
            ObservableList<String> tags = addView.getSelectedTags();//
            for (String tag : tags) {
                ProjectDB.addTag(ProjectDB.getTagID(tag), newProjectID);
            }

            ProjectDB.addCollaborator(newProjectID, Global.userID);

            TreeItem<Project> child = new TreeItem<Project>(ProjectDB.getProject(newProjectID));
            Global.TreeMap.put(newProjectID, child);
            addView.setError("");

            if (parentID == 0) {
                Global.projectsView.addChild(Global.root,child);
            } else {
                Global.projectsView.addChild(Global.TreeMap.get(parentID), child);
            }
        }
        Main.closeStage();
    }


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
    }


    public void editTask(String description, String newDescription, Task task) throws SQLException {
        if (newDescription.equals("")){deleteTask(task);}
        else if (validateDescription(newDescription)) { ProjectDB.editTask(description,newDescription,task.getProjectID());}
        Global.projectsView.displayTask();
    }

    /**
     * Adds a task to the parent project, adds it to the database
     * @throws Exception;
     * @throws SQLException;
     */
    public void addTask(String taskDescription, String taskParent) throws Exception, SQLException {
        //taskColumn.setCellValueFactory(new PropertyValueFactory<ProjectDB.Task, String>("description"));
        if (!taskParent.equals("") || ProjectDB.getProjectID(taskParent) != 0) {
            int projectID = ProjectDB.getProjectID(taskParent);
            ProjectDB.createTask(taskDescription, projectID);
        }
    }

    public void deleteTask(Task task) throws SQLException{
        ProjectDB.deleteTask(task.getDescription(),task.getProjectID());
    }

    /**
     * Displays it in the table view
     * @throws SQLException;
     */
    public ObservableList<Task> getTasks(TreeItem<Project> selectedProject) throws SQLException {
        if( selectedProject!=null && selectedProject.getValue() !=null) {
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
     *  Checks if the string has at least one alphabet character and as 1 to 20 characters
     * @param text;
     * @return boolean;
     */
    @FXML
    private boolean validateName(String text){
        Pattern p = Pattern.compile("^.*[a-zA-Z0-9]{1,20}$");
        Matcher m = p.matcher(text);
        return m.matches();
    }

    public String dateToString(Long date){
        System.out.println("long date dateToString " + date);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date * 86400000L);
    }

    public ObservableList<String> getCollaborators(TreeItem<Project> project) throws SQLException{
        List<Integer> collaborators_id = ProjectDB.getCollaborators(project.getValue().getId());
        List<String> collaboratorsList = new ArrayList<>();
        for(Integer integer : collaborators_id) {
            collaboratorsList.add(UserDB.getUserInfo(integer).get("uName"));
        }
        return FXCollections.observableArrayList(collaboratorsList);
    }

    public void deleteCollaborator(String username,int project) throws SQLException{
        System.out.println(project + " " + Integer.parseInt(UserDB.getUserInfo(username).get("id")));
        ProjectDB.deleteCollaborator(project, Integer.parseInt(UserDB.getUserInfo(username).get("id")));
    }

    public Boolean addCollaborator(String username, int project)throws SQLException{
        if (!UserDB.userExists(username)){return false;}
        int receiverID = Integer.parseInt(UserDB.getUserInfo(username).get("id"));
        if (ProjectDB.getCollaborators(project).contains(receiverID)){return true;}
        UserDB.sendInvitation(project, Global.userID, receiverID);
        return true;

    }


    public String listToString(ObservableList<String> list){ return list.toString().replaceAll("(^\\[|\\]$)",""); }

    public boolean exportProject(Project project,String path, String filetxt,int id){
            final int ID = project.getId();
            save(project,filetxt);
            try {
                final List<Task> tasks = ProjectDB.getTasks(ID);
                for (Task task : tasks) {
                    save(task, filetxt);
                }
                final List<Tag> tags = ProjectDB.getTags(ID);
                for (Tag tag : tags) {
                    save(tag, filetxt);
                }
                final List<Integer> subProjects = ProjectDB.getSubProjects(id);
                for (Integer sub : subProjects) {
                    exportProject(ProjectDB.getProject(sub),path,filetxt,id);
                }
                if (ID == id) {
                    System.out.println("ajaja");
                    zip(project.getTitle(),filetxt, path);
                }
                return true;
            }
            catch(Exception ignored) {return false;}
        
    }



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

    // title, description, date, parent_id
    public static boolean save(final Project project, final String fileName) {
        try {
            Gson gson = new Gson();
            String projectString = gson.toJson(project);
            FileWriter fw = new FileWriter(fileName, true);
            fw.write("Project " + projectString + "\n");
            fw.close();
            return true;
        }
        catch(Exception ignored) {return false;}
    }

    // title, description, date, parent_id
    public static boolean save(final Task task, final String fileName) {
        try {
            Gson gson = new Gson();
            String taskString = gson.toJson(task);
            FileWriter fw = new FileWriter(fileName, true);
            fw.write("Task " + taskString + "\n");
            fw.close();
            return true;
        }
        catch(Exception ignored) {return false;}
    }

    // title, description, date, parent_id
    public static boolean save(final Tag tag, final String fileName) {
        try {
            Gson gson = new Gson();
            String tagString = gson.toJson(tag);
            FileWriter fw = new FileWriter(fileName, true);
            fw.write("Tag " + tagString + "\n");
            fw.close();
            return true;
        }
        catch(Exception ignored) {return false;}
    }
}
