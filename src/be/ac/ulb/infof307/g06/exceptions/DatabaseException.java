package be.ac.ulb.infof307.g06.exceptions;

import be.ac.ulb.infof307.g06.models.AlertWindow;

/**
 * Exception to be thrown to the controllers when a database exception occurs
 */
public class DatabaseException extends Exception {
    AlertWindow alert;

    /**
     * @param e error thrown
     */
    public DatabaseException(Throwable e) {
        super(e);
        alert = new AlertWindow("Database Error", "A database access error has occurred", getMessage());
    }

    /**
     * Shows the a prompt with the error message
     */
    public void show() {
        alert.showErrorWindow();
    }
}
