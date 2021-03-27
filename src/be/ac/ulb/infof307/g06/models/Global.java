package be.ac.ulb.infof307.g06.models;

import be.ac.ulb.infof307.g06.views.ProjectViews.ProjectsViewController;
import javafx.scene.control.TreeItem;

import java.util.HashMap;
import java.util.Map;

/**
 *  A class that contains globals variables
 */
public class Global {
    //-------------- ATTRIBUTES ----------------
    //Sign up variables
    public static String firstName;
    public static String lastName;
    public static String email;
    public static String userName;
    public static String passwd;

    public static TreeItem<Project> root;
    public static ProjectsViewController projectsView;
    //Edit project's name
    public static String currentProject;
    public static Task selectedTask;
    //The user ID
    public static int userID;

    //Map that contains the user's projects
    public static Map<Integer, TreeItem<Project>> TreeMap = new HashMap<>();

    public static String popupProjectTitle;
    public static String popupProjectDescription;
    public static String popupSenderUsername;

    public static String popupTaskTitle;
    public static String popupTaskNewName;

}

