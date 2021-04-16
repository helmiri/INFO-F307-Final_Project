package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.CalendarViewController;
import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.AllDayView;
import com.calendarfx.view.page.WeekPage;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class CalendarController extends Controller implements CalendarViewController.ViewListener {
    private CalendarViewController viewController;
    private WeekPage page;
    private AllDayView view;

    public CalendarController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene) {
        super(user_db, project_db, stage, scene);
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
        view = new AllDayView();
        CalendarSource source = new CalendarSource("calendars");
        view.getCalendarSources().setAll(source);
        view.setAdjustToFirstDayOfWeek(false);
        view.setRowHeight(50);
        viewController = new CalendarViewController();
        viewController.setListener(this);
        scene = new Scene(view, 1515, 940);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
        createEntry(source, "test1", LocalDate.now(), LocalDate.now().plusDays(5), "blue");
        createEntry(source, "test2", LocalDate.now().plusDays(2), LocalDate.now().plusDays(3), "red");

    }
}
