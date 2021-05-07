package be.ac.ulb.infof307.g06.controllers.settingsControllers;

import be.ac.ulb.infof307.g06.controllers.Controller;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.settingsViews.SettingsViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsController extends Controller implements SettingsViewController.ViewListener {
    private FXMLLoader loader;

    public SettingsController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene) {
        super(user_db, project_db, stage, scene);
    }

    /**
     * Loads and displays the settings scene
     */
    @Override
    public void show() {
        AnchorPane pane = loadScene("SettingsView.fxml");
        if (pane == null) {
            return;
        }
        SettingsViewController controller = loader.getController();
        controller.setListener(this);
        controller.setDefaultScene();
        stage.setScene(new Scene(pane));
        stage.sizeToScene();
    }

    /**
     * Loads the layout of the tags scene
     *
     * @return The layout of the scene
     */
    @Override
    public AnchorPane getTagsScene() {
        return loadScene("TagsView.fxml");
    }

    /**
     * loads a scene
     *
     * @param view view
     * @return AnchorPane
     */
    private AnchorPane loadScene(String view) {
        loader = new FXMLLoader(SettingsViewController.class.getResource(view));
        try {
            return loader.load();
        } catch (IOException e) {
            new AlertWindow("Error", "Unable to load the window", e.getMessage()).errorWindow();
        }
        return null;
    }

    /**
     * Loads the layout of the storage scene
     *
     * @return The layout of the scene
     */
    @Override
    public AnchorPane getStorageScene() {
        return loadScene("StorageView.fxml");
    }

    /**
     * Loads the layout of the help scene
     *
     * @return The layout of the scene
     */
    @Override
    public AnchorPane getHelpScene() {
        return null;
    }

    /**
     * Loads the layout of the about scene
     *
     * @return The layout of the scene
     */
    @Override
    public AnchorPane getAboutScene() {
        return null;
    }

    /**
     * Loads the storage scene controller
     */
    @Override
    public void showStorage() {
        StorageController controller = new StorageController(user_db, project_db, stage, scene, loader.getController());
        controller.show();
    }

    /**
     * Loads the tag scene controller
     */
    @Override
    public void showTags() {
        TagsController controller = new TagsController(user_db, project_db, stage, scene, loader.getController());
        controller.show();
    }

    /**
     * Loads the help scene controller
     */
    @Override
    public void showHelp() {
    }

    @Override
    public void showAbout() { }

    /**
     * Loads the profile scene controller
     */
    @Override
    public void showProfile() {
        ProfileController controller = new ProfileController(user_db, project_db, stage, scene, loader.getController());
        controller.show();
    }

    /**
     * Return to the last scene
     */
    @Override
    public void onBackButtonPressed() {
        back();
    }

    /**
     * Loads the layout of the profile scene
     *
     * @return The layout of the scene
     */
    @Override
    public AnchorPane getProfileScene() {
        return loadScene("ProfileView.fxml");
    }
}
