package be.ac.ulb.infof307.g06.models.database;


import be.ac.ulb.infof307.g06.models.Invitation;
import be.ac.ulb.infof307.g06.models.User;
import com.dropbox.core.oauth.DbxCredential;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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
        ResultSet res;
        for (int i = 0; i < 10; i++) {
            res = state.executeQuery("Select userName from users where userName='" + testData.get(i).get("userName") + "'");
            assertTrue(res.next());
            assertTrue(userDB.userExists(testData.get(i).get("userName")));
            res.close();
        }
        state.close();
    }

    @Test
    @DisplayName("Data validation")
    public void testValidateData() throws SQLException {
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
    public void testGetUserInfo() throws SQLException {
        // queryUserInfo + getUserInfo test due to queryUserInfo being a private implementation method
        for (int i = 0; i < 10; i++) {
            User usrInfo = userDB.getUserInfo(i + 1);
            assertEquals(testData.get(i).get("fName"), usrInfo.getFirstName());
            assertEquals(testData.get(i).get("lName"), usrInfo.getLastName());
            assertEquals(testData.get(i).get("email"), usrInfo.getEmail());
            assertEquals(i + 1, usrInfo.getId());
        }
    }


    @Test
    @DisplayName("Send invitation")
    public void testSendInvitation() throws SQLException {
        // Necessary setup
        userDB.sendInvitation(1, 2, 1);
        userDB.validateData(testData.get(0).get("userName"), testData.get(0).get("password"));
        projectDB.createProject("Title", "Desc", 1000L, 1000L, 1);

        // Test
        List<Invitation> invitations = userDB.getInvitations(projectDB);
        assertEquals(1, invitations.get(0).getProject().getId());
        assertEquals(2, invitations.get(0).getSender().getId());
        assertEquals(1, invitations.get(0).getReceiver().getId());
    }


    @Test
    @DisplayName("Get user invitations")
    public void testGetInvitations() throws SQLException {
        userDB.sendInvitation(1, 1, 2);
        userDB.sendInvitation(5, 3, 2);
        assertTrue(userDB.validateData("User_2_userName", "User_2_password") > 0);
        List<Invitation> invitations = userDB.getInvitations(projectDB);
        assertTrue(invitations.size() == 2);
        assertEquals(1, invitations.get(0).getInvitationID());
        assertEquals(1, invitations.get(0).getSender().getId());
        // Different invitation
        assertEquals(2, invitations.get(1).getInvitationID());
        assertEquals(3, invitations.get(1).getSender().getId());

    }

    @Test
    @DisplayName("Remove invitations")
    public void testRemoveInvitation() throws SQLException {
        userDB.sendInvitation(1, 1, 2);
        userDB.sendInvitation(5, 3, 2);
        assertTrue(userDB.validateData("User_2_userName", "User_2_password") > 0);
        userDB.removeInvitation(1);
        assertEquals(1, userDB.getInvitations(projectDB).size());
        userDB.removeInvitation(2);
        assertEquals(0, userDB.getInvitations(projectDB).size());
    }

    @Test
    @DisplayName("Set user info")
    public void testSetUserInfo() throws SQLException {
        userDB.validateData("User_1_userName", "User_1_password");
        userDB.setUserInfo("NewFName", "NewLName", "NewEmail", "newPassword");
        User res = userDB.getUserInfo(1);

        assertEquals("NewFName", res.getFirstName());
        assertEquals("NewLName", res.getLastName());
        assertEquals("NewEmail", res.getEmail());
        userDB.disconnectUser();
        // Test new password
        assertEquals(1, userDB.validateData("User_1_userName", "newPassword"));
        // Wrong password
        assertEquals(-1, userDB.validateData("User_1_userName", "User_1_password"));
    }

    @Test
    @DisplayName("Disk usage getter")
    public void testGetDiskUsage() throws SQLException {
        userDB.validateData("User_1_userName", "User_1_password");
        assertEquals(0, userDB.getDiskUsage());
    }

    @Test
    @DisplayName("Available disk getter")
    public void testAvailableDisk() throws SQLException {
        userDB.validateData("User_1_userName", "User_1_password");
        userDB.setAdmin(256);
        assertEquals(256, userDB.availableDisk());
    }

    @Test
    @DisplayName("Disk usage update")
    public void testUpdateDiskUsage() throws SQLException {
        userDB.updateDiskUsage(0);
        assertEquals(0, userDB.getDiskUsage());
        userDB.updateDiskUsage(123456);
        assertEquals(123456, userDB.getDiskUsage());
    }

    @Test
    @DisplayName("Admin setter")
    public void testSetAdmin() throws SQLException {
        userDB.validateData("User_1_userName", "User_1_password");
        userDB.setAdmin(256);
        Statement state = db.createStatement();
        ResultSet res = state.executeQuery("select diskLimit from admin");
        assertEquals(256, res.getInt("diskLimit"));
        state.close();
        res.close();
    }

    @Test
    @DisplayName("First boot")
    public void testIsFirstBoot() throws SQLException {
        // Always false since the database is populated beforehand with test data
        assertFalse(userDB.isFirstBoot());
    }

    @Test
    @DisplayName("DropBox Credentials")
    public void testDropBoxCredentials() throws SQLException {
        // Insert and retrieve credential
        String accessToken = "TestToken";
        String refreshToken = "TestRefreshToken";
        Long expiration = 1000L;
        String appKey = "TestKey";
        String appSecret = "TestSecret";
        DbxCredential testCredential = new DbxCredential(accessToken, expiration, refreshToken, appKey, appSecret);
        userDB.addDropBoxCredentials(testCredential);
        DbxCredential credential = userDB.getDropBoxCredentials();
        assertEquals(testCredential.getAccessToken(), credential.getAccessToken());
        assertEquals(testCredential.getRefreshToken(), credential.getRefreshToken());
        assertEquals(testCredential.getExpiresAt(), credential.getExpiresAt());
        assertEquals(testCredential.getAppKey(), credential.getAppKey());
        assertEquals(testCredential.getAppSecret(), credential.getAppSecret());
    }

    @Test
    @DisplayName("Disk limit setter")
    public void testSetDiskLimit() throws SQLException {
        userDB.setAdmin(99);
        userDB.setLimit(1000);
        assertEquals(1000, userDB.getDiskLimit());
    }
}
