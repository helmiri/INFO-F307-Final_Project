package be.ac.ulb.infof307.g06.views;

import be.ac.ulb.infof307.g06.database.*;
import be.ac.ulb.infof307.g06.Main;
import be.ac.ulb.infof307.g06.models.Global;
import be.ac.ulb.infof307.g06.database.ProjectDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    private Text wrongEntriesField;


    // ----------------DATABASE---------------
    private ProjectDB projectsDB = new ProjectDB("Database.db");
    private UserDB userDB = new UserDB("Database.db");
    public LoginViewController() throws SQLException, ClassNotFoundException {
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

}

