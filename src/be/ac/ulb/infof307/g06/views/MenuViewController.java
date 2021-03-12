package be.ac.ulb.infof307.g06.views;

import be.ac.ulb.infof307.g06.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MenuViewController {

    // --------- MAIN MENU ------------
    /* DONE */

    @FXML
    private Button logOutBtn;
    @FXML
    private Button projectAccessBtn;

    /*TO DO*/

    @FXML
    private Button profileAccessBtn;
    @FXML
    private Button calendarAccessBtn;
    @FXML
    private Button settingsAccessBtn;
    @FXML
    private Button helpBtn;

    //----------- PROJECTS MENU -----------

    @FXML
    private Button projectManagementBtn;
    @FXML
    private Button statsAccessBtn;
    @FXML
    private Button mainMenuBtn;


    @FXML
    private void menuEvents(ActionEvent event) throws Exception {
        if( event.getSource() == projectAccessBtn) { Main.showProjectMenuScene();} //Access to the projects management menu
        else if( event.getSource() == logOutBtn) { Main.showLoginScene();} //Loging out
        else if( event.getSource() == mainMenuBtn) {Main.showMainMenuScene(); }
        else if( event.getSource() == statsAccessBtn) {Main.showStatisticsScene();}
        else if(event.getSource()  == projectManagementBtn){Main.showProjectManagementScene();}

    }
}
