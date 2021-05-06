package be.ac.ulb.infof307.g06.controllers.settingsControllers;

import be.ac.ulb.infof307.g06.controllers.Controller;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.settingsViews.TagsViewController;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;

public class TagsController extends Controller implements TagsViewController.ViewListener {
    TagsViewController viewController;

    //--------------- METHODS ----------------
    public TagsController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene, TagsViewController viewController) {
        super(user_db, project_db, stage, scene);
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
        } catch (SQLException e) {
            new AlertWindow("Error", "An error has occurred with the database while getting the tags: " + e).errorWindow();
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
        } catch (SQLException e) {
            new AlertWindow("Error", "Can't add the tag. " + e).errorWindow();
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
        try {
            project_db.editTag(selectedTag.getId(), text, toRGBCode);
            viewController.refresh(project_db.getAllTags());
        } catch (SQLException e) {
            new AlertWindow("Error", " " + e).errorWindow();
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
        } catch (SQLException e) {
            new AlertWindow("Error", " " + e).errorWindow();
        }
    }


}
