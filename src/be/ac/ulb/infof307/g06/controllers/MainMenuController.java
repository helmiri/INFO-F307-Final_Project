package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.controllers.projectControllers.ProjectController;
import be.ac.ulb.infof307.g06.controllers.settingsControllers.StorageController;
import be.ac.ulb.infof307.g06.controllers.settingsControllers.TagsController;
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
    public void showInvitationStage(Invitation invitation) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(InvitationViewController.class.getResource("InvitationView.fxml"));
        AnchorPane invitationPane = loader.load();
        InvitationViewController controller = loader.getController();
        controller.setListener(this);
        controller.show(invitation, invitationPane);
    }


    /**
     * Loads stage.
     *
     * @param height Integer
     * @param width Integer
     */
    public void load(Integer height, Integer width) {
        // Set main stage
        //stage.setResizable(true);
        stage.setHeight(height);
        stage.setWidth(width);
        stage.centerOnScreen();
        //stage.setResizable(false);

    }

    //--------------- STAGES ----------------
    @Override
    public void showMainMenu() {
        show();
    }

    @Override
    public void showProjectsMenu() {
        FXMLLoader loader = new FXMLLoader(MenuViewController.class.getResource("ProjectMenu.fxml"));
        try {
            scene = new Scene(loader.load());
            MenuViewController controller = loader.getController();
            controller.setListener(this);
            stage.setScene(scene);
            stage.sizeToScene();
        } catch (IOException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }

    }

    @Override
    public void showProjects() {
        ProjectController controller = new ProjectController(user_db, project_db, stage, scene);
        controller.show();
    }

    @Override
    public void showStats() {
        StatsController controller = new StatsController(user_db, project_db, stage, scene);
        controller.show();

        // Sous WINDOWS CA FONCTIONNE AUSSI QU'AVEC 1 SEULE LIGNE (controller.show())
    }

    @Override
    public void showStorage() {
        StorageController controller = new StorageController(user_db, project_db, stage, scene);
        controller.show();
    }

    @Override
    public void showSettings() {
        FXMLLoader loader = new FXMLLoader(MenuViewController.class.getResource("SettingsMenu.fxml"));
        try {
            scene = new Scene(loader.load());
            MenuViewController controller = loader.getController();
            controller.setListener(this);
            stage.setScene(scene);
            stage.sizeToScene();
        } catch (IOException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }
        //controller.show();
    }

    @Override
    public void showTags() {
        TagsController controller = new TagsController(user_db, project_db, stage, scene);
        controller.show();
    }

    @Override
    public void showCalendar() {
        CalendarController controller = new CalendarController(user_db, project_db, stage, scene);
        try {
            controller.show();
        } catch (IOException | SQLException e) {
            new AlertWindow("Error", "" + e).errorWindow();
        }
    }


    @Override
    public void logout() {
        listener.logout();
        listener.showLogin();
    }

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
