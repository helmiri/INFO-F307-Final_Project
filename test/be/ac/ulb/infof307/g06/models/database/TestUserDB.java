package be.ac.ulb.infof307.g06.models.database;


import be.ac.ulb.infof307.g06.models.Invitation;
import be.ac.ulb.infof307.g06.models.User;
import com.dropbox.core.oauth.DbxCredential;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestUserDB extends TestDatabase {

    private final String userNameField = "userName";
    private final String newPassword = "newPassword";

    public TestUserDB() throws ClassNotFoundException, SQLException {
    }

    @Test
    @DisplayName("User Insert")
    public void testAddUser() throws Exception {
        assertEquals(11, userDB.addUser("User1_fName", "User1_lname", "User1_userName", "user1.email.com", "123passwd"));
        // Manually check that the user has been inserted
        Statement state = db.createStatement();
        ResultSet res = state.executeQuery("Select fName from users where fName='User1_fName'");
        assertTrue(res.next());
        assertEquals("User1_fName", res.getString("fName"));
        state.close();
        res.close();
    }


    @Test
    @DisplayName("User does not exist")
    public void testUserDoesNotExist() throws SQLException {
        assertFalse(userDB.userExists("RandomUserName"));
        assertFalse(userDB.userExists("User_1userName")); // Malformed username
    }

    @Test
    @DisplayName("User Exists")
    public void testUserExists() throws Exception {
        for (Map<String, String> item : testData) {
            assertTrue(userDB.userExists(item.get(userNameField)));
        }
    }

    @Test
    @DisplayName("Wrong password login")
    public void testWrongPasswordValidation() throws SQLException {
        for (Map<String, String> item : testData) {
            assertEquals(0, userDB.validateData(item.get(userNameField), "RandomWrongPassword"));
        }
    }

    @Test
    @DisplayName("Wrong username login")
    public void testWrongUsernameValidation() throws SQLException {
        for (Map<String, String> item : testData) {
            assertEquals(0, userDB.validateData("NonExistentUserName", item.get("password")));
        }
    }

    @Test
    @DisplayName("Wrong login credentials")
    public void testWrongLoginCredentials() throws SQLException {
        assertEquals(0, userDB.validateData("NonExistentUserName", "RandomWrongPassword"));
    }

    @Test
    @DisplayName("Test Login")
    public void testValidateData() throws SQLException {
        // validateData should return the ID of the user on successful login (indexed at 1)
        for (int i = 0; i < testData.size(); i++) {
            assertEquals(i + 1, userDB.validateData(testData.get(i).get(userNameField), testData.get(i).get("password")));
        }
    }

    @Test
    @DisplayName("Get user info")
    public void testGetUserInfo() throws SQLException {
        // queryUserInfo + getUserInfo test due to queryUserInfo being a private implementation method
        for (int i = 0; i < testData.size(); i++) {
            User usrInfo = userDB.getUserInfo(i + 1);
            assertEquals(testData.get(i).get(userNameField), usrInfo.getUserName());
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
        userDB.sendInvitation(1, 2, activeUser.getID());
        projectDB.createProject("Title", "Desc", 1000L, 1000L, 1);

        // Test
        List<Invitation> invitations = userDB.getInvitations(projectDB);
        assertEquals(1, invitations.get(0).getProject().getId());
        assertEquals(2, invitations.get(0).getSender().getId());
        assertEquals(activeUser.getID(), invitations.get(0).getReceiver().getId());
    }


    @Test
    @DisplayName("Get user invitations")
    public void testGetInvitations() throws SQLException {
        userDB.sendInvitation(1, 1, activeUser.getID());
        userDB.sendInvitation(5, 3, activeUser.getID());
        List<Invitation> invitations = userDB.getInvitations(projectDB);
        assertEquals(2, invitations.size());
        // First invitation
        assertEquals(1, invitations.get(0).getInvitationID());
        assertEquals(1, invitations.get(0).getSender().getId());
        // Different invitation
        assertEquals(2, invitations.get(1).getInvitationID());
        assertEquals(3, invitations.get(1).getSender().getId());

    }

    @Test
    @DisplayName("Remove invitations")
    public void testRemoveInvitation() throws SQLException {
        userDB.sendInvitation(1, 1, activeUser.getID());
        userDB.sendInvitation(5, 3, activeUser.getID());
        userDB.removeInvitation(1);
        assertEquals(1, userDB.getInvitations(projectDB).size());
        userDB.removeInvitation(2);
        assertEquals(0, userDB.getInvitations(projectDB).size());
    }

    @Test
    @DisplayName("User info setter")
    public void testSetUserInfo() throws SQLException {
        userDB.setUserInfo("NewFName", "NewLName", "NewEmail", newPassword);
        User res = userDB.getUserInfo(activeUser.getID());

        assertEquals("NewFName", res.getFirstName());
        assertEquals("NewLName", res.getLastName());
        assertEquals("NewEmail", res.getEmail());
    }

    @Test
    @DisplayName("Login with wrong new password")
    public void testLoginWIthWrongNewPassword() throws SQLException {
        userDB.userSettingsSync(null, newPassword);
        userDB.disconnectUser();
        // Wrong password
        assertEquals(0, userDB.validateData(activeUser.getUserName(), testData.get(activeUser.getID() - 1).get("password")));
    }

    @Test
    @DisplayName("Login with correct new password")
    public void testLoginWithCorrectNewPassword() throws SQLException {
        userDB.userSettingsSync(null, newPassword);
        // Test new password
        assertEquals(activeUser.getID(), userDB.validateData(activeUser.getUserName(), newPassword));
    }

    @Test
    @DisplayName("Disk usage getter")
    public void testGetDiskUsage() throws SQLException {
        assertEquals(0, userDB.getDiskUsage());
    }

    @Test
    @DisplayName("Available disk getter")
    public void testAvailableDisk() throws SQLException {
        userDB.setAdmin(256);
        assertEquals(256, userDB.availableDisk());
    }

    @Test
    @DisplayName("Disk usage update")
    public void testUpdateDiskUsage() throws SQLException {
        userDB.updateDiskUsage(123456);
        assertEquals(123456, userDB.getDiskUsage());
    }

    @Test
    @DisplayName("Admin setter")
    public void testSetAdmin() throws SQLException {
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
        assertTrue(userDB.isFirstBoot());
    }

    @Test
    @DisplayName("Not first boot")
    public void testNotFirstBoot() throws SQLException {
        userDB.setAdmin(256);
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

        // Check that the credential returned has the same info as the one inserted
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
