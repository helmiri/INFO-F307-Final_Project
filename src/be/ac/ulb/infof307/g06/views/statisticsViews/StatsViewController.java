package be.ac.ulb.infof307.g06.views.statisticsViews;

import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Statistics;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StatsViewController{
    //-------------- ATTRIBUTES ----------------
    @FXML
    private Button backToProjectMenu;
    @FXML
    private Button projectViewBtn;
    @FXML
    private Button overallViewBtn;
    @FXML
    private Button exportCSVBtn;
    @FXML
    private Button exportJSONBtn;
    @FXML
    private Label collaboratorsNumber;
    @FXML
    private Label tasksNumber;
    @FXML
    private Label projectsNumber;
    @FXML
    private Label startDate;
    @FXML
    private Label estimatedDate;
    @FXML
    private Label finishDate;
    @FXML
    private Text msgExportText;
    @FXML
    private TextField fileNameTextField;
    @FXML
    private TreeTableColumn<Project, String> projectsColumn;
    @FXML
    private TreeTableView<Project> projectsTreeView;
    @FXML
    private PieChart projectsChart;
    @FXML
    private BarChart<?, ?> collaboratorsChart;
    private Project selectedProject;

    private final TreeItem<Project> root = new TreeItem<>();
    private StatsViewController.ViewListener listener;
    //private final StatsController controller =
    // new StatsController(1, new UserDB("Database.db"), new ProjectDB("Database.db"), new Stage());

    //--------------- METHODS ----------------
    /**
     * Initializes the main view and the tree. Sets values.
     */
    //public void init() { initOverallStats(); }

    /**
     * Initializes the tree table with values.
     */
    @FXML
    public void initTree(){
        try {
        List<Integer> projectsArray;
        projectsColumn  .setCellValueFactory(new TreeItemPropertyValueFactory<>("title"));
        projectsTreeView.setRoot(root);
        projectsArray = listener.getProjects();
        listener.setStats(projectsArray, root);
        } catch (DatabaseException e) {
            new AlertWindow("Error", "An error has occurred with the database: " + e).errorWindow();
        }
    }

    /**
     *
     */
    public void initOverallStats() {
        try{
        List<Integer> infos = listener.countOverallStats();
        projectsNumber.setText(Integer.toString(infos.get(0)));
        tasksNumber.setText(infos.get(1).toString());
        collaboratorsNumber.setText(Integer.toString(infos.get(2)));

        }catch(SQLException e){
            System.out.println("erreur");
        }
    }

    @FXML
    public void displayStats() throws SQLException {
        List<Integer> infos = listener.countProjectStats(getSelectedProject());
        projectsNumber.setText(Integer.toString(infos.get(0)));
        tasksNumber.setText(infos.get(1).toString());
        collaboratorsNumber.setText(Integer.toString(infos.get(2)));
    }

    @FXML
    public void onProjectSelected() throws SQLException {
        selectedProject = getSelectedProject();
        if (selectedProject == null) {
            return;
        }
        displayStats();
    }

    public Project getSelectedProject(){
        if (projectsTreeView.getSelectionModel().getSelectedItem() != null) {
            return projectsTreeView.getSelectionModel().getSelectedItem().getValue();
        }
        return null;
    }

    /**
     * The main method for button's events.
     *
     * @param event ActionEvent
     */
    @FXML
    private void statsEvents(ActionEvent event) {
        if      (event.getSource() == backToProjectMenu  ) { listener.onBackButtonClicked(); }
        else if (event.getSource() == overallViewBtn     ) { listener.show()               ; }
        else if (event.getSource() == projectViewBtn     ) { listener.showStatsProject()   ; }

//        else if (event.getSource() == exportJSONBtn || event.getSource() == exportCSVBtn) {
//            exports(event);
//        }
    }

    /**
     * Shows root in the tree table view.
     *
     * @param root TreeItem<Statistics>
     */
    public void expandRoot(TreeItem<Project> root){root.setExpanded(true);}

    /**
     * Adds a child to the related parent in the TreeTableView.
     *
     * @param parent TreeItem<Statistics>
     * @param child TreeItem<Statistics>
     */
    public void addChild(TreeItem<Project> parent, TreeItem<Project> child){
        parent.getChildren().add(child);
    }

    /**
     * Executes the right export method.
     *
     * @param event ActionEvent
     */
//    public void exports(ActionEvent event) {
//        String fileName = fileNameTextField.getText();
//        DirectoryChooser directoryChooser = new DirectoryChooser();
//        directoryChooser.setInitialDirectory(new File("src"));
//        File selectedDirectory = directoryChooser.showDialog(new Stage());
//        if (selectedDirectory != null) {
//            if (event.getSource() == exportJSONBtn) {
//                if (fileName.equals("")) { listener.exportStatsAsJson("\\Statistics.json",selectedDirectory.getAbsolutePath(), root); }
//                else { listener.exportStatsAsJson("\\" + fileName + ".json",selectedDirectory.getAbsolutePath(), root); }
//            } else if (event.getSource() == exportCSVBtn) {
//                if (fileName.equals("")) { listener.exportStatsAsCSV("\\Statistics.csv", selectedDirectory.getAbsolutePath(), root); }
//                else { listener.exportStatsAsCSV("\\" + fileName + ".csv", selectedDirectory.getAbsolutePath(), root); }
//            }
//        }
//    }

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
        void show();

        void showStatsProject();

        void onBackButtonClicked();

        List<Integer> getProjects() throws DatabaseException;

        void setStats(List<Integer> projects,TreeItem<Project> root) throws DatabaseException;

        void exportStatsAsJson(String fileName, String path, TreeItem<Statistics> root);

        void exportStatsAsCSV(String fileName, String path, TreeItem<Statistics> root);

        List<Integer> countOverallStats() throws SQLException;

        List<Integer> countProjectStats(Project selectedProject) throws SQLException;


    }
}
