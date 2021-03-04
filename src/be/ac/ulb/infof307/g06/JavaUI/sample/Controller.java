package be.ac.ulb.infof307.g06.JavaUI.sample;


import be.ac.ulb.infof307.g06.Main;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Controller {
    // --------------------- ATTRIBUTS -------------------------

    // ---------CONNECTION---------
    @FXML
    private TextField logInUsername;

    @FXML
    private PasswordField logInPassword;

    @FXML
    private Button connectionBtn;

    @FXML
    private Button registerBtn;

    // ---------SIGN UP---------
    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField email;

    @FXML
    private TextField signUpUsername;

    @FXML
    private TextField signUpPassword;

    @FXML
    private TextField passwordConfirmation;

    @FXML
    private Button SignUpBtn;

    @FXML
    private Button goToConnection;

    // ---------TERMS AND CONDITIONS---------

    @FXML
    private Button acceptConditionsBtn;

    @FXML
    private CheckBox acceptConditionsBox;

    // ---------STATISTICS---------

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
                && logInUsername.getText().length() <= 16
                && logInUsername.getText().length() > 8
                && logInPassword.getText().length() <= 16
                && logInPassword.getText().length() > 8 ){
            String passwd = logInPassword.getText();
            String user = logInUsername.getText();
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

    /*
    * IN PROGRESS
    @FXML
    private void logInConditions() throws Exception{
        if(        logInUsername.getText().length() <= 16
                && logInUsername.getText().length() >   8
                && logInPassword.getText().length() <= 16
                && logInPassword.getText().length() >   8){

        }
    }
    */


    @FXML
    private void mainMenu(String user) throws Exception {
        /*
            Creates a delay to say hello to the user before going to the main menu.
         */
        //PauseTransition delay = new PauseTransition(Duration.seconds(0.3));
        //delay.setOnFinished(actionEvent->helloUser.setText("Bienvenue " + user + "!"));
        //delay.play();
        Main.showStatisticsScene();

    }
}