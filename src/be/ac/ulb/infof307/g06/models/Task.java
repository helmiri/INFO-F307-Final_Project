package be.ac.ulb.infof307.g06.models;

/**
 * A task data object
 */
public class Task {
    //-------------- ATTRIBUTES ----------------
    int id;
    String description;
    String time;
    int projectID;
    long startDate;
    long endDate;
    //-------------- METHODS ----------------

    /**
     * Constructor.
     *
     * @param id          int
     * @param description String
     * @param projectID   int
     */
    public Task(int id, String description, int projectID, Long startDate, Long endDate) {
        this.id = id;
        this.description = description;
        this.projectID = projectID;
        this.startDate = startDate;
        this.endDate = endDate;
        time = Long.toString(getRemainingTime());
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public int getProjectID() {
        return projectID;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public int getSize() {
        return description.length() + 4;
    }

    public int getId() {
        return id;
    }

    public long getDuration() {
        return endDate - startDate;
    }

    public long getRemainingTime() {
        return endDate - System.currentTimeMillis() / 86400000L;
    }
}