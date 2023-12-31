package be.ac.ulb.infof307.g06.views.settingsViews;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

/**
 * The profile settings scene
 */
public class ProfileViewController {
    private ViewListener listener;
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
     * Retrieves the input data when the user clicks on the save button
     */
    @FXML
    private void save() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String newPassword = newPasswordField.getText();
        String confirmation = confirmationField.getText();
        if (!firstName.isBlank() && listener.saveFirstName(firstName)) {
            resetField(firstNameField, firstName);
        }
        if (!lastName.isBlank() && listener.saveLastName(lastName)) {
            resetField(lastNameField, lastName);
        }
        if (!email.isBlank() && listener.saveEmail(email)) {
            resetField(emailField, email);
        }
        if (!newPassword.isBlank()) {
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
    private void resetField(TextField field, String prompt) {
        field.clear();
        field.setPromptText(prompt);
    }

    /**
     * Controller communication
     */
    public interface ViewListener {
        /**
         * Save the new first name
         *
         * @param field The new first name
         * @return true on success, false otherwise
         */
        boolean saveFirstName(String field);

        /**
         * Save the new last name
         *
         * @param field The new last name
         * @return true on success, false otherwise
         */
        boolean saveLastName(String field);

        /**
         * Save the new email
         *
         * @param field The new email
         * @return true on success, false otherwise
         */
        boolean saveEmail(String field);

        /**
         * Saves the new password
         *
         * @param newPassword          The new entered password
         * @param confirmationPassword The entered password to confirm the validity
         */
        void savePassword(String newPassword, String confirmationPassword);
    }
}
