package be.ac.ulb.infof307.g06.JavaUI.sample;


import be.ac.ulb.infof307.g06.Main;
import com.gluonhq.charm.glisten.control.TextField;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.controlsfx.control.ToggleSwitch;

public class Controller {
    private Main main;

    // --------------------- ATTRIBUTS -------------------------

    @FXML
    private Text helloUser;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button connectionBtn;

    @FXML
    private Button registerBtn;

    @FXML
    private ToggleSwitch toggle_switch;

    // --------------------- METHODES -------------------------
    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField emailAddress;

    @FXML
    private TextField usernameRegister;

    @FXML
    private TextField registerPassword;

    @FXML
    private TextField passwordConfirmation;

    @FXML
    private Button SignUpBtn;

    @FXML
    private Button goToConnection;

    @FXML
    private Button acceptConditionsBtn;

    @FXML
    private CheckBox acceptConditionsBox;


    @FXML
    private void buttonEvents(ActionEvent event) throws Exception {
        if( event.getSource()== connectionBtn){
            String passwd;
            String user = username.getText();
            passwd = password.getText();
            mainMenu(event, passwd, user);
        }

        else if( event.getSource()== SignUpBtn     )  {
            main.showConditionsScene();
        }

        else if( event.getSource()== registerBtn   )  {main.showRegisterScene()  ;}
        else if( event.getSource()== goToConnection)  {main.showConnectionScene();}
        else if( event.getSource()== acceptConditionsBtn){
            if(acceptConditionsBox.isSelected()) {
                main.showStatisticsScene();

            }
        }

    }

    @FXML
    private void mainMenu(ActionEvent event, String passwd, String user) throws Exception {
        /*
            Creates a delay to say hello to the user before going to the main menu.
         */
        PauseTransition delay = new PauseTransition(Duration.seconds(0.3));
        delay.setOnFinished(actionEvent -> helloUser.setText("Bienvenue " + user + "!"));
        delay.play();
        main.showStatisticsScene();

    }
}