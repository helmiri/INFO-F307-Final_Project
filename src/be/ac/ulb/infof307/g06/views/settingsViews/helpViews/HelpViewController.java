package be.ac.ulb.infof307.g06.views.settingsViews.helpViews;


import javafx.animation.TranslateTransition;
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
            listener.profileHelp();
        }
    }

    /**
     * Animates the transition.
     *
     * @param duration double
     * @param node node
     * @param width double
     */
    public void translateAnimation(double duration, Node node, double width){
        TranslateTransition translateTransition= new TranslateTransition(Duration.seconds(duration), node);
        translateTransition.setByX(width);
        translateTransition.play();
    }
    int show = 0;

    /**
     * Goes to the next image.
     *
     * @param event ActionEvent
     */
    @FXML
    void next(ActionEvent event){
        if (show == 0){
            translateAnimation(0.1, pane1, -790);
            show ++;

        }
        else if (show == 1){
            translateAnimation(0.1, pane2, -790);
            show ++;

        }

    }

    /**
     * Goes to the previous image.
     *
     * @param event ActionEvent
     */
    @FXML
    void previous(ActionEvent event){
        if(show == 1){
            translateAnimation(0.1, pane1, 790);
            show--;

        }
        else if(show == 2){
            translateAnimation(0.1, pane2, 790);
            show--;
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
