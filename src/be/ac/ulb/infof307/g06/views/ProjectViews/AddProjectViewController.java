package be.ac.ulb.infof307.g06.views.ProjectViews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.sql.SQLException;

public class AddProjectViewController extends ProjectInputViewController{
    @FXML
    private Button addProjectBtn;

    @Override
    protected void events(ActionEvent event) throws SQLException {
        if (event.getSource() == addProjectBtn) {
            controller.addProject(this);
        }
    }

}