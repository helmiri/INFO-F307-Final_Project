package be.ac.ulb.infof307.g06.views.connectionViews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

/**
 * The user agreement prompt
 */
public class ConditionsViewController {
    //-------------- ATTRIBUTES -------------
    @FXML
    private Button acceptConditionsBtn;
    @FXML
    private CheckBox acceptConditionsBox;

    private ViewListener listener;
    //-------------- METHODS -------------

    /**
     * When the 'accept' button is pressed, set the user in the database and close the stage.
     *
     * @param event ActionEvent, the event.
     */
    @FXML
    private void conditionsEvents(ActionEvent event) {
        if (event.getSource() == acceptConditionsBtn) {
            if (acceptConditionsBox.isSelected()) {
                listener.onConditionsAccepted();
            } else {
                listener.onConditionsDeclined();
            }
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

    /**
     * Communicates to the controller which button has been clicked
     */
    public interface ViewListener {
        void onConditionsAccepted();

        void onConditionsDeclined();
    }
}
