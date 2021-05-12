package be.ac.ulb.infof307.g06.views.calendarViews;

import be.ac.ulb.infof307.g06.models.CalendarColor;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.AllDayView;
import com.calendarfx.view.WeekDayHeaderView;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.CheckComboBox;

import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * The calendar window
 */
public class CalendarViewController {
    //-------------- ATTRIBUTES -------------
    @FXML
    private AnchorPane layout;
    @FXML
    private Line line1;
    @FXML
    private Line line2;
    @FXML
    private Line line3;
    @FXML
    private Line line4;
    @FXML
    private Line line5;
    @FXML
    private Line line6;
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
    private Button viewBtn;
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
    @FXML
    private VBox monthViewBox;
    @FXML
    private VBox weekViewBox;
    @FXML
    private WeekDayHeaderView monthHeader1;
    @FXML
    private AllDayView monthWeek1;
    @FXML
    private WeekDayHeaderView monthHeader2;
    @FXML
    private AllDayView monthWeek2;
    @FXML
    private WeekDayHeaderView monthHeader3;
    @FXML
    private AllDayView monthWeek3;
    @FXML
    private WeekDayHeaderView monthHeader4;
    @FXML
    private AllDayView monthWeek4;
    @FXML
    private WeekDayHeaderView monthHeader5;
    @FXML
    private AllDayView monthWeek5;
    private boolean isMonthView;
    private CalendarViewController.ViewListener listener;
    private ArrayList<WeekDayHeaderView> monthHeaders;
    private ArrayList<AllDayView> monthDayViews;
    private ArrayList<Line> weekDaysSeparators;
    //-------------- METHODS -------------

