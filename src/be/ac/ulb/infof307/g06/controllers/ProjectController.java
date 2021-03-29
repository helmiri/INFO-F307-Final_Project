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
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import java.io.*;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javafx.stage.Modality;
import org.rauschig.jarchivelib.*;


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
        } catch (SQLException e) {
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "Error in window initialization: \n" + e);
        }
    }

    /**
     * Sets the loader to show the Project scene.
     */
    public static void show(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ProjectsViewController.class.getResource("ProjectsViewV2.fxml"));
        MainController.load(loader, 940, 1515);
    }

    /**
     * Sets the loader to show the stage to add a project.
     */
    public static void showAddProjectStage() {
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(ProjectsViewController.class.getResource("AddProjectView.fxml"));
        MainController.showStage("Add project", 541, 473, Modality.APPLICATION_MODAL, loader );
    }

    /**
     * Sets the loader to show the stage to edit a project.
     */
    public static void showEditProjectStage() {
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(ProjectsViewController.class.getResource("EditProjectView.fxml"));
        MainController.showStage("Edit Project", 541, 473, Modality.APPLICATION_MODAL, loader );
    }

    /**
     * Sets the loader to show the stage to edit a task.
     */
    public static void showEditTaskStage() {
        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(ProjectsViewController.class.getResource("TaskEditView.fxml"));
        MainController.showStage("Edit Task", 435, 256, Modality.APPLICATION_MODAL, loader );
    }

    /**
     * gets a project information and displays it
     *
     * @param view ProjectsViewController
     * @param selectedProject TreeItem<Project>
     */
    public void getProjectInfo(ProjectsViewController view,  TreeItem<Project> selectedProject){
        try{
            String description = selectedProject.getValue().getDescription();
            String title = selectedProject.getValue().getTitle();
            Long date = selectedProject.getValue().getDate();
            int id = selectedProject.getValue().getId();
            List<Tag> tags = ProjectDB.getTags(id);
            ObservableList<String> tagsName = FXCollections.observableArrayList();
            for (Tag tag : tags) { tagsName.add(tag.getDescription()); System.out.println("Add tag " + tag.getDescription()); }
            view.displayProject(title, description, date, tagsName);
        }catch(NullPointerException | SQLException e){
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "Error in fetching project data: \n" + e);
        }
    }

    /**
     *
     *
     * @param view ProjectsViewController
     */
    public void initCollaborators (ProjectsViewController view){
        try {
            ObservableList<String> names = FXCollections.observableArrayList();
            List<Integer> collaborators = null;
            collaborators = ProjectDB.getCollaborators(view.getSelectedProject().getValue().getId());
            for (Integer collaborator : collaborators) {names.add((UserDB.getUserInfo(collaborator).get("uName")));}
            view.insertCollaborator(names);
        } catch (SQLException e){
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "Error initializing collaborators: \n" + e);
        }
    }

    /**
     *
     * @param task Task
     * @param view ProjectsViewController
     */
    public void initTaskCollaborators (ProjectsViewController view, Task task){
        try {
            if (task != null) {
                ObservableList<String> names = FXCollections.observableArrayList();
                List<Integer> collaborators = ProjectDB.getTaskCollaborator(task.getId());
                for (Integer collaborator : collaborators) {
                    names.add((UserDB.getUserInfo(collaborator).get("uName")));
                }
                view.insertTaskCollaborators(names);
            }
        }catch (SQLException e){
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "Error initializing task collaborators: \n" + e);
        }
    }

    /**
     *
     *
     * @param collaborators ObservableList<String>
     * @param selectedTask Task
     * @param projectId int
     */
    public void assignCollaborators(ObservableList<String> collaborators, Task selectedTask, int projectId){
        try {
            for (String collaborator : collaborators) {
                ProjectDB.addTaskCollaborator(selectedTask.getId(), Integer.parseInt(UserDB.getUserInfo(collaborator).get("id")));
            }
        }catch(SQLException e){
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "Error in assigning collaborator to task: \n" + e);
        }
    }

    public void deleteTaskCollaborator(String collaborator, Task selectedTask){
        try{
            ProjectDB.deleteTaskCollaborator(selectedTask.getId(), Integer.parseInt(UserDB.getUserInfo(collaborator).get("id")));
        }catch(SQLException e){
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "Error in deleting task collaborator: \n" + e);
        }
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
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "Error in initializing input window: \n" + e);
        }
    }

    /**
     * returns if a user is in a task's collaborators
     *
     * @param taskId int
     * @param user String
     * @return boolean
     */
    public boolean isUserInTask(int taskId, String user){
        try{
            return ProjectDB.getTaskCollaborator(taskId).contains(Integer.parseInt(UserDB.getUserInfo(user).get("id")));
        }catch(SQLException e){
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "Error in fetching database: \n" + e);
        }
        return false;
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
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "Error in exporting project: \n" + e);
        }
    }

    /**
     * Initializes the map and displays projects on the tree table view.
     *
     * @param projects List<Integer>;

     */
    public void getProjects(List<Integer> projects){
        try {
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
        }catch(SQLException e){
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "Error in fetching projects: \n" + e);
        }
    }

    /**
     * deletes a project from the databse
     *
     * @param name String
     */
    public void deleteProject(String name){
        try{
            int projectID = ProjectDB.getProjectID(name);
            ProjectDB.deleteProject(projectID);
            init(Global.projectsView, Global.root);
        }catch(SQLException e){
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "Error in deleting project: \n" + e);
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
            MainController.closeStage();
        }catch(SQLException e){
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "Error in adding project: \n" + e);
        }
    }

    /**
     * Changes a project's informations with the new ones.
     *
     * @param inputView ProjectInputViewController
     */
    public void editProject(ProjectInputViewController inputView){
        try{
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
        }catch(SQLException e){
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "Error in editing project: \n" + e);
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
        try{
            List<Task> tasks = ProjectDB.getTasks(task.getProjectID());
            List<String> taskNames = new ArrayList<>();
            for (Task task2 : tasks) {
                taskNames.add(task2.getDescription());
            }
            if (taskNames.contains(newDescription)){
                MainController.alertWindow(Alert.AlertType.INFORMATION,"Alert","Task already exists");return;}
            if (newDescription.equals("")){deleteTask(task);}
            else if (validateDescription(newDescription)) { ProjectDB.editTask(description,newDescription,task.getProjectID());}
            Global.projectsView.displayTask();
        }catch(SQLException e){
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "Error in editing task: \n" + e);
        }
    }

    /**
     * Adds a task to the parent project, adds it to the database.
     *
     * @param taskDescription String
     * @param taskParent String
     */
    public void addTask(String taskDescription, String taskParent){
        if (taskDescription.isBlank()){return;}
        try{
            List<Task> tasks = ProjectDB.getTasks(ProjectDB.getProjectID(taskParent));
            List<String> taskNames = new ArrayList<>();
            for (Task task : tasks) {
                taskNames.add(task.getDescription());
            }
            if (taskNames.contains(taskDescription)){MainController.alertWindow(Alert.AlertType.INFORMATION,"Alert","Task already exists");return;}
            if (!taskParent.equals("") || ProjectDB.getProjectID(taskParent) != 0) {
                int projectID = ProjectDB.getProjectID(taskParent);
                ProjectDB.createTask(taskDescription, projectID);
            }
        }catch(SQLException e){
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "Error in adding task: \n" + e);
        }
    }

    /**
     * Deletes a task from the database and the table.
     *
     * @param task Task
     */
    public void deleteTask(Task task){
        try{
        ProjectDB.deleteTask(task.getDescription(),task.getProjectID());
        }catch(SQLException e){
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "Error in deleting task: \n" + e);
        }
    }

    /**
     * Displays it in the table view.
     *
     * @param selectedProject TreeItem<Project>
     */
    public ObservableList<Task> getTasks(TreeItem<Project> selectedProject){
        try{
            if( selectedProject!=null && selectedProject.getValue() !=null) {
                String projectTitle = selectedProject.getValue().getTitle();
                int projectID = ProjectDB.getProjectID(projectTitle);
                List<Task> taskList = ProjectDB.getTasks(projectID);
                return FXCollections.observableArrayList(taskList);
            }
        }catch(SQLException e){
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "Error in fetching tasks: \n" + e);
        }
        return FXCollections.observableArrayList();
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
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date * 86400000L);
    }

    public static Project getProject(String currentProject){
        Project project = new Project();
        try{
            project = ProjectDB.getProject( ProjectDB.getProjectID(currentProject));
        }catch(SQLException e){
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "Error fetching project: \n" + e);
        }
        return project;

    }

    /**
     * Returns collaborators' id of a project.
     *
     * @param project TreeItem<Project>
     * @return ObservableList<String>
     */
    public ObservableList<String> getCollaborators(TreeItem<Project> project){
        List<String> collaboratorsList = new ArrayList<>();
        try{
            List<Integer> collaborators_id = ProjectDB.getCollaborators(project.getValue().getId());
            for(Integer integer : collaborators_id) {
                collaboratorsList.add(UserDB.getUserInfo(integer).get("uName"));
            }
        }catch(SQLException e){
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "Error in fetching collaborators: \n" + e);
        }
        return FXCollections.observableArrayList(collaboratorsList);
    }

    /**
     * Deletes a collaborator linked to a project from the database.
     *
     * @param username String
     * @param project int
     */
    public void deleteCollaborator(String username,int project){
        try{
        ProjectDB.deleteCollaborator(project, Integer.parseInt(UserDB.getUserInfo(username).get("id")));
        }catch(SQLException e){
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "Error in deleting collaborator: \n" + e);
        }
    }

    /**
     * Adds a collaborator to a project and in the database.
     *
     * @param username String
     * @param project int
     * @return Boolean
     */
    public Boolean addCollaborator(String username, int project){
        try{
            if (!UserDB.userExists(username)){return false;}
            int receiverID = Integer.parseInt(UserDB.getUserInfo(username).get("id"));
            if (ProjectDB.getCollaborators(project).contains(receiverID)){return true;}
            UserDB.sendInvitation(project, Global.userID, receiverID);
            return true;
        }catch(SQLException e){
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "Error in adding collaborator: \n" + e);
        }
        return false;
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
     * @param project Project
     * @param fw FileWriter
     */
    public void exportProject1(Project project, FileWriter fw) throws IOException {
        try {
            final int ID = project.getId();
            saveProject(project, ProjectDB.getTasks(ID), ProjectDB.getTags(ID), fw);
            for (Integer subProject : ProjectDB.getSubProjects(ID)) {
                fw.write(",\n");
                exportProject1(ProjectDB.getProject(subProject), fw);
            }
        } catch (Exception ignored) {
            fw.close();
        }
    }

    /**
     *
     * @param project Project
     * @param archivePath String
     * @param jsonFile String
     * @return boolean
     */
    public boolean exportProject2(Project project, String archivePath, String jsonFile) {
        try {
            final int ID = project.getId();
            FileWriter fw = new FileWriter(jsonFile, true);
            fw.write("[\n");
            exportProject1(project, fw);
            fw.write("\n]");
            fw.close();
            zip(project.getTitle(), jsonFile, archivePath);
            deleteFile(jsonFile);
            return true;
        } catch (Exception ignored) {return false;}
    }

    /**
     *
     * @param project Project
     * @param tasks List<Task>
     * @param tags List<Tag>
     * @param fw FileWriter
     * @return boolean
     */
    public static boolean saveProject(Project project, List<Task> tasks, List<Tag> tags, FileWriter fw) {
        try {
            //Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Gson gson = new GsonBuilder().create();
            fw.write("[\n");
            gson.toJson(project, fw);
            fw.write(",\n");
            gson.toJson(tasks, fw);
            fw.write(",\n");
            gson.toJson(tags, fw);
            fw.write("\n]");
            return true;
        }
        catch(Exception ignored) {return false;}
    }

    /**
     *
     *
     * @param archivePath String
     * @return boolean
     */
    public boolean importProject(String archivePath) {
        File file = new File(archivePath);
        String parent = file.getAbsoluteFile().getParent();
        unzip(archivePath,parent);
        BufferedReader reader;
        Gson gson=new Gson();
        try {
            reader = new BufferedReader(new FileReader(parent+"/file.json"));
            String line = null;
            int count = 0;
            reader.readLine();
            int idParent = 0;
            int id = 0;
            int idTag = 0;
            Map<Integer, Integer> hm = new HashMap();
            while ((line = reader.readLine()) != null) {
                ++count;
                count %= 6;
                switch (count) {
                    case 1:
                        System.out.println("ouvrante "+line);
                        break;
                    case 2:
                        System.out.println("projet "+line);
                        Project project= gson.fromJson(line.substring(0, line.length()-1), Project.class);
                        if (hm.size()==0) {
                            id = ProjectDB.createProject(project.getTitle(), project.getDescription(), project.getDate(),0);
                            hm.put(project.getParent_id(), 0);
                        }
                        else{
                            id = ProjectDB.createProject(project.getTitle(), project.getDescription(), project.getDate(), hm.get(project.getParent_id()));
                            idParent = hm.get(project.getParent_id());
                        }
                        hm.put(project.getId(), id);
                        ProjectDB.addCollaborator(id, Global.userID);
                        break;
                    case 3:
                        System.out.println("tasks " + line);
                        Type listType = new TypeToken<ArrayList<Task>>(){}.getType();
                        List<Task> tasks = new Gson().fromJson(line.substring(0, line.length()-1), listType);
                        for (Task t : tasks) {
                            ProjectDB.createTask(t.getDescription(), id);
                        }
                        break;
                    case 4:
                        System.out.println("tag " + line);
                        Type listkind = new TypeToken<ArrayList<Tag>>(){}.getType();
                        List<Tag> tag= new Gson().fromJson(line, listkind);
                        for(Tag t : tag){
                            //verifier dans la bdd
                            idTag = ProjectDB.getTagID(t.getDescription());
                            if (idTag == 0) {
                                idTag = ProjectDB.createTag(t.getDescription(), t.getColor());
                            }
                            ProjectDB.addTag(idTag, id);
                        }
                        // update la view
                        TreeItem<Project> child = new TreeItem<>(ProjectDB.getProject(id));
                        Global.TreeMap.put(id, child);
                        if (idParent == 0) { Global.projectsView.addChild(Global.root, child); }
                        else { Global.projectsView.addChild(Global.TreeMap.get(idParent), child); }
                        break;
                    case 5:
                        System.out.println("fermante " + line);
                        break;
                    default:
                        if (line.equals("[")) {count = 1;}
                        else {System.out.println("fin " + line);}
                }
            }
            reader.close();
        } catch (IOException | SQLException e) {e.printStackTrace();}
        deleteFile(parent+"/file.json");
        return true;
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
                if (count > 1 || ! name.substring(name.length()-4).equals(".json")) {
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
        return true;
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

    @FXML
    public static void alertExportImport(String choice, boolean succeed){
        //TODO à mettre dans le main controller
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(choice);
        alert.setHeaderText(null);
        alert.getDialogPane().setMinWidth(900);
        if(succeed){
            alert.setContentText("Congrat,your project is "+ choice +"ed." );
            alert.showAndWait();
        }
        else {
            alert.setContentText("Sorry, failed to "+choice +" your project");
            alert.showAndWait();
        }
    }
}

