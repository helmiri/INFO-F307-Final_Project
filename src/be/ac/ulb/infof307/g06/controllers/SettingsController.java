//package be.ac.ulb.infof307.g06.controllers;
//
//import be.ac.ulb.infof307.g06.models.AlertWindow;
//import be.ac.ulb.infof307.g06.models.Tag;
//import be.ac.ulb.infof307.g06.models.database.ProjectDB;
//import be.ac.ulb.infof307.g06.models.database.UserDB;
//import be.ac.ulb.infof307.g06.views.MenuViewController;
//import be.ac.ulb.infof307.g06.views.StatisticsViews.StatsViewController;
//import be.ac.ulb.infof307.g06.views.StorageViewController;
//import be.ac.ulb.infof307.g06.views.TagsViewController;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.scene.layout.AnchorPane;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class SettingsController extends Controller  {
//    private TagsViewController tagsView;
//
//    public SettingsController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene) {
//        super(user_db, project_db, stage, scene);
//    }
//
//    /**
//     * Sets the loader to show the tags menu scene.
//     */
////    public void showTags() {
////        FXMLLoader loader = new FXMLLoader(TagsViewController.class.getResource("Tags.fxml"));
////        try {
////            //AnchorPane tagsPane = loader.load();
////            scene = new Scene(loader.load());
////            TagsViewController controller = loader.getController();
////            controller.setListener(this);
////            stage.setScene(scene);
////            controller.initialize(project_db.getAllTags());
////        } catch (IOException | SQLException e) {
////            e.printStackTrace();
////        }
////    }
//
//    public void showStorageMenu() {
//        FXMLLoader loader = new FXMLLoader(StorageViewController.class.getResource("StorageView.fxml"));
//        try {
//            loader.load();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Sets the loader to show the settings menu scene.
//     */
//    @Override
//    public void show() {
//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(MenuViewController.class.getResource("SettingsMenu.fxml"));
//        scene = stage.getScene();
//
//        try {
//            stage.setScene(new Scene(loader.load()));
//            System.out.println("AAAAAAAAAA" + loader.getController());
//            tagsView = loader.getController();
//            System.out.println("BBBBBBBBBB" + tagsView);
//            tagsView.setListener(this);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * @param view  TagsViewController
//     * @param title String
//     * @param color String
//     */
//
//    public void addTag(TagsViewController view, String title, String color) throws SQLException {
//        List<Tag> tags = project_db.getAllTags();
//        List<String> tagNames = new ArrayList<>();
//        for (Tag tag2 : tags) {
//            tagNames.add(tag2.getDescription());
//        }
//        if (tagNames.contains(title)) {
//            new AlertWindow("Alert", "Tag already exists.").informationWindow();
//            return;
//        }
//        project_db.createTag(title, color);
//    }
//
//    public void deleteTag(Tag tag) throws SQLException{
//        project_db.deleteTag(tag.getId());
//    }
//
//    /**
//     *
//     * @param view TagsViewController
//     * @param tag Tag
//     * @param newDescription String
//     * @param newColor String
//     */
//
//    public void editTag(TagsViewController view, Tag tag, String newDescription, String newColor) throws SQLException {
//        List<Tag> tags = project_db.getAllTags();
//        List<String> tagNames = new ArrayList<>();
//        for (Tag tag2 : tags) {
//            tagNames.add(tag2.getDescription());
//        }
//        if (tagNames.contains(newDescription) && !newDescription.equals(tag.getDescription())) {
//            new AlertWindow("Alert", "Tag already exists.").informationWindow();
//            return;
//        }
//        project_db.editTag(tag.getId(), newDescription, newColor);
//    }
//
//
////    @Override
////    public void onBackButtonClicked() {
////        this.stage.setScene(this.scene);
////    }
////
////    @Override
////    public void onAddButtonClicked(String text, String toRGBCode) {
////        try {
////            project_db.createTag(text, toRGBCode);
////        } catch (SQLException throwables) {
////            throwables.printStackTrace();
////        }
////    }
////
////    @Override
////    public void onUpdateButtonClicked(Tag selectedTag, String text, String toRGBCode) {
////        try {
////            project_db.editTag(selectedTag.getId(), text, toRGBCode);
////        } catch (SQLException throwables) {
////            throwables.printStackTrace();
////        }
////    }
////
////    @Override
////    public void deleteSelectedTag(Tag selectedTag) {
////        try {
////            project_db.deleteTag(selectedTag.getId());
////        } catch (SQLException throwables) {
////            throwables.printStackTrace();
////        }
////    }
//
//
//
//}
