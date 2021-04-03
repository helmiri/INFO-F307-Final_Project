package be.ac.ulb.infof307.g06.views;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class StorageViewController implements Initializable {
    public Button backBtn;
    public TextField accTokenField;
    public TextField clientIDField;
    public Button saveBtn;
    public AnchorPane anchorPane;
    public Button helpBtn;
    public ProgressBar diskBar;
    public Text usageText;
    public TextField limitField;
    public Text limitText;
    public Text adminText;
    private UserDB user_db;

    /**
     * Button handling
     *
     * @param actionEvent
     * @throws SQLException
     */
    public void events(ActionEvent actionEvent) throws SQLException {

        if (actionEvent.getSource() == backBtn) {
//            SettingsController.showSettingsMenu();
        } else if (actionEvent.getSource() == saveBtn) {
            if (saveSettings()) {
                new AlertWindow("Save", "Changes saved").informationWindow();
            }
        } else if (actionEvent.getSource() == helpBtn) {
            try {
                opnenLink();
            } catch (IOException | URISyntaxException e) {
                new AlertWindow("Error", "An error has occurred").errorWindow();
            }
        }
    }

    /**
     * Save the user's cloud settings (token, username, diskspace,..)
     *
     * @return
     * @throws SQLException
     */
    public boolean saveSettings() throws SQLException {
        String clientID = clientIDField.getText();
        String accToken = accTokenField.getText();
        boolean res = false;
        if (!clientID.isBlank() && !accToken.isBlank()) {
            user_db.addCloudCredentials(accTokenField.getText(), clientIDField.getText());
            res = true;
        } else if (clientID.isBlank() && accToken.isBlank()) {
            res = false;
        } else if (clientID.isBlank() || accToken.isBlank()) {
            new AlertWindow("Settings error", "Missing credentials").errorWindow();
            res = false;
        }

        if (user_db.isAdmin()) {
            String value = limitField.getText();
            if (!value.isBlank()) {
                user_db.setLimit(Integer.parseInt(limitField.getText()));
                res = true;
            }

        }
        return res;
    }

    /**
     * Link to the tutorial on how to use dropbox.
     *
     * @throws IOException
     * @throws URISyntaxException
     */
    public void opnenLink() throws IOException, URISyntaxException {
        new AlertWindow("Cloud service set up", "Follow the instructions to set up your credentials").informationWindow();
        Desktop.getDesktop().browse(new URL("https://github.com/ULB-INFOF307/2021-groupe-6/blob/master/Dropbox_On-Boarding.md").toURI());
    }

    @Override
    /**
     * Initializing the cloud parameters.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO: pass UserDB as parameter
        try {
            user_db = new UserDB("Database.db");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        try {
            user_db.updateDiskUsage(/*ProjectDB.getSizeOnDisk()*/ 2);

            UnitValue usage = new UnitValue();
            UnitValue limit = new UnitValue();
            limit.value = user_db.getDiskLimit();
            usage.value = user_db.getDiskUsage();
            diskBar.setProgress(usage.value / limit.value);

            limit.getUnit();
            usage.getUnit();
            usageText.setText(Double.toString(usage.value) + usage.unit + " / " + Double.toString(limit.value) + limit.unit);
            if (user_db.isAdmin()) {
                adminText.setVisible(true);
                limitText.setVisible(true);
                limitField.setVisible(true);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    private class UnitValue {
        public String unit;
        public double value;


        public void getUnit() {
            if (value < 1000) {
                unit = "B";
            } else if (value < 1000000 && value > 1000) {
                value = value / 10000;
                unit = "KB";
            } else if (value > 1000000 && value < 1000000000) {
                value = value / 1000000;
                unit = "MB";
            } else {
                value = value / 1000000;
                unit = "GB";
            }
        }
    }
}
