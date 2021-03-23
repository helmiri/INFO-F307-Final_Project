package be.ac.ulb.infof307.g06.views;

import be.ac.ulb.infof307.g06.Main;
import be.ac.ulb.infof307.g06.controllers.SignUpController;
import be.ac.ulb.infof307.g06.database.UserDB;
import be.ac.ulb.infof307.g06.models.UserInformations;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.SQLException;

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
     * @throws SQLException throws database exceptions
     * @throws IOException throws In and Out exceptions
     */
    @FXML
    private void signUpEvents(ActionEvent event) throws IOException, SQLException {
        if( event.getSource()== signUpBtn)  { signUpConditions() ;}
        else if( event.getSource()== backBtn){ Main.showLoginScene();}
    }

    /**
     * Creates an account with the sign up informations if they are valid, if not, shows up a pop up window.
     *
     * @throws SQLException throws database exceptions
     * @throws IOException throws In and Out exceptions
     */
    @FXML
    private void signUpConditions() throws SQLException, IOException {
        if (       controller.validateTextField(lastNameField,"^[^±!@£$%^&*_+§¡€#¢§¶•ªº«\\/<>?:;|=.,]{1,64}$")
                && controller.validateTextField(firstNameField,"^[^±!@£$%^&*_+§¡€#¢§¶•ªº«\\/<>?:;|=.,]{1,64}$")
                && controller.validateTextField(signUpUsernameField,"^[^±!@£$%^&*+§¡€#¢§¶•ª º«\\/<>?:;|=.,]{6,16}$")
                && controller.validateTextField(signUpPasswordField,"((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#_$%!]).{6,16})")
                && controller.validateTextField(emailField, "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
                && passwordConfirmationField.getText().equals(signUpPasswordField.getText())) {
            controller.setInformations(firstNameField.getText(),lastNameField.getText(),emailField.getText(),signUpUsernameField.getText(),signUpPasswordField.getText());
            if (!(UserDB.userExists(UserInformations.getUsername()))) { Main.showConditionsStage(); }
            else{signUpTxtPopup.setText("This user already exists!");}
        } else {
            alertWindow();
        }
    }

    /**
     *  Creates an alert window.
     */
    @FXML
    private void alertWindow(){
        //TODO à mettre dans le main controller
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Wrong enters");
        alert.setHeaderText(null);
        alert.getDialogPane().setMinWidth(900);
        alert.setContentText("One of the sign up options is wrong:\n   - The last name and the first name must not contain any special characters\n   - The email Address must be a valid one: 'an.example@gmail.com'\n   - The username must not contain any special characters or spaces (8 to 16 chars)\n   - The password must have at least one lowercase character, one uppercase character and one special character from '@#_$%!' (6 to 16 chars)\n");
        alert.showAndWait();
    }
}
