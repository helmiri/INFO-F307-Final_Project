package be.ac.ulb.infof307.g06.JavaUI.sample;

import be.ac.ulb.infof307.g06.database.*;
import be.ac.ulb.infof307.g06.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
    // --------------------- ATTRIBUTS -------------------------
    // ---------CONNECTION---------
    @FXML
    private TextField logInUsernameField;
    @FXML
    private PasswordField logInPasswordField;
    @FXML
    private Button connectionBtn;
    @FXML
    private Button registerBtn;
    @FXML
    private Text wrongEntriesField;
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
    private Button goToConnectionBtn;
    @FXML
    private Text signUpTxtPopup;

    // ---------TERMS AND CONDITIONS---------
    @FXML
    private Button acceptConditionsBtn;
    @FXML
    private CheckBox acceptConditionsBox;

    // -------- PROJECT MENU ----------
    @FXML
    private Button projectMangementBtn;
    @FXML
    private Button statsAccessBtn;
    @FXML
    private Button mainMenuBtn;

    // --------- MAIN MENU ------------
    @FXML
    private Button profileAccessBtn;
    @FXML
    private Button projectAccessBtn;
    @FXML
    private Button calendarAccessBtn;
    @FXML
    private Button settingsAccessBtn;
    @FXML
    private Button helpBtn;
    @FXML
    private Button logOutBtn;
    @FXML
    private Button goToStatsBtn;
    @FXML
    private Button backToMainMenu;

    // ----------------DATABASE---------------
    private ProjectDB projectsDB = new ProjectDB("Database.db");
    private UserDB userDB = new UserDB("Database.db");
    public Controller() throws SQLException, ClassNotFoundException {
    }

    // --------------------- METHODES -------------------------
    /**
     * The main method for button's events
     * @param event;
     * @throws Exception;
     */
    @FXML
    private void buttonEvents(ActionEvent event) throws Exception {
        //TODO: Rename cette méthode en LogInEvents?
        if( event.getSource()== connectionBtn      )  { logInConditions(); }
        else if( event.getSource()== registerBtn   )  { Main.showSignUpScene();  ;}
        else if( event.getSource()== signUpBtn)  { signUpConditions()        ;}
        else if( event.getSource()== goToConnectionBtn)  { Main.showLoginScene();}
        else if( event.getSource() == logOutBtn) { Main.showLoginScene();}
        else if( event.getSource() == projectAccessBtn) { Main.showProjectMenuScene();}
        else if( event.getSource() == mainMenuBtn) {Main.showMainMenuScene(); }
        else if( event.getSource() == statsAccessBtn) {Main.showStatisticsScene();}
        else if(event.getSource()  == projectMangementBtn){Main.showProjectManagementScene();}
        else if( event.getSource() == goToStatsBtn) { Main.showStatisticsScene(); }
        else if( event.getSource() == backToMainMenu) {Main.showMainMenuScene();}
        if( event.getSource()== acceptConditionsBtn){
            if(acceptConditionsBox.isSelected()){
                Global.userID= UserDB.addUser(Global.firstName,Global.lastName,Global.username,Global.email,Global.passwd);
                Main.closeConditionsStage();
                Main.showMainMenuScene();
            }
        }

    }

    /**
     * Gets the log in informations and see if the user already exists
     * @throws Exception
     */
    @FXML
    private void logInConditions() throws Exception{
        String passwd = logInPasswordField.getText();
        String user = logInUsernameField.getText();

        Global.userID= UserDB.validateData(user,passwd);
        if(Global.userID!=0){ Main.showMainMenuScene(); }
        else{wrongEntriesField.setText("This user does not exist or the password/username is wrong");}
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
            Global.username = signUpUsernameField.getText();
            Global.passwd = signUpPasswordField.getText();
            //TODO: what if 2 users have the same email address?
            if (!(UserDB.userExists(Global.username))) { Main.showConditionsStage(); }
            else{signUpTxtPopup.setText("This user already exists");}
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

