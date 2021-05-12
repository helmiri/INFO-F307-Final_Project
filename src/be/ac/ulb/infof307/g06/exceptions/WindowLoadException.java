package be.ac.ulb.infof307.g06.exceptions;

import be.ac.ulb.infof307.g06.models.AlertWindow;

public class WindowLoadException extends Exception {
    AlertWindow alert;

    /**
     * @param e error thrown
     */
    public WindowLoadException(Throwable e) {
        super(e);
        alert = new AlertWindow("Loading error", "Could not load the window", getMessage());
    }

    /**
     * Shows the a prompt with the error message
     */
    public void show() {
        alert.showErrorWindow();
    }
}
