package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.MenuViewController;
import be.ac.ulb.infof307.g06.views.StorageViewController;
import be.ac.ulb.infof307.g06.views.TagsViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SettingsController extends Controller {

    public SettingsController(int userID, UserDB user_db, ProjectDB project_db, Stage stage, Scene scene) {
        super(userID, user_db, project_db, stage, scene);
    }

    /**
     * Sets the loader to show the tags menu scene.
     */
    public void showTags() {
        FXMLLoader loader = new FXMLLoader(TagsViewController.class.getResource("Tags.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void showStorageMenu() {
        FXMLLoader loader = new FXMLLoader(StorageViewController.class.getResource("StorageView.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the loader to show the settings menu scene.
     */
    @Override
    public void show() {
        FXMLLoader loader = new FXMLLoader(MenuViewController.class.getResource("SettingsMenu.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param view  TagsViewController
     * @param title String
     * @param color String
     */

    public void addTag(TagsViewController view, String title, String color) throws SQLException {
        List<Tag> tags = project_db.getAllTags();
        List<String> tagNames = new ArrayList<>();
        for (Tag tag2 : tags) {
            tagNames.add(tag2.getDescription());
        }
        if (tagNames.contains(title)) {
            new AlertWindow("Alert", "Tag already exists.").informationWindow();
            return;
        }
        project_db.createTag(title, color);
    }

    public void deleteTag(Tag tag) throws SQLException{
        project_db.deleteTag(tag.getId());
    }

    /**
     *
     * @param view TagsViewController
     * @param tag Tag
     * @param newDescription String
     * @param newColor String
     */

    public void editTag(TagsViewController view, Tag tag, String newDescription, String newColor) throws SQLException {
        List<Tag> tags = project_db.getAllTags();
        List<String> tagNames = new ArrayList<>();
        for (Tag tag2 : tags) {
            tagNames.add(tag2.getDescription());
        }
        if (tagNames.contains(newDescription) && !newDescription.equals(tag.getDescription())) {
            new AlertWindow("Alert", "Tag already exists.").informationWindow();
            return;
        }
        project_db.editTag(tag.getId(), newDescription, newColor);
    }





}
