package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.CalendarViewController;
import be.ac.ulb.infof307.g06.views.projectViews.ProjectsViewController;
import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.AllDayView;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DayViewBase;
import com.calendarfx.view.DetailedWeekView;
import com.calendarfx.view.page.WeekPage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class CalendarController extends Controller implements CalendarViewController.ViewListener {
    private CalendarViewController viewController;
    private WeekPage page;
    private AllDayView view;

    public CalendarController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene) {
        super(user_db, project_db, stage, scene);
    }

    @Override
    public void show() throws IOException, SQLException {
        Calendar test = new Calendar("Week overview");
        Calendar days = new Calendar();
        Calendar currentDay = new Calendar();
        view = new AllDayView();

        CalendarSource source = new CalendarSource("calendars");
        source.getCalendars().setAll(test, days, currentDay);
        view.getCalendarSources().setAll(source);
        view.setAdjustToFirstDayOfWeek(false);
        view.setRowHeight(50);
        viewController = new CalendarViewController();
        currentDay.setStyle("orange");
        test.setStyle("olive");
        days.setStyle("white");
        viewController.setListener(this);
        scene = new Scene(view, 1515,940);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();

        for (int i = 0; i < 25; i++) {
            Entry<String> testEntry = new Entry<>("" + LocalDate.now().plusDays(i));
            testEntry.changeStartDate(LocalDate.now().plusDays(i));
            testEntry.changeEndDate(LocalDate.now().plusDays(i));
            testEntry.setFullDay(true);
            if (i == 0) {
                currentDay.addEntry(testEntry);
            } else {
                days.addEntry(testEntry);
            }
        }

        Entry<String> testEntry = new Entry<>("test");
        testEntry.changeStartDate(LocalDate.now());
        testEntry.changeEndDate(LocalDate.of(2021, 4, 16));
        testEntry.setFullDay(true);
        test.addEntry(testEntry);
        Entry<String> testEntry2 = new Entry<>("tes2");
        testEntry2.changeStartDate(LocalDate.of(2021, 4, 15));
        testEntry2.changeEndDate(LocalDate.of(2021, 4, 18));
        testEntry2.setFullDay(true);

        testEntry2.getStyleClass().setAll("style5");
        test.addEntry(testEntry2);

    }
}
