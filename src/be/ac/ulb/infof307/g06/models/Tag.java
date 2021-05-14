package be.ac.ulb.infof307.g06.models;


/**
 * A Tag data object
 */
public class Tag {
    private final int id;
    private final String description;
    private final String color;

    /**
     * @param newID          The ID of the tag
     * @param newDescription The description
     * @param newColor       Its color in HEX format
     */
    public Tag(int newID, String newDescription, String newColor) {
        id = newID;
        description = newDescription;
        color = newColor;
    }

    /**
     * @return the id of the tag
     */
    public int getId() {
        return id;
    }
    /**
     * @return the description of the tag
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the color of the tag
     */
    public String getColor() {
        return color;
    }

    /**
     * @return the size of the tag
     */
    public int getSize() {
        // 4 = The size in bytes of an int
        // description.length() The size in bytes of a string
        // color.length The size in bytes of the color (colors are represented by their hex number)
        return 4 + description.length() + color.length();
    }
}
