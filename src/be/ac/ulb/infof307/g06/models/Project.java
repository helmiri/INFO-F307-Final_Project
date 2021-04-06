package be.ac.ulb.infof307.g06.models;

import javafx.collections.ObservableList;

import java.util.List;

public class Project {
    //-------------- ATTRIBUTES ----------------
    private final int id;
    private final String title;
    private final String description;
    private final Long date;
    private int projectId;
    private ObservableList<String> tagsName;
    //-------------- METHODS ----------------

    /**
     * Constructor.
     *
     * @param id          int
     * @param title       String
     * @param description String
     * @param date        Long
     * @param projectId   int
     */
    public Project(int id, String title, String description, Long date, int projectId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.projectId = projectId;
    }

    public Project(int id, String title, String description, Long date, ObservableList<String> tags) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        tagsName = tags;
    }

    public Project() {
        id = 0;
        title = "";
        description = "";
        date = 0L;
        projectId = 0;
    }

    public int getId() {
        return id;
    }

    public List<String> getTags() {
        return tagsName;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Long getDate() { return date; }
    public int getParent_id() { return projectId; }
    public int getSize() {
        int res = 0;

        // id, parent_id, date
        res += 4 + 4 + 8;

        // Title, description
        res += title.length() + description.length();

        return res;
    }
}