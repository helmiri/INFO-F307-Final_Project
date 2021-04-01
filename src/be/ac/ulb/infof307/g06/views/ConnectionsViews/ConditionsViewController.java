package be.ac.ulb.infof307.g06.views.ConnectionsViews;

import be.ac.ulb.infof307.g06.controllers.connection.LoginController;
import be.ac.ulb.infof307.g06.controllers.MainController;
import be.ac.ulb.infof307.g06.controllers.connection.SignUpController;
import be.ac.ulb.infof307.g06.models.Global;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

public class ConditionsViewController {
    //-------------- ATTRIBUTES -------------
    @FXML
    private Button acceptConditionsBtn;
    @FXML
    private CheckBox acceptConditionsBox;

    public ViewListener listener;
    //-------------- METHODS -------------

    /**
     * When the 'accept' button is pressed, checks set the user in the database and close the stage.
     *
     * @param event ActionEvent
     */
    @FXML
    private void conditionsEvents(ActionEvent event) {
        if (event.getSource() == acceptConditionsBtn) {
            if (acceptConditionsBox.isSelected()) {
                if (Global.userID != 0) {
                    listener.onConditionsAccepted();
                } else {
                    listener.onConditionsDeclined();
                }
            }
        }
    }

    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    public interface ViewListener {
        void onConditionsAccepted();

        void onConditionsDeclined();
    }
}
