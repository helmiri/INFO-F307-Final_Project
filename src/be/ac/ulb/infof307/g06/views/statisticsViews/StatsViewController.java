package be.ac.ulb.infof307.g06.views.statisticsViews;

import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private Tooltip collaboratorsToolTip;
    @FXML
    private Tooltip tasksToolTip;
    @FXML
    private Tooltip projectsToolTip;
    @FXML
    private Tooltip barChartToolTip;
    @FXML
    private Tooltip pieChartToolTip;
    @FXML
    private Tooltip exportToolTip;
    @FXML
    private Label pieChartLabel;
    @FXML
    private Label barChartLabel;
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
    private Label collaboratorsNumberLabel;
    @FXML
    private Label tasksNumberLabel;
    @FXML
    private Label projectsNumberLabel;
    @FXML
    private Label startDate;
    @FXML
    private Label estimatedDate;
    @FXML
    private Label taskEndDateLabel;
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
    private TableView<Task> tasksTableView;
    @FXML
    private TableColumn<Task, String> tasksColumn;
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
        else if (event.getSource() == exportJSONBtn || event.getSource() == exportCSVBtn     ) { exports(event); }
    }

    /**
     * Initializes the project view with the tree table with values and sets stats on the screen.
     */
    @FXML
    public void initTables(){
        List<Integer> projectsArray;
        tasksColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        projectsColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("title"));
        projectsTreeView.setRoot(root);
        projectsArray = listener.getProjects();
        listener.setProjectsTable(projectsArray, root);
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
        List<Integer> projects= listener.getProjects();
        if (projects != null) {
            ObservableList<PieChart.Data> datas = FXCollections.observableArrayList();
            for (int i = 0; i < projects.size(); i++) {
                projectsChart.setPrefWidth(513 + (10 * i));
                projectsChart.setPrefHeight(322 + (4 * i));
                pieChartAnchorPane.setPrefWidth(513 + (10 * i));
                pieChartAnchorPane.setPrefHeight(322 + (4 * i));
                Project project = listener.getProjectsFromID(projects.get(i));
                if(project !=null) {
                    datas.add(i, new PieChart.Data(project.getTitle(), project.getDuration()));
                }
            }
            projectsChart.setData(datas);
        }
    }

    /**
     * Initializes the bar chart that shows the number of tasks of projects.
     */
    public void barChartInitializer(){
        XYChart.Series<String,Integer> series = new XYChart.Series<>();

        List<Integer> projects = listener.getProjects();
        if (projects != null) {
            int numberOfProjects = 0;
            for (Integer integer : projects) {
                Project project = listener.getProjectsFromID(integer);
                Integer tasksCount = listener.countTasksOfAProject(project.getId());
                if(project != null && tasksCount != 0) {
                    numberOfProjects++;
                    tasksChart.setPrefWidth(513 + (numberOfProjects * 30));
                    barChartAnchorPane.setPrefWidth(513 + (numberOfProjects * 30));
                    barChartPane.setPrefWidth(513 + (numberOfProjects * 30));
                    series.getData().add(new XYChart.Data<>(project.getTitle(), tasksCount));

                }
            }
            tasksChart.getData().add(series);
        }

    }

    /**
     * Displays stats of the number of sub/projects, tasks and collaborators.
     */
    public void displayBasicStats(List<Integer> infos) {
        projectsNumberLabel.setText(Integer.toString(infos.get(0)));
        tasksNumberLabel.setText(infos.get(1).toString());
        collaboratorsNumberLabel.setText(Integer.toString(infos.get(2)));
    }

    /**
     * Displays stats for a specific project.
     */
    public void displayProjectStats() {
        List<Integer> infos = listener.countIndividualProjectStats(selectedProject);
        displayBasicStats(infos);
        tasksTableView.setItems(listener.setTasksTable(selectedProject));
        startDate.setText(listener.dateToString(selectedProject.getStartDate()));
        estimatedDate.setText(listener.dateToString(selectedProject.getEndDate()));
        taskEndDateLabel.setText("?/?/?");
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
     * Displays the end date of a task when we click on it.
     */
    public void onTaskSelected(){
        Task selectedTask = getSelectedTask();
        if(selectedTask == null){
            taskEndDateLabel.setText("?/?/?");
        }
        else{
            taskEndDateLabel.setText(listener.dateToString(selectedTask.getEndDate()));
        }
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
     * Gets the selected task in the table.
     *
     * @return Task, the selected task in the task table view.
     */
    public Task getSelectedTask(){
        if(tasksTableView.getSelectionModel().getSelectedItem() != null){
            return tasksTableView.getSelectionModel().getSelectedItem();
        }
        return null;
    }

    /**
     * Resets the texts in labels in statistics.
     */
    public void resetTextLabel(){
        projectsNumberLabel.setText("0");
        tasksNumberLabel.setText("0");
        collaboratorsNumberLabel.setText("0");
        startDate.setText("?/?/?");
        estimatedDate.setText("?/?/?");
        taskEndDateLabel.setText("?/?/?");

    }

    /**
     * Shows root in the tree table view.
     *
     * @param root selected root
     */
    public void expandRoot(TreeItem<Project> root){root.setExpanded(true);}

    /**
     * Adds a child to the related parent in the TreeTableView.
     *
     * @param parent The project's parent
     * @param child  The project's child
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
        if(fileName.equals("")){
            fileName="Statistics";
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("src"));
        File selectedDirectory = directoryChooser.showDialog(new Stage());
        if (selectedDirectory != null) {
            if (event.getSource() == exportJSONBtn && !isOverallView) {
                listener.exportStatsAsJson("\\" + fileName + ".json", selectedDirectory.getAbsolutePath(), root);
            }
            else if (event.getSource() == exportCSVBtn && !isOverallView) {
                listener.exportStatsAsCSV("\\" + fileName + ".csv", selectedDirectory.getAbsolutePath(), root);
            }
            else if(event.getSource() == exportJSONBtn && isOverallView){
                 listener.exportAsJSONOverallView("\\" + fileName + ".json",selectedDirectory.getAbsolutePath());
            }
            else if (event.getSource() == exportCSVBtn && isOverallView){
                 listener.exportAsCSVOverallView("\\" + fileName + ".csv", selectedDirectory.getAbsolutePath());
            }
        }
    }

    //--------------- LISTENER ----------------
    /**
     * Sets the listener.
     *
     * @param listener ViewListener, the listener to the view.
     */
    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    public interface ViewListener {
        void show();

        void showIndividualStats();

        void onBackButtonClicked();

        List<Integer> getProjects() ;

        Integer countTasksOfAProject(int projectId) ;

        Project getProjectsFromID(int id) ;

        void setProjectsTable(List<Integer> projects,TreeItem<Project> root) ;

        ObservableList<Task> setTasksTable(Project selectedProject);

        void exportStatsAsJson(String fileName, String path, TreeItem<Project> root);

        void exportStatsAsCSV(String fileName, String path, TreeItem<Project> root);

        List<Integer> countOverallStats() ;

        List<Integer> countIndividualProjectStats(Project selectedProject);

        void exportAsCSVOverallView(String fileName, String path);

        void exportAsJSONOverallView(String fileName, String path);

        String dateToString(Long date);

    }
}
