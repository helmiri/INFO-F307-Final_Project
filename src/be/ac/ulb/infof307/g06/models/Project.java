package be.ac.ulb.infof307.g06.models;

public class Project {
    //-------------- ATTRIBUTES ----------------
    int id;
    String title;
    String description;
    Long date;
    int projectId;
    //-------------- METHODS ----------------
    /**
     * Constructor.
     *
     * @param id int
     * @param title String
     * @param description String
     * @param date Long
     * @param projectId int
     */
    public Project(int id, String title, String description, Long date, int projectId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Long getDate() {
        return date;
    }

    public int getParent_id() {
        return parent_id;
    }

    public int getSize() {
        int res = 0;

        // id, parent_id, date
        res += 4 + 4 + 8;

        // Title, description
        res += title.length() + description.length();

        return res;
    }
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Long getDate() { return date; }
    public int getParent_id() { return projectId; }
}