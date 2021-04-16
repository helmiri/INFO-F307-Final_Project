package be.ac.ulb.infof307.g06.views;

import com.calendarfx.view.AllDayView;
import com.calendarfx.view.WeekDayHeaderView;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

public class CalendarViewController {
    @FXML
    private AllDayView tasks;
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
    private AllDayView projects;
    @FXML
    private CheckComboBox<?> projectComboBox;

    private ViewListener listener;

    public void showCalendar(Stage primaryStage) {


    }

    public void setColor(String color) {
        colorsComboBox.setValue(color);
        System.out.println(color);
    }

    public void fillColors(ObservableList<String> colors) {
        colorsComboBox.setItems(colors);
    }

    @FXML
    private void calendarEvents(ActionEvent event) {
        if (event.getSource() == previousWeekBtn) {
            listener.prevWeek();
        } else if (event.getSource() == todayBtn) {
            listener.goToday();
        } else if (event.getSource() == nextWeekBtn) {
            listener.nextWeek();
        }
    }

    public AllDayView getProjectsView() {
        return projects;
    }

    public AllDayView getTasksView() {
        return tasks;
    }

    public WeekDayHeaderView getWeekDays() {
        return weekDays;
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
