package be.ac.ulb.infof307.g06.models;

public class Task {
    int id;
    String description;
    int projectID;

    public Task(int id, String description, int projectID) {
        this.id = id;
        this.description = description;
        this.projectID = projectID;
    }

    public String getDescription() {return description;}
    public int getProjectID(){return projectID;}
    public int getId() {
        return id;
    }
}