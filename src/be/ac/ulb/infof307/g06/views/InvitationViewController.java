package be.ac.ulb.infof307.g06.views;

import be.ac.ulb.infof307.g06.controllers.MainController;
import be.ac.ulb.infof307.g06.models.Global;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
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
     */
    @FXML
    public void initFields(){
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
     */
    @FXML
    public void events(ActionEvent event){
        if(event.getSource() == acceptBtn){
            MainController.invitation(true, Global.popupProjectTitle, Global.userID);
            MainController.closeStage();
        }
        if(event.getSource() == declineBtn){
            MainController.invitation(false, Global.popupProjectTitle, Global.userID);
            MainController.closeStage();
        }
    }


}
