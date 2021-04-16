package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.CalendarColor;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;

import be.ac.ulb.infof307.g06.views.calendarViews.CalendarViewController;
import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.AllDayView;
import com.calendarfx.view.DateControl;
import com.calendarfx.view.WeekDayHeaderView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.junit.platform.commons.util.CollectionUtils;

import javax.naming.Context;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class CalendarController extends Controller implements CalendarViewController.ViewListener {
    CalendarColor colorObject = new CalendarColor();
    private CalendarViewController viewController;
    private AllDayView projects;
    private AllDayView tasks;
    private CalendarSource projectSource;
    private CalendarSource taskSource;
    private WeekDayHeaderView header;
    private Entry<?> selectedProject;
    private LocalDate currentDate = LocalDate.now();
    private ObservableList<String> currentProjects = FXCollections.observableArrayList();
    private String selectedColor;
    private Map<String, String> projectsColor = new HashMap<>();
    private Map<String, Calendar> calendarMap = new HashMap<>();

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
            viewController.initComboBox(projects);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void createEntry(CalendarSource source, String name, LocalDate start, LocalDate end, String color) {
        Entry<String> testEntry = new Entry<>(name);
        testEntry.changeStartDate(start);
        testEntry.changeEndDate(end);
        testEntry.setFullDay(true);
        if (calendarMap.containsKey(color)) {
            calendarMap.get(color).addEntry(testEntry);
        } else {
            Calendar newCalendar = new Calendar();
            newCalendar.setStyle(color);
            source.getCalendars().add(newCalendar);
            newCalendar.addEntry(testEntry);
            calendarMap.put(color, newCalendar);
        }


    }

    @Override
    public void show() throws IOException, SQLException {
        projectSource = new CalendarSource("calendars");
        taskSource = new CalendarSource("tasks");

        FXMLLoader loader = new FXMLLoader(CalendarViewController.class.getResource("CalendarView.fxml"));
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        viewController = loader.getController();
        viewController.setListener(this);
        stage.setScene(scene);
        stage.sizeToScene();
        projects = viewController.getProjectsView();
        tasks = viewController.getTasksView();
        projects.setEntryFactory(param -> null);
        projects.setEntryContextMenuCallback(new Callback<DateControl.EntryContextMenuParameter, ContextMenu>() {
            @Override
            public ContextMenu call(DateControl.EntryContextMenuParameter entryContextMenuParameter) {
                return null;
            }
        });
        projects.setEntryDetailsCallback(new Callback<DateControl.EntryDetailsParameter, Boolean>() {
            @Override
            public Boolean call(DateControl.EntryDetailsParameter entryDetailsParameter) {
                onProjectSelected();
                return null;
            }
        });
        projects.setContextMenuCallback(null);
        projects.setOnMouseClicked(null);

        header = viewController.getWeekDays();
        projects.getCalendarSources().setAll(projectSource);
        tasks.getCalendarSources().setAll(taskSource);
        projects.setRowHeight(50);

        projects.setAdjustToFirstDayOfWeek(true);
        tasks.setAdjustToFirstDayOfWeek(true);

        viewController.fillColors(colorObject);
        initCalendar();
    }


    public void onProjectSelected() {
        ObservableList<Entry<?>> entry = FXCollections.observableArrayList(projects.getSelections());
        selectedProject = entry.get(0);
        viewController.setColor(entry.get(0).getCalendar().getStyle());
    }

    @Override
    public void changeColor(String color) {
        selectedProject.getCalendar().setStyle(color);
        projects.refreshData();
    }

    @Override
    public void nextWeek() {

        currentDate = currentDate.plusDays(7);
        header.setDate(currentDate);
        projects.setDate(currentDate);
        tasks.setDate(currentDate);
    }

    @Override
    public void prevWeek() {
        currentDate = currentDate.minusDays(7);
        header.setDate(currentDate);
        projects.setDate(currentDate);
        tasks.setDate(currentDate);
    }

    @Override
    public void addProject(ObservableList<? extends String> projectsList) {
        try {
            projectSource.getCalendars().clear();
            taskSource.getCalendars().clear();
            projects.refreshData();
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
                createEntry(projectSource, currentProject.getTitle(), LocalDate.ofEpochDay(currentProject.getStartDate()), LocalDate.ofEpochDay(currentProject.getEndDate()), color);
            }

        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }
    }

    @Override
    public void goToday() {
        currentDate = LocalDate.now();
        header.setDate(currentDate);
        projects.setDate(currentDate);
        tasks.setDate(currentDate);
    }
}
