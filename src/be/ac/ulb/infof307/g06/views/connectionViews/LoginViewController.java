package be.ac.ulb.infof307.g06.views.connectionViews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * The login stage
 */
public class LoginViewController{
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
     * The main method for button's events.
     *
     * @param event ActionEvent, the event.
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

    /**
     * Sets the listener.
     *
     * @param listener ViewListener, the listener to the controller.
     */
    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    /**
     * Used to communicate with the login controller
     */
    public interface ViewListener {
        void login(String username, String password);

        void signup();
    }

}

