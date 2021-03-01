package DBTests;


import be.ac.ulb.infof307.g06.database.UserDB;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class TestUserDB {
    private static Connection db;
    private static List<Map<String, String>> testData;
    private static List<String> dbFields;

    @BeforeAll
    public static void setup() throws ClassNotFoundException {

        dbFields = new ArrayList<>(5);
        dbFields.add("fName");
        dbFields.add("lName");
        dbFields.add("userName");
        dbFields.add("email");
        dbFields.add("password");
        Class.forName("org.sqlite.JDBC");
        testData = new ArrayList<>(10);
        Map<String, String> userData;
        for (int i = 0; i < 10; i++) {
            userData = new HashMap<>();
            for (String dbField : dbFields) {
                userData.put(dbField, "User_" + (i + 1) + '_' + dbField);
            }
            testData.add(i, userData);
        }
    }

    @BeforeEach
    private void prepareData() throws SQLException {
        /*
          Populate tesDB with testData
         */
        db = DriverManager.getConnection("jdbc:sqlite:test/DBTests/testDB.db");
        for (int i = 0; i < 10; i++) {
            PreparedStatement state1 = db.prepareStatement("INSERT INTO users(fName, lName, userName, email, password) VALUES (?,?,?,?,?)");
            for (int j = 0; j < 5; j++) {
                state1.setString(j + 1, testData.get(i).get(dbFields.get(j)));
            }
            state1.execute();
        }
        db.close();
    }

    @Test
    @DisplayName("Insert")
    public void testAddUser() throws SQLException, ClassNotFoundException {
        assertTrue(UserDB.addUser("User1_fName", "User1_lname", "User1_userName", "user1.email.com", "123passwd"));
        db = DriverManager.getConnection("jdbc:sqlite:test/DBTests/testDB.db");
        Statement state = db.createStatement();
        ResultSet res = state.executeQuery("Select fName from users where fName='User1_fName'");

        assertTrue(res.next());
        assertEquals("User1_fName", res.getString("fName"));
        db.close();
    }

    @Test
    @DisplayName("User Exists")
    public void testUserExists() throws SQLException, ClassNotFoundException {
        assertFalse(UserDB.userExists("doesNotExist"));
        db = DriverManager.getConnection("jdbc:sqlite:test/DBTests/testDB.db");
        Statement state = db.createStatement();
        for (int i = 0; i < 10; i++) {
            ResultSet res = state.executeQuery("Select userName from users where userName='" + testData.get(i).get("userName") + "'");
            assertTrue(res.next());
        }
        db.close();
    }
    @Test
    @DisplayName("Data validation")
    public void testValidateData() throws SQLException, ClassNotFoundException {
        db = DriverManager.getConnection("jdbc:sqlite:test/DBTests/testDB.db");
        Statement state = db.createStatement();
        for (int i = 0; i < 10; i++) {
            ResultSet res = state.executeQuery("SELECT userName FROM users WHERE userName='" + testData.get(i).get("userName") + "'");
            assertTrue(res.next());
        }


        assertFalse(UserDB.validateData("DoesNotExist", "password"));
        assertFalse(UserDB.validateData("userName", "wrongPasswd"));

        for (int i = 0; i < 10; i++) {
            assertTrue(UserDB.validateData(testData.get(i).get("userName"), testData.get(i).get("password")));
        }
        db.close();
    }

    @Test
    @DisplayName("Get user info")
    public void testGetUserInfo() throws SQLException, ClassNotFoundException {

        for (int i = 0; i < 10; i++) {
            Map<String, String> usrInfo = UserDB.getUserInfo(testData.get(i).get("userName"));
            assertEquals(testData.get(i).get("fName"), usrInfo.get("fName"));
            assertEquals(testData.get(i).get("lName"), usrInfo.get("lName"));
            assertEquals(testData.get(i).get("email"), usrInfo.get("email"));
        }

    }

    @AfterEach
    public void clear() throws SQLException {
        /*
          Clear testDB for a fresh start after each Test
         */
        db = DriverManager.getConnection("jdbc:sqlite:test/DBTests/testDB.db");
        Statement state = db.createStatement();
        state.executeUpdate("Delete from users");
        db.close();
    }

}
