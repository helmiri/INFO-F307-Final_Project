package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.exceptions.WindowLoadException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Base class for controllers. Controllers should extend this class to ensure proper functionality
 */
public abstract class Controller {
    //--------------- ATTRIBUTES ----------------
    protected Stage stage;
    protected Scene currentScene;
    protected Scene prevScene;
    protected String DB_PATH;

    /**
     * Constructor
     *
     * @param stage   Stage, a stage
     * @param scene   Scene, a scene
     * @param DB_PATH String, the path to the database
     */
    //--------------- METHODS ----------------
    public Controller(Stage stage, Scene scene, String DB_PATH) {
        this.stage = stage;
        prevScene = scene;
        this.DB_PATH = DB_PATH;
    }

    /**
     * Returns to previous stage
     */
    public void back() {
        stage.setScene(prevScene);
        stage.sizeToScene();
    }

    /**
     * Shows the controller's screen
     */
    public abstract void show() throws WindowLoadException;

    protected Object loadView(Class<?> viewController, String view) throws IOException {
        FXMLLoader loader = new FXMLLoader(viewController.getResource(view));
        loader.load();
        return loader.getController();
    }
}
