package be.ac.ulb.infof307.g06.models;

import javafx.collections.ObservableList;

public class Project {
    //-------------- ATTRIBUTES ----------------
    int id;
    String title;
    String description;
    Long date;
    int projectId;
    ObservableList<String> tagsName;
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

    public Project(int id, String title, String description, Long date, ObservableList<String> tags) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.tagsName = tags;
    }

    public Project() {
        this.id = 0;
        this.title = "";
        this.description = "";
        this.date = 0L;
        this.projectId = 0;
    }

    public int getId() {
        return id;
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