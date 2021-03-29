package be.ac.ulb.infof307.g06.views.ProjectViews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AddProjectViewController extends ProjectInputViewController{
    //-------------- ATTRIBUTE ----------------
    @FXML
    private Button addProjectBtn;

    //--------------- METHOD ----------------
    /**
     * The main method for button's events
     *
     * @param event ActionEvent
     */
    @Override
    protected void events(ActionEvent event){
        if (event.getSource() == addProjectBtn) {
            controller.addProject(this);
        }
    }

}