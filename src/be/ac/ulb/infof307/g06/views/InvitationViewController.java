package be.ac.ulb.infof307.g06.views;

import be.ac.ulb.infof307.g06.Main;
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
    //------------INVITATION--------------

    @FXML
    private Button acceptBtn;
    @FXML
    private Button declineBtn;
    @FXML
    private TextField senderNameTextField;
    @FXML
    private TextField projectNameTextField;
    @FXML
    private TextField descriptionTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        try{
            initFields();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    @FXML
    public void initFields() throws SQLException {
        String projectName = Global.popupProjectTitle;
        String projectDescription = Global.popupProjectDescription;
        String senderName = Global.popupSenderUsername;
        senderNameTextField.setText(senderName);
        projectNameTextField.setText(projectName);
        descriptionTextField.setText(projectDescription);
    }

    @FXML
    public void events(ActionEvent event) throws SQLException {
        if(event.getSource() == acceptBtn){
            acceptInvitation();
            Main.closeStage();
        }
        if(event.getSource() == declineBtn){
            declineInvitation();
            Main.closeStage();
        }
    }

    public void acceptInvitation() throws SQLException{
        ProjectDB.addCollaborator(ProjectDB.getProjectID(Global.popupProjectTitle), Global.userID);
        UserDB.removeInvitation(ProjectDB.getProjectID(Global.popupProjectTitle), Global.userID);
    }

    public void declineInvitation()throws SQLException{
        UserDB.removeInvitation(ProjectDB.getProjectID(Global.popupProjectTitle), Global.userID);
    }

}
