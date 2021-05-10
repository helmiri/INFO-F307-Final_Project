package be.ac.ulb.infof307.g06.controllers.settingsControllers;

import be.ac.ulb.infof307.g06.controllers.Controller;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.settingsViews.helpViews.HelpViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * Main controller for the help section.
 */
public class HelpController extends Controller implements HelpViewController.ViewListener {
    private final HelpViewController helpViewController;

    /**
     * Constructor
     *
     * @param user_db UserDB, the user database
     * @param project_db ProjectDB, the project database
     * @param stage Stage, a stage
     * @param scene Scene, a scene
     * @param helpViewController HelpViewController, the view controller
     * @param DB_PATH String, the path to the database
     */
    public HelpController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene, HelpViewController helpViewController, String DB_PATH) {
        super(user_db, project_db, stage, scene, DB_PATH);
        this.helpViewController = helpViewController;
    }

    /**
     * Shows help menu
     */
    @Override
    public void show() {
        helpViewController.setListener(this);
    }

    /**
     * Shows the accurate pop up according to the given title and FXML file name.
     *
     * @param fileName String, FXML file's name.
     * @param title    String, the pop up name.
     */
    @Override
    public void showHelp(String fileName, String title, MediaPlayer player) {
        try {
            FXMLLoader loader = new FXMLLoader(HelpViewController.class.getResource(fileName));
            AnchorPane projectPane = loader.load();
            HelpViewController controller = loader.getController();
            controller.setPlayer(player);
            Stage helpStage = new Stage();
            helpStage.initModality(Modality.APPLICATION_MODAL);
            helpStage.setTitle(title);
            helpStage.setScene(new Scene(projectPane));
            helpStage.centerOnScreen();
            helpStage.setResizable(false);
            helpStage.show();
        } catch (IOException e) {
            new AlertWindow("Error", "An error occurred", e.getMessage()).showErrorWindow();
        }
    }
}
