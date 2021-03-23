package be.ac.ulb.infof307.g06.views;

import be.ac.ulb.infof307.g06.Main;
import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.database.UserDB;
import be.ac.ulb.infof307.g06.models.Global;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.sql.SQLException;

public class LoginViewController {
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
    private Text loginErrMsg;
    @FXML
    private TextField logInUsernameField;
    @FXML
    private PasswordField logInPasswordField;
    private final LoginController controller = new LoginController();
    //--------------- METHODS ----------------
    /**
     * Initializes the database for the projects and users.
     *
     * @param url URL
     * @param resourceBundle ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            controller.init();
        } catch (SQLException | ClassNotFoundException throwables) {
            System.out.println("An error has occurred!");
        }
    }

    /**
     * The main method for button's events
     *
     * @param event ActionEvent
     * @throws SQLException throws database error
     * @throws IOException throws in and out exception error
     */
    @FXML
    private void events(ActionEvent event) throws SQLException, IOException {
        if (event.getSource() == connectionBtn) { logInConditions(); }
        else if (event.getSource() == registerBtn) { Main.showSignUpScene(); }
    }

    /**
     * Gets the log in informations and see if the user already exists.
     *
     * @throws SQLException throws database error
     * @throws IOException throws in and out exception error
     */
    @FXML
    private void logInConditions() throws SQLException, IOException {
        String passwd = getTextField(logInPasswordField);
        String user = getTextField(logInUsernameField);
        Global.userID = controller.validateUserID(passwd,user);
        switch (Global.userID) {
            case 0 -> loginErrMsg.setText("This user does not exist or the password/username is wrong");
            case -1 -> loginErrMsg.setText("This user is already connected");
            default -> Main.showMainMenuScene();
        }
    }

    /**
     * Returns the text inside a text field.
     *
     * @param textField TextField
     * @return String
     */
    @FXML
    public String getTextField(TextField textField){ return textField.getText(); }

}

