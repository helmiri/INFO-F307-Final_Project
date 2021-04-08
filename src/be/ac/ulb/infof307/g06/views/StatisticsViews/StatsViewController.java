package be.ac.ulb.infof307.g06.views.StatisticsViews;

import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Statistics;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class StatsViewController{
    //-------------- ATTRIBUTES ----------------
    @FXML
    private Button exportJSONBtn;
    @FXML
    private Button exportCSVBtn;
    @FXML
    private Button backToProjectMenu;
    @FXML
    private Button logOutBtn;
    @FXML
    private TextField fileNameTextField;
    @FXML
    private Text msgExportText;
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
    protected StatsViewController.ViewListener listener;
    //private final StatsController controller =
    // new StatsController(1, new UserDB("Database.db"), new ProjectDB("Database.db"), new Stage());

    //--------------- METHODS ----------------


    /**
     * Initializes the main view and the tree. Sets values.
     */
    public void init() {
        initTree();
        List<Integer> projectsArray;
        try {
            projectsArray = listener.getProjects();
            listener.setStats(projectsArray, root);
        } catch (DatabaseException e) {
            new AlertWindow("Error", "An error has occurred with the database: " + e).errorWindow();
        }
    }

    /**
     * The main method for button's events.
     *
     * @param event ActionEvent
     */
    @FXML
    private void statsEvents(ActionEvent event) {
        if (event.getSource() == backToProjectMenu) {
            listener.onBackButtonClicked();
            //MainController.showProjectMenu();
        }
        else if (event.getSource() == logOutBtn) {
            System.out.println("ICI");
//            LoginController.show();
        }
        else if (event.getSource() == exportJSONBtn || event.getSource() == exportCSVBtn) {
            exports(event);
        }
    }

    /**
     * Initializes the tree table with values.
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
     * Shows root in the tree table view.
     *
     * @param root TreeItem<Statistics>
     */
    public void expandRoot(TreeItem<Statistics> root){root.setExpanded(true);}

    /**
     * Adds a child to the related parent in the TreeTableView.
     *
     * @param parent TreeItem<Statistics>
     * @param child TreeItem<Statistics>
     */
    public void addChild(TreeItem<Statistics> parent, TreeItem<Statistics> child){
        parent.getChildren().add(child);
    }

    /**
     * Executes the right export method.
     *
     * @param event ActionEvent
     */
    public void exports(ActionEvent event) {
        String fileName = fileNameTextField.getText();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("src"));
        File selectedDirectory = directoryChooser.showDialog(new Stage());
        if (selectedDirectory != null) {
            if (event.getSource() == exportJSONBtn) {
                if (fileName.equals("")) { listener.exportStatsAsJson("\\Statistics.json",selectedDirectory.getAbsolutePath(), root); }
                else { listener.exportStatsAsJson("\\" + fileName + ".json",selectedDirectory.getAbsolutePath(), root); }
            } else if (event.getSource() == exportCSVBtn) {
                if (fileName.equals("")) { listener.exportStatsAsCSV("\\Statistics.csv", selectedDirectory.getAbsolutePath(), root); }
                else { listener.exportStatsAsCSV("\\" + fileName + ".csv", selectedDirectory.getAbsolutePath(), root); }
            }
        }
    }

    /**
     * Sets text message for exportation.
     *
     * @param msg String
     */
    public void setMsg(String msg){ msgExportText.setText(msg); }

    //--------------- LISTENER ----------------
    public void setListener(ViewListener listener) {
        this.listener = listener;
    }


    public interface ViewListener {
        void onBackButtonClicked();

        List<Integer> getProjects() throws DatabaseException;

        void setStats(List<Integer> projects,TreeItem<Statistics> root) throws DatabaseException;

        void exportStatsAsJson(String fileName, String path, TreeItem<Statistics> root);

        void exportStatsAsCSV(String fileName, String path, TreeItem<Statistics> root);
    }
}
