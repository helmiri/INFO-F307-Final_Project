package be.ac.ulb.infof307.g06.models;

public class Invitation {
    private final int invitationID;
    private final Project project;
    //-------------- ATTRIBUTES ----------------
    User receiver;
    User sender;

    public Invitation(int id, Project project, User receiver, User sender) {
        this.project = project;
        invitationID = id;
        this.receiver = receiver;
        this.sender = sender;
    }
    //-------------- METHODS ----------------

    public Project getProject() {
        return project;
    }

    public User getSender() {
        return sender;
    }

    public int getInvitationID() {
        return invitationID;
    }

    public User getReceiver() {
        return receiver;
    }

}
