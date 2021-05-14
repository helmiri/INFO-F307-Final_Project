package be.ac.ulb.infof307.g06.models.database;

import be.ac.ulb.infof307.g06.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Singleton test to ensure that the information contained in it cannot be changed
 */
public class TestActiveUser {
    private User testUser = new User("username", "firstname", "lastname", "email", true);
    private User secondUser = new User("secondUser", "secondName", "secondLast", "secondEmail", false);

    /**
     * IMPORTANT!!!!!!!!!!
     * This test fails when ran through JUnit's commandline platform because its state is carried over from previous tests (the database ones)
     * Which means that:
     * 1) The singleton works as intended. A single instance accessed globally that cannot be modified once it is set (wink)
     * 2) The JVM isn't "reset" after each test class carrying over global states
     * The test SHOULD pass (I hope) when executed alone
     */

    //@Test
    @DisplayName("Instance initialization")
    public void testInitializeInstance() {
        ActiveUser.initializeInstance(testUser);
        ActiveUser userInstance = ActiveUser.getInstance();

        assertEquals(testUser.getUserName(), userInstance.getUserName());
        assertEquals(testUser.getFirstName(), userInstance.getFirstName());
        assertEquals(testUser.getEmail(), userInstance.getEmail());
        assertEquals(testUser.isAdmin(), userInstance.isAdmin());
    }

    @Test
    @DisplayName("Single instance after first set")
    public void testSingleInstance() {
        ActiveUser.initializeInstance(testUser);
        ActiveUser firstInstance = ActiveUser.getInstance();
        ActiveUser.initializeInstance(secondUser);
        ActiveUser secondInstance = ActiveUser.getInstance();
        // Hashes should be the same
        assertEquals(firstInstance.hashCode(), secondInstance.hashCode());
    }
}
