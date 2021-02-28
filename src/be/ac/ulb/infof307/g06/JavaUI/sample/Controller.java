package be.ac.ulb.infof307.g06.JavaUI.sample;

import javafx.application.Application;
import com.gluonhq.charm.glisten.control.TextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.ToggleSwitch;
import javafx.event.ActionEvent;

public class Controller {

    @FXML
    private AnchorPane root;

    @FXML
    private TextField Username;

    @FXML
    private TextField Password;

    @FXML
    private Button ConnectionButton;

    @FXML
    private Button register_btn;

    @FXML
    private ToggleSwitch toggle_switch;

    @FXML
    private void buttonEvents(ActionEvent event) {

    }

}