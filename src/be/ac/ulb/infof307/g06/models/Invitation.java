package be.ac.ulb.infof307.g06.models;



/**
 * Invitation data object
 */
public class Invitation {
    private final int invitationID;
    private final Project project;
    //-------------- ATTRIBUTES ----------------
    private final User receiver;
    private final User sender;

    /**
     * constructor
     * @param id the id of the invitation
     * @param project the project
     * @param receiver the receiver
     * @param sender the sender
     */
    public Invitation(int id, Project project, User receiver, User sender) {
        this.project = project;
        invitationID = id;
        this.receiver = receiver;
        this.sender = sender;
    }
    //-------------- METHODS ----------------

    /**
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * @return the sender of the invitation
     */
    public User getSender() {
        return sender;
    }

    /**
     * @return the id of the invitation
     */
    public int getInvitationID() {
        return invitationID;
    }

    /**
     * @return the user that receives the invitation
     */
    public User getReceiver() {
        return receiver;
    }

}
