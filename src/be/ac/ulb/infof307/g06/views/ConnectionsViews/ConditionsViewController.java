package be.ac.ulb.infof307.g06.views.ConnectionsViews;

import be.ac.ulb.infof307.g06.Main;
import be.ac.ulb.infof307.g06.controllers.SignUpController;
import be.ac.ulb.infof307.g06.models.Global;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import java.io.IOException;
import java.sql.SQLException;

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
     * @throws SQLException throws database exceptions
     * @throws IOException throws In and Out exceptions
     */
    @FXML
    private void events(ActionEvent event) throws SQLException, IOException {
        if( event.getSource()== acceptConditionsBtn) {
            if (acceptConditionsBox.isSelected()) {
                Global.userID = controller.setUserID();
                Main.closeStage();
                Main.showMainMenuScene();
            }
        }
    }
}