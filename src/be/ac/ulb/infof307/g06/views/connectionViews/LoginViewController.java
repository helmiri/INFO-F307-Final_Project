package be.ac.ulb.infof307.g06.views.connectionViews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginViewController implements Initializable {
    //-------------- ATTRIBUTES ----------------
    @FXML
    private Button connectionBtn;
    @FXML
    private Button registerBtn;
    @FXML
    private Text loginErrMsg;
    @FXML
    private TextField logInUsernameField;
    @FXML
    private PasswordField logInPasswordField;

    protected ViewListener listener;
    //--------------- METHODS ----------------

    /**
     * Initializes the database for the projects and users.
     *
     * @param url            URL
     * @param resourceBundle ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    /**
     * The main method for button's events
     *
     * @param event ActionEvent
     */
    @FXML
    private void logInEvents(ActionEvent event) {
        if (event.getSource() == connectionBtn) {
            listener.login(getTextField(logInUsernameField), getTextField(logInPasswordField));
        } else if (event.getSource() == registerBtn) {
            listener.signup();
        }
    }


    /**
     * Gets the log in informations and see if the user already exists.
     */
    /*
    @FXML
    private void logInConditions() {
        String passwd = getTextField(logInPasswordField);
        String user = getTextField(logInUsernameField);
        Global.userID = controller.validateUserID(passwd,user);
        switch (Global.userID) {
            case 0 -> loginErrMsg.setText("This user does not exist or the password/username is wrong");
            case -1 -> loginErrMsg.setText("This user is already connected");
            default -> MainController.showMainMenu();
        }
    }
    */

    /**
     * Returns the text inside a text field.
     *
     * @param textField TextField
     * @return String
     */
    @FXML
    public String getTextField(TextField textField) {
        return textField.getText();
    }

    //--------------- LISTENER ----------------
    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    public interface ViewListener {
        void login(String username, String password);

        void signup();
    }

}

