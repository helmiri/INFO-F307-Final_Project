package be.ac.ulb.infof307.g06.views.connectionViews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * The signup scene
 */
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
    private ViewListener listener;

    //---------------- METHODS ----------------

    /**
     * The main method for button's events.
     *
     * @param event ActionEvent, the event.
     */
    @FXML
    private void signUpEvents(ActionEvent event) {
        if (event.getSource() == signUpBtn) {
            listener.signup(firstNameField.getText(), lastNameField.getText(), signUpUsernameField.getText(), emailField.getText(), signUpPasswordField.getText(), passwordConfirmationField.getText());
        } else if (event.getSource() == backBtn) {
            listener.back();
        }
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
     * Communicates to the controller which button has been clicked
     */
    public interface ViewListener {
        /**
         * On signup  button clicked
         *
         * @param firstName            The first name entered
         * @param lastName             The last name entered
         * @param userName             The username entered
         * @param email                The email entered
         * @param password             The password entered
         * @param passwordConfirmation The confirmation password entered
         */
        void signup(String firstName, String lastName, String userName, String email, String password, String passwordConfirmation);

        /**
         * Go back to previous scene
         */
        void back();
    }
}
