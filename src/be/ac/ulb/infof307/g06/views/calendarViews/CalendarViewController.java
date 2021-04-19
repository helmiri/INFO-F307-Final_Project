package be.ac.ulb.infof307.g06.views.calendarViews;

import be.ac.ulb.infof307.g06.models.CalendarColor;
import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.AllDayView;
import com.calendarfx.view.WeekDayHeaderView;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Callback;
import org.controlsfx.control.CheckComboBox;

import java.awt.*;
import java.time.LocalDate;
import java.util.Map;

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
    private AllDayView projects;
    @FXML
    private CheckComboBox<String> projectComboBox;
    @FXML
    private ComboBox<String> colorsComboBox;
    @FXML
    private Label monthLabel;

    private CalendarViewController.ViewListener listener;

    public void initComboBox(ObservableList<String> projects, Map<String, String> allProjects) {
        projectComboBox.getItems().addAll(projects);
        for (String project : allProjects.keySet()) {
            projectComboBox.getCheckModel().check(project);
        }
        projectComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) change -> addProject(change.getList()));
    }

    public void addProject(ObservableList<? extends String> list) {
        listener.addProject(list);
        projects.refreshData();
        tasks.refreshData();
    }

    public void setColor(String selectedColor) {
        for (int i = 0; i < colorsComboBox.getItems().size(); i++) {
            if (colorsComboBox.getItems().get(i).equals(selectedColor)) {
                colorsComboBox.getSelectionModel().select(i);
            }
        }
    }

    public void fillColors(CalendarColor colorObject) {
        colorsComboBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
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

    public void init(CalendarSource projectSource, CalendarSource taskSource) {
        projects.setEntryFactory(param -> null);
        projects.setEntryContextMenuCallback(entryContextMenuParameter -> null);
        projects.setEntryDetailsCallback(entryDetailsParameter -> {
            listener.onProjectSelected(projects.getSelections());
            return null;
        });
        projects.setContextMenuCallback(null);
        projects.setOnMouseClicked(null);
        projects.getCalendarSources().setAll(projectSource);
        tasks.getCalendarSources().setAll(taskSource);
        projects.setRowHeight(50);
        projects.setShowToday(false);
        tasks.setShowToday(false);
        projects.setAdjustToFirstDayOfWeek(true);
        tasks.setAdjustToFirstDayOfWeek(true);
    }

    @FXML
    public void onColorSelected() {
        listener.changeColor(colorsComboBox.getSelectionModel().getSelectedItem(), projects.getSelections());
        tasks.refreshData();
        projects.refreshData();
    }

    public void setNewDate(LocalDate date) {
        monthLabel.setText(date.getMonth() + " " + date.getYear());
        weekDays.setDate(date);
        projects.setDate(date);
        tasks.setDate(date);
    }

    @FXML
    private void calendarEvents(ActionEvent event) {

        if (event.getSource() == previousWeekBtn) {
            listener.prevWeek();
        } else if (event.getSource() == todayBtn) {
            listener.goToday();
        } else if (event.getSource() == nextWeekBtn) {
            listener.nextWeek();
        } else if (event.getSource() == backBtn) {
            listener.back();

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
        void back();

        void nextWeek();

        void onProjectSelected(ObservableSet<Entry<?>> selections);

        void prevWeek();

        void addProject(ObservableList<? extends String> projects);

        void goToday();

        void changeColor(String color, ObservableSet<Entry<?>> selections);
    }

}
