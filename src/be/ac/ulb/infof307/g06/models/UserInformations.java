package be.ac.ulb.infof307.g06.models;

public class UserInformations {
    //-------------- ATTRIBUTES ----------------
    private static String firstName;
    private static String lastName;
    private static String email;
    private static String username;
    private static String passwd;

    //-------------- METHODS ----------------
    public static String getFirstName() { return firstName; }
    public static String getLastName() { return lastName; }
    public static String getEmail() { return email; }
    public static String getUsername() { return username; }
    public static String getPasswd() { return passwd; }

    public static void setFirstName(String firstName) { UserInformations.firstName = firstName; }
    public static void setLastName(String lastName) { UserInformations.lastName = lastName; }
    public static void setEmail(String email) { UserInformations.email = email; }
    public static void setUsername(String username) { UserInformations.username = username; }
    public static void setPasswd(String passwd) { UserInformations.passwd = passwd; }

}