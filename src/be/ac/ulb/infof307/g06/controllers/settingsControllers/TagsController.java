package be.ac.ulb.infof307.g06.controllers.settingsControllers;

import be.ac.ulb.infof307.g06.controllers.Controller;
import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.views.settingsViews.TagsViewController;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Main controller for the tags section.
 */
public class TagsController extends Controller implements TagsViewController.ViewListener {
    private final TagsViewController viewController;
    private ProjectDB projectDB;

    /**
     * Constructor
     *
     * @param stage          Stage, a stage
     * @param viewController TagsViewController, the view controller
     */
    //--------------- METHODS ----------------
    public TagsController(Stage stage, TagsViewController viewController) throws DatabaseException {
        super(stage);
        this.viewController = viewController;
        try {
            projectDB = new ProjectDB();
        } catch (SQLException error) {
            throw new DatabaseException(error);
        }
    }

    /**
     * Shows tags menu
     */
    @Override
    public void show() {
        viewController.setListener(this);
        try {
            viewController.initialize(projectDB.getAllTags());
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
            projectDB.createTag(text, toRGBCode);
            viewController.refresh(projectDB.getAllTags());
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
            projectDB.editTag(selectedTag.getId(), text, toRGBCode);
            viewController.refresh(projectDB.getAllTags());
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
            projectDB.deleteTag(selectedTag.getId());
            viewController.refresh(projectDB.getAllTags());
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
    }
}
