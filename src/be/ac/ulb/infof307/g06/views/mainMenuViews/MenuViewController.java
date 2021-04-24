package be.ac.ulb.infof307.g06.views.mainMenuViews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MenuViewController {
    //-------------- ATTRIBUTES ----------------

    //* MAIN MENU *
    @FXML
    private Button logOutBtn;
    @FXML
    private Button projectAccessBtn;
    @FXML
    private Button settingsAccessBtn;
    @FXML
    private Button profileAccessBtn;
    @FXML
    private Button calendarAccessBtn;
    @FXML
    private Button helpBtn;
    //* PROJECTS MENU *
    @FXML
    private Button projectManagementBtn;
    @FXML
    private Button statsAccessBtn;
    @FXML
    private Button mainMenuBtn;

    //* SETTINGS MENU *
    @FXML
    private Button languageBtn;
    @FXML
    private Button backBtn;

    private ViewListener listener;

    //--------------- METHODS ----------------


    /**
     * The main method for button's events.
     *
     * @param event ActionEvent
     */
    @FXML
    private void events(ActionEvent event) {
        if (event.getSource() == projectAccessBtn) {
            listener.showProjectsMenu();
        } else if (event.getSource() == logOutBtn) {
            listener.logout();
        } else if (event.getSource() == mainMenuBtn) {
            listener.showMainMenu();
        } else if (event.getSource() == statsAccessBtn) {
            listener.showStats();
        } else if (event.getSource() == settingsAccessBtn) {
            listener.showSettings();
        } else if (event.getSource() == projectManagementBtn) {
            listener.showProjects();
        } else if (event.getSource() == languageBtn) {
            System.out.println("test language button");
        } else if (event.getSource() == backBtn) {
            listener.showMainMenu();
        } else if (event.getSource() == calendarAccessBtn) {
            listener.showCalendar();
        }
    }


    //--------------- LISTENER ----------------
    public void setListener(ViewListener listener) { this.listener = listener; }

    public interface ViewListener {
        void showMainMenu();

        void showProjectsMenu();

        void showProjects();

        void showStats();

        void showSettings();

        void showCalendar();

        void logout();
    }
}
