package be.ac.ulb.infof307.g06.exceptions;

import be.ac.ulb.infof307.g06.models.AlertWindow;

public class DatabaseException extends Exception {
    AlertWindow alert;

    public DatabaseException(Throwable e) {
        super(e);
        alert = new AlertWindow("Database Error", "A database access error has occurred", getMessage());
    }

    public void show() {
        alert.showErrorWindow();
    }
}
