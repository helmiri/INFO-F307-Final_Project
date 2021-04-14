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


        view = new AllDayView();

        CalendarSource source = new CalendarSource("calendars");
        source.getCalendars().setAll(test);
        view.getCalendarSources().setAll(source);

        view.setAdjustToFirstDayOfWeek(false);

        viewController = new CalendarViewController();

        viewController.setListener(this);

        scene = new Scene(view);
        stage.setScene(scene);
        stage.show();

        for (int i = 1; i < 25; i++) {
            Entry<String> testEntry = new Entry<>("" + LocalDate.of(2021, 4, i));
            testEntry.changeStartDate(LocalDate.of(2021, 4, i));
            testEntry.changeEndDate(LocalDate.of(2021, 4, i));
            testEntry.setFullDay(true);
            test.addEntry(testEntry);
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
        test.addEntry(testEntry2);

    }
}
