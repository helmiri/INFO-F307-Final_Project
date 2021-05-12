package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.controllers.calendarControllers.CalendarController;
import be.ac.ulb.infof307.g06.controllers.projectControllers.ProjectController;
import be.ac.ulb.infof307.g06.controllers.settingsControllers.SettingsController;
import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.exceptions.WindowLoadException;
import be.ac.ulb.infof307.g06.views.mainMenuViews.InvitationController;
import be.ac.ulb.infof307.g06.views.mainMenuViews.MenuViewController;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller for the main menu.
 */
public class MainMenuController extends Controller implements MenuViewController.ViewListener {
    //--------------- ATTRIBUTE ----------------
    private Listener listener;
    private InvitationController invitationController;

    /**
     * Constructor
     *
     * @param stage   Stage, a stage
     * @param scene   Scene, a scene
     * @param DB_PATH String, the path to the database
     */
    //--------------- METHODS ----------------
    public MainMenuController(Stage stage, Scene scene, String DB_PATH) {
        super(stage, scene, DB_PATH);
        try {
            invitationController = new InvitationController(stage, scene, DB_PATH);
        } catch (DatabaseException error) {
            error.show();
        }
    }

    /**
     * Sets the loader to show the Main menu scene.
     */
    @Override
    public void show() {
        try {
            MenuViewController controller = (MenuViewController) loadView(MenuViewController.class, "MenuView.fxml");
            controller.setListener(this);
            controller.show(stage);
            if (invitationController != null) {
                // The exception in the constructor was not thrown
                invitationController.show();
            }
        } catch (IOException error) {
            new WindowLoadException(error).show();
        }
    }

    //--------------- STAGES ----------------

    /**
     * Shows Main Menu
     */
    @Override
    public void showMainMenu() {
        show();
    }

    /**
     * Shows settings menu
     */
    @Override
    public void showSettings() {
        SettingsController controller = new SettingsController(stage, stage.getScene(), DB_PATH);
        controller.show();
    }

    /**
     * Show the projects menu
     */
    @Override
    public void showProjects() {
        try {
            ProjectController controller = new ProjectController(stage, stage.getScene(), DB_PATH);
            controller.show();
        } catch (SQLException | ClassNotFoundException error) {
            new DatabaseException(error).show();
        }
    }

    /**
     * Shows stats menu
     */
    @Override
    public void showStats() {
        try {
            StatsController controller = new StatsController(stage, stage.getScene(), DB_PATH);
            controller.show();
        } catch (DatabaseException error) {
            error.show();
        }
    }


    /**
     * Shows calendar menu
     */
    @Override
    public void showCalendar() {
        try {
            CalendarController controller = new CalendarController(stage, stage.getScene(), DB_PATH);
            controller.show();
        } catch (DatabaseException error) {
            error.show();
        } catch (WindowLoadException e) {
            e.show();
        }
    }

    /**
     * Logs user out
     */
    @Override
    public void logout() {
        listener.logout();
        listener.showLogin();
    }


    //--------------- LISTENER ----------------

    /**
     * Sets listener
     *
     * @param listener Listener
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * The listener and his methods.
     */
    public interface Listener {

        /**
         * Logs the user out.
         */
        void logout();

        /**
         * Shows the login
         */
        void showLogin();
    }
}
