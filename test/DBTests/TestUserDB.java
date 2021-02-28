package DBTests;


import be.ac.ulb.infof307.g06.database.UserDB;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class TestUserDB {
    private static Connection db;

    @BeforeEach
    public void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        db = DriverManager.getConnection("jdbc:sqlite:test/DBTests/testDB.db");
    }

    @AfterEach
    public void close() throws SQLException {
        db.close();
    }

    @Test
    @DisplayName("Insert")
    public void testAddUser() throws SQLException, ClassNotFoundException {
        assertTrue(UserDB.addUser("User1_fName", "User1_lname", "User1_userName", "user1.email.com", "123passwd"));

        connect();
        Statement state = db.createStatement();
        ResultSet res = state.executeQuery("Select fName from users where fName='User1_fName'");

        assertTrue(res.next());
        assertEquals("User1_fName", res.getString("fName"));
    }

    @Test
    @DisplayName("User Exists")
    public void testUserExists() throws SQLException, ClassNotFoundException {
        assertFalse(UserDB.userExists("doesNotExist"));
        PreparedStatement state1 = db.prepareStatement("INSERT INTO users(fName, lName, userName, email, password) VALUES (?,?,?,?,?)");
        state1.setString(1, "fName");
        state1.setString(2, "lName");
        state1.setString(3, "userName");
        state1.setString(4, "email");
        state1.setString(5, "password");
        state1.execute();

        Statement state = db.createStatement();
        ResultSet res = state.executeQuery("Select userName from users where userName='userName'");
        assertTrue(res.next());

    }

    @Test
    @DisplayName("Data validation")
    public void testValidateData() throws SQLException, ClassNotFoundException {
        PreparedStatement state1 = db.prepareStatement("INSERT INTO users(fName, lName, userName, email, password) VALUES (?,?,?,?,?)");
        state1.setString(1, "fName");
        state1.setString(2, "lName");
        state1.setString(3, "userName");
        state1.setString(4, "email");
        state1.setString(5, "password");
        state1.execute();

        Statement state = db.createStatement();
        ResultSet res = state.executeQuery("SELECT userName FROM users WHERE userName='userName'");
        assertTrue(res.next());

        assertFalse(UserDB.validateData("DoesNotExist", "password"));
        assertFalse(UserDB.validateData("userName", "wrongPasswd"));
        assertTrue(UserDB.validateData("userName", "password"));
    }

    @Test
    @DisplayName("Get user info")
    public void testGetUserInfo() throws SQLException, ClassNotFoundException {
        PreparedStatement state1 = db.prepareStatement("INSERT INTO users(fName, lName, userName, email, password) VALUES (?,?,?,?,?)");
        state1.setString(1, "fName");
        state1.setString(2, "lName");
        state1.setString(3, "userName");
        state1.setString(4, "email");
        state1.setString(5, "password");
        state1.execute();

        Map<String, String> usrInfo = UserDB.getUserInfo("userName");
        assertEquals("fName", usrInfo.get("fName"));
        assertEquals("lName", usrInfo.get("lName"));
        assertEquals("email", usrInfo.get("email"));
    }

    @AfterAll
    public static void clear() throws SQLException {
        db = DriverManager.getConnection("jdbc:sqlite:test/DBTests/testDB.db");
        Statement state = db.createStatement();
        state.executeUpdate("Delete from users");
        db.close();
    }

}
