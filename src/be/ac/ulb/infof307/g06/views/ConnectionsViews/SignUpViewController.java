package be.ac.ulb.infof307.g06.views.ConnectionsViews;

import be.ac.ulb.infof307.g06.controllers.LoginController;
import be.ac.ulb.infof307.g06.controllers.MainController;
import be.ac.ulb.infof307.g06.controllers.SignUpController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class SignUpViewController {
    //-------------- ATTRIBUTES ----------------
    @FXML
    private Button signUpBtn;
    @FXML
    private Button backBtn;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField signUpUsernameField;
    @FXML
    private TextField signUpPasswordField;
    @FXML
    private TextField passwordConfirmationField;
    @FXML
    private Text signUpTxtPopup;
    private final SignUpController controller=new SignUpController();
    //---------------- METHODS ----------------

    /**
     * Button's events.
     *
     * @param event ActionEvent
     */
    @FXML
    private void signUpEvents(ActionEvent event) {
        if( event.getSource()== signUpBtn)  { signUpConditions() ;}
        else if( event.getSource()== backBtn){ LoginController.show();}
    }

    /**
     * Creates an account with the sign up informations if they are valid, if not, shows up a pop up window.
     *
     */
    @FXML
    private void signUpConditions() {
        if (       controller.validateTextField(lastNameField,"^[^±!@£$%^&*_+§¡€#¢§¶•ªº«\\/<>?:;|=.,]{1,64}$")
                && controller.validateTextField(firstNameField,"^[^±!@£$%^&*_+§¡€#¢§¶•ªº«\\/<>?:;|=.,]{1,64}$")
                && controller.validateTextField(signUpUsernameField,"^[^±!@£$%^&*+§¡€#¢§¶•ª º«\\/<>?:;|=.,]{6,16}$")
                && controller.validateTextField(signUpPasswordField,"((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#_$%!]).{6,16})")
                && controller.validateTextField(emailField, "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
                && passwordConfirmationField.getText().equals(signUpPasswordField.getText())) {
            controller.setInformations(firstNameField.getText(),lastNameField.getText(),emailField.getText(),signUpUsernameField.getText(),signUpPasswordField.getText());
            if (!(controller.doesUserExist())) { SignUpController.showConditionStage(); }
            else{signUpTxtPopup.setText("This user already exists!");}
        } else {
            String contextText = """
                    One of the sign up options is wrong:
                       - The last name and the first name must not contain any special characters
                       - The email Address must be a valid one: 'an.example@gmail.com'
                       - The username must not contain any special characters or spaces (8 to 16 chars)
                       - The password must have at least one lowercase character, one uppercase character and one special character from '@#_$%!' (6 to 16 chars)
                    """;
            MainController.alertWindow(Alert.AlertType.WARNING,"Wrong enters",contextText);
        }
    }
}
