package be.ac.ulb.infof307.g06.models;

public class Task {
    //-------------- ATTRIBUTES ----------------
    int id;
    String description;
    int projectID;

    //-------------- METHODS ----------------
    /**
     * Constructor.
     *
     * @param id int
     * @param description String
     * @param projectID int
     */
    public Task(int id, String description, int projectID) {
        this.id = id;
        this.description = description;
        this.projectID = projectID;
    }

    public String getDescription() {
        return description;
    }

    public int getProjectID() {
        return projectID;
    }

    public int getSize() {
        return description.length() + 4;
    }
    public int getId() { return id; }
}