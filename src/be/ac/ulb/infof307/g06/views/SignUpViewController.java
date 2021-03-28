package be.ac.ulb.infof307.g06.views;

import be.ac.ulb.infof307.g06.Main;
import be.ac.ulb.infof307.g06.models.Global;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpViewController {
    // ---------SIGN UP---------
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
    private Button signUpBtn;
    @FXML
    private Text signUpTxtPopup;

    @FXML
    private Button backBtn;

    // ---------TERMS AND CONDITIONS---------
    @FXML
    private Button acceptConditionsBtn;
    @FXML
    private CheckBox acceptConditionsBox;


    @FXML
    private void signUpEvents(ActionEvent event) throws Exception{
        if (event.getSource() == signUpBtn) {
            signUpConditions();
        }
        if (event.getSource() == acceptConditionsBtn) {
            if (acceptConditionsBox.isSelected()) {
                Global.userID = UserDB.addUser(Global.firstName, Global.lastName, Global.userName, Global.email, Global.passwd);
                Main.showMainMenuScene();
            }
        } else if (event.getSource() == backBtn) {
            Main.showLoginScene();
        }
    }

    /**
     * Creates an account with the sign up informations if they are valid, if not, shows up a pop up window
     * @throws Exception
     */
    @FXML
    private void signUpConditions() throws Exception{
        if (validateFirstName()
                && validateLastName()
                && validateUsername(signUpUsernameField)
                && validatePassword(signUpPasswordField)
                && validateEmail()
                && passwordConfirmationField.getText().equals(signUpPasswordField.getText())) {
            Global.firstName = firstNameField.getText();
            Global.lastName = lastNameField.getText();
            Global.email = emailField.getText();
            Global.userName = signUpUsernameField.getText();
            Global.passwd = signUpPasswordField.getText();
            //TODO: what if 2 users have the same email address?
            if (!(UserDB.userExists(Global.userName))) {
                Main.showConditionsStage();
            } else {
                signUpTxtPopup.setText("This user already exists");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wrong enters");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinWidth(900);
            alert.setContentText("One of the sign up options is wrong:\n" +
                    "   - The last name and the first name must not contain any special characters\n" +
                    "   - The email Address must be a valid one: 'an.example@gmail.com'\n" +
                    "   - The username must not contain any special characters or spaces (8 to 16 chars)\n" +
                    "   - The password must have at least one lowercase character, one uppercase character and one special character from '@#_$%!' (6 to 16 chars)\n");
            alert.showAndWait();
        }
    }

    /**
     * Checks if the string as at least one character(special characters excepted) and has 1 to 64 characters
     * @return boolean;
     */
    @FXML
    private boolean validateLastName(){
        Pattern p = Pattern.compile("^[^±!@£$%^&*_+§¡€#¢§¶•ªº«\\/<>?:;|=.,]{1,64}$");
        Matcher m = p.matcher(lastNameField.getText());
        return m.matches();
    }

    /**
     * Checks if the string as at least one character(special characters excepted) and has 1 to 64 characters
     * @return boolean;
     */
    @FXML
    private boolean validateFirstName(){
        Pattern p = Pattern.compile("^[^±!@£$%^&*_+§¡€#¢§¶•ªº«\\/<>?:;|=.,]{1,64}$");
        Matcher m = p.matcher(firstNameField.getText());
        return m.matches();
    }

    /**
     * Checks if the mail address is valid
     * @return boolean;
     */
    @FXML
    private boolean validateEmail(){
        Pattern p = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
        Matcher m = p.matcher(emailField.getText());
        return m.matches();
    }

    /**
     * Checks if the string has no special characters and has 6 to 16 characters
     * @return boolean;
     */
    @FXML
    private boolean validateUsername(TextField field){
        Pattern p = Pattern.compile("^[^±!@£$%^&*+§¡€#¢§¶•ª º«\\/<>?:;|=.,]{6,16}$");
        Matcher m = p.matcher(field.getText());
        return m.matches();
    }

    /**
     * Checks if the string as at least one upper and one lower case letter, one special character in the list and has 6 to 16 characters
     * @return boolean;
     */
    @FXML
    private boolean validatePassword(TextField field){
        Pattern p = Pattern.compile("((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#_$%!]).{6,16})");
        Matcher m = p.matcher(field.getText());
        return m.matches();
    }
}
