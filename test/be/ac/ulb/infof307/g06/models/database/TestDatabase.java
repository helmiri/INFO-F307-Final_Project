package be.ac.ulb.infof307.g06.models.database;


import be.ac.ulb.infof307.g06.models.User;
import be.ac.ulb.infof307.g06.models.encryption.Hash;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class TestDatabase {
    protected static final String DB_PATH = "test/be/ac/ulb/infof307/g06/models/database/testDB.db";
    protected DatabaseConnection db;
    protected List<Map<String, String>> testData;
    protected  List<String> dbFields;
    protected  UserDB userDB;
    protected  ProjectDB projectDB;
    protected CalendarDB calendarDB;
    protected ActiveUser activeUser;

    public TestDatabase() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");

        DatabaseConnection.connect(DB_PATH);
        db = DatabaseConnection.getInstance();
        dbFields = new ArrayList<>(5);
        dbFields.add("fName");
        dbFields.add("lName");
        dbFields.add("userName");
        dbFields.add("email");
        dbFields.add("password");

        testData = new ArrayList<>(10);
        Map<String, String> userData;
        for (int i = 0; i < 10; i++) {
            userData = new HashMap<>();
            for (String dbField : dbFields) {
                userData.put(dbField, "User_" + (i + 1) + '_' + dbField);
            }
            testData.add(i, userData);
        }
        userDB = new UserDB();
        projectDB = new ProjectDB();
        calendarDB = new CalendarDB();
    }

    @BeforeAll
    public static void setup() {
        File dbFile = new File(DB_PATH);
        dbFile.delete();
    }

    @BeforeEach
    private void setTestUser() {
        // Randomness used to ensure that no matter the user in the test set all tests pass consistently
        int id = new Random().nextInt(testData.size());
        Map<String, String> userInfo = testData.get(id); // Get a random user each execution
        ActiveUser.initializeInstance(new User(userInfo.get("userName"), userInfo.get("firstName"), userInfo.get("lastName"), userInfo.get("email"), new Random().nextBoolean(), id + 1)); // +1 because user IDs indexed at 1
        activeUser = ActiveUser.getInstance();
    }

    /**
     * Clears the contents of the database for a fresh start after each test
     *
     * @throws SQLException On error accessing the database
     */
    @AfterEach
    public void clear() throws SQLException {
        /*
          Clear testDB for a fresh start after each Test
         */
        Statement state = db.createStatement();
        state.executeUpdate("DELETE FROM users");
        state.executeUpdate("DELETE FROM Project");
        state.executeUpdate("DELETE FROM Projects");
        state.executeUpdate("DELETE FROM Collaborator");
        state.executeUpdate("DELETE FROM Task");
        state.executeUpdate("DELETE FROM admin");
        state.executeUpdate("DELETE FROM Invitations");
        state.executeUpdate("DELETE FROM Collaborator");
        state.executeUpdate("DELETE FROM Tag");
        state.executeUpdate("DELETE FROM Tag_projects");
        state.executeUpdate("DELETE FROM tasks_users");
        state.close();

    }

    @BeforeEach
    private void prepareUserData() throws SQLException {
        /*
          Populate tesDB with testData
         */
        String[] keys = {"id"};
        PreparedStatement state;
        Hash hash = new Hash();
        for (int i = 0; i < 10; i++) {
            state = db.prepareStatement("INSERT INTO users(fName, lName, userName, email, password) VALUES (?,?,?,?,?)", keys);
            for (int j = 0; j < dbFields.size() - 1; j++) {
                state.setString(j + 1, testData.get(i).get(dbFields.get(j)));
            }
            String hashPassword = hash.hashPassword(testData.get(i).get("password"), testData.get(i).get("userName")); // change salt
            state.setString(dbFields.size(), hashPassword);
            state.execute();
            state.close();
        }
    }
}
