package be.ac.ulb.infof307.g06.views;

import com.calendarfx.view.AllDayView;
import com.calendarfx.view.WeekDayHeaderView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

public class CalendarViewController {
    @FXML
    private Button backBtn;
    @FXML
    private Button previousWeekBtn;
    @FXML
    private Button todayBtn;
    @FXML
    private Button nextWeekBtn;
    @FXML
    private WeekDayHeaderView weekDays;
    @FXML
    private AnchorPane anchorPaneDayView;
    @FXML
    private AllDayView allDayViewID;
    @FXML
    private CheckComboBox<?> projectComboBox;

    private ViewListener listener;

    public void showCalendar(Stage primaryStage) {


    }


    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    public interface ViewListener {
        void nextWeek();

        void prevWeek();

        void goToday();
    }

}
