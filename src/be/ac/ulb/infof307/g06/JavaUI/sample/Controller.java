package be.ac.ulb.infof307.g06.JavaUI.sample;


import be.ac.ulb.infof307.g06.Main;
import com.gluonhq.charm.glisten.control.TextField;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.controlsfx.control.ToggleSwitch;

public class Controller {
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

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField emailAddress;

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
    private Button visualiseBtn;

    // --------------------- METHODES -------------------------

    @FXML
    private void buttonEvents(ActionEvent event) throws Exception {
        //TODO: Rename cette méthode en LogInEvents?
        //TODO: Changer "16" par une constante ou bien une valeur récupérer maximale
        //TODO: Méthode pour les conditions de connection
        //TODO: Méthode pour les conditions de register
        if( event.getSource()== connectionBtn
                && username.getText().length() <= username.getMaxLength()
                && username.getText().length() > 8
                && password.getText().length() < 16
                && password.getText().length() > 8 ){
            String passwd = password.getText();
            String user = username.getText();
            mainMenu(user);
        }

        else if( event.getSource()== registerBtn   )  { Main.showRegisterScene()  ;}

        else if( event.getSource()== SignUpBtn     )  { Main.showConditionsScene();}

        else if( event.getSource()== goToConnection)  { Main.showConnectionScene();}

        else if( event.getSource()== acceptConditionsBtn){
            if(acceptConditionsBox.isSelected()){
                Main.showStatisticsScene();
                Main.closeConditionsStage();
            }
        }
        else if(event.getSource() ==visualiseBtn){
            Main.showVisualScene();
        }
    }

    @FXML
    private void mainMenu(String user) throws Exception {
        /*
            Creates a delay to say hello to the user before going to the main menu.
         */
        PauseTransition delay = new PauseTransition(Duration.seconds(0.3));
        delay.setOnFinished(actionEvent->helloUser.setText("Bienvenue " + user + "!"));
        delay.play();
        Main.showStatisticsScene();

    }
}