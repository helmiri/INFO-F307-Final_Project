package be.ac.ulb.infof307.g06.controllers.settingsControllers;

import be.ac.ulb.infof307.g06.controllers.Controller;
import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.settingsViews.StorageViewController;
import be.ac.ulb.infof307.g06.views.settingsViews.TagsViewController;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Main controller for the tags section.
 */
public class TagsController extends Controller implements TagsViewController.ViewListener {
    TagsViewController viewController;

    /**
     * Constructor
     *
     * @param user_db UserDB, the user database
     * @param project_db ProjectDB, the project database
     * @param stage Stage, a stage
     * @param scene Scene, a scene
     * @param viewController TagsViewController, the view controller
     * @param DB_PATH String, the path to the database
     */
    //--------------- METHODS ----------------
    public TagsController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene, TagsViewController viewController, String DB_PATH) {
        super(user_db, project_db, stage, scene, DB_PATH);
        this.viewController = viewController;
    }

    /**
     * Shows tags menu
     */
    @Override
    public void show() {
        viewController.setListener(this);
        try {
            viewController.initialize(project_db.getAllTags());
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
    }


    /**
     * Adds a tag
     *
     * @param text      tag title
     * @param toRGBCode tag color
     */
    @Override
    public void onAddButtonClicked(String text, String toRGBCode) {
        try {
            project_db.createTag(text, toRGBCode);
            viewController.refresh(project_db.getAllTags());
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
    }

    /**
     * Updates a tag
     *
     * @param selectedTag selected tag
     * @param text        tag title
     * @param toRGBCode   tag color
     */
    @Override
    public void onUpdateButtonClicked(Tag selectedTag, String text, String toRGBCode) {
        if (selectedTag == null) {
            new AlertWindow("Invalid selection", "A tag must first be selected").showInformationWindow();
            return;
        }
        try {
            project_db.editTag(selectedTag.getId(), text, toRGBCode);
            viewController.refresh(project_db.getAllTags());
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
    }

    /**
     * Deletes a tag
     *
     * @param selectedTag selected tag
     */
    @Override
    public void deleteSelectedTag(Tag selectedTag) {
        try {
            project_db.deleteTag(selectedTag.getId());
            viewController.refresh(project_db.getAllTags());
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
    }
}
