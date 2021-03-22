package be.ac.ulb.infof307.g06.views;

import be.ac.ulb.infof307.g06.Main;
import be.ac.ulb.infof307.g06.controllers.StatsController;
import be.ac.ulb.infof307.g06.models.Statistics;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;

public class StatsViewController implements Initializable {
    //-------------- ATTRIBUTES ----------------
    @FXML
    private Button exportBtn;
    @FXML
    private Button backToProjectMenu;
    @FXML
    private Button logOutBtn;
    @FXML
    private TreeTableView<Statistics> projectsTreeView;
    @FXML
    private TreeTableColumn<Statistics, String> projectsColumn;
    @FXML
    private TreeTableColumn<Statistics, String> collaboratorsColumn;
    @FXML
    private TreeTableColumn<Statistics, String> tasksColumn;
    /*
    @FXML
    private TreeTableColumn<Statistics, String> realColumn;
    */
    @FXML
    private TreeTableColumn<Statistics, String> estimatedColumn;
    private final TreeItem<Statistics> root = new TreeItem<>();
    private final StatsController controller= new StatsController();
    //--------------- METHODS ----------------
    /**
     * Initializes the tree table view for the statistics of the project +
     * loads user's projects and initializes the map.
     *
     * @param url;
     * @param resourceBundle;
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        controller.init(this, root);
    }

    /**
     * The main method for button's events
     *
     * @param event;
     * @throws Exception;
     */
    @FXML
    private void buttonEvents(ActionEvent event) throws Exception {
        if(event.getSource() == backToProjectMenu) { Main.showProjectMenuScene(); }
        else if(event.getSource() == logOutBtn) { Main.showLoginScene(); }
    }

    /**
     * Initializes the tree table with values
     */
    @FXML
    public void initTree() {
        projectsColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("title"));
        collaboratorsColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("collaborators"));
        tasksColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("tasks"));
        //realColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("finalDate"));
        estimatedColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("estimatedDate"));
        projectsTreeView.setRoot(root);
    }

    /**
     * Shows root in the tree table view
     *
     * @param root TreeItem<Statistics>
     */
    public void expandRoot(TreeItem<Statistics> root){root.setExpanded(true);}

    /**
     * Adds a child to the related parent in the TreeTableView
     *
     * @param parent TreeItem<Statistics>
     * @param child TreeItem<Statistics>
     */
    public void addChild(TreeItem<Statistics> parent, TreeItem<Statistics> child){
        parent.getChildren().add(child);
    }
}
