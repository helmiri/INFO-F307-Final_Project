package be.ac.ulb.infof307.g06.controllers;

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

import javax.naming.Context;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class CalendarController extends Controller implements CalendarViewController.ViewListener {
    private CalendarViewController viewController;
    private AllDayView projects;
    private AllDayView tasks;
    private CalendarSource projectSource;
    private CalendarSource taskSource;
    private WeekDayHeaderView header;
    private String selectedProject;
    private LocalDate currentDate = LocalDate.now();//
    private ComboBox<String> colorsComboBox;
    private String selectedColor;

    public CalendarController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene) {
        super(user_db, project_db, stage, scene);
    }

    public void initCalendar() {

    }

    public void createEntry(CalendarSource source, String name, LocalDate start, LocalDate end, String color) {
        Calendar newCalendar = new Calendar();
        newCalendar.setStyle(color);
        source.getCalendars().add(newCalendar);
        Entry<String> testEntry = new Entry<>(name);
        testEntry.changeStartDate(start);
        testEntry.changeEndDate(end);
        testEntry.setFullDay(true);
        newCalendar.addEntry(testEntry);
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
        createEntry(projectSource, "project1", LocalDate.now().minusDays(2), LocalDate.now().plusDays(20), "blue");
        createEntry(taskSource, "task1", LocalDate.now().minusDays(20), LocalDate.now(), "blue");

    }

    public void fillColors() {
        ObservableList<String> colors = FXCollections.observableArrayList("gainsboro", "silver", "darkgray", "gray", "dimgray ", "black", "whitesmoke", "lightgray", "lightcoral", "rosybrown", "indianred", "red", "maroon", "snow", "mistyrose", "salmon", "orangered");
        colorsComboBox.setItems(colors);
    }

    public void onProjectSelected() {
        ObservableList<Entry<?>> entry = FXCollections.observableArrayList(projects.getSelections());
        selectedProject = entry.get(0).getTitle();
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
    public void goToday() {
        currentDate = LocalDate.now();
        header.setDate(currentDate);
        projects.setDate(currentDate);
        tasks.setDate(currentDate);
    }
}
