package be.ac.ulb.infof307.g06.views.settingsViews;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * The storage settings scene
 */
public class StorageViewController {
    //-------------- ATTRIBUTES ----------------

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

    //--------------- METHODS ----------------

    /**
     * The main method for button's events.
     *
     * @param actionEvent ActionEvent, the event.
     *                    throws SQLException
     */
    public void events(ActionEvent actionEvent) {
        if (actionEvent.getSource() == saveBtn) {
            if (listener.saveSettings(limitField.getText(), this)) {
                new AlertWindow("Save", "Changes saved").showInformationWindow();
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
        refresh(diskLimit, diskUsage, isAdmin);
        showAdminSettings(isAdmin);
    }

    /**
     * Refresh disk usage and limit fields.
     *
     * @param diskLimit Long, the disk limit.
     * @param diskUsage Long, the disk usage.
     * @param isAdmin boolean, checks if it's the admin or a collaborator.
     */
    public void refresh(long diskLimit, long diskUsage, boolean isAdmin) {
        UnitValue limit = refreshStorageUse(diskLimit, diskUsage);
        if (isAdmin) {
            limitField.clear();
            limitField.setPromptText(limit.getValue() + limit.getUnit());
        }
    }

    /**
     * Refresh the storage use.
     *
     * @param diskLimit long
     * @param diskUsage long
     * @return the disk limit value
     */
    public UnitValue refreshStorageUse(long diskLimit, long diskUsage) {
        UnitValue usage = new UnitValue(diskUsage);
        UnitValue limit = new UnitValue(diskLimit);
        //noinspection IntegerDivisionInFloatingPointContext (will never happen)
        diskBar.setProgress(diskUsage / diskLimit);
        usageText.setText(usage.getValue() + usage.getUnit() + " / " + limit.getValue() + limit.getUnit());
        return limit;
    }

    /**
     * Sets text fields with the admin informations.
     *
     * @param isAdmin boolean, to checks if it's the admin of not.
     */
    private void showAdminSettings(boolean isAdmin) {
        if (!isAdmin) {
            return;
        }
        adminText.setVisible(true);
        limitText.setVisible(true);
        limitField.setVisible(true);
        separator.setVisible(true);
        saveBtn.setVisible(true);
    }


    //--------------- LISTENER ----------------
    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    /**
     * Communicates to the controller which action has been taken by the user
     */
    public interface ViewListener {
        boolean saveSettings(String limit, StorageViewController storageViewController);

        void authenticateGoogleDrive();

        void authenticateDropBox();
    }

    private static class UnitValue {
        private String unit;
        private long value;

        UnitValue(long newValue) {
            value = newValue;
            convert();
        }

        public long getValue() {
            return value;
        }

        public String getUnit() {
            return unit;
        }

        /**
         * Converts the storage value.
         */
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
