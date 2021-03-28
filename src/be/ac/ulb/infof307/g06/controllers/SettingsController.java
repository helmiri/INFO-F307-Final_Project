package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.views.MenuViewController;
import be.ac.ulb.infof307.g06.views.TagsViewController;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SettingsController {
    public static void showSettingsMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MenuViewController.class.getResource("SettingsMenu.fxml"));
        MainController.load(loader, 940,1515);
    }

    public static void showTagsMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(TagsViewController.class.getResource("Tags.fxml"));
        MainController.load(loader,940,1515);
    }

    public void addTag(TagsViewController view, String title, String color) throws SQLException {
        List<Tag> tags = ProjectDB.getAllTags();
        List<String> tagNames = new ArrayList<>();
        for (Tag tag2 : tags) {
            tagNames.add(tag2.getDescription());
        }
        if (tagNames.contains(title)){
            view.showAlert("Tag already exists");return;}
        ProjectDB.createTag(title, color);
    }
    public void deleteTag(Tag tag) throws SQLException{
        ProjectDB.deleteTag(tag.getId());
    }
    public void editTag(TagsViewController view, Tag tag, String newDescription, String newColor) throws SQLException{
        List<Tag> tags = ProjectDB.getAllTags();
        List<String> tagNames = new ArrayList<>();
        for (Tag tag2 : tags) {
            tagNames.add(tag2.getDescription());
        }
        if (tagNames.contains(newDescription) && !newDescription.equals(tag.getDescription())){
            view.showAlert("Tag already exists");return;}
        ProjectDB.editTag(tag.getId(), newDescription,newColor);
    }
}
