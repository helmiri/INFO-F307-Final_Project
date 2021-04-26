package be.ac.ulb.infof307.g06.models;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

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

    public void setAttributes() {
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        dialogPane = alert.getDialogPane();
    }

    public void resize(int height, int width) {
        dialogPane.setMinWidth(width);
        dialogPane.setMaxHeight(height);
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
