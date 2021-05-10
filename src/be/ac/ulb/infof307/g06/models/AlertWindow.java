package be.ac.ulb.infof307.g06.models;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Region;



/**
 * Creates and shows alert windows with information
 */
public class AlertWindow {
    Alert alert;
    String title;
    String headerText;
    DialogPane dialogPane;
    String message;

    /**
     * Creates an alert window
     *
     * @param title   The window title
     * @param message The message to be displayed
     */
    public AlertWindow(String title, String message) {
        this.title = title;
        this.message = message;
        headerText = null;
    }

    /**
     * Creates an alert window
     *
     * @param title      The window title
     * @param headerText The header message
     * @param message    The message to be displayed
     */
    public AlertWindow(String title, String headerText, String message) {
        this(title, message);
        this.headerText = headerText;
    }

    /**
     * Handles the window's attributes
     */
    public void setAttributes() {
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        resize();
        dialogPane = alert.getDialogPane();
    }

    /**
     * Sets the size of the window to adjust to the text
     */
    public void resize() {
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
    }

    /**
     * Displays an information alert window
     */
    public void showInformationWindow() {
        alert = new Alert(Alert.AlertType.INFORMATION);
        setAttributes();
        alert.showAndWait();
    }

    /**
     * Displays an error alert window
     */
    public void showErrorWindow() {
        alert = new Alert(Alert.AlertType.ERROR);
        setAttributes();
        alert.showAndWait();
    }

    /**
     * Displays a warning alert window
     */
    public void showWarningWindow() {
        alert = new Alert(Alert.AlertType.WARNING);
        setAttributes();
        alert.showAndWait();
    }
}
