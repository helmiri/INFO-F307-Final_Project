package be.ac.ulb.infof307.g06.views.settingsViews.helpViews;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import javafx.scene.Node;
import java.io.IOException;

public class HelpViewController{
    //-------------- ATTRIBUTES ----------------
// TODO : Changer structure des Controllers ! + EXCEPTIONS
    @FXML
    private AnchorPane  pane1;

    @FXML
    private AnchorPane  pane2;

    @FXML
    private AnchorPane  pane3;


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
    private void helpEvents(ActionEvent event) throws IOException {
        if (event.getSource() == projectManagementHelpBtn){
            listener.projectManagementHelp();
        }
        else if (event.getSource() == storageHelpBtn){
            listener.storageHelp();
        }
        else if (event.getSource() == exportImportHelpBtn){
            listener.exportImportHelp();
        }
        else if (event.getSource() == calendarHelpBtn){
            listener.calendarHelp();
        }
        else if (event.getSource() == tagsHelpBtn){
            listener.tagsHelp();
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
        void projectManagementHelp() throws IOException;
        void storageHelp() throws IOException;
        void calendarHelp() throws IOException;
        void exportImportHelp()throws IOException;
        void tagsHelp() throws IOException;
        void profileHelp() throws IOException;
    }
}
