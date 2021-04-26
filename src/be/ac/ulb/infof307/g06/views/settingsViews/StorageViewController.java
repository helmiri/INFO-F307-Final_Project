package be.ac.ulb.infof307.g06.views.settingsViews;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.sql.SQLException;

public class StorageViewController {
    @FXML
    private Button dBoxButton;
    @FXML
    private Button gDriveButton;
    @FXML
    private Separator separator;

    @FXML
    private Button saveBtn;
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
        if (actionEvent.getSource() == saveBtn) {
            if (listener.saveSettings(limitField.getText(), this)) {
                new AlertWindow("Save", "Changes saved").informationWindow();
            }
        } else if (actionEvent.getSource() == dBoxButton) {
            listener.authenticateDropBox();
        } else if (actionEvent.getSource() == gDriveButton) {
            listener.authenticateGoogleDrive();
        }
    }


    /**
     * Initializing the cloud parameters.
     */
    public void initialize(long diskLimit, int diskUsage, boolean isAdmin) {
        try {
            refresh(diskLimit, diskUsage, isAdmin);
            showAdminSettings(isAdmin);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void refresh(long diskLimit, long diskUsage, boolean isAdmin) {
        UnitValue limit = refreshStorageUse(diskLimit, diskUsage);
        if (isAdmin) {
            limitField.clear();
            limitField.setPromptText(limit.getValue() + limit.getUnit());
        }
    }

    public UnitValue refreshStorageUse(long diskLimit, long diskUsage) {
        UnitValue usage = new UnitValue(diskUsage);
        UnitValue limit = new UnitValue(diskLimit);
        //noinspection IntegerDivisionInFloatingPointContext (will never happen)
        diskBar.setProgress(diskUsage / diskLimit);
        usageText.setText(usage.getValue() + usage.getUnit() + " / " + limit.getValue() + limit.getUnit());
        return limit;
    }

    private void showAdminSettings(boolean isAdmin) throws SQLException {
        if (!isAdmin) {
            return;
        }
        adminText.setVisible(true);
        limitText.setVisible(true);
        limitField.setVisible(true);
        separator.setVisible(true);
        saveBtn.setVisible(true);
    }

    public void setListener(ViewListener listener) {
        this.listener = listener;
    }


    //--------------- LISTENER ----------------

    public interface ViewListener {
        boolean saveSettings(String limit, StorageViewController storageViewController) throws SQLException;

        void authenticateGoogleDrive();

        void authenticateDropBox();
    }

    private class UnitValue {
        String unit;
        private long value;

        public UnitValue(long newValue) {
            value = newValue;
            convert();
        }

        public long getValue() {
            return value;
        }

        public String getUnit() {
            return unit;
        }

        public void convert() {
            long KILOBYTE = 1000L;
            long MEGABYTE = 1000L * 1000L;
            long GIGABYTE = 1000L * 1000L * 1000L;
            long TERABYTE = 1000L * 1000L * 1000L * 1000L;
            if (value < KILOBYTE) {
                unit = "B";
            } else if (value < MEGABYTE) {
                value = value / KILOBYTE;
                unit = "KB";
            } else if (value < GIGABYTE) {
                value = value / MEGABYTE;
                unit = "MB";
            } else if (value < TERABYTE) {
                value = value / GIGABYTE;
                unit = "GB";
            } else {
                value = value / TERABYTE;
                unit = "TB";
            }
        }
    }
}
