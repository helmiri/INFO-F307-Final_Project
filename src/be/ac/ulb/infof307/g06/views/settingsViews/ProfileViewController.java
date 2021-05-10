package be.ac.ulb.infof307.g06.views.settingsViews;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

/**
 * Controller for the profile menu.
 */
public class ProfileViewController {
    ViewListener listener;
    @FXML
    private AnchorPane pane;
    @FXML
    private Button saveButton;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmationField;
    @FXML
    private Text userName;

    /**
     * Sets this class' listener
     *
     * @param profileController The listener
     */
    public void setListener(ViewListener profileController) {
        listener = profileController;
    }

    /**
     * Sets the text fields prompt texts
     *
     * @param userName  The current username
     * @param firstName The current first name
     * @param lastName  The current last name
     * @param email     The current email
     */
    public void initialize(String userName, String firstName, String lastName, String email) {
        this.userName.setText(userName);
        firstNameField.setPromptText(firstName);
        lastNameField.setPromptText(lastName);
        emailField.setPromptText(email);
    }

    /**
     * Retrieves the input data
     *
     * @param actionEvent Should only be triggered by the save button
     */
    @FXML
    private void save(ActionEvent actionEvent) {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String newPassword = newPasswordField.getText();
        String confirmation = confirmationField.getText();
        if (!firstName.isBlank()) {
            if (listener.saveFirstName(firstName)) {
                resetFIeld(firstNameField, firstName);
            }
        } else if (!lastName.isBlank()) {
            if (listener.saveLastName(lastName)) {
                resetFIeld(lastNameField, lastName);
            }
        } else if (!email.isBlank()) {
            if (listener.saveEmail(email)) {
                resetFIeld(emailField, email);
            }
        } else if (!newPassword.isBlank()) {
            listener.savePassword(newPassword, confirmation);
            newPasswordField.clear();
            confirmationField.clear();
        }
    }

    /**
     * Clears the text field and sets a new prompt
     *
     * @param field  The Text field
     * @param prompt The new prompt
     */
    private void resetFIeld(TextField field, String prompt) {
        field.clear();
        field.setPromptText(prompt);
    }

    /**
     * Communicates to the controller which button has been pressed.
     */
    public interface ViewListener {
        boolean saveFirstName(String field);

        boolean saveLastName(String field);

        boolean saveEmail(String field);

        void savePassword(String newPassword, String confirmationPassword);
    }
}
