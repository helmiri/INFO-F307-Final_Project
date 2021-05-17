package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.controllers.calendarControllers.CalendarController;
import be.ac.ulb.infof307.g06.controllers.connectionControllers.LoginController;
import be.ac.ulb.infof307.g06.controllers.projectControllers.ProjectController;
import be.ac.ulb.infof307.g06.controllers.settingsControllers.SettingsController;
import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.exceptions.WindowLoadException;
import be.ac.ulb.infof307.g06.models.database.ActiveUser;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.mainMenuViews.InvitationController;
import be.ac.ulb.infof307.g06.views.mainMenuViews.MenuViewController;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller for the main menu.
 */
public class MainMenuController extends Controller implements MenuViewController.ViewListener {
    //--------------- ATTRIBUTE ----------------
    private InvitationController invitationController;
    private UserDB userDB;
    private ActiveUser.PrivateAccess friend;

    /**
     * Constructor
     *
     * @param stage Stage, a stage
     */
    //--------------- METHODS ----------------
    public MainMenuController(Stage stage) {
        super(stage);
        try {
            userDB = new UserDB();
            invitationController = new InvitationController(stage);
        } catch (DatabaseException error) {
            error.show();
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }

    }


    /**
     * Sets the loader to show the Main menu scene.
     */
    @Override
    public final void show() {
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
        SettingsController controller = new SettingsController(stage);
        controller.show();
    }

    /**
     * Show the projects menu
     */
    @Override
    public void showProjects() {
        try {
            ProjectController controller = new ProjectController(stage);
            controller.show();
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
    }

    /**
     * Shows stats menu
     */
    @Override
    public void showStats() {
        try {
            StatsController controller = new StatsController(stage);
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
            CalendarController controller = new CalendarController(stage);
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
    public void onLogout() {
        try {
            ActiveUser.getInstance().grantAccess(this);
            userDB.disconnectUser();
            friend.resetInstance();
            LoginController controller = new LoginController(stage);
            controller.show();
        } catch (SQLException error) {
            new DatabaseException(error, "Unable to disconnect").show();
        } catch (DatabaseException | WindowLoadException e) {
            new DatabaseException(e).show();
        }
    }

    /**
     * Requests ActiveUser for access to its private methods through a nested class that controls said access
     * See ActiveUser.PrivateAccess doc for an explanation on why this is needed.
     *
     * @param privateAccess The instance of the class
     */
    public void getAccess(ActiveUser.PrivateAccess privateAccess) {
        friend = privateAccess;
    }
}
