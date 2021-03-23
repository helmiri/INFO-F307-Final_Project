package be.ac.ulb.infof307.g06.models;

public class Task {
    String description;
    int projectID;

    public Task(String description, int id) {
        this.description = description;
        this.projectID = id;
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
}