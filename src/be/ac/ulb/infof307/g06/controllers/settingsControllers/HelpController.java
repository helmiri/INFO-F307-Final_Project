package be.ac.ulb.infof307.g06.controllers.settingsControllers;

import be.ac.ulb.infof307.g06.controllers.Controller;
import be.ac.ulb.infof307.g06.exceptions.WindowLoadException;
import be.ac.ulb.infof307.g06.views.settingsViews.helpViews.HelpViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;


/**
 * Main controller for the help section.
 */
public class HelpController extends Controller implements HelpViewController.ViewListener {
    private HelpViewController helpViewController;

    /**
     * Constructor
     *
     * @param stage Stage, a stage
     */
    public HelpController(Stage stage, HelpViewController viewController) {
        super(stage);
        helpViewController = viewController;
        helpViewController.setListener(this);
    }

    /**
     * Nothing to show by this controller
     */
    @Override
    public void show() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadVideo(String path, String title) {
        File file = new File(System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g06/resources/videos");
        String newPath = file + "/" + path;
        Media media = new Media(Paths.get(newPath).toUri().toString());
        FXMLLoader loader = new FXMLLoader(HelpViewController.class.getResource("TutorialView.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            new WindowLoadException(e).show();
            return;
        }
        helpViewController = loader.getController();

        helpViewController.setListener(this);
        helpViewController.setMediaPlayer(new MediaPlayer(media));
        createStage(title);
    }

    /**
     * Creates the window where the tutorial will be displayed
     *
     * @param title Title of the window
     */
    public void createStage(String title) {
        Stage tutorialStage = new Stage();
        tutorialStage.initModality(Modality.APPLICATION_MODAL);
        tutorialStage.setTitle(title);
        tutorialStage.centerOnScreen();
        tutorialStage.setResizable(false);
        helpViewController.show(tutorialStage);
    }
}
