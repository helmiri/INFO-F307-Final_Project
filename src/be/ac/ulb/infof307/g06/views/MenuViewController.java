package be.ac.ulb.infof307.g06.views;

import be.ac.ulb.infof307.g06.controllers.*;
import be.ac.ulb.infof307.g06.database.UserDB;
import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.Global;
import be.ac.ulb.infof307.g06.models.Project;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MenuViewController implements Initializable {
    //-------------- ATTRIBUTES ----------------
    @FXML
    private Button logOutBtn;
    @FXML
    private Button projectAccessBtn;
    @FXML
    private Button settingsAccessBtn;
    @FXML
    private Button profileAccessBtn;
    @FXML
    private Button calendarAccessBtn;
    @FXML
    private Button helpBtn;
    @FXML
    private Button projectManagementBtn;
    @FXML
    private Button statsAccessBtn;
    @FXML
    private Button mainMenuBtn;
    @FXML
    private Button tagsBtn;
    @FXML
    private Button languageBtn;
    @FXML
    private Button backBtn;
    private MainController controller;

    //--------------- METHODS ----------------
    /**
     * Initializes the controller and checks the invitations.
     *
     * @param url URL
     * @param resourceBundle ResourceBundle
     */
    public void initialize(URL url, ResourceBundle resourceBundle){
        controller = new MainController();
        controller.init(this);
        try {
            controller.checkInvites();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The main method for button's events.
     *
     * @param event ActionEvent
     */
    @FXML
    private void events(ActionEvent event) {
        if (event.getSource() == projectAccessBtn) { MainController.showProjectMenu(); }
        else if (event.getSource() == logOutBtn) {
            try {
                UserDB.disconnectUser(Global.userID);
            } catch (SQLException e) {
                MainController.alertWindow(Alert.AlertType.ERROR,"Error","Couldn't disconnect the user: "+e);
            }
            LoginController.show(); }
        else if (event.getSource() == mainMenuBtn) { MainController.showMainMenu(); }
        else if (event.getSource() == statsAccessBtn) { StatsController.show(); }
        else if (event.getSource() == settingsAccessBtn) { SettingsController.showSettingsMenu(); }
        else if (event.getSource() == projectManagementBtn) { ProjectController.show(); }
        else if (event.getSource() == tagsBtn) { SettingsController.showTagsMenu(); }
        else if (event.getSource() == languageBtn) { System.out.println("test language button"); }
        else if (event.getSource() == backBtn) { MainController.showMainMenu(); }
    }

    /**
     * Shows invitation popup.
     *
     * @param projectId int
     * @param senderId int
     */
    public void showInvitation(int projectId, int senderId){
        try {
            Project project = ProjectDB.getProject(projectId);
            Global.popupProjectTitle = project.getTitle();
            Global.popupProjectDescription = project.getDescription();
            Global.popupSenderUsername = UserDB.getUserInfo(senderId).get("uName");
            MainController.showInvitationStage();
        }catch(SQLException e){
            MainController.alertWindow(Alert.AlertType.ERROR,"Error", "An error has occurred with the database: "+e);
        }
    }
}
