package be.ac.ulb.infof307.g06.views.ProjectViews;

import be.ac.ulb.infof307.g06.Main;
import be.ac.ulb.infof307.g06.controllers.ProjectController;
import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.database.UserDB;
import be.ac.ulb.infof307.g06.models.Global;
import be.ac.ulb.infof307.g06.database.UserDB;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.Task;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.text.Text;

import org.controlsfx.control.CheckComboBox;
import javafx.util.Callback;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ProjectsViewController implements Initializable {
    //----------ATTRIBUTES---------
    // ---------PROJECTS MENU------
    @FXML
    private TreeTableView<Project> treeProjects;
    @FXML
    private TreeTableColumn<Project, String> treeProjectColumn;

    private TreeItem<Project> root = new TreeItem<Project>();
    @FXML
    private Button addProjectBtn;

    @FXML
    private ListView<String> projectTags;

    @FXML
    private TextArea projectsDescription;

    @FXML
    private Text projectsTitle;

    @FXML
    private Label projectsDate;

    @FXML
    private Button addBtn;
    @FXML
    private Button editBtn;
    // ----------------TASK---------------
    @FXML
    private TableView<Task> taskTable;
    @FXML
    private TableColumn<Task,String> taskColumn;
    @FXML
    private Button addTaskbtn;
    @FXML
    private Button backBtn;
    @FXML
    private TextArea descriptionTask;
    @FXML
    private TextField taskParent;
    @FXML
    private TextField taskName;
    @FXML
    private Button editTaskBtn;


    //---------------TASK COLLABORATORS------------

    @FXML
    private TableView<String> taskCollaboratorTable;
    @FXML
    private TableColumn<String, String> taskCollaboratorColumn;
    @FXML
    private Button assignTaskCollaboratorBtn;
    @FXML
    private CheckComboBox<String> collabComboBox;



    //-------------- PROJECT COLLABORATORS----------

    @FXML
    private TableView<String> collaboratorsTable;
    @FXML
    private TableColumn<String, String> collaboratorsColumn;
    @FXML
    private Button addCollaboratorsBtn;
    @FXML
    private TextArea collaboratorsName;


    //---------Import--Export-----------------
    @FXML
    private Button  exportProjectBtn;
    //@FXML
    //private Button importBtn;




    //---------------EDIT PROJECTS----------

    //----------------CONTROLLER--------------
    private ProjectController controller;
    //---------------METHODES----------------

    /**
     * The main method for button's events
     * @param ;
     * @throws Exception;
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        controller = new ProjectController();
        controller.init(this, root);
    }

    @FXML
    public void showRoot(){
        treeProjects.setShowRoot(false);
    }
    @FXML
    public void refresh(){
        treeProjects.setShowRoot(true);
    }

    /**
     * The main method for button's events
     * @param event ;
     * @throws Exception;
     */
    @FXML
    private void events(ActionEvent event) throws Exception {
        if( event.getSource()== addTaskbtn){ addTask();}

        //else if( event.getSource()== EditProjectBtn){editProject();}
        else if( event.getSource()== addBtn ) {Main.showAddProjectStage(); }
        else if( event.getSource() == assignTaskCollaboratorBtn){assignCollaborators();}
        else if( event.getSource()== editBtn ) {
            if (projectsTitle.getText()!= ""){
                Global.currentProject = projectsTitle.getText();
                Main.showEditProjectStage();}
        }
        else if( event.getSource()== backBtn){ Main.showMainMenuScene(); }
        else if (event.getSource()==exportProjectBtn){exportProject();}
    }
    //public String getProjectExport(){return String.valueOf(projectExportList.getValue()); }
    public TreeItem<Project> getSelectedProject(){return treeProjects.getSelectionModel().getSelectedItem();}

    @FXML
    public void initTree(){
        treeProjects.setRoot(root);
        treeProjectColumn.setCellValueFactory(new TreeItemPropertyValueFactory<Project, String>("title"));

        collaboratorsTable.setEditable(true);
        collaboratorsColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        collaboratorsColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        taskCollaboratorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        taskCollaboratorColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        taskColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("description"));
        taskColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        taskTable.setEditable(true);
        taskTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                try {
                    if (task == null)
                        setStyle("");
                    else if (ProjectDB.getTaskCollaborator(task.getId()).contains(Global.userID))
                        setStyle("-fx-background-color: #baffba;");
                    else if (!ProjectDB.getTaskCollaborator(task.getId()).contains(Global.userID))
                        setStyle("-fx-background-color: #ffd7d1;");
                    else
                        setStyle("");
                } catch (SQLException e) {
                }
            }
        });

    }

    /**
     * Clears the tables and the map(to refresh when needed)
     * @throws SQLException;
     */
    public void clearProjects(){
        treeProjects.getRoot().getChildren().clear();
        /*
        projectSelection.getItems().clear();
        projectSelection.setPromptText("Select project")*/
    }

    @FXML
    public void addChild(TreeItem<Project> parent, TreeItem<Project> child){
        parent.getChildren().add(child);
    }

    public void insertCollaborator(ObservableList<String> names){
        collabComboBox.getItems().setAll(names);
    }

    public void insertTaskCollaborators(ObservableList<String> names){
        taskCollaboratorTable.setItems(names);
    }

    /**
     * Creates a project and add it to the Database and the map + displays it in the tree table view
     * @throws SQLException;
     */


    /**
     * Deletes a project in the Database and in the tree table view
     * @param event;
     * @throws SQLException;
     */
    @SuppressWarnings("unchecked")
    @FXML
    private void deleteProject(ActionEvent event) throws SQLException{
        if(treeProjects.getSelectionModel().getSelectedItem()!= null && treeProjects.getSelectionModel().getSelectedItem().getValue()!=null) {
            Project child= treeProjects.getSelectionModel().getSelectedItem().getValue();
            String projectName = child.getTitle();
            int projectID = ProjectDB.getProjectID(projectName);
            ProjectDB.deleteProject(projectID);
            int parentID = child.getParent_id();

            if (parentID == 0) {
                root.getChildren().removeAll(treeProjects.getSelectionModel().getSelectedItem());
            } else {
                Global.TreeMap.get(parentID).getChildren().removeAll(treeProjects.getSelectionModel().getSelectedItem());
            }
        }
        UserDB.updateDiskUsage(ProjectDB.getSizeOnDisk());
    }



    @FXML
    public void displayTask() throws SQLException {
        TreeItem<Project> selectedProject = getSelectedProject();
        ObservableList<Task> oTaskList = controller.getTasks(selectedProject);
        taskTable.setItems(oTaskList);
    }

    public void showTaskEdition() throws Exception {
        Global.selectedTask = getSelectedTask();
        Main.showEditTaskStage();
    }


    public void addTask() throws Exception{
        controller.addTask(descriptionTask.getText(),getSelectedProject().getValue().getTitle());
        displayTask();
    }

    public void deleteTask() throws SQLException{
        Task task = getSelectedTask();
        controller.deleteTask(task);
        taskTable.getItems().removeAll(task);
    }

    public void exportProject(){
        TreeItem<Project> selectedProject = getSelectedProject();
        if(selectedProject!= null && selectedProject.getValue()!=null){
            System.out.println(selectedProject.getValue().getTitle());
            controller.exportProject(selectedProject.getValue(),
                    "C:\\Users\\hodai\\Desktop\\ulb_2020_2021",
                    "C:\\Users\\hodai\\Desktop\\ulb_2020_2021\\est.txt",
                    selectedProject.getValue().getId());
        }
        else {
            System.out.println("aucun projet");
        }
    }


    public void deleteCollaborator() throws SQLException{
        String collaborator = getSelectedUser();
        controller.deleteCollaborator(collaborator, getSelectedProject().getValue().getId());
        collaboratorsTable.getItems().removeAll(collaborator);
    }


    @FXML
    public void displayCollaborators() throws SQLException {
        TreeItem<Project> selectedProject = getSelectedProject();
        ObservableList<String> oCollaboratorsList = controller.getCollaborators(selectedProject);
        collaboratorsTable.setItems(oCollaboratorsList);
    }

    public void addCollaborator() throws SQLException{
        if(collaboratorsName.getText() != ""){
            if(!controller.addCollaborator(collaboratorsName.getText(), getSelectedProject().getValue().getId())){
                // TODO Show "error user doesn't exist" message
            } else {
                // TODO Show "invitation sent" message
            }
        }
    }

    @FXML
    public void assignCollaborators() throws SQLException{
        ObservableList<String> selectedCollaborators = collabComboBox.getCheckModel().getCheckedItems();
        Task selectedTask = Global.selectedTask;
        controller.assignCollaborators(selectedCollaborators, selectedTask, getSelectedProject().getValue().getId());
        displayTask();
    }

    @FXML
    public void deleteTaskCollaborator() throws SQLException {
        controller.deleteTaskCollaborator(getSelectedTaskCollaborator(),Global.selectedTask);
    }

    @FXML
    public void onTaskSelected() throws SQLException{
        if (getSelectedTask() != null) {
            Global.selectedTask = getSelectedTask();
            controller.initTaskCollaborators(this, Global.selectedTask);
            controller.initTaskCollaborators(this, getSelectedTask());
            ObservableList<String> items = collabComboBox.getItems();
            for (String item : items) {
                collabComboBox.getItemBooleanProperty(item).set(false);
                if (ProjectDB.getTaskCollaborator(getSelectedTask().getId()).contains(Integer.parseInt(UserDB.getUserInfo(item.toString()).get("id")))) {
                    collabComboBox.getItemBooleanProperty(item).set(true);
                }
            }
        }
    }

    @FXML
    public Task getSelectedTask(){
        return taskTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    public String getSelectedTaskCollaborator(){
        return taskCollaboratorTable.getSelectionModel().getSelectedItem();
    }


    @FXML
    public String getSelectedUser(){
        return collaboratorsTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    public void displayProject() throws SQLException {
        try {
            TreeItem<Project> selectedProject = getSelectedProject();

            String description = selectedProject.getValue().getDescription();
            String title = selectedProject.getValue().getTitle();
            Long date = selectedProject.getValue().getDate();
            int id = selectedProject.getValue().getId();
            List<Tag> tags = ProjectDB.getTags(id);
            ObservableList<String> tagsName = FXCollections.observableArrayList();
            for (Tag tag : tags) { tagsName.add(tag.getDescription()); }
            projectsDescription.setText(description);
            projectsDate.setText(controller.dateToString(date));
            projectsTitle.setText(title);
            projectTags.setItems(tagsName);
            displayTask();
            displayCollaborators();

            controller.initCollaborators(this);

            projectTags.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
                @Override
                public ListCell<String> call(ListView<String> p) {
                    ListCell<String> cell = new ListCell<String>() {
                        @Override
                        protected void updateItem(String t, boolean bln) {
                            super.updateItem(t, bln);
                            try {
                                if (t != null){
                                    System.out.println(t);
                                    setText(t);
                                    setStyle("-fx-text-fill: "+ ProjectDB.getTag(ProjectDB.getTagID(t)).getColor()+";");
                                }

                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        }
                    };
                    return cell;
                }
            });


        }catch(NullPointerException throwables){
            System.out.println(throwables);

        }

    }

    public void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

}
