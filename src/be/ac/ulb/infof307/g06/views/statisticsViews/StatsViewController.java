package be.ac.ulb.infof307.g06.views.statisticsViews;

import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Project;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.sql.SQLException;
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
    private AnchorPane barChartAnchorPane;
    @FXML
    private AnchorPane pieChartAnchorPane;
    @FXML
    private Pane barChartPane;
    @FXML
    private TreeTableColumn<Project, String> projectsColumn;
    @FXML
    private TreeTableView<Project> projectsTreeView;
    @FXML
    private PieChart projectsChart;
    @FXML
    private BarChart<?, ?> collaboratorsChart;
    private Project selectedProject;
    private boolean isOverallView=true;

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
        isOverallView=false;
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
        isOverallView=true;
        tasksChart.setLegendVisible(false);
        pieChartInitializer();

    }

    public void pieChartInitializer(){
        try{
            List<Integer> projects= listener.getProjects();
            ObservableList<PieChart.Data> datas= FXCollections.observableArrayList();
            for(int i=0;i<projects.size();i++){
                projectsChart.setPrefWidth(513+(10*i));
                projectsChart.setPrefHeight(322+(4*i));
                pieChartAnchorPane.setPrefWidth(513+(10*i));
                pieChartAnchorPane.setPrefHeight(322+(4*i));
                Project project =listener.getProjectsFromID(projects.get(i));
                datas.add(i,new PieChart.Data(project.getTitle(),project.getDuration()));
            }
            projectsChart.setData(datas);

        }
        catch(DatabaseException e){
            new AlertWindow("Error", "An error has occured with the database.").errorWindow();
        }

    }

    @FXML
    public void displayStats() throws SQLException {
        Project selectedProjet = getSelectedProject();
        List<Integer> infos = listener.countProjectStats(selectedProject);
        projectsNumber.setText(Integer.toString(infos.get(0)));
        tasksNumber.setText(infos.get(1).toString());
        collaboratorsNumber.setText(Integer.toString(infos.get(2)));
        startDate.setText(listener.dateToString(selectedProjet.getStartDate()));
        estimatedDate.setText(listener.dateToString(selectedProjet.getEndDate()));
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
        else if (event.getSource() == overallViewBtn     ) { listener.show()               ;}
        else if (event.getSource() == projectViewBtn     ) { listener.showStatsProject()   ;}
        else if (event.getSource() == exportJSONBtn || event.getSource() == exportCSVBtn) {
            exports(event);
        }
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
    public void exports(ActionEvent event) {
        String fileName = fileNameTextField.getText();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("src"));
        File selectedDirectory = directoryChooser.showDialog(new Stage());
        if (selectedDirectory != null) {
            System.out.println(isOverallView);
            if (event.getSource() == exportJSONBtn && !isOverallView) {
                if (fileName.equals("")) { listener.exportStatsAsJson("\\Statistics.json",selectedDirectory.getAbsolutePath(), root); }
                else { listener.exportStatsAsJson("\\" + fileName + ".json",selectedDirectory.getAbsolutePath(), root); }
              }
            else if (event.getSource() == exportCSVBtn && !isOverallView) {
                if (fileName.equals("")) { listener.exportStatsAsCSV("\\Statistics.csv", selectedDirectory.getAbsolutePath(), root); }
                else { listener.exportStatsAsCSV("\\" + fileName + ".csv", selectedDirectory.getAbsolutePath(), root); }
            }
            else if(event.getSource() == exportJSONBtn && isOverallView){
                if (fileName.equals("")) { listener.exportAsJSONOverallView("\\Statistics.json",selectedDirectory.getAbsolutePath()); }
                else { listener.exportAsJSONOverallView("\\" + fileName + ".json",selectedDirectory.getAbsolutePath()); }
            }
            else if (event.getSource() == exportCSVBtn && isOverallView){
                if (fileName.equals("")) { listener.exportAsCSVOverallView("\\Statistics.csv", selectedDirectory.getAbsolutePath()); }
                else { listener.exportAsCSVOverallView("\\" + fileName + ".csv", selectedDirectory.getAbsolutePath()); }
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
        void show();

        void showStatsProject();

        void onBackButtonClicked();

        List<Integer> getProjects() throws DatabaseException;

        void setStats(List<Integer> projects,TreeItem<Project> root) throws DatabaseException;

        void exportStatsAsJson(String fileName, String path, TreeItem<Project> root);

        void exportStatsAsCSV(String fileName, String path, TreeItem<Project> root);

        List<Integer> countOverallStats() throws SQLException;

        List<Integer> countProjectStats(Project selectedProject) throws SQLException;

        void exportAsCSVOverallView(String fileName, String path);

        void exportAsJSONOverallView(String fileName, String path);

        Project getProjectsFromID(int id) throws DatabaseException;

        String dateToString(Long date);

        Integer countTasksOfAProject(int project_id) throws SQLException;
    }
}
