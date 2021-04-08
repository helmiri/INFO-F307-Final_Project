package be.ac.ulb.infof307.g06.views.ConnectionsViews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;

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
     * Button's events.
     *
     * @param event ActionEvent
     */
    @FXML
    private void signUpEvents(ActionEvent event) throws IOException {
        if (event.getSource() == signUpBtn) {
            listener.signup(firstNameField.getText(), lastNameField.getText(), signUpUsernameField.getText(), emailField.getText(), signUpPasswordField.getText(), passwordConfirmationField.getText());
        } else if (event.getSource() == backBtn) {
            listener.back();
        }
    }

    //--------------- LISTENER ----------------
    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    public interface ViewListener {
        void signup(String firstName, String lastName, String userName, String email, String password, String passwordConfirmation);

        void back();
    }
}
