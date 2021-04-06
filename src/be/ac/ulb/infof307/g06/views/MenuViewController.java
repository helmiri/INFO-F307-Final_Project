package be.ac.ulb.infof307.g06.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MenuViewController {
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

    private ViewListener listener;

    //--------------- METHODS ----------------


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
    public void showInvitation(int projectId, int senderId) {
        // TODO Needs user_db as parameter
        /*
        try {
            Project project = project_db.getProject(projectId);
            Global.popupProjectTitle = project.getTitle();
            Global.popupProjectDescription = project.getDescription();
            Global.popupSenderUsername = user_db.getUserInfo(senderId).getUserName();
            MainController.showInvitationStage();
        } catch (SQLException e) {
            new AlertWindow("Database error", "Could not access the database").errorWindow();
        }

         */
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
