package be.ac.ulb.infof307.g06.models;

public class Statistics {
    //-------------- ATTRIBUTES ----------------
    String title;
    String collaborators;
    String tasks;
    String finalDate;
    String estimatedDate;
    //-------------- METHODS ----------------
    /**
     * Constructor.
     *
     * @param id int
     * @param title int
     * @param collaborators String
     * @param tasks String
     * @param estimatedDate String
     */
    public Statistics(Integer id, String title, String collaborators, String tasks, String estimatedDate){
        this.title=title;
        this.collaborators=collaborators;
        this.tasks=tasks;
        //this.realDate=finalDate;
        this.estimatedDate=estimatedDate;
    }
    public String getTitle() { return title; }
    public String getCollaborators() { return collaborators; }
    public String getTasks() { return tasks; }
    //public String getFinalDate() { return finalDate; }
    public String getEstimatedDate() { return estimatedDate; }

}
