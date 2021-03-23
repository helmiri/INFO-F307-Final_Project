package be.ac.ulb.infof307.g06.models;

public class Tag {
    int id;
    String description;
    int color;

    public Tag(int id, String description, int color){
        this.id = id;
        this.description =description;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getColor() {
        return color;
    }

    public int getSize() {
        return 4 + description.length() + 4;
    }
}
