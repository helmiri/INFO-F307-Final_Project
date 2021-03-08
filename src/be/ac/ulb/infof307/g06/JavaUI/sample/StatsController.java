package be.ac.ulb.infof307.g06.JavaUI.sample;

import be.ac.ulb.infof307.g06.Main;
import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.database.ProjectDB.Project;
import be.ac.ulb.infof307.g06.database.ProjectDB.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTreeTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class StatsController implements Initializable {
    // --------- Boutons ------

    @FXML
    private Button exportBtn;

    @FXML
    private Button backToProjectMenu;

    @FXML
    private Button logOutBtn;

    // TreeView
    @FXML
    private TreeTableView<Project> projectsTreeView;

    @FXML
    private TreeTableColumn<Project, String> projectsColumn;

    @FXML
    private TreeTableColumn<Object, Integer> collaboratorsColumn;

    @FXML
    private TreeTableColumn<Task, String> tasksColumn;

    @FXML
    private TreeTableColumn<Integer, String> realColumn;


    @FXML
    private TreeTableColumn<Integer, String> estimatedColumn;

    private TreeItem<ProjectDB.Project> root = new TreeItem<ProjectDB.Project>();


    //---------------METHODE----------------
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTree();
        try {
            loadProjects();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML
    private void buttonEvents(ActionEvent event) throws Exception {
        //TODO: Rename cette méthode en LogInEvents?
        if(event.getSource() == backToProjectMenu) { Main.ShowProjectsMenu(); }
        else if(event.getSource() == logOutBtn) { Main.showConnectionScene(); }
        else if(event.getSource() == exportBtn) { setData(); }


    }

    @FXML
    private void initTree() {
        projectsColumn.setCellValueFactory(new TreeItemPropertyValueFactory<Project, String>("title"));
        //tasksColumn.setCellValueFactory(new TreeItemPropertyValueFactory<Task, String>("description"));


        //collaboratorsColumn.setCellValueFactory(new TreeItemPropertyValueFactory<Project, String>("id"));
        projectsTreeView.setRoot(root);
    }

    private void loadProjects() throws SQLException {
        List<Integer> projectsArray = ProjectDB.getUserProjects(Global.userID);
        getProjects(projectsArray, root);
    }

    private void setData() throws SQLException {
        String projectName = projectsTreeView.getSelectionModel().getSelectedItem().getValue().getTitle();
        int projectID = ProjectDB.getProjectID(projectName);
        int collaboratorsNbr = ProjectDB.countCollaborators(projectID);

        List<Integer> collabList =  ProjectDB.getCollaborators(projectID);
        System.out.println(collaboratorsNbr);


//        ObservableList<Integer> observableListCollab = FXCollections.observableArrayList(collabList);
//        collaboratorsColumn.setCellFactory(ComboBoxTreeTableCell.forTreeTableColumn(observableListCollab));
        projectsTreeView.setRoot(root);

    }



    public void getProjects(List<Integer> projects, TreeItem<Project> parent) throws SQLException{
        projectsTreeView.setShowRoot(false);
        for(Integer project : projects){
            Project childProject= ProjectDB.getProject(project);
            int parentID= childProject.getParent_id();
            String title= childProject.getTitle();
            int childID= ProjectDB.getProjectID(title);

            System.out.println("Project= "+childProject+" parentID= "+parentID+ " childID= "+childID+ " description= "+title);

            TreeItem<Project> child = new TreeItem<Project>(childProject);
            Global.TreeMap.put(childID, child);
            if (parentID== 0){
                root.getChildren().add(child);
            } else {
                Global.TreeMap.get(parentID).getChildren().add(child);
            }
        }
        projectsTreeView.setShowRoot(true);
    }


//    @FXML
//    private void showSubProjects(ActionEvent event) throws SQLException{
//        treeProjectColumn.setCellValueFactory(new TreeItemPropertyValueFactory<ProjectDB.Project, String>("title"));
//
//        String projectName = treeProjects.getSelectionModel().getSelectedItem().getValue().getTitle();
//        int projectID = ProjectDB.getProjectID(projectName);
//        List<Integer> subProjects = ProjectDB.getSubProjects(projectID);
//        getProjects(subProjects,treeProjects.getSelectionModel().getSelectedItem());
//    }*/
//
//    @FXML
//    private void showDetailsProject(ActionEvent event) throws SQLException{
//        String projectName = treeProjects.getSelectionModel().getSelectedItem().getValue().getTitle();
//        int projectID= ProjectDB.getProjectID(projectName);
//        Project showProject= ProjectDB.getProject(projectID);
//        String description= showProject.getDescription();
//        String tags=showProject.getTags();
//
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Details");
//        alert.setHeaderText(null);
//        alert.getDialogPane().setMinWidth(600);
//        alert.setResizable(false);
//        alert.setContentText("DESCRIPTION:\n" +description+"\n\n"+"TAGS:\n"+tags);
//        alert.showAndWait();
//    }
//

}
