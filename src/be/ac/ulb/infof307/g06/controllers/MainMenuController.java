package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.controllers.project.ProjectController;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.MenuViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class MainMenuController extends Controller implements MenuViewController.ViewListener {
    public Listener listener;


    public MainMenuController(int userID, UserDB user_db, ProjectDB project_db, Stage stage, Scene scene) {
        super(userID, user_db, project_db, stage, scene);
    }

    /**
     * Sets the loader to show the Main menu scene.
     */
    @Override
    public void show() {
        try {
            if (user_db.isFirstBoot()) {
                user_db.setAdmin(256 * 1000000);
            }
            FXMLLoader loader = new FXMLLoader(MenuViewController.class.getResource("MenuView.fxml"));
            scene = new Scene(loader.load());
            MenuViewController controller = loader.getController();
            controller.setListener(this);
            load(940, 1515);
            stage.setScene(scene);
        } catch (SQLException | IOException e) {
            // TODO Exception
        }
    }


    /**
     * Sets the loader to show the stage with an invitation to join a project.
     */
    public void showInvitationStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(MenuViewController.class.getResource("InvitationView.fxml"));
        AnchorPane invitationPane = loader.load();
        MainController.showStage("Invitation", 571, 473, Modality.APPLICATION_MODAL, invitationPane);
    }


    public void load(Integer height, Integer width) {
        // Set main stage
        stage.setResizable(true);
        stage.setHeight(height);
        stage.setWidth(width);
        stage.centerOnScreen();
        stage.setResizable(false);

    }

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
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void showProjects() {
        ProjectController controller = new ProjectController(userID, user_db, project_db, stage, scene);
        controller.show();
    }

    @Override
    public void showStats() {
        StatsController controller = new StatsController(userID, user_db, project_db, stage, scene);
        controller.show();
    }

    @Override
    public void showStorage() {
        SettingsController controller = new SettingsController(userID, user_db, project_db, stage, scene);
        controller.showStorageMenu();
    }

    @Override
    public void showSettings() {
        SettingsController controller = new SettingsController(userID, user_db, project_db, stage, scene);
        controller.show();
    }

    @Override
    public void showTags() {
        SettingsController controller = new SettingsController(userID, user_db, project_db, stage, scene);
        controller.showTags();
    }


    @Override
    public void logout() {
        listener.logout();
        listener.showLogin();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void logout();
        void showLogin();
    }
}
