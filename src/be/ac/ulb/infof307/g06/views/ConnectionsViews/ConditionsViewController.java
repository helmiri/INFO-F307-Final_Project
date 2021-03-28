package be.ac.ulb.infof307.g06.views.ConnectionsViews;

import be.ac.ulb.infof307.g06.controllers.LoginController;
import be.ac.ulb.infof307.g06.controllers.MainController;
import be.ac.ulb.infof307.g06.controllers.SignUpController;
import be.ac.ulb.infof307.g06.models.Global;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

import java.io.IOException;

public class ConditionsViewController {
    //-------------- ATTRIBUTES -------------
    @FXML
    private Button acceptConditionsBtn;
    @FXML
    private CheckBox acceptConditionsBox;
    private final SignUpController controller=new SignUpController();
    //-------------- METHODS -------------

    /**
     * When the 'accept' button is pressed, checks set the user in the database and close the stage.
     *
     * @param event ActionEvent
     * @throws IOException throws In and Out exceptions
     */
    @FXML
    private void conditionsEvents(ActionEvent event) throws IOException {
        if (event.getSource() == acceptConditionsBtn) {
            if (acceptConditionsBox.isSelected()) {
                Global.userID = controller.setUserID();
                if (Global.userID != 0) {
                    MainController.closeStage();
                    MainController.showMainMenu();
                } else {
                    LoginController.show();
                }
            }
        }
    }
}
