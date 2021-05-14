package be.ac.ulb.infof307.g06.controllers.settingsControllers;

import be.ac.ulb.infof307.g06.controllers.Controller;
import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.exceptions.WindowLoadException;
import be.ac.ulb.infof307.g06.views.settingsViews.SettingsViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main controller for the settings.
 */
public class SettingsController extends Controller implements SettingsViewController.ViewListener {
    private FXMLLoader loader;

    /**
     * Constructor
     *
     * @param stage Stage, a stage
     */
    public SettingsController(Stage stage) {
        super(stage);
    }

    /**
     * Loads and displays the settings scene
     */
    @Override
    public void show() {
        try {
            SettingsViewController controller = (SettingsViewController) loadView(SettingsViewController.class, "SettingsView.fxml");
            controller.setListener(this);
            controller.show(stage);
        } catch (IOException error) {
            new WindowLoadException(error).show();
        }
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
     * @param view The fxml file to be loaded
     * @return The scene or null on error
     */
    private AnchorPane loadScene(String view) {
        loader = new FXMLLoader(SettingsViewController.class.getResource(view));
        try {
            return loader.load();
        } catch (IOException error) {
            new WindowLoadException(error).show();
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
        return loadScene("helpViews/HelpMenuView.fxml");
    }

    /**
     * Loads the layout of the about scene
     *
     * @return The layout of the scene
     */
    @Override
    public AnchorPane getAboutScene() {
        return loadScene("AboutView.fxml");
    }

    /**
     * Loads the storage scene controller
     */
    @Override
    public void showStorage() {
        try {
            StorageController controller = new StorageController(stage, loader.getController());
            controller.show();
        } catch (DatabaseException error) {
            error.show();
        }
    }

    /**
     * Loads the tag scene controller
     */
    @Override
    public void showTags() {
        try {
            TagsController controller = new TagsController(stage, loader.getController());
            controller.show();
        } catch (DatabaseException error) {
            error.show();
        }
    }

    /**
     * Loads the help scene controller
     */
    @Override
    public void showHelp() {
        HelpController controller = new HelpController(stage, loader.getController());
        controller.show();
    }

    /**
     * Loads the profile scene controller
     */
    @Override
    public void showProfile() {
        try {
            ProfileController controller = new ProfileController(stage, loader.getController());
            controller.show();
        } catch (DatabaseException e) {
            e.show();
        }
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
