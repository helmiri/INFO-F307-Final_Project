package be.ac.ulb.infof307.g06.views;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;

public class StorageViewController {
    @FXML
    private Button backBtn;
    @FXML
    private TextField accTokenField;
    @FXML
    private TextField clientIDField;
    @FXML
    private Button saveBtn;
    @FXML
    private Button helpBtn;
    @FXML
    private ProgressBar diskBar;
    @FXML
    private Text usageText;
    @FXML
    private TextField limitField;
    @FXML
    private Text limitText;
    @FXML
    private Text adminText;

    private ViewListener listener;

    /**
     * Button handling
     *
     * @param actionEvent
     * @throws SQLException
     */
    public void events(ActionEvent actionEvent) throws SQLException {

        if (actionEvent.getSource() == backBtn) {
            listener.onBackButtonClicked();
        } else if (actionEvent.getSource() == saveBtn) {
            if (listener.saveSettings(clientIDField.getText(), accTokenField.getText(), limitField.getText())) {
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
     * Link to the tutorial on how to use dropbox.
     *
     * @throws IOException
     * @throws URISyntaxException
     */
    public void opnenLink() throws IOException, URISyntaxException {
        new AlertWindow("Cloud service set up", "Follow the instructions to set up your credentials").informationWindow();
        Desktop.getDesktop().browse(new URL("https://github.com/ULB-INFOF307/2021-groupe-6/blob/master/Dropbox_On-Boarding.md").toURI());
    }

    /**
     * Initializing the cloud parameters.
     */
    public void initialize(int diskLimit, int diskUsage, boolean isAdmin) {
        try {
            UnitValue usage = new UnitValue();
            UnitValue limit = new UnitValue();
            limit.setValue(diskLimit);
            usage.setValue(diskUsage);
            diskBar.setProgress(usage.getValue() / limit.getValue());
            usageText.setText(usage.getValue() + usage.getUnit() + " / " + limit.getValue() + limit.getUnit());
            showAdminSettings(isAdmin);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void showAdminSettings(boolean isAdmin) throws SQLException {
        if (!isAdmin) {
            return;
        }
        adminText.setVisible(true);
        limitText.setVisible(true);
        limitField.setVisible(true);

    }

    public void setListener(ViewListener listener) {
        this.listener = listener;
    }


    //--------------- LISTENER ----------------

    public interface ViewListener {
        void onBackButtonClicked();

        boolean saveSettings(String clientID, String accessToken, String limit) throws SQLException;
    }

    private class UnitValue {
        private double value;

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public String getUnit() {
            String unit;
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
            return unit;
        }
    }
}
