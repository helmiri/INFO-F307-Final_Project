package be.ac.ulb.infof307.g06.models.database;

import be.ac.ulb.infof307.g06.models.User;


/**
 * Encapsulates a single instance of a User class
 * This class represents the connected user's information that is needed to be shared across the entire application
 * instead of having to pass it as a parameter across the call chain to reach the class where it will be used or
 * querying the database for this information every time that is needed.
 * <p>
 * This class uses the Singleton pattern... Well, a Singleton-inspired design pattern because it has an instance initialization method and
 * I'm not sure if the Singleton allows that BUT it respects the properties of this design pattern:
 * - Ensures that only one instance of the ActiveUser class ever exists through a private constructor
 * - The initializeInstance ensures that only one instance will be created even if called multiple times. The first one will be used.
 * - A single static instance of the encapsulated class that will be shared across all instances of this class
 * - The project classes have global access to this class
 * <p>
 * The encapsulated User class must be assigned at runtime after successful Login/SignUp by the user.
 * There is no way of doing it by using a "Pure" implementation of a Singleton
 * There is no multithreading involved so there is no reason that a NullPointerException will be thrown at runtime
 * if the developer initialized it prior to using it (the end-user should never see it)
 * If that happens, the developer using this class should sit in the corner and think about their actions because they're not using this class as intended >:(
 */
public class ActiveUser {
    @SuppressWarnings("StaticVariableOfConcreteClass") // Justified in this case to ensure a single instance
    private static ActiveUser activeUser;
    private final User user;

    private ActiveUser(User newUser) {
        user = newUser;
    }

    /**
     * @return The single instance of this class
     * @throws NullPointerException when initializeInstance hasn't been called prior to calling this method
     */
    public static ActiveUser getInstance() {
        return activeUser;
    }

    /**
     * Initializes the active user's information. If called multiple times, the first instance passed as parameter will be used
     *
     * @param newUser The user's information.
     */
    public static void initializeInstance(User newUser) {
        if (activeUser == null) {
            activeUser = new ActiveUser(newUser);
        }
    }

    /**
     * Self explanatory
     *
     * @return the user's [field]
     */

    public String getUserName() {
        return user.getUserName();
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName() {
        return user.getLastName();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public boolean isAdmin() {
        return user.isAdmin();
    }

    public int getID() {
        return user.getId();
    }
}
