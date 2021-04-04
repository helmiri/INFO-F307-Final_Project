package be.ac.ulb.infof307.g06.models.database;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestUserDB extends TestDatabase {


    public TestUserDB() throws ClassNotFoundException {
    }

    @Test
    @DisplayName("Insert")
    public void testAddUser() throws Exception {
        UserDB userDB = new UserDB(DB_PATH);
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
        UserDB userDB = new UserDB(DB_PATH);
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
//
//    @Test
//    @DisplayName("Data validation")
//    public void testValidateData() throws SQLException {
//        Statement state = db.createStatement();
//        ResultSet res = null;
//        for (int i = 0; i < 10; i++) {
//            res = state.executeQuery("SELECT userName FROM users WHERE userName='" + testData.get(i).get("userName") + "'");
//            assertTrue(res.next());
//            res.close();
//        }
//        state.close();
//        assertEquals(0, UserDB.validateData("DoesNotExist", "password"));
//        assertEquals(0, UserDB.validateData("userName", "wrongPasswd"));
//
//        for (int i = 0; i < 10; i++) {
//            assertEquals(i + 1, UserDB.validateData(testData.get(i).get("userName"), testData.get(i).get("password")));
//        }
//    }
//
//    @Test
//    @DisplayName("Get user info")
//    public void testGetUserInfo() throws SQLException {
//        for (int i = 0; i < 10; i++) {
//            Map<String, String> usrInfo = UserDB.getUserInfo(testData.get(i).get("userName"));
//            assertEquals(testData.get(i).get("fName"), usrInfo.get("fName"));
//            assertEquals(testData.get(i).get("lName"), usrInfo.get("lName"));
//            assertEquals(testData.get(i).get("email"), usrInfo.get("email"));
//        }
//    }
//
//    @Test
//    @DisplayName("Send invitation")
//    public void testSendInvitation() throws SQLException {
//        int id1 = UserDB.sendInvitation(1, 1, 2);
//        Invitation invitation = UserDB.getInvitation(id1);
//        assertEquals(1, invitation.getProject_id());
//        assertEquals(1, invitation.getSender_id());
//        assertEquals(2, invitation.getReceiver_id());
//    }
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
//    @Test
//    @DisplayName("Add access token")
//    public void testAddAccToken() throws SQLException {
//        Global.userID = 1;
//        UserDB.addCloudCredentials("Random_Token_String", "CLIENTID");
//        Statement state = db.createStatement();
//        ResultSet res = state.executeQuery("SELECT accToken from users where userName='User_1_userName'");
//        String token = res.getString("accToken");
//        assertEquals("Random_Token_String", token);
//        state.close();
//    }
//
//    @Test
//    @DisplayName("Get access token")
//    public void testGetToken() throws SQLException {
//        Global.userID = 1;
//        Statement state = db.createStatement();
//        state.executeUpdate("UPDATE users SET accToken='RANDOM_TOKEN' where id='1'");
//        state.executeUpdate("UPDATE users SET clientID='RANDOM_CLIENTID' where id='1'");
//        state.close();
//        Map<String, String> res = UserDB.getCloudCredentials();
//        assertEquals("RANDOM_TOKEN", res.get("accToken"));
//        assertEquals("RANDOM_CLIENTID", res.get("clientID"));
//    }
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
