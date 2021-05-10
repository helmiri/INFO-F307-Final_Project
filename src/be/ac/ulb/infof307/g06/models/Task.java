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
     * @param startDate the starting date
     * @param endDate the ending date
     */
    public Task(int id, String description, int projectID, Long startDate, Long endDate) {
        this.id = id;
        this.description = description;
        this.projectID = projectID;
        this.startDate = startDate;
        this.endDate = endDate;
        time = Long.toString(getRemainingTime());
    }

    /**
     * @return the remaining time of the task
     */
    public String getTime() {
        return time;
    }
    /**
     * @return the description of the task
     */
    public String getDescription() {
        return description;
    }
    /**
     * @return the id of the project
     */
    public int getProjectID() {
        return projectID;
    }
    /**
     * @return the starting date of the task
     */
    public long getStartDate() {
        return startDate;
    }
    /**
     * @return the ending date of the task
     */
    public long getEndDate() {
        return endDate;
    }
    /**
     * @return the size of the task
     */
    public int getSize() {
        return description.length() + 4;
    }
    /**
     * @return the id of the task
     */
    public int getId() {
        return id;
    }
    /**
     * @return the duration of the task
     */
    public long getDuration() {
        return endDate - startDate;
    }
    /**
     * @return the remaining time of the task
     */
    public long getRemainingTime() {
        return endDate - System.currentTimeMillis() / 86400000L;
    }
}