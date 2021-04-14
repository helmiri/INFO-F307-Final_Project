package be.ac.ulb.infof307.g06.views;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.CalendarView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;

public class CalendarViewController {
    private ViewListener listener;

    public void showCalendar(Stage primaryStage) {


    }


    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    public interface ViewListener {

    }

}
