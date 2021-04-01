package be.ac.ulb.infof307.g06.views;

import be.ac.ulb.infof307.g06.controllers.*;
import be.ac.ulb.infof307.g06.controllers.connection.LoginController;
import be.ac.ulb.infof307.g06.controllers.project.ProjectController;
import be.ac.ulb.infof307.g06.models.Global;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
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
    public Button storageBtn;
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
    public ViewListener listener;

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
    private void events(ActionEvent event) throws Exception {
        if (event.getSource() == projectAccessBtn) {
            listener.showProjectsMenu();
        } else if (event.getSource() == logOutBtn) {
            listener.logout();
        } else if (event.getSource() == mainMenuBtn) {
            listener.showMainMenu();
        } else if (event.getSource() == statsAccessBtn) {
            listener.showStats();
        } else if (event.getSource() == settingsAccessBtn) {
            listener.showSettings();
        } else if (event.getSource() == projectManagementBtn) {
            listener.showProjects();
        } else if (event.getSource() == tagsBtn) {
            listener.showTags();
        } else if (event.getSource() == languageBtn) {
            System.out.println("test language button");
        } else if (event.getSource() == backBtn) {
            listener.showMainMenu();
        } else if (event.getSource() == storageBtn) {
            listener.showStorage();
        }
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
        } catch (SQLException e) {
            MainController.alertWindow(Alert.AlertType.ERROR, "Error", "An error has occurred with the database: " + e);
        }
    }

    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    public interface ViewListener {
        void showMainMenu();

        void showProjectsMenu();

        void showProjects();

        void showStats();

        void showStorage();

        void showSettings();

        void showTags();

        void logout();
    }
}
