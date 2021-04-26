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
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
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
    private BarChart<String, Integer> tasksChart;
    private Project selectedProject;
    private boolean isOverallView=true;
    private final TreeItem<Project> root = new TreeItem<>();
    private StatsViewController.ViewListener listener;
    //--------------- METHODS ----------------
    /**
     * The main method for button's events.
     *
     * @param event ActionEvent
     */
    @FXML
    private void statsEvents(ActionEvent event) {
        if      (event.getSource() == backToProjectMenu  ) { listener.onBackButtonClicked(); }
        else if (event.getSource() == overallViewBtn     ) { listener.show()               ;}
        else if (event.getSource() == projectViewBtn     ) { listener.showIndividualStats()   ;}
        else if (event.getSource() == exportJSONBtn
                || event.getSource() == exportCSVBtn     ) { exports(event); }
    }

    /**
     * Initializes the project view with the tree table with values and sets stats on the screen.
     */
    @FXML
    public void initTreeTableView(){
        try {
            List<Integer> projectsArray;
            projectsColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("title"));
            projectsTreeView.setRoot(root);
            projectsArray = listener.getProjects();
            listener.setStats(projectsArray, root);
        } catch (DatabaseException e) {
            new AlertWindow("Error", "An error has occurred with the database: " + e).errorWindow();
        }
        isOverallView=false;
        overallViewBtn.setDisable(false);
        projectViewBtn.setDisable(true);
    }

    /**
     * Initializes the overall view, sets stats and charts.
     */
    public void initOverallStats() {
        List<Integer> infos = listener.countOverallStats();
        displayBasicStats(infos);
        isOverallView = true;
        tasksChart.setLegendVisible(true);
        pieChartInitializer();
        barChartInitializer();
        overallViewBtn.setDisable(true);
        projectViewBtn.setDisable(false);
    }

    /**
     * Initializes the pie chart that shows projects by their time.
     */
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
            new AlertWindow("Error", "An error has occurred with the database: "+e).errorWindow();
        }
    }

    /**
     * Initializes the bar chart that shows the number of tasks of projects.
     */
    public void barChartInitializer(){
        XYChart.Series<String,Integer> series = new XYChart.Series<>();
        try{
            List<Integer> projects = listener.getProjects();
            int numberOfProjects=0;
            for (Integer integer : projects) {
                Project project = listener.getProjectsFromID(integer);
                Integer tasksCount = listener.countTasksOfAProject(project.getId());
                if (tasksCount != 0) {
                    numberOfProjects++;
                    tasksChart.setPrefWidth(513 + (numberOfProjects * 30));
                    barChartAnchorPane.setPrefWidth(513 + (numberOfProjects * 30));
                    barChartPane.setPrefWidth(513 + (numberOfProjects * 30));
                    series.getData().add(new XYChart.Data<>(project.getTitle(), tasksCount));
                }
            }
            tasksChart.getData().add(series);
        }
        catch(DatabaseException e){
            new AlertWindow("Error", "An error has occurred with the database while loading the bar chart: "+e).errorWindow();
        }
    }

    /**
     * Displays stats of the number of sub/projects, tasks and collaborators.
     */
    public void displayBasicStats(List<Integer> infos) {
        projectsNumber.setText(Integer.toString(infos.get(0)));
        tasksNumber.setText(infos.get(1).toString());
        collaboratorsNumber.setText(Integer.toString(infos.get(2)));
    }

    /**
     * Displays stats for a specific project.
     */
    public void displayProjectStats() {
        List<Integer> infos = listener.countIndividualProjectStats(selectedProject);
        Project selectedProject = getSelectedProject();
        displayBasicStats(infos);
        startDate.setText(listener.dateToString(selectedProject.getStartDate()));
        estimatedDate.setText(listener.dateToString(selectedProject.getEndDate()));
    }

    /**
     * Displays stats when a project is selected.
     */
    public void onProjectSelected(){
        selectedProject = getSelectedProject();
        if (selectedProject == null) {
            resetTextLabel();
            return;
        }
        displayProjectStats();
    }

    /**
     * Resets the texts in labels in statistics.
     */
    public void resetTextLabel(){
        projectsNumber.setText("0");
        tasksNumber.setText("0");
        collaboratorsNumber.setText("0");
        startDate.setText("?/?/?");
        estimatedDate.setText("?/?/?");
        finishDate.setText("?/?/?");

    }

    /**
     * Gets the selected project in the tree table.
     *
     * @return Project, the selected project on the tree table.
     */
    public Project getSelectedProject(){
        if (projectsTreeView.getSelectionModel().getSelectedItem() != null) {
            return projectsTreeView.getSelectionModel().getSelectedItem().getValue();
        }
        return null;
    }

    /**
     * Shows root in the tree table view.
     *
     * @param root TreeItem<Project>
     */
    public void expandRoot(TreeItem<Project> root){root.setExpanded(true);}

    /**
     * Adds a child to the related parent in the TreeTableView.
     *
     * @param parent TreeItem<Project>
     * @param child TreeItem<Project>
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

    //--------------- LISTENER ----------------
    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    public interface ViewListener {
        void show();

        void showIndividualStats();

        void onBackButtonClicked();

        List<Integer> getProjects() throws DatabaseException;

        Integer countTasksOfAProject(int project_id) throws DatabaseException;

        Project getProjectsFromID(int id) throws DatabaseException;

        void setStats(List<Integer> projects,TreeItem<Project> root) throws DatabaseException;

        void exportStatsAsJson(String fileName, String path, TreeItem<Project> root);

        void exportStatsAsCSV(String fileName, String path, TreeItem<Project> root);

        List<Integer> countOverallStats() ;

        List<Integer> countIndividualProjectStats(Project selectedProject);

        void exportAsCSVOverallView(String fileName, String path);

        void exportAsJSONOverallView(String fileName, String path);

        String dateToString(Long date);

    }
}
