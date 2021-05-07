package be.ac.ulb.infof307.g06.models;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Region;

import java.util.Optional;

public class AlertWindow {
    Alert alert;
    String title;
    String headerText;
    DialogPane dialogPane;
    String message;

    public AlertWindow(String title, String message) {
        this.title = title;
        this.message = message;
        headerText = null;
    }

    public AlertWindow(String title, String message, String header) {
        this(title, header);
        headerText = message;
    }

    public void setAttributes() {
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        resize();
        dialogPane = alert.getDialogPane();
    }

    public void resize() {
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
    }

    public void informationWindow() {
        alert = new Alert(Alert.AlertType.INFORMATION);
        setAttributes();
        alert.showAndWait();
    }

    public boolean confirmationWindow() {
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        setAttributes();
        Optional<ButtonType> result = alert.showAndWait();
        return result.filter(buttonType -> buttonType == ButtonType.OK).isPresent();
    }

    public void errorWindow() {
        alert = new Alert(Alert.AlertType.ERROR);
        setAttributes();
        alert.showAndWait();
    }

    public void warningWindow() {
        alert = new Alert(Alert.AlertType.WARNING);
        setAttributes();
        alert.showAndWait();
    }
}
