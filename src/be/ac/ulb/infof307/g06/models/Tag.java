package be.ac.ulb.infof307.g06.models;

/**
 * A Tag data object
 */
public class Tag {
    private final int id;
    private final String description;
    private final String color;

    /**
     * @param id          The ID of the tag
     * @param description The description
     * @param color       Its color in HEX format
     */
    public Tag(int id, String description, String color) {
        this.id = id;
        this.description = description;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }

    public int getSize() {
        return 4 + description.length() + 4;
    }
}
