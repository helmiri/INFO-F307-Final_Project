package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
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
    protected final Scene prevScene;

    /**
     * Constructor
     *
     * @param stage The stage of the application
     */
    //--------------- METHODS ----------------
    public Controller(Stage stage) {
        this.stage = stage;
        prevScene = stage.getScene();
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
    public abstract void show() throws WindowLoadException, DatabaseException;

    protected Object loadView(Class<?> viewController, String view) throws IOException {
        FXMLLoader loader = new FXMLLoader(viewController.getResource(view));
        loader.load();
        return loader.getController();
    }
}
