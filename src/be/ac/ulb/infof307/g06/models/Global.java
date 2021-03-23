package be.ac.ulb.infof307.g06.models;

import com.dropbox.core.v2.DbxClientV2;
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
    public static String email;
    public static String userName;
    public static String passwd;

    //The user ID
    public static int userID;

    //Map that contains the user's projects
    public static Map<Integer, TreeItem<Project>> TreeMap = new HashMap<>();

    // Cloud service
    public static DbxClientV2 dboxClient;
}
