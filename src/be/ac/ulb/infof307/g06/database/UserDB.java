
package be.ac.ulb.infof307.g06.database;

import be.ac.ulb.infof307.g06.models.Global;
import be.ac.ulb.infof307.g06.models.Invitation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class UserDB extends Database {
    private static int diskLimit = 268435456;

    public UserDB(String dbName) throws ClassNotFoundException, SQLException {
        super(dbName);
    }

    /**
     * Adds a user to the database
     *
     * @param fName    First name
     * @param lName    Last name
     * @param userName User name
     * @param email    email
     * @param password password
     * @return the unique identifier of the newly inserted user
     * @throws SQLException Error accessing the database
     */
    public static int addUser(String fName, String lName, String userName, String email, String password) throws SQLException {
        connect();
        String[] key = {"id"};
        PreparedStatement state = db.prepareStatement("INSERT INTO users(fName, lName, userName, email, password) VALUES (?,?,?,?,?)", key);
        state.setString(1, fName);
        state.setString(2, lName);
        state.setString(3, userName);
        state.setString(4, email);
        state.setString(5, password);
//        state.setInt(6, diskLimit);
        state.execute();
        ResultSet rs = state.getGeneratedKeys();
        int res = rs.getInt(1);
        close(state, rs);
        return res;
    }

    public int getMaxLimit() {
        return diskLimit;
    }

    /**
     * Queries the database for the requested userName
     *
     * @param userName userName to be searched
     * @return true if found, false if not
     * @throws SQLException
     */
    public static boolean userExists(String userName) throws SQLException {
        Statement state = connect();
        ResultSet res = state.executeQuery("SELECT userName FROM users WHERE userName='" + userName + "'");
        boolean found = res.next();
        close(state, res);
        return found;
    }

    /**
     * Checks the validity of the password associated with the username
     *
     * @param userName input username
     * @param password input password
     * @return The unique identifier of the user if the password matches, 0 if the data is invalid
     * @throws SQLException
     */
    public static int validateData(String userName, String password) throws SQLException {
        if (!userExists(userName)) {
            return 0;
        }
        Statement state = connect();
        ResultSet res = state.executeQuery("SELECT id, password, status FROM main.users WHERE userName='" + userName + "'");
        Integer key = validate(password, state, res);
        close(state, res);
        if (key == null) return -1;
        return key;
    }

    private static Integer validate(String password, Statement state, ResultSet res) throws SQLException {
        if (res.getBoolean("status")) {
            return null;
        }
        boolean valid = res.getString("password").equals(password);
        int key = 0;
        if (valid) {
            key = res.getInt("id");
            state.executeUpdate("UPDATE users SET status=true where id='" + key + "'");
        }
        return key;
    }

    /**
     * Queries the database for the user's information
     *
     * @param userName The user's username
     * @return Map<String, String> where the key is the field's name containing fName, lName, and email
     * @throws SQLException On error accessing the database
     */
    public static Map<String, String> getUserInfo(String userName) throws SQLException {
        Map<String, String> res = new HashMap<>();
        if (!userExists(userName)) {
            return res;
        }

        Statement state = connect();
        ResultSet usrInfo = state.executeQuery("Select id, fName, lName, email, accToken, clientID from users where userName='" + userName + "'");

        res.put("id", Integer.toString(usrInfo.getInt("id")));
        res.put("fName", usrInfo.getString("fName"));
        res.put("lName", usrInfo.getString("lName"));
        res.put("email", usrInfo.getString("email"));
        res.put("accToken", "");
        res.put("clientID", "");

        if (!usrInfo.getString("accToken").isEmpty()) {
            res.put("accToken", usrInfo.getString("accToken"));
        }
        if (!usrInfo.getString("clientID").isEmpty()) {
            res.put("clientID", usrInfo.getString("clientID"));
        }
        close(state, usrInfo);
        return res;
    }

    public static Map<String, String> getUserInfo(int id) throws SQLException {
        Map<String, String> res = new HashMap<>();
        Statement state = connect();
        ResultSet usrInfo = state.executeQuery("Select userName, fName, lName, email, status from users where id='" + id + "'");

        res.put("uName", usrInfo.getString("userName"));
        res.put("fName", usrInfo.getString("fName"));
        res.put("lName", usrInfo.getString("lName"));
        res.put("email", usrInfo.getString("email"));
        close(state, usrInfo);
        return res;
    }

    public static void disconnectUser(int userID) throws SQLException {
        Statement state = connect();
        state.executeUpdate("UPDATE users SET status=false WHERE id='" + userID + "'");
        close(state);
    }

    public static int  sendInvitation(int project_id, int sender_id, int receiver_id) throws SQLException {
        Statement state = connect();
        ResultSet rs = null;
        int id;
        try {   // Generate id
            rs = state.executeQuery("SELECT id, MAX(id) FROM Invitations;");
            id = rs.getInt("id");
            id++;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            id = 1;
        }
        state.execute("INSERT INTO Invitations(id, project_id, user1_id, user2_id) VALUES('" + id + "','" + project_id + "', '" + sender_id + "','" + receiver_id + "');");
        close(state, rs);
        return id;
    }

    public static void removeInvitation(int project_id, int receiver_id) throws SQLException {
        Statement state = connect();
        state.execute("DELETE FROM Invitations WHERE project_id = '" + project_id + "' AND user2_id = '" + receiver_id + "';");
        close(state);
    }

    public static List<Invitation> getInvitations(int user_id) throws SQLException{
        List<Invitation> invitations = new ArrayList<>();
        Statement state = connect();
        ResultSet rs = state.executeQuery("SELECT id, project_id, user1_id FROM Invitations WHERE user2_id = '" + user_id + "';");
        while (rs.next()) {
            invitations.add(new Invitation(rs.getInt("id"), rs.getInt("project_id"), rs.getInt("user1_id"), user_id));
        }
        close(state, rs);
        return invitations;
    }
    public static Invitation getInvitation(int id) throws SQLException{
        Invitation invitation;
        Statement state = connect();
        ResultSet rs = state.executeQuery("SELECT id, project_id, user1_id, user2_id FROM Invitations WHERE id = '" + id + "';");
        invitation = new Invitation(rs.getInt("id"), rs.getInt("project_id"), rs.getInt("user1_id"), rs.getInt("user2_id"));
        close(state, rs);
        return invitation;
    }


    public static HashMap<String, String> getCloudCredentials() throws SQLException {
        Statement state = connect();
        ResultSet res = state.executeQuery("SELECT accToken, clientID from users where id='" + Global.userID + "'");
        HashMap<String, String> credentials = new HashMap<>();
        credentials.put("accToken", res.getString("accToken"));
        credentials.put("clientID", res.getString("clientID"));
        close(state, res);
        return credentials;
    }

    public static void addAccToken(String token, String clientID) throws SQLException {
        Statement state = connect();
        state.executeUpdate("UPDATE users SET accToken='" + token + "', clientID='" + clientID + "' where id='" + Global.userID + "'");
        close(state);
    }

    public static void setUserInfo(String userName, String fName, String lName, String email, String newPassword) throws SQLException {
        Statement state = connect();
        setField(userName, fName, "fName", state);
        setField(userName, lName, "lName", state);
        setField(userName, email, "email", state);
        setField(userName, newPassword, "password", state);
        close(state);
    }

    private static void setField(String userName, String info, String field, Statement state) throws SQLException {
        if (info.isBlank()) {
            return;
        }
        state.executeUpdate("UPDATE users SET " + field + "='" + info + "' WHERE userName='" + userName + "'");
    }

    public static int getDiskUsage() throws SQLException {
        Statement state = connect();
        ResultSet res = state.executeQuery("SELECT diskUsage from users where id='" + Global.userID + "'");
        int disk = res.getInt("diskUsage");
        close(state, res);
        return disk;
    }

    public static int availableDisk() throws SQLException {
        return getDiskLimit() - getDiskUsage();
    }

    public static void updateDiskUsage(int diff) throws SQLException {
        Statement state = connect();
        state.executeUpdate("UPDATE users SET diskUsage='" + diff + "' where id='" + Global.userID + "'");
        close(state);
    }

    private static int getDiskLimit() throws SQLException {
        Statement state = connect();
        ResultSet res = state.executeQuery("SELECT diskLimit FROM admin");
        int limit = res.getInt("diskLimit");
        close(state, res);
        return limit;
    }

    public static void setAdmin(int diskLimit) throws SQLException {
        Statement state = connect();
        state.executeUpdate("INSERT INTO admin(id, diskLimit) VALUES(" + Global.userID + "," + diskLimit + ")");
        close(state);
    }

}