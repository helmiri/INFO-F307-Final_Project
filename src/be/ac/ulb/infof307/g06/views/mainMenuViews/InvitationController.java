package be.ac.ulb.infof307.g06.views.mainMenuViews;

import be.ac.ulb.infof307.g06.controllers.Controller;
import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.exceptions.WindowLoadException;
import be.ac.ulb.infof307.g06.models.Invitation;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.User;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class InvitationController extends Controller implements InvitationViewController.ViewListener {

    private UserDB userDB;
    private ProjectDB projectDB;

    /**
     * Constructor
     *
     * @param stage   Stage, a stage
     * @param scene   Scene, a scene
     * @param DB_PATH String, the path to the database
     */
    public InvitationController(Stage stage, Scene scene, String DB_PATH) throws DatabaseException {
        super(stage, scene, DB_PATH);
        try {
            userDB = new UserDB(DB_PATH);
            projectDB = new ProjectDB(DB_PATH);
        } catch (ClassNotFoundException | SQLException error) {
            throw new DatabaseException(error, "Could not load invitations");
        }
    }

    /**
     * Shows the invitation that the user has received
     */
    @Override
    public void show() {
        try {
            for (Invitation invitation : userDB.getInvitations(projectDB)) {
                showInvitationStage(invitation);
            }
        } catch (SQLException error) {
            new DatabaseException(error).show();
        } catch (IOException error) {
            new WindowLoadException(error).show();
        }
    }

    /**
     * Sets the loader to show the stage with an invitation to join a project.
     *
     * @param invitation The invitation to be accepted/declined
     */
    public void showInvitationStage(Invitation invitation) throws IOException {
        InvitationViewController controller = (InvitationViewController) loadView(InvitationViewController.class, "InvitationView.fxml");
        controller.setListener(this);
        controller.show(invitation);
    }

    /**
     * Accepts an invitation and adds collaborator to project then closes the stage
     *
     * @param invitation      Invitation object
     * @param invitationStage Invitation Stage
     */
    @Override
    public void acceptInvitation(Invitation invitation, Stage invitationStage) {
        Project project = invitation.getProject();
        User receiver = invitation.getReceiver();
        try {
            projectDB.addCollaborator(project.getId(), receiver.getId());
            userDB.removeInvitation(invitation.getInvitationID());
            invitationStage.close();
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
    }

    /**
     * Declines invitation to project
     *
     * @param invitation      Invitation object
     * @param invitationStage Invitation Stage
     */
    @Override
    public void declineInvitation(Invitation invitation, Stage invitationStage) {
        try {
            userDB.removeInvitation(invitation.getInvitationID());
            invitationStage.close();
        } catch (SQLException error) {
            new DatabaseException(error).show();
        }
    }
}
