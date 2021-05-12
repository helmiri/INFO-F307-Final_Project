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

public class StatsViewController {
    private final TreeItem<Project> root = new TreeItem<>();
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
    private StatsViewController.ViewListener listener;
    //--------------- METHODS ----------------

    /**
     * The main method for button's events.
     *
     * @param event ActionEvent
     */
    @FXML
    private void statsEvents(ActionEvent event) {
        if (event.getSource() == backToProjectMenu) {
            listener.onBackButtonClicked();
        } else if (event.getSource() == overallViewBtn) {
            listener.show();
        } else if (event.getSource() == projectViewBtn) {
            listener.showIndividualStats();
        } else if (event.getSource() == exportJSONBtn || event.getSource() == exportCSVBtn) {
            exports(event);
        }
    }

    /**
     * Initializes the project view with the tree table with values and sets stats on the screen.
     */
    @FXML
    public void initTables() {
        List<Integer> projectsArray;
        tasksColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        projectsColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("title"));
        projectsTreeView.setRoot(root);
        projectsArray = listener.getProjects();
        listener.setProjectsTable(projectsArray, root);
        overallViewBtn.setDisable(false);
        projectViewBtn.setDisable(true);
    }

    /**
     * Initializes the overall view, sets stats and charts.
     */
    public void initOverallStats() {
        List<Integer> infos = listener.countOverallStats();
        displayBasicStats(infos);
        tasksChart.setLegendVisible(true);
        pieChartInitializer();
        barChartInitializer();
        overallViewBtn.setDisable(true);
        projectViewBtn.setDisable(false);
    }

    /**
     * Initializes the pie chart that shows projects by their time.
     */
    public void pieChartInitializer() {
        List<Integer> projects = listener.getProjects();
        if (projects != null) {
            ObservableList<PieChart.Data> datas = FXCollections.observableArrayList();
            for (int i = 0; i < projects.size(); i++) {
                projectsChart.setPrefWidth(513 + (10 * i));
                projectsChart.setPrefHeight(322 + (4 * i));
                pieChartAnchorPane.setPrefWidth(513 + (10 * i));
                pieChartAnchorPane.setPrefHeight(322 + (4 * i));
                Project project = listener.getProjectsFromID(projects.get(i));
                if (project != null) {
                    datas.add(i, new PieChart.Data(project.getTitle(), project.getDuration()));
                }
            }
            projectsChart.setData(datas);
        }
    }

    /**
     * Initializes the bar chart that shows the number of tasks of projects.
     */
    public void barChartInitializer() {
        XYChart.Series<String, Integer> series = new XYChart.Series<>();

        List<Integer> projects = listener.getProjects();
        if (projects != null) {
            int numberOfProjects = 0;
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
    public void onProjectSelected() {
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
    public void onTaskSelected() {
        Task selectedTask = getSelectedTask();
        if (selectedTask == null) {
            taskEndDateLabel.setText("?/?/?");
        } else {
            taskEndDateLabel.setText(listener.dateToString(selectedTask.getEndDate()));
        }
    }


    /**
     * Gets the selected project in the tree table.
     *
     * @return Project, the selected project on the tree table.
     */
    public Project getSelectedProject() {
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
    public Task getSelectedTask() {
        if (tasksTableView.getSelectionModel().getSelectedItem() != null) {
            return tasksTableView.getSelectionModel().getSelectedItem();
        }
        return null;
    }

    /**
     * Resets the texts in labels in statistics.
     */
    public void resetTextLabel() {
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
    public void expandRoot(TreeItem<Project> root) {
        root.setExpanded(true);
    }

    /**
     * Adds a child to the related parent in the TreeTableView.
     *
     * @param parent The project's parent
     * @param child  The project's child
     */
    public void addChild(TreeItem<Project> parent, TreeItem<Project> child) {
        parent.getChildren().add(child);
    }

    /**
     * Executes the right export method.
     *
     * @param event ActionEvent
     */
    public void exports(ActionEvent event) {
        String fileName = fileNameTextField.getText();
        if (fileName.equals("")) {
            fileName = "Statistics";
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("src"));
        File selectedDirectory = directoryChooser.showDialog(new Stage());
        if (selectedDirectory != null) {
            String separator = determineOS(selectedDirectory.getAbsolutePath());
            if (event.getSource() == exportJSONBtn) {
                listener.exportStatsAsJson(separator + fileName + ".json", selectedDirectory.getAbsolutePath());
            } else if (event.getSource() == exportCSVBtn) {
                listener.exportStatsAsCSV(separator + fileName + ".csv", selectedDirectory.getAbsolutePath());
            }
        }
    }

    /**
     * Determines the OS for the absolute path thanks to the separator.
     *
     * @param absolutePath The absolute path of the directory selected.
     * @return The separator ("\" or "/") in the path according to the OS.
     */
    public String determineOS(String absolutePath) {
        String separator = "/";
        if (!absolutePath.contains(separator)) {
            separator = "\\";
        }
        return separator;
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

    /**
     * Communicates to the controller which button has been clicked
     */
    public interface ViewListener {
        /**
         * Displays the scene
         */
        void show();

        /**
         * Shows the selected project's scene
         */
        void showIndividualStats();

        /**
         * Back to the previous scene
         */
        void onBackButtonClicked();

        /**
         * Fetches the user's projects
         *
         * @return A list containing the IDs of the projects
         */
        List<Integer> getProjects();

        /**
         * Counts the number of tasks of a project
         *
         * @param projectId The project ID
         * @return The number of tasks
         */
        Integer countTasksOfAProject(int projectId);

        /**
         * Retrieves to project
         *
         * @param id The project ID
         * @return The project data object
         */
        Project getProjectsFromID(int id);

        /**
         * Populates the table with the project titles
         *
         * @param projects The list of project IDs
         * @param root     The item in the table
         */
        void setProjectsTable(List<Integer> projects, TreeItem<Project> root);

        /**
         * Initializes the task table
         *
         * @param selectedProject The project who's tasks are to be shown
         * @return The list of tasks
         */
        ObservableList<Task> setTasksTable(Project selectedProject);

        /**
         * Exportation in JSON format
         *
         * @param fileName The name of the file
         * @param path     The path where it will be saved
         */
        void exportStatsAsJson(String fileName, String path);

        /**
         * Exportation in CSV format
         *
         * @param fileName The name of the file
         * @param path     The path where it will be saved
         */
        void exportStatsAsCSV(String fileName, String path);

        /**
         * Counts the number of collaborators, tasks and projects across all projects
         *
         * @return a list containing the number of each statistic
         */
        List<Integer> countOverallStats();

        /**
         * Counts the number of collaborators and tasks of a selected project
         *
         * @param selectedProject the selected project
         * @return A list with the statistics
         */
        List<Integer> countIndividualProjectStats(Project selectedProject);

        /**
         * Converts a date to string format
         *
         * @param date The date in long format
         * @return The date in string format
         */
        String dateToString(Long date);

    }
}
