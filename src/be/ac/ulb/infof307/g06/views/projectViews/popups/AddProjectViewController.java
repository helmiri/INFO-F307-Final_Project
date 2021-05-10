package be.ac.ulb.infof307.g06.views.projectViews.popups;

import be.ac.ulb.infof307.g06.views.projectViews.ProjectsViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * The project creation scene
 */
public class AddProjectViewController extends ProjectInputViewController {
    //-------------- ATTRIBUTE ----------------
    @FXML
    private Button addProjectBtn;
    //--------------- METHODS ----------------

    /**
     * The main method for button's events.
     *
     * @param event ActionEvent, the event.
     */
    @FXML
    protected void events(ActionEvent event) {
        if (event.getSource() == addProjectBtn) {
            listener.onAddProject(getNameProject(), getDescriptionProject(), getDateProject(), getEndDateProject(), getSelectedTags(), getParentProjectName());
            close();
        }
    }

    public void init(ProjectsViewController.ViewListener listener, Stage stage) {
        super.init(listener, stage);
    }

}