package be.ac.ulb.infof307.g06.views.projectViews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class CloudSelectionViewController {

    @FXML
    private Button GDriveButton;
    @FXML
    private CloudSelectionViewController.ViewListener listener;
    private Stage stage;

    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    public void show(AnchorPane pane) {
        stage = new Stage();
        stage.setScene(new Scene(pane));
        stage.setTitle("Cloud service selection");
        stage.showAndWait();
    }

    public void serviceSelect(ActionEvent actionEvent) {
        stage.close();
        if (actionEvent.getSource() == GDriveButton) {
            listener.selectGoogleDrive();
        } else {
            listener.selectDropBox();
        }
    }

    public interface ViewListener {
        void selectGoogleDrive();
        void selectDropBox();
    }
}
