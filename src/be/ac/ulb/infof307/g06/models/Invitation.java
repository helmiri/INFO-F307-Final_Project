package be.ac.ulb.infof307.g06.models;

public class Invitation {
    //-------------- ATTRIBUTES ----------------
    int id;
    int projectId;
    int senderId;
    int receiverId;
    //-------------- METHODS ----------------
    /**
     * Constructor.
     *
     * @param id int
     * @param projectId int
     * @param senderId int
     * @param receiverId int
     */
    public Invitation(int id, int projectId, int senderId, int receiverId) {
        this.id = id;
        this.projectId = projectId;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public int getId() { return id; }
    public int getProject_id() { return projectId; }
    public int getSender_id() { return senderId; }
    public int getReceiver_id() { return receiverId; }
}
