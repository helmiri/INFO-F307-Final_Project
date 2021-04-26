package be.ac.ulb.infof307.g06.models.database;


import be.ac.ulb.infof307.g06.models.User;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestDatabase {
    protected static final String DB_PATH = "test/be/ac/ulb/infof307/g06/models/database/testDB.db";
    protected Connection db;
    protected List<Map<String, String>> testData;
    protected List<String> dbFields;
    protected UserDB userDB;
    protected ProjectDB projectDB;
    protected CalendarDB calendarDB;


    public TestDatabase() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
    }

    @AfterAll
    public void deleteDB() throws SQLException {
        db.close();
        userDB.disconnectDB();
        projectDB.disconnectDB();
        calendarDB.disconnectDB();
        File dbFile = new File(DB_PATH);
        if (!dbFile.delete()) {
            System.out.println("Unable to delete testDB. Connection left open?");
        }
    }

    @BeforeAll
    void setup() throws SQLException, ClassNotFoundException {
        File dbFile = new File(DB_PATH);
        if (dbFile.exists()) {
            if (!dbFile.delete()) {
                System.out.println("ERROR: testDB.db file found and unable to be deleted.\nDelete the file to avoid test errors");
            }
        }
        db = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
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

        userDB = new UserDB(DB_PATH);
        projectDB = new ProjectDB(DB_PATH);
        calendarDB = new CalendarDB(DB_PATH);
    }

    @SuppressWarnings("SqlWithoutWhere")
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
        PreparedStatement state;
        for (int i = 0; i < 10; i++) {
            state = db.prepareStatement("INSERT INTO users(fName, lName, userName, email, password) VALUES (?,?,?,?,?)");
            for (int j = 0; j < 5; j++) {
                state.setString(j + 1, testData.get(i).get(dbFields.get(j)));
            }
            state.execute();
            state.close();
        }
    }

    @Test
    @DisplayName("User getter")
    public void testGetCurrentUser() throws SQLException {
        userDB.validateData("User_1_userName", "User_1_password");
        User user = userDB.getCurrentUser();
        assertEquals("User_1_userName", user.getUserName());
    }
}
