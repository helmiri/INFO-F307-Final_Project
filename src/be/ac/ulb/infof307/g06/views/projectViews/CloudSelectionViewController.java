package be.ac.ulb.infof307.g06.views.projectViews;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class CloudSelectionViewController {
    //-------------- ATTRIBUTES ----------------
    @FXML
    private Button GDriveButton;
    @FXML
    private CloudSelectionViewController.ViewListener listener;
    private Stage stage;
    //--------------- METHODS ----------------

    /**
     * Show the cloud selection stage.
     *
     * @param pane AnchorPane, the pane for the cloud selection.
     */
    public void show(AnchorPane pane) {
        stage = new Stage();
        stage.setScene(new Scene(pane));
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

    public interface ViewListener {
        void selectGoogleDrive();
        void selectDropBox();
    }
}
