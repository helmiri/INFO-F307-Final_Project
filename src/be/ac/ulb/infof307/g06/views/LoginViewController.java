package be.ac.ulb.infof307.g06.views;

import be.ac.ulb.infof307.g06.Main;
import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.database.UserDB;
import be.ac.ulb.infof307.g06.models.Global;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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


    // ----------------DATABASE---------------
    private final ProjectDB projectsDB = new ProjectDB("Database.db");
    private final UserDB userDB = new UserDB("Database.db");

    public LoginViewController() throws SQLException, ClassNotFoundException {
    }

    // --------------------- METHODES -------------------------

    /**
     * The main method for button's events
     *
     * @param event;
     * @throws Exception;
     */
    @FXML
    private void buttonEvents(ActionEvent event) throws Exception {
        //TODO: Rename cette méthode en LogInEvents?
        if (event.getSource() == connectionBtn) {
            logInConditions();
        } else if (event.getSource() == registerBtn) {
            Main.showSignUpScene();
        }

    }

    /**
     * Gets the log in informations and see if the user already exists
     *
     * @throws Exception
     */
    @FXML
    private void logInConditions() throws Exception {
        String passwd = logInPasswordField.getText();
        String user = logInUsernameField.getText();

        Global.userID = UserDB.validateData(user, passwd);

        switch (Global.userID) {
            case 0 -> loginErrMsg.setText("This user does not exist or the password/username is wrong");
            case -1 -> loginErrMsg.setText("This user is already connected");
            default -> Main.showMainMenuScene();
        }
    }
}

