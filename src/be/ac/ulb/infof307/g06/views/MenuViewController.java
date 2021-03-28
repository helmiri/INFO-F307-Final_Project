package be.ac.ulb.infof307.g06.views;

import be.ac.ulb.infof307.g06.Main;
import be.ac.ulb.infof307.g06.controllers.MainController;
import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.database.UserDB;
import be.ac.ulb.infof307.g06.models.Global;
import be.ac.ulb.infof307.g06.models.Project;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MenuViewController implements Initializable {
    //-------------- ATTRIBUTES ----------------

    //* MAIN MENU *
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
    public Button cloudBtn;

    //* PROJECTS MENU *
    @FXML
    private Button projectManagementBtn;
    @FXML
    private Button statsAccessBtn;
    @FXML
    private Button mainMenuBtn;

    //* SETTINGS MENU *
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
     * @throws SQLException throws SQLException
     */
    @FXML
    private void events(ActionEvent event) throws Exception {
        if (event.getSource() == projectAccessBtn) { Main.showProjectMenuScene(); }
        else if (event.getSource() == logOutBtn) {
            UserDB.disconnectUser();
            Main.showLoginScene(); }
        else if (event.getSource() == mainMenuBtn) { Main.showMainMenuScene(); }
        else if (event.getSource() == statsAccessBtn) { Main.showStatisticsScene(); }
        else if (event.getSource() == settingsAccessBtn) { Main.showSettingsMenuScene(); }
        else if (event.getSource() == projectManagementBtn) { Main.showProjectManagementScene(); }
        else if (event.getSource() == tagsBtn) { Main.showTagsMenu(); }
        else if (event.getSource() == languageBtn) { System.out.println("test language button"); }
        else if (event.getSource() == backBtn) {
            Main.showMainMenuScene();
        } else if (event.getSource() == cloudBtn) {
            Main.showCloudSetting();
        }
    }

    /**
     * Shows invitation popup.
     *
     * @param projectId int
     * @param senderId int
     * @throws java.lang.Exception throws Exception
     */
    public void showInvitation(int projectId, int senderId)throws java.lang.Exception{
        Project project = ProjectDB.getProject(projectId);
        Global.popupProjectTitle = project.getTitle();
        Global.popupProjectDescription = project.getDescription();
        Global.popupSenderUsername = UserDB.getUserInfo(senderId).get("uName");
        Main.showInvitationStage();
    }
}
