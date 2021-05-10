package be.ac.ulb.infof307.g06.controllers.settingsControllers;
import be.ac.ulb.infof307.g06.controllers.Controller;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.settingsViews.helpViews.HelpViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.Paths;

public class HelpController extends Controller implements HelpViewController.ViewListener {
    private final HelpViewController helpViewController;
    private FXMLLoader loader;

    public HelpController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene, HelpViewController helpViewController) {
        super(user_db, project_db, stage, scene);
        this.helpViewController = helpViewController;
    }
    // TODO : Handle exceptions
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
     * @param title String, the pop up name.
     * @throws IOException
     */
    public void showHelp(String fileName, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelpViewController.class.getResource(fileName));
        AnchorPane projectPane = loader.load();
        Stage helpStage = new Stage();
        helpStage.initModality(Modality.APPLICATION_MODAL);
        helpStage.setTitle(title);
        helpStage.setScene(new Scene(projectPane));
        helpStage.centerOnScreen();
        helpStage.setResizable(false);
        helpStage.show();
    }


    /**
     * Shows project management help.
     *
     * @throws IOException
     */
    @Override
    public void projectManagementHelp() throws IOException {
        showHelp("ProjectManagementTest.fxml", "Project Management");
    }

    @Override
    public String okok(){
        return "/home/aline/Bureau/BA3/Genie/ProjetGenie/2021-groupe-6/src/be/ac/ulb/infof307/g06/resources/screenshots/yseult-indelebile-lyric-video.mp4";

    }

    /**
     * Shows storage help.
     *
     * @throws IOException
     */
    @Override
    public void storageHelp() throws IOException {
        showHelp("HelpMenuView.fxml", "Storage");
    }

    /**
     * Shows export/import help.
     *
     * @throws IOException
     */
    @Override
    public void exportImportHelp() throws IOException {
        showHelp("HelpMenuView.fxml", "Export/Import");
    }

    /**
     * Shows calendar help.
     *
     * @throws IOException
     */
    @Override
    public void calendarHelp() throws IOException {
        showHelp("HelpMenuView.fxml", "Calendar");
    }

    /**
     * Shows tags help.
     *
     * @throws IOException
     */
    @Override
    public void tagsHelp() throws IOException {
        showHelp("HelpMenuView.fxml", "Tags");
    }

    /**
     * Shows profile help.
     *
     * @throws IOException
     */
    @Override
    public void profileHelp() throws IOException {
        showHelp("HelpMenuView.fxml", "Profile");
    }
}
