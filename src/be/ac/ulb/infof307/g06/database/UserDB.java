
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

    public UserDB(String dbName) throws ClassNotFoundException, SQLException {
        super(dbName);
    }

    @Override
    protected void createTables() throws SQLException {
        Statement state = connect();
        state.execute("CREATE TABLE IF NOT EXISTS users(id Integer, fName varchar(20), lName varchar(20), userName varchar(20)," +
                "email varchar(40), password varchar(20), status boolean, accToken varchar(64), clientID varchar(64), diskUsage integer, primary key (id));");
        state.execute("CREATE TABLE IF NOT EXISTS admin(id integer, diskLimit integer)");
        close(state);
    }

    public static boolean isFirstBoot() throws SQLException {
        Statement state = connect();
        ResultSet res = state.executeQuery("SELECT * FROM users;");
        boolean empty = res.isClosed();
        close(state, res);
        return empty;
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
     * @throws SQLException If a database access error occurs
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
        state.execute();
        ResultSet rs = state.getGeneratedKeys();
        int res = rs.getInt(1);
        close(state, rs);
        return res;
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
     * @return Map<String, String> where the key is the field's name containing fName, lName, email, accToken, clientID
     * @throws SQLException If a database access error occurs
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


    /**
     * Queries the database for the user's information
     *
     * @param id The user's id
     * @return Map<String, String> where the key is the field's name containing fName, lName, email, uName
     * @throws SQLException If a database access error occurs
     */

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


    /**
     * Disconnects the current user
     *
     * @throws SQLException When a database access error occurs
     */
    public static void disconnectUser() throws SQLException {
        Statement state = connect();
        state.executeUpdate("UPDATE users SET status=false WHERE id='" + Global.userID + "'");
        close(state);
    }


    public static int sendInvitation(int project_id, int sender_id, int receiver_id) throws SQLException {
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

    public static Invitation getInvitation(int id) throws SQLException {
        Invitation invitation;
        Statement state = connect();
        ResultSet rs = state.executeQuery("SELECT id, project_id, user1_id, user2_id FROM Invitations WHERE id = '" + id + "';");
        invitation = new Invitation(rs.getInt("id"), rs.getInt("project_id"), rs.getInt("user1_id"), rs.getInt("user2_id"));
        close(state, rs);
        return invitation;
    }

    /**
     * Queries the access token and clientID of the user's cloud service
     *
     * @return A HashMap containing accToken and clientID
     * @throws SQLException
     */
    public static HashMap<String, String> getCloudCredentials() throws SQLException {
        Statement state = connect();
        ResultSet res = state.executeQuery("SELECT accToken, clientID from users where id='" + Global.userID + "'");
        HashMap<String, String> credentials = new HashMap<>();
        credentials.put("accToken", res.getString("accToken"));
        credentials.put("clientID", res.getString("clientID"));
        close(state, res);
        return credentials;
    }

    /**
     * Sets the user's cloud credentials
     *
     * @param token    The access token
     * @param clientID The cloud service's client ID
     * @throws SQLException When a database access error occurs
     */
    public static void addCloudCredentials(String token, String clientID) throws SQLException {
        Statement state = connect();
        state.executeUpdate("UPDATE users SET accToken='" + token + "', clientID='" + clientID + "' where id='" + Global.userID + "'");
        close(state);
    }

    /**
     * Sets the user's information
     *
     * @param fName       new first name
     * @param lName       new last name
     * @param email       new email address
     * @param newPassword new password
     * @throws SQLException When a database access error occurs
     */
    public static void setUserInfo(String fName, String lName, String email, String newPassword) throws SQLException {
        Statement state = connect();
        setField(fName, "fName", state);
        setField(lName, "lName", state);
        setField(email, "email", state);
        setField(newPassword, "password", state);
        close(state);
    }

    /**
     * Edits a user's info field
     *
     * @param info  New info
     * @param field Field type
     * @param state Statement object used to execute the query
     * @throws SQLException When a database access error occurs
     */
    private static void setField(String info, String field, Statement state) throws SQLException {
        if (info.isBlank()) {
            return;
        }
        state.executeUpdate("UPDATE users SET " + field + "='" + info + "' WHERE id='" + Global.userID + "'");
    }

    /**
     * Queries a user's storage usage
     *
     * @return The memory usage in bytes
     * @throws SQLException When a database access error occurs
     */
    public static int getDiskUsage() throws SQLException {
        Statement state = connect();
        ResultSet res = state.executeQuery("SELECT diskUsage from users where id='" + Global.userID + "'");
        int disk = res.getInt("diskUsage");
        close(state, res);
        return disk;
    }

    /**
     * Queries disk space available
     *
     * @return Available disk space in bytes
     * @throws SQLException
     */
    public static int availableDisk() throws SQLException {
        return getDiskLimit() - getDiskUsage();
    }

    /**
     * Sets the new memory usage
     *
     * @param diff The new space usage in bytes
     * @throws SQLException When a database access error occurs
     */
    public static void updateDiskUsage(int diff) throws SQLException {
        Statement state = connect();
        state.executeUpdate("UPDATE users SET diskUsage='" + diff + "' where id='" + Global.userID + "'");
        close(state);
    }

    /**
     * Returns the max memory usage allowed per user
     *
     * @return Limit in bytes
     * @throws SQLException When a database access error occurs
     */
    private static int getDiskLimit() throws SQLException {
        Statement state = connect();
        ResultSet res = state.executeQuery("SELECT diskLimit FROM admin");
        int limit = res.getInt("diskLimit");
        close(state, res);
        return limit;
    }

    /**
     * This method should only be called at the first boot
     * It should be used to set the first user to sign up as the system's administrator
     *
     * @param diskLimit (Only parameter for now) Memory usage limit to be applied to all users in bytes
     * @throws SQLException
     */
    public static void setAdmin(int diskLimit) throws SQLException {
        Statement state = connect();
        state.executeUpdate("INSERT INTO admin(id, diskLimit) VALUES(" + Global.userID + "," + diskLimit + ")");
        close(state);
    }

}