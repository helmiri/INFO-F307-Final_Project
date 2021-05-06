package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.controllers.calendarControllers.CalendarController;
import be.ac.ulb.infof307.g06.controllers.projectControllers.ProjectController;
import be.ac.ulb.infof307.g06.controllers.settingsControllers.SettingsController;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Invitation;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.User;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.mainMenuViews.InvitationViewController;
import be.ac.ulb.infof307.g06.views.mainMenuViews.MenuViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class MainMenuController extends Controller implements MenuViewController.ViewListener, InvitationViewController.ViewListener {
    //--------------- ATTRIBUTE ----------------
    private Listener listener;

    //--------------- METHODS ----------------
    public MainMenuController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene) {
        super(user_db, project_db, stage, scene);
    }

    /**
     * Sets the loader to show the Main menu scene.
     */
    @Override
    public void show() {
        try {
            FXMLLoader loader = new FXMLLoader(MenuViewController.class.getResource("MenuView.fxml"));
            scene = new Scene(loader.load());
            MenuViewController controller = loader.getController();
            controller.setListener(this);
            stage.setScene(scene);
            stage.sizeToScene();
            load(940, 1515);
            for (Invitation invitation : user_db.getInvitations(project_db)) {
                showInvitationStage(invitation);
            }
        } catch (IOException | SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }
    }


    /**
     * Sets the loader to show the stage with an invitation to join a project.
     */
    public void showInvitationStage(Invitation invitation) throws IOException {
        FXMLLoader loader = new FXMLLoader(InvitationViewController.class.getResource("InvitationView.fxml"));
        AnchorPane invitationPane = loader.load();
        InvitationViewController controller = loader.getController();
        controller.setListener(this);
        controller.show(invitation, invitationPane);
    }


    /**
     * Loads stage.
     *
     * @param height Integer, height of the window.
     * @param width  Integer, width of the window.
     */
    public void load(Integer height, Integer width) {
        // Set main stage
        stage.setHeight(height);
        stage.setWidth(width);
        stage.centerOnScreen();
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
        SettingsController controller = new SettingsController(user_db, project_db, stage, scene);
        controller.show();
    }

    @Override
    public void showProjects() {
        ProjectController controller = new ProjectController(user_db, project_db, stage, scene);
        controller.show();
    }

    /**
     * Shows stats menu
     */
    @Override
    public void showStats() {
        StatsController controller = new StatsController(user_db, project_db, stage, scene);
        controller.show();
    }


    /**
     * Shows calendar menu
     */
    @Override
    public void showCalendar() {
        CalendarController controller = new CalendarController(user_db, project_db, stage, scene);
        try {
            controller.show();
        } catch (SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
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

    /**
     * Accepts an invitation and adds collaborator to project
     *
     * @param invitation      Invitation object
     * @param invitationStage Invitation Stage
     */
    @Override
    public void acceptInvitation(Invitation invitation, Stage invitationStage) {
        Project project = invitation.getProject();
        User receiver = invitation.getReceiver();
        try {
            project_db.addCollaborator(project.getId(), receiver.getId());
            user_db.removeInvitation(invitation.getInvitationID());
            invitationStage.close();
        } catch (SQLException e) {
            new AlertWindow("Database error", "Could not access the database \n" + e).errorWindow();
        }
    }

    /**
     * Declines invitation to project
     *
     * @param invitation      Invitation object
     * @param invitationStage Invitation Stage
     */
    @Override
    public void declineInvitation(Invitation invitation, Stage invitationStage) {
        try {
            user_db.removeInvitation(invitation.getInvitationID());
            invitationStage.close();
        } catch (SQLException e) {
            new AlertWindow("Database error", "Could not access the database \n" + e).errorWindow();
        }
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
