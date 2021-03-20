package be.ac.ulb.infof307.g06.controllers;
import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.Global;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.views.ProjectViews.ProjectInputViewController;
import be.ac.ulb.infof307.g06.views.ProjectViews.ProjectsViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectController implements Initializable {
    private ProjectsViewController view= new ProjectsViewController();
    private TreeItem<Project> root = new TreeItem<Project>();

    public void init(ProjectsViewController view, TreeItem<Project> root) {
        Global.projectsView = view;
        Global.root = root;
        view.initTree();
        try {
            ProjectDB.createTag("tag1",0);
            ProjectDB.createTag("tag2",0);
            ProjectDB.createTag("tag3",0);
            //initComboBox();
            view.clearProjects();
            Global.TreeMap.clear();

            List<Integer> projectsArray = ProjectDB.getUserProjects(Global.userID);
            getProjects(projectsArray);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
                view.addChild(root, child);
            } else {
                view.addChild(Global.TreeMap.get(parentID), child);
            }
        }
        Global.projectsView.refresh();

    }

    public void addProject() throws SQLException{
        //TODO: add conditions to projects creation
        int parentID=0;
        String nameProject = addView.getNameProject();
        String descriptionProject = addView.getDescriptionProject();
        LocalDate dateProject = addView.getDateProject();
        String parentProject = addView.getParentProjectName();
        if(nameProject == "" ) {
            addView.setError("Cannot add a project with an empty title.");}
        //else if(!validateName(nameProject.getText())){ErrorText.setText("Project's name is invalid (must contain 1 to 20 characters and at least one letter");}
        else if (ProjectDB.getProjectID(nameProject) != 0){
            addView.setError("A project with the same title already exists.");}
        else if(dateProject == null){
            addView.setError("Cannot create a project without a date.");}
        else if (parentProject == "" || ProjectDB.getProjectID(parentProject)!=0){
            if(parentProject != ""){ parentID= ProjectDB.getProjectID(parentProject);}

            int newProjectID = ProjectDB.createProject(nameProject,descriptionProject,dateProject.toEpochDay(),parentID);

            //tags
            ObservableList<String> tags = addView.getSelectedTags();//
            for (int i=0; i<tags.size(); i++){
                ProjectDB.addTag(ProjectDB.getTagID(tags.get(i)), newProjectID);
            }

            ProjectDB.addCollaborator(newProjectID, Global.userID);

            TreeItem<Project> child = new TreeItem<Project>(ProjectDB.getProject(newProjectID));
            Global.TreeMap.put(newProjectID, child);
            addView.setError("");

            if (parentID == 0) {
                view.addChild(root,child);
            } else {
                view.addChild(Global.TreeMap.get(parentID), child);
            }
        }
        Main.closeStage();
    }

    public void editTask(String description, String newDescription, Task task) throws SQLException {
        if (newDescription==""){deleteTask(task);}
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
}
