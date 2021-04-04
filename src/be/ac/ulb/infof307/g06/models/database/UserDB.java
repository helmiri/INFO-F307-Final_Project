
package be.ac.ulb.infof307.g06.models.database;

import be.ac.ulb.infof307.g06.models.Invitation;
import be.ac.ulb.infof307.g06.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class UserDB extends Database {
    User currentUser;
    boolean isAdmin;

    public UserDB(String dbName) throws ClassNotFoundException, SQLException {
        super(dbName);
    }

    public boolean isAdmin() throws SQLException {
        ResultSet res = sqlQuery("SELECT id FROM admin");
        boolean check = false;
        if (res.getInt("id") == currentUser.getId()) {
            check = true;
        }
        res.close();
        return check;
    }

    /**
     * This method should only be called at the first boot
     * It should be used to set the first user to sign up as the system's administrator
     *
     * @param diskLimit (Only parameter for now) Memory usage limit to be applied to all users in bytes
     * @throws SQLException
     */
    public void setAdmin(int diskLimit) throws SQLException {
        sqlUpdate("INSERT INTO admin(id, diskLimit) VALUES(" + 1 + "," + diskLimit + ")");
    }

    public void setLimit(int value) throws SQLException {
        sqlUpdate("UPDATE admin SET diskLimit='" + value + "'");
    }

    @Override
    protected void createTables() throws SQLException {
        sqlUpdate("CREATE TABLE IF NOT EXISTS users(id Integer, fName varchar(20), lName varchar(20), userName varchar(20)," +
                "email varchar(40), password varchar(20), status boolean, accToken varchar(64), clientID varchar(64), diskUsage integer, primary key (id));");
        sqlUpdate("CREATE TABLE IF NOT EXISTS admin(id integer, diskLimit integer)");
    }

    public boolean isFirstBoot() throws SQLException {
        ResultSet res = sqlQuery("SELECT * FROM users;");
        boolean empty = res.isClosed();
        res.close();
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
    public int addUser(String fName, String lName, String userName, String email, String password) throws SQLException {
        String[] key = {"id"};
        PreparedStatement state = db.prepareStatement("INSERT INTO users(fName, lName, userName, email, password, diskUsage) VALUES (?,?,?,?,?,?)", key);
        state.setString(1, fName);
        state.setString(2, lName);
        state.setString(3, userName);
        state.setString(4, email);
        state.setString(5, password);
        state.setInt(6, 0);
        state.execute();
        ResultSet rs = state.getGeneratedKeys();
        int res = rs.getInt(1);
        rs.close();
        state.close();
        currentUser = new User(userName, fName, lName, email, false);
        currentUser.setId(res);
        return res;
    }

    /**
     * Queries the database for the requested userName
     *
     * @param userName userName to be searched
     * @return true if found, false if not
     * @throws SQLException
     */
    public boolean userExists(String userName) throws SQLException {
        ResultSet res = sqlQuery("SELECT userName FROM users WHERE userName='" + userName + "'");
        boolean found = res.next();
        res.close();
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
    public int validateData(String userName, String password) throws SQLException {
        if (!userExists(userName)) {
            return 0;
        }
        ResultSet res = sqlQuery("SELECT id, password, status FROM main.users WHERE userName='" + userName + "'");
        Integer key = validate(password, res);
        res.close();
        if (key == null) {
            return -1;
        } else if (key > 0) {
            currentUser = getUserInfo(key);
        }
        return key;
    }

    /**
     * Queries the database for the user's information
     *
     * @return Map<String, String> where the key is the field's name containing fName, lName, email, accToken, clientID
     * @throws SQLException If a database access error occurs
     */
    /*public static Map<String, String> getUserInfo(String userName) throws SQLException {
        Map<String, String> res = new HashMap<>();
        if (!userExists(userName)) {
            return res;
        }

        Statement state = getStatement();
        ResultSet usrInfo = sqlQuery("Select id, fName, lName, email, accToken, clientID from users where userName='" + userName + "'");

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
    }*/
    private Integer validate(String password, ResultSet res) throws SQLException {
        if (res.getBoolean("status")) {
            return null;
        }
        boolean valid = res.getString("password").equals(password);
        int key = 0;
        if (valid) {
            key = res.getInt("id");
            sqlUpdate("UPDATE users SET status=true where id='" + key + "'");
        }
        return key;
    }

    /**
     * Queries the database for the user's information
     *
     * @param id The user's id
     * @return Map<String, String> where the key is the field's name containing fName, lName, email, uName
     * @throws SQLException If a database access error occurs
     */

    public User getUserInfo(int id) throws SQLException {
        ResultSet usrInfo = sqlQuery("Select id, userName, fName, lName, email, status from users where id='" + id + "'");
        User user = new User(usrInfo.getString("userName"), usrInfo.getString("fName"), usrInfo.getString("lName"), usrInfo.getString("email"), false);
        user.setId(id);
        usrInfo.close();
        return user;
    }

    /**
     * Disconnects the current user
     *
     * @throws SQLException When a database access error occurs
     */
    public void disconnectUser() throws SQLException {
        sqlUpdate("UPDATE users SET status=false WHERE id='" + currentUser.getId() + "'");
    }

    public int sendInvitation(int project_id, int sender_id, int receiver_id) throws SQLException {
        ResultSet rs = null;
        int id;
        try {   // Generate id
            rs = sqlQuery("SELECT id, MAX(id) FROM Invitations;");
            id = rs.getInt("id");
            id++;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            id = 1;
        }
        sqlUpdate("INSERT INTO Invitations(id, project_id, user1_id, user2_id) VALUES('" + id + "','" + project_id + "', '" + sender_id + "','" + receiver_id + "');");
        assert rs != null;
        rs.close();
        return id;
    }

    public void removeInvitation(int project_id, int receiver_id) throws SQLException {
        sqlUpdate("DELETE FROM Invitations WHERE project_id = '" + project_id + "' AND user2_id = '" + receiver_id + "';");
    }

    public List<Invitation> getInvitations(int user_id) throws SQLException {
        List<Invitation> invitations = new ArrayList<>();
        ResultSet rs = sqlQuery("SELECT id, project_id, user1_id FROM Invitations WHERE user2_id = '" + user_id + "';");
        while (rs.next()) {
            invitations.add(new Invitation(rs.getInt("id"), rs.getInt("project_id"), rs.getInt("user1_id"), user_id));
        }
        rs.close();
        return invitations;
    }

    public Invitation getInvitation(int id) throws SQLException {
        Invitation invitation;
        ResultSet rs = sqlQuery("SELECT id, project_id, user1_id, user2_id FROM Invitations WHERE id = '" + id + "';");
        invitation = new Invitation(rs.getInt("id"), rs.getInt("project_id"), rs.getInt("user1_id"), rs.getInt("user2_id"));
        rs.close();
        return invitation;
    }

    /**
     * Queries the access token and clientID of the user's cloud service
     *
     * @return A HashMap containing accToken and clientID
     * @throws SQLException
     */
    public HashMap<String, String> getCloudCredentials() throws SQLException {
        ResultSet res = sqlQuery("SELECT accToken, clientID from users where id='" + currentUser.getId() + "'");
        HashMap<String, String> credentials = new HashMap<>();
        credentials.put("accToken", res.getString("accToken"));
        credentials.put("clientID", res.getString("clientID"));
        res.close();
        return credentials;
    }

    /**
     * Sets the user's cloud credentials
     *
     * @param token    The access token
     * @param clientID The cloud service's client ID
     * @throws SQLException When a database access error occurs
     */
    public void addCloudCredentials(String token, String clientID) throws SQLException {
        sqlUpdate("UPDATE users SET accToken='" + token + "', clientID='" + clientID + "' where id='" + currentUser.getId() + "'");
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
    public void setUserInfo(String fName, String lName, String email, String newPassword) throws SQLException {
        setField(fName, "fName");
        setField(lName, "lName");
        setField(email, "email");
        setField(newPassword, "password");
    }

    /**
     * Edits a user's info field
     *
     * @param info  New info
     * @param field Field type
     * @throws SQLException When a database access error occurs
     */
    private void setField(String info, String field) throws SQLException {
        if (info.isBlank()) {
            return;
        }
        sqlUpdate("UPDATE users SET " + field + "='" + info + "' WHERE id='" + currentUser.getId() + "'");
    }

    /**
     * Queries a user's storage usage
     *
     * @return The memory usage in bytes
     * @throws SQLException When a database access error occurs
     */
    public int getDiskUsage() throws SQLException {
        ResultSet res = sqlQuery("SELECT diskUsage from users where id='" + currentUser.getId() + "'");
        int disk = res.getInt("diskUsage");
        res.close();
        return disk;
    }

    /**
     * Queries disk space available
     *
     * @return Available disk space in bytes
     * @throws SQLException
     */
    public int availableDisk() throws SQLException {
        return getDiskLimit() - getDiskUsage();
    }

    /**
     * Sets the new memory usage
     *
     * @param diff The new space usage in bytes
     * @throws SQLException When a database access error occurs
     */
    public void updateDiskUsage(int diff) throws SQLException {
        sqlUpdate("UPDATE users SET diskUsage='" + diff + "' where id='" + currentUser.getId() + "'");
    }

    /**
     * Returns the max memory usage allowed per user
     *
     * @return Limit in bytes
     * @throws SQLException When a database access error occurs
     */
    public int getDiskLimit() throws SQLException {
        ResultSet res = sqlQuery("SELECT diskLimit FROM admin");
        int limit = res.getInt("diskLimit");
        res.close();
        return limit;
    }


}