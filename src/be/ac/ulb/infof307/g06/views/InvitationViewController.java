package be.ac.ulb.infof307.g06.views;

import be.ac.ulb.infof307.g06.controllers.MainController;
import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.database.UserDB;
import be.ac.ulb.infof307.g06.models.Global;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class InvitationViewController implements Initializable {
    //-------------- ATTRIBUTES ----------------
    @FXML
    private Button acceptBtn;
    @FXML
    private Button declineBtn;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField senderNameTextField;
    @FXML
    private TextField projectNameTextField;
    //--------------- METHODS ----------------
    /**
     * Launchs the initFields method.
     *
     * @param url URL
     * @param resourceBundle ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        initFields();
    }

    /**
     * Initializes the fields related to the edition of a project.
     *
     * @throws SQLException throws SQLException
     */
    @FXML
    public void initFields() {
        String projectName = Global.popupProjectTitle;
        String projectDescription = Global.popupProjectDescription;
        String senderName = Global.popupSenderUsername;
        senderNameTextField.setText(senderName);
        projectNameTextField.setText(projectName);
        descriptionTextField.setText(projectDescription);
    }

    /**
     * The main method for button's events.
     *
     * @param event ActionEvent
     * @throws SQLException throws SQLException
     */
    @FXML
    public void events(ActionEvent event) throws SQLException{
        if(event.getSource() == acceptBtn){
            acceptInvitation();
            MainController.closeStage();
        }
        if(event.getSource() == declineBtn){
            declineInvitation();
            MainController.closeStage();
        }
    }

    /**
     * Accepts an invitation.
     *
     * @throws SQLException throws SQLException
     */
    public void acceptInvitation() throws SQLException{
        ProjectDB.addCollaborator(ProjectDB.getProjectID(Global.popupProjectTitle), Global.userID);
        UserDB.removeInvitation(ProjectDB.getProjectID(Global.popupProjectTitle), Global.userID);
    }

    /**
     * Declines an invitation.
     *
     * @throws SQLException throws SQLException
     */
    public void declineInvitation() throws SQLException{
        UserDB.removeInvitation(ProjectDB.getProjectID(Global.popupProjectTitle), Global.userID);
    }

}
