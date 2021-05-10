package be.ac.ulb.infof307.g06.controllers.calendarControllers;

import be.ac.ulb.infof307.g06.controllers.Controller;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.CalendarColor;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.models.database.CalendarDB;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.calendarViews.CalendarViewController;
import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Main controller class for the calendar's view. Handles the monthly and weekly view.
 */
public class CalendarController extends Controller implements CalendarViewController.ViewListener {
    CalendarColor colorObject = new CalendarColor();
    private CalendarViewController viewController;
    private CalendarSource projectSource;
    private CalendarSource taskSource;
    private Entry<?> selectedProject;
    private LocalDate currentDate = LocalDate.now();
    private final Map<String, String> projectsColor = new HashMap<>();
    private final Map<String, Calendar> projectsMap = new HashMap<>();
    private final Map<String, Calendar> tasksMap = new HashMap<>();
    private CalendarDB database;

    /**
     * Constructor.
     *
     * @param user_db UserDB, the user database
     * @param project_db ProjectDB, the projects database
     * @param stage Stage, a stage
     * @param scene Scene, a scene
     * @param DB_PATH String, the path to the database
     */
    public CalendarController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene, String DB_PATH) {
        super(user_db, project_db, stage, scene, DB_PATH);
    }

    /**
     * Initialises calendar
     */
    public void initCalendar() {
        try {
            List<Integer> userProjects = project_db.getUserProjects(user_db.getCurrentUser().getId());
            ObservableList<String> projects = FXCollections.observableArrayList();
            for (Integer project : userProjects) {
                projects.add(project_db.getProject(project).getTitle());
            }

            Map<String, String> allProjects = database.getProjects();
            viewController.initComboBox(projects, allProjects);
            loadProjects(allProjects);
        } catch (SQLException error) {
            new AlertWindow("Error", "" + error).showErrorWindow();
        }
    }

    /**
     * Adds all projects and tasks to calendar
     *
     * @param allProjects projects to insert in calendar
     * @throws SQLException exception
     */
    private void loadProjects(Map<String, String> allProjects) throws SQLException {
        for (String project : allProjects.keySet()) {
            projectsColor.put(project, allProjects.get(project));
            Project currentProject = project_db.getProject(project_db.getProjectID(project));
            insertProjectInCalendar(currentProject, allProjects.get(project));
        }
    }

    /**
     * Inserts entry in calendar
     *
     * @param source      Calendar source
     * @param name        Entry name
     * @param start       Entry start time
     * @param end         Entry end time
     * @param color       Entry color string
     * @param projectName Project's name
     */
    public void createEntry(CalendarSource source, String name, LocalDate start, LocalDate end, String color, String projectName) {
        if (source.getName().equals("projects")) {
            finaliseEntryCreation(source, name, start, end, color, projectsMap);
        } else {
            finaliseEntryCreation(source, projectName, start, end, color, tasksMap);
        }
    }

    /**
     * @param source Calendar source
     * @param name   Entry name
     * @param start  Entry start time
     * @param end    Entry end time
     * @param color  Entry color string
     * @param map    Map to insert entry
     */
    private void finaliseEntryCreation(CalendarSource source, String name, LocalDate start, LocalDate end, String color, Map<String, Calendar> map) {
        Entry<String> testEntry = new Entry<>(name);
        testEntry.changeStartDate(start);
        testEntry.changeEndDate(end);
        testEntry.setFullDay(true);
        Calendar newCalendar = new Calendar(name);
        if (map.containsKey(name)) {
            map.get(name).addEntry(testEntry);
        } else {
            newCalendar.setStyle(color);
            source.getCalendars().add(newCalendar);
            newCalendar.addEntry(testEntry);
            map.put(name, newCalendar);
        }
    }

    /**
     * Shows calendar menu
     *
     * @throws SQLException exception
     */
    @Override
    public void show() throws SQLException {
        projectSource = new CalendarSource("projects");
        taskSource = new CalendarSource("tasks");
        FXMLLoader loader = new FXMLLoader(CalendarViewController.class.getResource("CalendarView.fxml"));
        try {
            database = new CalendarDB("Database.db");
            scene = new Scene(loader.load());
        } catch (IOException | ClassNotFoundException error) {
            new AlertWindow("Error", "" + error).showErrorWindow();
        }
        viewController = loader.getController();
        viewController.setListener(this);
        stage.setScene(scene);
        stage.sizeToScene();
        viewController.init(projectSource, taskSource);
        viewController.fillColors(colorObject);
        viewController.setNewDate(currentDate);
        initCalendar();
    }

    /**
     * Sets combobox color on project selected
     *
     * @param projects list of selected projects
     */
    public void onProjectSelected(ObservableSet<Entry<?>> projects) {
        ObservableList<Entry<?>> entry = FXCollections.observableArrayList(projects);
        selectedProject = entry.get(0);
        viewController.setColor(entry.get(0).getCalendar().getStyle());
    }

    /**
     * Clears all entries in calendars
     */
    public void clearCalendarEvents() {
        for (int i = 0; i < projectSource.getCalendars().size(); i++) {
            projectSource.getCalendars().get(i).clear();
        }
        for (int i = 0; i < taskSource.getCalendars().size(); i++) {
            taskSource.getCalendars().get(i).clear();
        }
    }

    /**
     * Changes the color of an entry
     *
     * @param color      color string
     * @param selections entry to change color
     */
    @Override
    public void changeColor(String color, ObservableSet<Entry<?>> selections) {
        ObservableList<Entry<?>> entry = FXCollections.observableArrayList(selections);
        if (entry.size() != 0) {
            Calendar calendar = selectedProject.getCalendar();
            calendar.setStyle(color);
            try {
                database.removeProject(selectedProject.getTitle());
                database.addProject(selectedProject.getTitle(), color);
                projectsColor.remove(selectedProject.getTitle());
                projectsColor.put(selectedProject.getTitle(), color);
                if (project_db.countTasks(project_db.getProjectID(selectedProject.getTitle())) != 0) {
                    tasksMap.get(selectedProject.getTitle()).setStyle(color);
                    calendar = tasksMap.get(selectedProject.getTitle());
                    calendar.setStyle(color);
                }
            } catch (SQLException error) {
                new AlertWindow("Error", "" + error).showErrorWindow();
            }

        }
    }

    /**
     * Moves view to next/previous week/month or today
     *
     * @param today   go to today
     * @param month   is current monthview
     * @param forward forward or backwards
     */
    @Override
    public void changeDate(boolean today, boolean month, boolean forward) {
        if (month) {
            currentDate = currentDate.plusMonths(forward ? 1 : -1);
        } else {
            currentDate = currentDate.plusWeeks(forward ? 1 : -1);
        }
        if (today) {
            currentDate = LocalDate.now();
        }
        viewController.setNewDate(currentDate);
    }

    /**
     * returns current date
     *
     * @return current date
     */
    @Override
    public LocalDate getCurrentDate() {
        return currentDate;
    }

    /**
     * adds selected project(s) to calendars
     *
     * @param projectsList List of selected projects in checkcombobox
     */
    @Override
    public void addProject(ObservableList<? extends String> projectsList) {
        try {
            clearCalendarEvents();
            database.emptyTable();
            for (String project : projectsList) {
                String color;
                if (projectsColor.containsKey(project)) {
                    color = projectsColor.get(project);
                } else {
                    Random rand = new Random();
                    color = colorObject.getAllColors().get(rand.nextInt(colorObject.getAllColors().size()));
                    projectsColor.put(project, color);
                }
                Project currentProject = project_db.getProject(project_db.getProjectID(project));
                database.addProject(currentProject.getTitle(), color);
                insertProjectInCalendar(currentProject, color);
            }
        } catch (SQLException error) {
            new AlertWindow("Error", "" + error).showErrorWindow();
        }

    }

    /**
     * Create an entry in the calendar with the selected project.
     *
     * @param currentProject Project to insert in calendar
     * @param color          Project Color string
     * @throws SQLException exception
     */
    private void insertProjectInCalendar(Project currentProject, String color) throws SQLException {
        createEntry(projectSource,
                currentProject.getTitle(),
                LocalDate.ofEpochDay(currentProject.getStartDate()),
                LocalDate.ofEpochDay(currentProject.getEndDate()),
                color,
                currentProject.getTitle()
        );
        for (Task task : project_db.getTasks(currentProject.getId())) {
            createEntry(taskSource,
                    task.getDescription(),
                    LocalDate.ofEpochDay(task.getStartDate()),
                    LocalDate.ofEpochDay(task.getEndDate()),
                    color,
                    currentProject.getTitle()
            );
        }
    }
}
