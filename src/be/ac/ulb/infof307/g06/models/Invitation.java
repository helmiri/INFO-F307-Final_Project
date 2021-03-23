package be.ac.ulb.infof307.g06.models;

public class Invitation {
    int id;
    int project_id;
    int sender_id;
    int receiver_id;
    public Invitation(int id, int project_id, int sender_id, int receiver_id) {
        this.id = id;
        this.project_id = project_id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
    }
    public int getId() {
        return id;
    }

    public int getProject_id() {
        return project_id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public int getReceiver_id() {
        return receiver_id;
    }
}
