package be.ac.ulb.infof307.g06.models;

import javafx.scene.control.Alert;
import javafx.scene.layout.Region;


/**
 * Creates and shows alert windows with information
 */
public class AlertWindow {
    private final String title;
    private final String message;
    private Alert alert;
    private String headerText;

    /**
     * Creates an alert window
     *
     * @param newTitle   The window newTitle
     * @param newMessage The newMessage to be displayed
     */
    public AlertWindow(String newTitle, String newMessage) {
        title = newTitle;
        message = newMessage;
        headerText = null;
    }

    /**
     * Creates an alert window
     *
     * @param newTitle   The window newTitle
     * @param newHeader  The header newMessage
     * @param newMessage The newMessage to be displayed
     */
    public AlertWindow(String newTitle, String newHeader, String newMessage) {
        this(newTitle, newMessage);
        headerText = newHeader;
    }


    /**
     * Handles the window's attributes
     */
    public void setAttributes() {
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        resize();
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
