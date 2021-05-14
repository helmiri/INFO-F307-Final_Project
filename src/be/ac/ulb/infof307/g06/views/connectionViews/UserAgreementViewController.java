package be.ac.ulb.infof307.g06.views.connectionViews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The user agreement prompt
 */
public class UserAgreementViewController {

    //-------------- ATTRIBUTES -------------
    @FXML
    private AnchorPane layout;
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


    public void show(Stage stage) {
        stage.setTitle("User Agreement");
        stage.setScene(new Scene(layout));
        stage.sizeToScene();
        stage.showAndWait();
    }

    /**
     * Communicates to the controller which button has been clicked
     */
    public interface ViewListener {
        /**
         * Action to be performed when the user accepts the agreement
         */
        void onConditionsAccepted();

        /**
         * Action to be performed when the user declines the agreement
         */
        void onConditionsDeclined();
    }
}
