package be.ac.ulb.infof307.g06.JavaUI.sample;

import java.text.DateFormat;
import java.util.Date;

public class Project {
    private String name;
    private String description;
    private String tags; //should be an array
    //private Date date= new Date();

    public Project(String name, String description, String tags){
        this.name        = name;
        this.description = description;
        this.tags        = tags;
    }

    public String getTags() {
        return tags;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

}
