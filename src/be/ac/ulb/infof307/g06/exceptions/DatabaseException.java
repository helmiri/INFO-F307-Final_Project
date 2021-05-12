package be.ac.ulb.infof307.g06.exceptions;

import be.ac.ulb.infof307.g06.models.AlertWindow;

/**
 * Exception to be thrown to the controllers when a database exception occurs
 */
public class DatabaseException extends Exception {
    private static final String windowTitle = "Database Error";
    private AlertWindow alert;

    /**
     * @param e error thrown
     */
    public DatabaseException(Throwable e) {
        super(e);
        alert = new AlertWindow(windowTitle, "A database access error has occurred", getMessage());
    }

    public DatabaseException(Throwable error, String errorMessage) {
        super(error);
        alert = new AlertWindow(windowTitle, errorMessage, getMessage());
    }

    /**
     * Shows the a prompt with the error message
     */
    public void show() {
        alert.showErrorWindow();
    }
}
