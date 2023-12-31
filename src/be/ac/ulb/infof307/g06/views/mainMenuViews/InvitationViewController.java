package be.ac.ulb.infof307.g06.views.mainMenuViews;

import be.ac.ulb.infof307.g06.models.Invitation;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The invitation prompt
 */
public class InvitationViewController {
    //-------------- ATTRIBUTES ----------------
    @FXML
    private AnchorPane layout;
    @FXML
    private Button acceptBtn;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField senderNameTextField;
    @FXML
    private TextField projectNameTextField;

    private Stage stage;
    private ViewListener listener;

    private Invitation invitation;
    //--------------- METHODS ----------------

    /**
     * Initializes the fields related to the edition of a project.
     *
     * @param invitation The invitation to be shown
     */
    public void show(Invitation invitation) {
        Project invitedProject = invitation.getProject();
        User sender = invitation.getSender();

        this.invitation = invitation;
        stage = new Stage();
        senderNameTextField.setText(sender.getUserName());
        projectNameTextField.setText(invitedProject.getTitle());
        descriptionTextField.setText(invitedProject.getDescription());
        showStage();
    }

    /**
     * Shows the "invitation" stage.
     *
     */
    private void showStage() {
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Invitation");
        stage.setScene(new Scene(layout, 571, 473));
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.showAndWait();
    }

    /**
     * The main method for button's events.
     *
     * @param event ActionEvent, the event.
     */
    @FXML
    public void events(ActionEvent event) {
        if (event.getSource() == acceptBtn) {
            listener.acceptInvitation(invitation, stage);
        } else {
            listener.declineInvitation(invitation, stage);
        }
    }

    //-------------- LISTENER ----------------

    /**
     * Sets the listener.
     *
     * @param listener ViewListener, the listener to the controller.
     */
    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    /**
     * Communicates to the controller which button has been clicked
     */
    public interface ViewListener {
        /**
         * @param invitation The invitation to be accepted
         * @param stage      The stage to be closed
         */
        void acceptInvitation(Invitation invitation, Stage stage);

        /**
         * T
         *
         * @param invitation The invitation to be declined
         * @param stage      The stage to be closed
         */
        void declineInvitation(Invitation invitation, Stage stage);
    }

}
