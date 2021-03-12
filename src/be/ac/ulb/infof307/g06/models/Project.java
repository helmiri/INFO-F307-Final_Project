package be.ac.ulb.infof307.g06.models;

public class Project {
    int id;
    String title;
    String description;
    String tags;
    Long date;
    int parent_id;

    public Project(int id, String title, String description, String tags, Long date, int parent_id) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.date = date;
        this.parent_id = parent_id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTags() {
        return tags;
    }

    public Long getDate() {
        return date;
    }

    public int getParent_id() {
        return parent_id;
    }
}