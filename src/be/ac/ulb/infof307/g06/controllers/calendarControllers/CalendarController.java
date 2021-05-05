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

    public CalendarController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene) {
        super(user_db, project_db, stage, scene);
    }

    public void initCalendar() {
        try {
            List<Integer> userProjects = project_db.getUserProjects(user_db.getCurrentUser().getId());
            ObservableList<String> projects = FXCollections.observableArrayList();
            for (Integer project : userProjects) {
                projects.add(project_db.getProject(project).getTitle());
            }

            Map<String, String> allProjects = database.getProjects();
            viewController.initComboBox(projects, allProjects);
            for (String project : allProjects.keySet()) {
                Project currentProject = project_db.getProject(project_db.getProjectID(project));
                createEntry(projectSource,
                        project,
                        LocalDate.ofEpochDay(currentProject.getStartDate()),
                        LocalDate.ofEpochDay(currentProject.getEndDate()),
                        allProjects.get(project),
                        project
                );
                projectsColor.put(project, allProjects.get(project));
                for (Task task : project_db.getTasks(currentProject.getId())) {
                    createEntry(taskSource,
                            task.getDescription(),
                            LocalDate.ofEpochDay(task.getStartDate()),
                            LocalDate.ofEpochDay(task.getEndDate()),
                            allProjects.get(project),
                            currentProject.getTitle()
                    );
                }
            }
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }
    }

    public void createEntry(CalendarSource source, String name, LocalDate start, LocalDate end, String color, String projectName) {
        Entry<String> testEntry = new Entry<>(name);
        testEntry.changeStartDate(start);
        testEntry.changeEndDate(end);
        testEntry.setFullDay(true);
        Calendar newCalendar = new Calendar(name);
        if (source.getName().equals("projects")) {
            if (projectsMap.containsKey(name)) {
                projectsMap.get(name).addEntry(testEntry);
            } else {

                newCalendar.setStyle(color);
                source.getCalendars().add(newCalendar);
                newCalendar.addEntry(testEntry);
                projectsMap.put(name, newCalendar);
            }
        } else {
            if (tasksMap.containsKey(projectName)) {
                tasksMap.get(projectName).addEntry(testEntry);
            } else {
                newCalendar.setStyle(color);
                source.getCalendars().add(newCalendar);
                newCalendar.addEntry(testEntry);
                tasksMap.put(projectName, newCalendar);
            }
        }
    }

    @Override
    public void show() throws SQLException {
        projectSource = new CalendarSource("projects");
        taskSource = new CalendarSource("tasks");
        FXMLLoader loader = new FXMLLoader(CalendarViewController.class.getResource("CalendarView.fxml"));
        try {
            database = new CalendarDB("Database.db");
            scene = new Scene(loader.load());
        } catch (IOException | ClassNotFoundException e) {
            new AlertWindow("Error", "" + e).errorWindow();
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

    public void onProjectSelected(ObservableSet<Entry<?>> projects) {
        ObservableList<Entry<?>> entry = FXCollections.observableArrayList(projects);
        selectedProject = entry.get(0);
        viewController.setColor(entry.get(0).getCalendar().getStyle());
    }

    public void clearCalendarEvents() {
        for (int i = 0; i < projectSource.getCalendars().size(); i++) {
            projectSource.getCalendars().get(i).clear();
        }
        for (int i = 0; i < taskSource.getCalendars().size(); i++) {
            taskSource.getCalendars().get(i).clear();
        }
    }

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
            } catch (SQLException e) {
                new AlertWindow("Error", "" + e).errorWindow();
            }

        }
    }

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

    @Override
    public LocalDate getCurrentDate() {
        return currentDate;
    }

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
                createEntry(projectSource,
                        currentProject.getTitle(),
                        LocalDate.ofEpochDay(currentProject.getStartDate()),
                        LocalDate.ofEpochDay(currentProject.getEndDate()),
                        color,
                        currentProject.getTitle()
                );
                database.addProject(currentProject.getTitle(), color);
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
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }

    }


    @Override
    public void back() {
        super.back();
    }
}
