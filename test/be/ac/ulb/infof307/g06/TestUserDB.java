package be.ac.ulb.infof307.g06;


import be.ac.ulb.infof307.g06.database.UserDB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class TestUserDB extends TestDatabase {


    public TestUserDB() throws ClassNotFoundException {
    }

    @Test
    @DisplayName("Insert")
    public void testAddUser() throws SQLException {
        assertEquals(11, UserDB.addUser("User1_fName", "User1_lname", "User1_userName", "user1.email.com", "123passwd"));
        Statement state = db.createStatement();
        ResultSet res = state.executeQuery("Select fName from users where fName='User1_fName'");

        assertTrue(res.next());
        assertEquals("User1_fName", res.getString("fName"));
        state.close();
        res.close();
    }

    @Test
    @DisplayName("User Exists")
    public void testUserExists() throws SQLException {
        assertFalse(UserDB.userExists("doesNotExist"));

        Statement state = db.createStatement();
        for (int i = 0; i < 10; i++) {
            ResultSet res = state.executeQuery("Select userName from users where userName='" + testData.get(i).get("userName") + "'");
            assertTrue(res.next());
        }
        state.close();
    }

    @Test
    @DisplayName("Data validation")
    public void testValidateData() throws SQLException {
        Statement state = db.createStatement();
        for (int i = 0; i < 10; i++) {
            ResultSet res = state.executeQuery("SELECT userName FROM users WHERE userName='" + testData.get(i).get("userName") + "'");
            assertTrue(res.next());
        }
        state.close();
        assertEquals(0, UserDB.validateData("DoesNotExist", "password"));
        assertEquals(0, UserDB.validateData("userName", "wrongPasswd"));

        for (int i = 0; i < 10; i++) {
            assertEquals(i + 1, UserDB.validateData(testData.get(i).get("userName"), testData.get(i).get("password")));
        }
    }

    @Test
    @DisplayName("Get user info")
    public void testGetUserInfo() throws SQLException {
        for (int i = 0; i < 10; i++) {
            Map<String, String> usrInfo = UserDB.getUserInfo(testData.get(i).get("userName"));
            assertEquals(testData.get(i).get("fName"), usrInfo.get("fName"));
            assertEquals(testData.get(i).get("lName"), usrInfo.get("lName"));
            assertEquals(testData.get(i).get("email"), usrInfo.get("email"));
        }
    }

    @Test
    @DisplayName("Add access token")
    public void testAddAccToken() throws SQLException {
        UserDB.addAccToken("Random_Token_String", "User_1_userName");
        Statement state = db.createStatement();
        ResultSet res = state.executeQuery("SELECT accToken from users where userName='User_1_userName'");
        String token = res.getString("accToken");
        assertEquals("Random_Token_String", token);
        state.close();
    }

    @Test
    @DisplayName("Get access token")
    public void testGetToken() throws SQLException {
        Statement state = db.createStatement();
        state.executeUpdate("UPDATE users SET accToken='RANDOM_TOKEN' where userName='User_1_userName'");
        state.close();
        assertEquals("RANDOM_TOKEN", UserDB.getAccToken("User_1_userName"));
    }
}

