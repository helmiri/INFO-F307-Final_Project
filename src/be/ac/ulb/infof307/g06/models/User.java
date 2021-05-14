package be.ac.ulb.infof307.g06.models;


/**
 * User data object
 */
public class User {
    private int id;
    private boolean isAdmin;
    private String userName, firstName, lastName, email;

    /**
     * constructor
     * @param userName the username
     * @param firstName the firstname
     * @param lastName the lastname
     * @param email the email
     * @param isAdmin the administrator status of the user
     */
    public User(String userName, String firstName, String lastName, String email, boolean isAdmin) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public User(String userName, String firstName, String lastName, String email, boolean isAdmin, int id) {
        this(userName, firstName, lastName, email, isAdmin);
        this.id = id;
    }

    /**
     * @return the admin status of the user
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * @return the id of the user
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id
     * Sets the id of the user
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the username of the user
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the username
     * Sets the username of the user
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstname
     * Sets the first name of the user
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastname of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastname
     * Sets the last name of the user
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email
     * Sets the email of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

}
