package be.ac.ulb.infof307.g06.controllers.settingsControllers;

import be.ac.ulb.infof307.g06.controllers.Controller;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.settingsViews.SettingsViewController;
import be.ac.ulb.infof307.g06.views.settingsViews.StorageViewController;
import be.ac.ulb.infof307.g06.views.settingsViews.helpViews.HelpViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class HelpController extends Controller implements HelpViewController.ViewListener {
    private final HelpViewController helpViewController;
    private FXMLLoader loader;

    public HelpController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene, HelpViewController helpViewController) {
        super(user_db, project_db, stage, scene);
        this.helpViewController = helpViewController;
    }

    /**
     * Shows help menu
     */
    @Override
    public void show() {
        helpViewController.setListener(this);

    }



}
