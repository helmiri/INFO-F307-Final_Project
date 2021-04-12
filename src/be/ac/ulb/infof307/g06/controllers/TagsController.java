package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.TagsViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;

public class TagsController extends Controller implements TagsViewController.ViewListener {
    TagsViewController viewController;
    //--------------- METHODS ----------------
    public TagsController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene) {
        super(user_db, project_db, stage, scene);
    }

    @Override
    public void show() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(TagsViewController.class.getResource("Tags.fxml"));
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        viewController = loader.getController();
        viewController.setListener(this);
        stage.setScene(scene);
        try {
            viewController.initialize(project_db.getAllTags());
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO EXCEPTION
        }
    }

    @Override
    public void onBackButtonClicked() {
        stage.hide();
        stage.setScene(prevScene);
        stage.show();
    }

    @Override
    public void onAddButtonClicked(String text, String toRGBCode) {
        try {
            project_db.createTag(text, toRGBCode);
            viewController.refresh(project_db.getAllTags());
        } catch (SQLException throwables) {
            new AlertWindow("Error", "Can't add the tag.").errorWindow();
            throwables.printStackTrace();
        }
    }

    @Override
    public void onUpdateButtonClicked(Tag selectedTag, String text, String toRGBCode) {
        try {
            project_db.editTag(selectedTag.getId(), text, toRGBCode);
            viewController.refresh(project_db.getAllTags());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public void deleteSelectedTag(Tag selectedTag) {
        try {
            project_db.deleteTag(selectedTag.getId());
            viewController.refresh(project_db.getAllTags());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


}
