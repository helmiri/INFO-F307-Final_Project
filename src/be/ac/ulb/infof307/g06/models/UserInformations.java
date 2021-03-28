package be.ac.ulb.infof307.g06.models;

public final class UserInformations {
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

    public static void setFirstName(String firstNameSetter) { firstName = firstNameSetter; }
    public static void setLastName(String lastNameSetter) { lastName = lastNameSetter; }
    public static void setEmail(String emailSetter) { email = emailSetter; }
    public static void setUsername(String usernameSetter) { username = usernameSetter; }
    public static void setPasswd(String passwdSetter) { passwd = passwdSetter; }
}