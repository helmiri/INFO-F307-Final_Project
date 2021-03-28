package be.ac.ulb.infof307.g06.views;

import be.ac.ulb.infof307.g06.controllers.SettingsController;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;

public class StorageViewController {
    public Button backBtn;
    public TextField accTokenField;
    public TextField clientIDField;
    public Button saveBtn;
    public AnchorPane anchorPane;
    public Button helpBtn;

    public void events(ActionEvent actionEvent) throws Exception {

        if (actionEvent.getSource() == backBtn) {
            SettingsController.showSettingsMenu();
        } else if (actionEvent.getSource() == saveBtn) {
            saveSettings();
        } else if (actionEvent.getSource() == helpBtn) {
            showHelp();
        }
    }

    private void showHelp() {
    }

    public void saveSettings() throws SQLException {
        UserDB.addCloudCredentials(accTokenField.getText(), clientIDField.getText());
    }
}
