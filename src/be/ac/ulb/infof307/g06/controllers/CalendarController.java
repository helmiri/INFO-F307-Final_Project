package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;

import be.ac.ulb.infof307.g06.views.CalendarViewController;
import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.AllDayView;
import com.calendarfx.view.WeekDayHeaderView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class CalendarController extends Controller implements CalendarViewController.ViewListener {
    private CalendarViewController viewController;
    private AllDayView allDay;
    private CalendarSource source;
    private WeekDayHeaderView header;
    private LocalDate currentDate;

    public CalendarController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene) {
        super(user_db, project_db, stage, scene);
    }

    public void initCalendar() {

    }

    public void createEntry(String name, LocalDate start, LocalDate end, String color) {
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
        source = new CalendarSource("calendars");


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
        allDay = (AllDayView) scene.lookup("allDayViewID");
        allDay.getCalendarSources().setAll(source);
        allDay.setAdjustToFirstDayOfWeek(false);
        allDay.setRowHeight(50);
        header = (WeekDayHeaderView) scene.lookup("weekDays");
        createEntry("test", LocalDate.now(), LocalDate.now(), "blue");
    }

    @Override
    public void nextWeek() {
        header.setDate(currentDate.plusDays(7));
        allDay.setDate(currentDate.plusDays(7));
    }

    @Override
    public void prevWeek() {
        header.setDate(currentDate.minusDays(7));
        allDay.setDate(currentDate.minusDays(7));
    }

    @Override
    public void goToday() {
        header.setDate(LocalDate.now());
        allDay.setDate(LocalDate.now());
    }
}
