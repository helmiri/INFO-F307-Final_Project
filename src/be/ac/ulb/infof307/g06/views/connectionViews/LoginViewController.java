package be.ac.ulb.infof307.g06.views.connectionViews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * The login stage
 */
public class LoginViewController {
    //-------------- ATTRIBUTES ----------------
    @FXML
    private AnchorPane layout;
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

    private ViewListener listener;
    //--------------- METHODS ----------------

    /**
     * The main method for button's events.
     *
     * @param event ActionEvent, the event.
     */
    @FXML
    private void logInEvents(ActionEvent event) {
        if (event.getSource() == connectionBtn) {
            loginErrMsg.setText(listener.login(getTextField(logInUsernameField), getTextField(logInPasswordField)));
        } else if (event.getSource() == registerBtn) {
            listener.signupClicked();
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
     * Sets the newListener.
     *
     * @param newListener ViewListener, the newListener to the controller.
     */
    public void setListener(ViewListener newListener) {
        listener = newListener;
    }

    /**
     * Initializes the scene
     *
     * @param stage The application stage where the scene will be set
     */
    public void show(Stage stage) {
        stage.setScene(new Scene(layout));
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Used to communicate with the login controller
     */
    public interface ViewListener {
        /**
         * @param username The username entered
         * @param password The password entered
         */
        String login(String username, String password);

        /**
         * On signup button clicked
         */
        void signupClicked();
    }

}

