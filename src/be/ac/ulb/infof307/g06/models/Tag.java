package be.ac.ulb.infof307.g06.models;

public class Tag {
    //-------------- ATTRIBUTES ----------------
    int id;
    String description;
    int color;
    //-------------- METHODS ----------------
    /**
     * Constructor.
     *
     * @param id int
     * @param description String
     * @param color int
     */
    public Tag(int id, String description, int color){
        this.id = id;
        this.description =description;
        this.color = color;
    }

    public int getId() { return id; }
    public String getDescription() { return description; }
    public int getColor() { return color; }
}
