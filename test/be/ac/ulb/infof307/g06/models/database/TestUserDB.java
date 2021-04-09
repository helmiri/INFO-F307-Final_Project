package be.ac.ulb.infof307.g06.models.database;


import be.ac.ulb.infof307.g06.models.Invitation;
import be.ac.ulb.infof307.g06.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestUserDB extends TestDatabase {


    public TestUserDB() throws ClassNotFoundException {
    }

    @Test
    @DisplayName("Insert")
    public void testAddUser() throws Exception {
        assertEquals(11, userDB.addUser("User1_fName", "User1_lname", "User1_userName", "user1.email.com", "123passwd"));
        Statement state = db.createStatement();
        ResultSet res = state.executeQuery("Select fName from users where fName='User1_fName'");
        assertTrue(res.next());
        assertEquals("User1_fName", res.getString("fName"));
        state.close();
        res.close();
    }

    @Test
    @DisplayName("User Exists")
    public void testUserExists() throws Exception {
        assertFalse(userDB.userExists("doesNotExist"));

        Statement state = db.createStatement();
        ResultSet res = null;
        for (int i = 0; i < 10; i++) {
            res = state.executeQuery("Select userName from users where userName='" + testData.get(i).get("userName") + "'");
            assertTrue(res.next());
            assertTrue(userDB.userExists(testData.get(i).get("userName")));
        }

        state.close();
        res.close();
    }

    @Test
    @DisplayName("Data validation")
    public void testValidateData() throws SQLException, ClassNotFoundException {
        Statement state = db.createStatement();
        ResultSet res;
        for (int i = 0; i < 10; i++) {
            res = state.executeQuery("SELECT userName FROM users WHERE userName='" + testData.get(i).get("userName") + "'");
            assertTrue(res.next());
            res.close();
        }
        state.close();
        assertEquals(0, userDB.validateData("DoesNotExist", "password"));
        assertEquals(0, userDB.validateData("userName", "wrongPasswd"));

        for (int i = 0; i < 10; i++) {
            assertEquals(i + 1, userDB.validateData(testData.get(i).get("userName"), testData.get(i).get("password")));
        }
    }

    @Test
    @DisplayName("Get user info")
    public void testGetUserInfo() throws SQLException, ClassNotFoundException {
        for (int i = 0; i < 10; i++) {
            User usrInfo = userDB.getUserInfo(i + 1);
            assertEquals(testData.get(i).get("fName"), usrInfo.getFirstName());
            assertEquals(testData.get(i).get("lName"), usrInfo.getLastName());
            assertEquals(testData.get(i).get("email"), usrInfo.getEmail());
            assertEquals(testData.get(i).get("accToken"), usrInfo.getAccessToken());
            assertEquals(testData.get(i).get("clientID"), usrInfo.getClientID());
            assertEquals(i + 1, usrInfo.getId());
        }
    }


    @Test
    @DisplayName("Send invitation")
    public void testSendInvitation() throws SQLException {
        // Necessary setup
        userDB.sendInvitation(1, 2, 1);
        userDB.validateData(testData.get(0).get("userName"), testData.get(0).get("password"));
        projectDB.createProject("Title", "Desc", 1000L, 1);

        // Test
        List<Invitation> invitations = userDB.getInvitations(projectDB);
        assertEquals(1, invitations.get(0).getProject().getId());
        assertEquals(2, invitations.get(0).getSender().getId());
        assertEquals(1, invitations.get(0).getReceiver().getId());
    }

    //
//    @Test
//    @DisplayName("Get user invitations")
//    public void testGetInvitations() throws SQLException {
//        UserDB.sendInvitation(1, 1, 2);
//        List<Invitation> invitations = UserDB.getInvitations(2);
//        assertEquals(invitations.get(0).getProject_id(), 1);
//    }
//
//    @Test
//    @DisplayName("Remove invitations")
//    public void testRemoveInvitation() throws SQLException {
//        UserDB.sendInvitation(1, 1, 2);
//        UserDB.removeInvitation(1, 2);
//        List<Invitation> invitations = UserDB.getInvitations(2);
//        assertEquals(invitations.size(), 0);
//    }
//
//
    @Test
    @DisplayName("Add access token")
    public void testAddAccessToken() throws SQLException, ClassNotFoundException {
        assertTrue(userDB.validateData(testData.get(0).get("userName"), testData.get(0).get("password")) > 0);
        userDB.addCloudCredentials("Random_Token_String", "CLIENTID");
        Statement state = db.createStatement();
        ResultSet res = state.executeQuery("SELECT accessToken from users where userName='User_1_userName'");
        String token = res.getString("accessToken");
        assertEquals("Random_Token_String", token);
        state.close();
    }

    @Test
    @DisplayName("Get access token")
    public void testGetToken() throws SQLException, ClassNotFoundException {
        Statement state = db.createStatement();
        assertTrue(userDB.validateData(testData.get(0).get("userName"), testData.get(0).get("password")) > 0);
        state.executeUpdate("UPDATE users SET accessToken='RANDOM_TOKEN' where id='1'");
        state.executeUpdate("UPDATE users SET clientID='RANDOM_CLIENTID' where id='1'");
        state.close();
        Map<String, String> res = userDB.getCloudCredentials();
        assertEquals("RANDOM_TOKEN", res.get("accessToken"));
        assertEquals("RANDOM_CLIENTID", res.get("clientID"));
    }
//
//    @Test
//    @DisplayName("Set user info")
//    public void testSetUserInfo() throws SQLException {
//        Global.userID = 1;
//        UserDB.setUserInfo("NewFName", "NewLName", "NewEmail", "newPassword");
//        Map<String, String> res = UserDB.getUserInfo(1);
//
//        assertEquals("NewFName", res.get("fName"));
//        assertEquals("NewLName", res.get("lName"));
//        assertEquals("NewEmail", res.get("email"));
//        assertEquals(1, UserDB.validateData("User_1_userName", "newPassword"));
//    }
//
//    @Test
//    @DisplayName("Disk usage getter")
//    public void testGetDiskUsage() throws SQLException {
//        UserDB.getDiskUsage();
//    }
//
//    @Test
//    @DisplayName("Available disk getter")
//    public void testAvailableDisk() throws SQLException {
//        Global.userID = 1;
//        UserDB.setAdmin(256);
//        assertEquals(256, UserDB.availableDisk());
//    }
//
//    @Test
//    @DisplayName("Disk usage update")
//    public void testUpdateDiskUsage() throws SQLException {
//        UserDB.updateDiskUsage(0);
//        assertEquals(0, UserDB.getDiskUsage());
//        UserDB.updateDiskUsage(123456);
//        assertEquals(123456, UserDB.getDiskUsage());
//    }
//
//    @Test
//    @DisplayName("Admin setter")
//    public void testSetAdmin() throws SQLException {
//        Global.userID = 1;
//        UserDB.setAdmin(256);
//        Statement state = db.createStatement();
//        ResultSet res = state.executeQuery("select diskLimit from admin");
//        assertEquals(256, res.getInt("diskLimit"));
//        state.close();
//        res.close();
//    }
}
