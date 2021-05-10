package be.ac.ulb.infof307.g06.views.projectViews;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The stage on which the user can select which cloud service provider to use
 */
public class CloudSelectionViewController {
    //-------------- ATTRIBUTES ----------------
    @FXML
    private AnchorPane layout;
    @FXML
    private Button GDriveButton;
    @FXML
    private CloudSelectionViewController.ViewListener listener;
    private Stage stage;
    //--------------- METHODS ----------------

    /**
     * Show the cloud selection stage.
     */
    public void show() {
        stage = new Stage();
        stage.setScene(new Scene(layout));
        stage.setTitle("Cloud service selection");
        stage.setOnCloseRequest(Event::consume);
        stage.showAndWait();
    }

    /**
     * The main method for cloud selection events.
     *
     * @param actionEvent ActionEvent, the event.
     */
    public void serviceSelect(ActionEvent actionEvent) {
        stage.close();
        if (actionEvent.getSource() == GDriveButton) {
            listener.selectGoogleDrive();
        } else {
            listener.selectDropBox();
        }
    }

    //--------------- LISTENER ----------------

    /**
     * Sets the listener.
     *
     * @param listener ViewListener, the listener to the controller.
     */
    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    /**
     * Communicates to the controller which cloud service the user has selected
     */
    public interface ViewListener {
        void selectGoogleDrive();

        void selectDropBox();
    }
}
