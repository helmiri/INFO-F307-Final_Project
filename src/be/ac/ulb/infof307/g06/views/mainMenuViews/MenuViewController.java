package be.ac.ulb.infof307.g06.views.mainMenuViews;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The main menu scene
 */
public class MenuViewController {

    //-------------- ATTRIBUTES ----------------
    @FXML
    private AnchorPane layout;
    @FXML
    private Button logOutBtn;
    @FXML
    private Button settingsAccessBtn;
    @FXML
    private Button calendarAccessBtn;
    @FXML
    private Button helpBtn;
    @FXML
    private Button projectManagementBtn;
    @FXML
    private Button statsAccessBtn;
    @FXML
    private Button mainMenuBtn;
    private ViewListener listener;

    //--------------- METHODS ----------------

    public void show(Stage stage) {
        stage.setScene(new Scene(layout));
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    /**
     * The main method for button's events.
     *
     * @param event ActionEvent, the event.
     */
    @FXML
    private void events(ActionEvent event) {
        if (event.getSource() == logOutBtn) {
            listener.onLogout();
        } else if (event.getSource() == mainMenuBtn) {
            listener.showMainMenu();
        } else if (event.getSource() == statsAccessBtn) {
            listener.showStats();
        } else if (event.getSource() == settingsAccessBtn) {
            listener.showSettings();
        } else if (event.getSource() == projectManagementBtn) {
            listener.showProjects();
        } else if (event.getSource() == calendarAccessBtn) {
            listener.showCalendar();
        }
        else if (event.getSource() == helpBtn){
            new AlertWindow("Help", "Let your mouse for a few seconds on a button to see a tooltip.").showInformationWindow();
        }
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
     * Communicates to the controller which button has been pressed
     */
    public interface ViewListener {
        /**
         * Show the main menu scene
         */
        void showMainMenu();

        /**
         * Shows the project scene
         */
        void showProjects();

        /**
         * Shows the stats scene
         */
        void showStats();

        /**
         * shows the settings scene
         */
        void showSettings();

        /**
         * shows the calendar scene
         */
        void showCalendar();

        /**
         * Logs the user out
         */
        void onLogout();
    }
}
