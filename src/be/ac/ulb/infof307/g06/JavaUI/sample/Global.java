package be.ac.ulb.infof307.g06.JavaUI.sample;

import be.ac.ulb.infof307.g06.database.ProjectDB;
import javafx.scene.control.TreeItem;

import java.util.HashMap;
import java.util.Map;

public class Global {
    public static String firstName;
    public static String lastName;
    public static String email ;
    public static String username ;
    public static String passwd ;
    public static int userID;
    public static Map<Integer, TreeItem<ProjectDB.Project>> TreeMap = new HashMap<>();
}
