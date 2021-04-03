package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.views.MenuViewController;
import be.ac.ulb.infof307.g06.views.StorageViewController;
import be.ac.ulb.infof307.g06.views.TagsViewController;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SettingsController {
    /**
     * Sets the loader to show the settings menu scene.
     */
    public static void showSettingsMenu() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MenuViewController.class.getResource("SettingsMenu.fxml"));
        MainController.load(loader, 940,1515);
    }

    /**
     * Sets the loader to show the tags menu scene.
     */
    public static void showTagsMenu() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(TagsViewController.class.getResource("Tags.fxml"));
        MainController.load(loader,940,1515);
    }

    /**
     *
     * @param view TagsViewController
     * @param title String
     * @param color String
     */

    public void addTag(TagsViewController view, String title, String color) throws SQLException {
        List<Tag> tags = ProjectDB.getAllTags();
        List<String> tagNames = new ArrayList<>();
        for (Tag tag2 : tags) {
            tagNames.add(tag2.getDescription());
        }
        if (tagNames.contains(title)) {
            new AlertWindow("Alert", "Tag already exists.").informationWindow();
            return;
        }
        ProjectDB.createTag(title, color);
    }

    public void deleteTag(Tag tag) throws SQLException{
        ProjectDB.deleteTag(tag.getId());
    }

    /**
     *
     * @param view TagsViewController
     * @param tag Tag
     * @param newDescription String
     * @param newColor String
     */

    public void editTag(TagsViewController view, Tag tag, String newDescription, String newColor) throws SQLException {
        List<Tag> tags = ProjectDB.getAllTags();
        List<String> tagNames = new ArrayList<>();
        for (Tag tag2 : tags) {
            tagNames.add(tag2.getDescription());
        }
        if (tagNames.contains(newDescription) && !newDescription.equals(tag.getDescription())) {
            new AlertWindow("Alert", "Tag already exists.").informationWindow();
            return;
        }
        ProjectDB.editTag(tag.getId(), newDescription, newColor);
    }

    public static void showStorageMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(StorageViewController.class.getResource("StorageView.fxml"));
        MainController.load(loader, 940, 1515);
    }

    public void showTags() {
    }

    public void showSettings() {
    }

    public void showStorageSettings() {
    }
}
