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
     * Shows menu
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
     * Returns tags scene
     *
     * @return tags scene
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
            new AlertWindow("Error", "" + e).errorWindow();
        }
        return null;
    }

    /**
     * Gets the storage scene
     *
     * @return Anchorpane storage scene
     */
    @Override
    public AnchorPane getStorageScene() {
        return loadScene("StorageView.fxml");
    }

    @Override
    public AnchorPane getLanguageScene() {
        return null;
    }

    @Override
    public AnchorPane getHelpScene() {
        return null;
    }

    @Override
    public AnchorPane getAboutScene() {
        return null;
    }

    /**
     * Shows storage menu
     */
    @Override
    public void showStorage() {
        StorageController controller = new StorageController(user_db, project_db, stage, scene, loader.getController());
        controller.show();
    }

    /**
     * shows tags menu
     */
    @Override
    public void showTags() {
        TagsController controller = new TagsController(user_db, project_db, stage, scene, loader.getController());
        controller.show();
    }

    @Override
    public void showHelp() {
    }

    @Override
    public void showAbout() { }

    @Override
    public void onBackButtonPressed() {
        back();
    }
}
