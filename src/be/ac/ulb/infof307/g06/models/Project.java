package be.ac.ulb.infof307.g06.models;

import javafx.collections.ObservableList;

import java.util.List;


/**
 * A project data object
 */
public class Project {
    //-------------- ATTRIBUTES ----------------
    private final int id;
    private final String title;
    private final String description;
    private final Long startDate;
    private final Long endDate;
    private int projectId;
    private ObservableList<String> tagsName;
    //-------------- METHODS ----------------

    /**
     * Constructor.
     *
     * @param id          The ID of the project
     * @param title       The title
     * @param description The description
     * @param startDate   The starting date
     * @param endDate     The ending date
     * @param projectId   The parent ID
     */
    public Project(int id, String title, String description, Long startDate, Long endDate, int projectId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectId = projectId;
    }

    /**
     * Constructor.
     *
     * @param id          The ID of the project
     * @param title       The title
     * @param description The description
     * @param startDate   The starting date
     * @param endDate     The ending date
     * @param tags  The tags
     */
    public Project(int id, String title, String description, Long startDate, Long endDate, ObservableList<String> tags) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        tagsName = tags;
    }

    /**
     * Constructor.
     */
    public Project() {
        id = 0;
        title = "";
        description = "";
        startDate = 0L;
        endDate = 0L;
        projectId = 0;
    }

    /**
     * @return the id of the project
     */
    public int getId() {
        return id;
    }

    /**
     * @return the tags of the project
     */
    public List<String> getTags() {
        return tagsName;
    }

    /**
     * @return the title of the project
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the description of the project
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the starting date of the project
     */
    public Long getStartDate() {
        return startDate;
    }

    /**
     * @return the ending date of the project
     */
    public Long getEndDate() {
        return endDate;
    }

    /**
     * @return the id of the parent of the project
     */
    public int getParentId() {
        return projectId;
    }

    /**
     * @return the size of the project
     */
    public int getSize() {
        int res = 0;

        // id, parent_id, date
        res += 4 + 4 + 8;

        // Title, description
        res += title.length() + description.length();

        return res;
    }

    /**
     * @return the duration of the project
     */
    public Long getDuration() {
        return endDate - startDate;
    }
}