    /**
     * Initializes the combo box with all the projects.
     *
     * @param projects    ObservableList that contains projects titles.
     * @param allProjects Map that contains selected projects and their color in the calendar.
     */
    public void initComboBox(ObservableList<String> projects, Map<String, String> allProjects) {
        projectComboBox.getItems().addAll(projects);
        for (String project : allProjects.keySet()) {
            projectComboBox.getCheckModel().check(project);
        }
        projectComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) change -> addProject(change.getList()));
    }

    /**
     * Adds selected projects in the calendar view.
     *
     * @param list ObservableList which contains the selected projects
     */
    public void addProject(ObservableList<? extends String> list) {
        listener.addProject(list);
        projects.refreshData();
        tasks.refreshData();
    }

    /**
     * Set the colors combo box with the color of the selected project.
     *
     * @param selectedColor String, color of the selected project.
     */
    public void setColor(String selectedColor) {
        for (int i = 0; i < colorsComboBox.getItems().size(); i++) {
            if (colorsComboBox.getItems().get(i).equals(selectedColor)) {
                colorsComboBox.getSelectionModel().select(i);
            }
        }
    }

    /**
     * Fills colors in the colors combo box.
     *
     * @param colorObject CalendarColor, colors of the calendar
     */
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

    /**
     * Initializes the calendar.
     *
     * @param projectSource CalendarSource, calendar object for projects
     * @param taskSource    CalendarSource, calendar object for tasks
     * @param stage         Tha application window
     */
    public void show(CalendarSource projectSource, CalendarSource taskSource, Stage stage) {
        stage.setScene(new Scene(layout));
        isMonthView = false;
        monthHeaders = new ArrayList<>(Arrays.asList(monthHeader1, monthHeader2, monthHeader3, monthHeader4, monthHeader5));
        monthDayViews = new ArrayList<>(Arrays.asList(monthWeek1, monthWeek2, monthWeek3, monthWeek4, monthWeek5));
        weekDaysSeparators = new ArrayList<>(Arrays.asList(line1, line2, line3, line4, line5, line6));
        projects.setEntryFactory(param -> null);
        projects.setEntryContextMenuCallback(entryContextMenuParameter -> null);
        projects.setEntryDetailsCallback(entryDetailsParameter -> {
            listener.onProjectSelected(projects.getSelections());
            return null;
        });
        for (AllDayView view : monthDayViews) {
            view.setEntryDetailsCallback(entryDetailsParameter -> {
                listener.onProjectSelected(view.getSelections());
                return null;
            });
        }
        projects.setContextMenuCallback(null);
        projects.setOnMouseClicked(null);
        projects.getCalendarSources().setAll(projectSource);
        tasks.getCalendarSources().setAll(taskSource);
        for (AllDayView view : monthDayViews) {
            view.getCalendarSources().setAll(projectSource);
        }
        monthViewBox.setVisible(false);
        weekViewBox.setVisible(true);
        weekDays.setVisible(true);
    }

    /**
     * When a color is selected to change the color of a project in the calendar view.
     */
    @FXML
    public void onColorSelected() {
        ObservableSet<Entry<?>> selections = FXCollections.observableSet();
        if (isMonthView) {
            for (AllDayView view : monthDayViews) {
                selections.addAll(view.getSelections());
            }
        } else {
            selections = projects.getSelections();
        }
        listener.changeColor(colorsComboBox.getSelectionModel().getSelectedItem(), selections);
        tasks.refreshData();
        projects.refreshData();
        for (AllDayView view : monthDayViews) {
            view.refreshData();
        }
    }

    /**
     * Settles a new week when we move in the calendar.
     *
     * @param date LocalDate, the current date.
     */
    public void setNewDate(LocalDate date) {
        monthLabel.setText(date.getMonth() + " " + date.getYear());
        weekDays.setDate(date);
        projects.setDate(date);
        initMonthDays(date);
        tasks.setDate(date);
        setLinesStyle(date);
    }

    /**
     * Sets lines color if it's the current date or not.
     *
     * @param date LocalDate, the current date to set lines.
     */
    public void setLinesStyle(LocalDate date) {
        boolean isCurrent = date.equals(LocalDate.now());
        for (Line weekDaysSeparator : weekDaysSeparators) {
            weekDaysSeparator.setStyle("-fx-stroke: BLACK;");
        }
        if (isCurrent && !isMonthView) {
            switch (date.getDayOfWeek()) {
                case MONDAY -> line1.setStyle("-fx-stroke: red; -fx-stroke-width: 3px");
                case TUESDAY -> {
                    line1.setStyle("-fx-stroke: red; -fx-stroke-width: 3px");
                    line2.setStyle("-fx-stroke: red; -fx-stroke-width: 3px");
                }
                case WEDNESDAY -> {
                    line2.setStyle("-fx-stroke: red; -fx-stroke-width: 3px");
                    line3.setStyle("-fx-stroke: red; -fx-stroke-width: 3px");
                }
                case THURSDAY -> {
                    line3.setStyle("-fx-stroke: red; -fx-stroke-width: 3px");
                    line4.setStyle("-fx-stroke: red; -fx-stroke-width: 3px");
                }
                case FRIDAY -> {
                    line4.setStyle("-fx-stroke: red; -fx-stroke-width: 3px");
                    line5.setStyle("-fx-stroke: red; -fx-stroke-width: 3px");
                }
                case SATURDAY -> {
                    line5.setStyle("-fx-stroke: red; -fx-stroke-width: 3px");
                    line6.setStyle("-fx-stroke: red; -fx-stroke-width: 3px");
                }
                case SUNDAY -> line6.setStyle("-fx-stroke: red; -fx-stroke-width: 3px");
            }
        }
    }

    /**
     * Sets dates for the month view.
     *
     * @param date LocalDate, the current date to set days
     */
    public void initMonthDays(LocalDate date) {
        LocalDate firstMonday = date.withDayOfMonth(1).with(DayOfWeek.MONDAY);
        for (int i = 0; i < 5; i++) {
            LocalDate workingDate = firstMonday.plusDays(7 * i);
            if (
                    workingDate.toEpochDay() <= LocalDate.now().toEpochDay() ||
                            LocalDate.now().toEpochDay() <= workingDate.plusDays(7).toEpochDay()) {
                monthHeaders.get(i).setShowToday(true);
                monthDayViews.get(i).setShowToday(true);
            } else {
                monthHeaders.get(i).setShowToday(false);
                monthDayViews.get(i).setShowToday(false);
            }
            monthHeaders.get(i).setDate(workingDate);
            monthDayViews.get(i).setDate(workingDate);
        }
    }

    /**
     * The main method for button's events.
     *
     * @param event ActionEvent, the event.
     */
    @FXML
    private void calendarEvents(ActionEvent event) {
        if (event.getSource() == previousWeekBtn) {
            listener.changeDate(false, isMonthView, false);
        } else if (event.getSource() == todayBtn) {
            listener.changeDate(true, isMonthView, false);
        } else if (event.getSource() == nextWeekBtn) {
            listener.changeDate(false, isMonthView, true);
        } else if (event.getSource() == backBtn) {
            listener.back();
        } else if (event.getSource() == viewBtn) {
            switchView();
        }
    }

    /**
     * Switches between WeekView and MonthView
     */
    private void switchView() {
        monthViewBox.setVisible(!isMonthView);
        weekViewBox.setVisible(isMonthView);
        weekDays.setVisible(isMonthView);
        isMonthView = !isMonthView;
        listener.changeDate(true, false, false);
        setLinesStyle(listener.getCurrentDate());
    }

    //--------------- LISTENER ----------------

    /**
     * Sets the listener.
     *
     * @param listener ViewListener, the listener to the controller.
     */
    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    /**
     * Communicates to the controller which button has been clicked
     */
    public interface ViewListener {
        /**
         * Return to previous scene
         */
        void back();

        /**
         * Action performed when the user selects a project
         *
         * @param selections The selection
         */
        void onProjectSelected(ObservableSet<Entry<?>> selections);

        /**
         * Changes the view based on the user's selection
         *
         * @param today   The current day
         * @param month   The month
         * @param forward Advance the calendar forward in time
         */
        void changeDate(boolean today, boolean month, boolean forward);

        /**
         * Add a project to the view
         *
         * @param projects The list of projects
         */
        void addProject(ObservableList<? extends String> projects);

        /**
         * Fetches the current date
         *
         * @return The current date
         */
        LocalDate getCurrentDate();

        /**
         * Changes the color of a project and all it's tasks
         *
         * @param color      The new color
         * @param selections The selected project
         */
        void changeColor(String color, ObservableSet<Entry<?>> selections);
    }

}
