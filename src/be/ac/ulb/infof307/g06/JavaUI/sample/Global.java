package be.ac.ulb.infof307.g06.JavaUI.sample;

import be.ac.ulb.infof307.g06.database.ProjectDB;
import javafx.scene.control.TreeItem;

import java.util.HashMap;
import java.util.Map;

/**
 *  A class that contains globals variables
 */
public class Global {
    //Sign up variables
    public static String firstName;
    public static String lastName;
    public static String email ;
    public static String username ;
    public static String passwd ;

    //The user ID
    public static int userID;

    //Map that contains the user's projects
    public static Map<Integer, TreeItem<ProjectDB.Project>> TreeMap = new HashMap<>();
}
