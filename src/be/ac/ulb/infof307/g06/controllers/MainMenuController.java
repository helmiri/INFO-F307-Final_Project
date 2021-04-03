package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.controllers.project.ProjectController;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.MenuViewController;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class MainMenuController implements MenuViewController.ViewListener {
    public Listener listener;
    private Stage stage;
    private boolean isFirstBoot;

    public MainMenuController(Stage stage, Listener listener) {
        this.stage = stage;
        this.listener = listener;
    }

    /**
     * Sets the loader to show the Main menu scene.
     */
    public void show(boolean isFirstBoot) {
        if (isFirstBoot) {
            try {
                UserDB.setAdmin(256 * 1000000);
            } catch (SQLException throwables) {
                //alertWindow(Alert.AlertType.ERROR, "Database error", "Could not access the ");
            }
        }
        FXMLLoader loader = new FXMLLoader(MenuViewController.class.getResource("MenuView.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MenuViewController controller = loader.getController();
        controller.setListener(this);
        load(940, 1515);
    }

    /**
     * Sets the loader to show the project menu scene.
     */
    public void showProjectMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(MenuViewController.class.getResource("ProjectMenu.fxml"));
        loader.load();
        load(940, 1515);
    }

    /**
     * Sets the loader to show the stage with an invitation to join a project.
     */
    public void showInvitationStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(MenuViewController.class.getResource("InvitationView.fxml"));
        loader.load();
        MainController.showStage("Invitation", 571, 473, Modality.APPLICATION_MODAL, loader);
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
        show(isFirstBoot);
    }

    @Override
    public void showProjectsMenu() {
        showProjectsMenu();
    }

    @Override
    public void showProjects() {
        ProjectController controller = new ProjectController();
        try {
            controller.showProjects();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showStats() {
        StatsController controller = new StatsController();
        controller.show();
    }

    @Override
    public void showStorage() {
        SettingsController controller = new SettingsController();
        controller.showStorageSettings();
    }

    @Override
    public void showSettings() {
        SettingsController controller = new SettingsController();
        controller.showSettings();
    }

    @Override
    public void showTags() {
        SettingsController controller = new SettingsController();
        controller.showTags();
    }

    @Override
    public void logout() {
        listener.logout();
        listener.showLogin();
    }

    public interface Listener {
        void logout();

        void showLogin();
    }
}
