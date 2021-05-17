package be.ac.ulb.infof307.g06.controllers.calendarControllers;

import be.ac.ulb.infof307.g06.controllers.Controller;
import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.exceptions.WindowLoadException;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.CalendarColor;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.models.database.ActiveUser;
import be.ac.ulb.infof307.g06.models.database.CalendarDB;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.views.calendarViews.CalendarViewController;
import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
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
    final CalendarColor colorObject = new CalendarColor();
    private CalendarViewController viewController;
    private CalendarSource projectSource;
    private CalendarSource taskSource;
    private Entry<?> selectedProject;
    private LocalDate currentDate = LocalDate.now();
    private final Map<String, String> projectsColor = new HashMap<>();
    private final Map<String, Calendar> projectsMap = new HashMap<>();
    private final Map<String, Calendar> tasksMap = new HashMap<>();
    private CalendarDB calendarDB;
    private ProjectDB projectDB;

    /**
     * Constructor.
     *
     * @param stage Stage, a stage
     */
    public CalendarController(Stage stage) throws DatabaseException {
        super(stage);
        try {
            projectDB = new ProjectDB();
            calendarDB = new CalendarDB();
        } catch (SQLException error) {
            throw new DatabaseException(error);
        }

    }

    /**
     * Initialises calendar
     */
    public void initCalendar() {
        ActiveUser activeUser = ActiveUser.getInstance();
        try {
            List<Integer> userProjects = projectDB.getUserProjects(activeUser.getID());
            ObservableList<String> projects = FXCollections.observableArrayList();
            for (Integer project : userProjects) {
                projects.add(projectDB.getProject(project).getTitle());
            }

            Map<String, String> allProjects = calendarDB.getProjects();
            viewController.initComboBox(projects, allProjects);
            loadProjects(allProjects);
        } catch (SQLException error) {
            new DatabaseException(error).show();
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
            Project currentProject = projectDB.getProject(projectDB.getProjectID(project));
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
            finaliseEntryCreation(source, name, start, end, color, projectsMap, projectName);
        } else {
            finaliseEntryCreation(source, name, start, end, color, tasksMap, projectName);
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
    private void finaliseEntryCreation(CalendarSource source, String name, LocalDate start, LocalDate end, String color, Map<String, Calendar> map, String projectName) {
        Entry<String> testEntry = new Entry<>(name);
        testEntry.changeStartDate(start);
        testEntry.changeEndDate(end);
        testEntry.setFullDay(true);
        Calendar newCalendar = new Calendar(name);
        if (map.containsKey(projectName)) {
            map.get(projectName).addEntry(testEntry);
        } else {
            newCalendar.setStyle(color);
            source.getCalendars().add(newCalendar);
            newCalendar.addEntry(testEntry);
            map.put(projectName, newCalendar);
        }
    }

    /**
     * Shows calendar menu
     */
    @Override
    public void show() throws WindowLoadException {
        try {
            viewController = (CalendarViewController) loadView(CalendarViewController.class, "CalendarView.fxml");
        } catch (IOException error) {
            throw new WindowLoadException(error);
        }
        projectSource = new CalendarSource("projects");
        taskSource = new CalendarSource("tasks");
        viewController.setListener(this);
        viewController.show(projectSource, taskSource, stage);
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
                calendarDB.removeProject(selectedProject.getTitle());
                calendarDB.addProject(selectedProject.getTitle(), color);
                projectsColor.remove(selectedProject.getTitle());
                projectsColor.put(selectedProject.getTitle(), color);
                if (projectDB.countTasks(projectDB.getProjectID(selectedProject.getTitle())) != 0) {
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
     * @param month   is current month view
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
     * @param projectsList List of selected projects in checkComboBox
     */
    @Override
    public void addProject(ObservableList<? extends String> projectsList) {
        try {
            clearCalendarEvents();
            calendarDB.emptyTable();
            for (String project : projectsList) {
                String color;
                if (projectsColor.containsKey(project)) {
                    color = projectsColor.get(project);
                } else {
                    Random rand = new Random();
                    color = colorObject.getAllColors().get(rand.nextInt(colorObject.getAllColors().size()));
                    projectsColor.put(project, color);
                }
                Project currentProject = projectDB.getProject(projectDB.getProjectID(project));
                calendarDB.addProject(currentProject.getTitle(), color);
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
        for (Task task : projectDB.getTasks(currentProject.getId())) {
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
