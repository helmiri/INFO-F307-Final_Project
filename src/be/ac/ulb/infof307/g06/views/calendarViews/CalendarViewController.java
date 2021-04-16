package be.ac.ulb.infof307.g06.views;

import be.ac.ulb.infof307.g06.models.CalendarColor;
import com.calendarfx.view.AllDayView;
import com.calendarfx.view.WeekDayHeaderView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
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

    public void setColor(String selectedColor) {
        for (int i = 0; i < colorsComboBox.getItems().size(); i++) {
            if (colorsComboBox.getItems().get(i).equals(selectedColor)) {
                colorsComboBox.getSelectionModel().select(i);
            }
        }
    }

    public void fillColors(CalendarColor colorObject) {
        colorsComboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {

            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        if (item != null) {
                            super.updateItem(item, empty);
                            setText(item);
                            Color color = Color.decode(colorObject.getHex(item));
                            String adjustedColor;
                            if (color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114 > 186) {
                                adjustedColor = "#000000";
                            } else {
                                adjustedColor = "#ffffff";
                            }
                            setStyle("-fx-text-fill: " + adjustedColor + "; -fx-background-color: " + item + ";");
                        }
                    }
                };
            }
        });
        colorsComboBox.setButtonCell(colorsComboBox.getCellFactory().call(null));
        colorsComboBox.setItems(colorObject.getAllColors());

    }

    @FXML
    public void onColorSelected() {
        listener.changeColor(colorsComboBox.getSelectionModel().getSelectedItem());
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

        void addProject(ObservableList<? extends String> projects);

        void goToday();

        void changeColor(String color);
    }

}
