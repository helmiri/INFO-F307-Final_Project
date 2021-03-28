package be.ac.ulb.infof307.g06.controllers;

import be.ac.ulb.infof307.g06.database.UserDB;
import be.ac.ulb.infof307.g06.models.Global;
import be.ac.ulb.infof307.g06.models.Invitation;
import be.ac.ulb.infof307.g06.views.MenuViewController;
import java.sql.SQLException;
import java.util.List;

public class MainController {
    //-------------- ATTRIBUTES ----------------
    private MenuViewController view;

    /**
     * Initializes the main view.
     *
     * @param view MenuViewController
     */
    //--------------- METHODS ----------------
    public void init(MenuViewController view){
        this.view = view;
    }

    /**
     * Checks the invitation requests.
     *
     * @throws SQLException throws SQLException
     * @throws java.lang.Exception throws Exception
     */
    public void checkInvites() throws SQLException, java.lang.Exception{
        List<Invitation> invitations = UserDB.getInvitations(Global.userID);
        for (Invitation invitation : invitations) {
            view.showInvitation(invitation.getProject_id(), invitation.getSender_id());
        }
    }
}
