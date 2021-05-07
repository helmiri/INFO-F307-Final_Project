package be.ac.ulb.infof307.g06.views.settingsViews.helpViews;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HelpViewController{
    //-------------- ATTRIBUTES ----------------


    @FXML
    private Button projectManagementHelpBtn;

    @FXML
    private Button storageHelpBtn;

    @FXML
    private Button exportImportHelpBtn;

    @FXML
    private Button calendarHelpBtn;

    @FXML
    private Button tagsHelpBtn;

    @FXML
    private Button profileHelpBtn;

    private ViewListener listener;
    //--------------- METHODS ----------------
    /**
     * The main method for button's events.
     *
     * @param event ActionEvent
     */
    @FXML
    private void helpEvents(ActionEvent event) {
        if (event.getSource() == projectManagementHelpBtn){
            System.out.println("Project management button");
        }
        else if (event.getSource() == storageHelpBtn){
            System.out.println("Storage help button");
        }
        else if (event.getSource() == exportImportHelpBtn){
            System.out.println("export/import help button");
        }
        else if (event.getSource() == calendarHelpBtn){
            System.out.println("Calendar help Button");
        }
        else if (event.getSource() == tagsHelpBtn){
            System.out.println("Tags help button");
        }
        else if (event.getSource() == profileHelpBtn){
            System.out.println("profile help button");
        }
    }


    //--------------- LISTENER ----------------
    /**
     * Sets the listener.
     *
     * @param listener ViewListener, the listener to the view.
     */
    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    public interface ViewListener {

    }
}
