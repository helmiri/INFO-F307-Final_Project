package be.ac.ulb.infof307.g06.views;

import be.ac.ulb.infof307.g06.Main;
import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.database.ProjectDB.Project;
import be.ac.ulb.infof307.g06.models.Global;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class StatsViewController implements Initializable {
    // Boutons

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
    private TreeTableColumn<Integer, String> collaboratorsColumn;

    @FXML
    private TreeTableColumn<Integer, String> tasksColumn;

    @FXML
    private TreeTableColumn<Integer, String> realColumn;

    @FXML
    private TreeTableColumn<Integer, String> estimatedColumn;

    private TreeItem<ProjectDB.Project> root = new TreeItem<ProjectDB.Project>();


    //---------------METHODE----------------

    /**
     * Initializes the tree table view for the statistics of the project +
     * loads user's projects and initializes the map.
     * @param url;
     * @param resourceBundle;
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTree();
        try {
            loadProjects();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * The main method for button's events
     * @param event;
     * @throws Exception;
     */
    @FXML
    private void buttonEvents(ActionEvent event) throws Exception {
        if(event.getSource() == backToProjectMenu) { Main.showProjectMenuScene(); }
        else if(event.getSource() == logOutBtn) { Main.showLoginScene(); }
    }

    @FXML
    private void initTree() {
        projectsColumn.setCellValueFactory(new TreeItemPropertyValueFactory<Project, String>("title"));

        projectsTreeView.setRoot(root);
    }

    /**
     * Loads projects
     * @throws SQLException;
     */
    private void loadProjects() throws SQLException {
        List<Integer> projectsArray = ProjectDB.getUserProjects(Global.userID);
        getProjects(projectsArray, root);
    }

    /**
     * Initializes the map and display projects on the tree table view
     * @param projects;
     * @param parent;
     * @throws SQLException;
     */
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




}
