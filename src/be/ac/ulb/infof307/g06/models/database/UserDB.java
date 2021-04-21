
package be.ac.ulb.infof307.g06.models.database;

import be.ac.ulb.infof307.g06.models.Invitation;
import be.ac.ulb.infof307.g06.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class UserDB extends Database {

    public UserDB(String dbName) throws ClassNotFoundException, SQLException {
        super(dbName);
    }

    public boolean isAdmin() throws SQLException {
        ResultSet res = sqlQuery("SELECT id FROM admin");
        boolean check = res.getInt("id") == currentUser.getId();
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

    @SuppressWarnings("SqlWithoutWhere")
    public void setLimit(long value) throws SQLException {
        sqlUpdate("UPDATE admin SET diskLimit='" + value + "'");
    }

    @Override
    protected void createTables() throws SQLException {
        sqlUpdate("CREATE TABLE IF NOT EXISTS users(id Integer, fName varchar(20), lName varchar(20), userName varchar(20)," +
                "email varchar(40), password varchar(20), status boolean, accessToken varchar(64), clientID varchar(64), diskUsage integer, primary key (id));");
        sqlUpdate("CREATE TABLE IF NOT EXISTS admin(id integer, diskLimit long)");
        sqlUpdate("CREATE TABLE IF NOT EXISTS DropBoxCredentials(id integer, userID varchar(256), accountID varchar(256), accessToken varchar(256), expiration long, refreshToken varchar(256))");
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
        Database.currentUser = new User(userName, fName, lName, email, false);
        Database.currentUser.setId(res);
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
        }
        Database.currentUser = getUserInfo(key);
        if (Database.currentUser == null) {
            return 0;
        }
        return key;
    }


    public boolean isAdmin(int id) throws SQLException {
        ResultSet res = sqlQuery("SELECT id from admin where id='" + id + "'");
        boolean bool = res.isClosed();
        if (!bool) {
            res.close();
        }
        return bool;
    }

    private void setCloudCredentials(ResultSet usrInfo, User user) throws SQLException {
        if (usrInfo.getString("accessToken") != null) {
            user.setAccessToken(usrInfo.getString("accessToken"));
        } else {
            user.setAccessToken("");
        }
        if (usrInfo.getString("clientID") != null) {
            user.setClientID(usrInfo.getString("clientID"));
        } else {
            user.setClientID("");
        }
    }

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
        String idField = "from users where id='" + id + "'";
        return queryUserInfo(idField);
    }

    /**
     * Queries the database for the user's information
     *
     * @return Map<String, String> where the key is the field's name containing fName, lName, email, accessToken, clientID
     * @throws SQLException If a database access error occurs
     */
    @SuppressWarnings("SqlResolve")
    public User getUserInfo(String userName) throws SQLException {
        if (!userExists(userName)) {
            return null;
        }
        String usernameField = "from users where userName='" + userName + "'";
        return queryUserInfo(usernameField);
    }

    @SuppressWarnings("SqlResolve")
    private User queryUserInfo(String idField) throws SQLException {
        ResultSet usrInfo = sqlQuery("Select id, userName, fName, lName, email, accessToken, clientID " + idField);
        if (usrInfo.isClosed()) {
            return null;
        }
        User user = new User(usrInfo.getString("userName"), usrInfo.getString("fName"),
                usrInfo.getString("lName"), usrInfo.getString("email"), isAdmin(usrInfo.getInt("id")));
        setCloudCredentials(usrInfo, user);
        user.setId(usrInfo.getInt("id"));
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

    public void sendInvitation(int project_id, int sender_id, int receiver_id) throws SQLException {
        ResultSet rs = null;
        int id;
        try {   // Generate id
            rs = sqlQuery("SELECT id, MAX(id) FROM Invitations;");
            id = rs.getInt("id");
            id++;
        } catch (Exception e) {
            id = 1;
        }
        sqlUpdate("INSERT INTO Invitations(id, project_id, user1_id, user2_id) VALUES('" + id + "','" + project_id + "', '" + sender_id + "','" + receiver_id + "');");
        assert rs != null;
        rs.close();
    }

    public void removeInvitation(int inviteID) throws SQLException {
        sqlUpdate("DELETE FROM Invitations WHERE id = '" + inviteID + "';");
    }

    public List<Invitation> getInvitations(ProjectDB projectDB) throws SQLException {
        List<Invitation> invitations = new ArrayList<>();
        ResultSet rs = sqlQuery("SELECT id, project_id, user1_id FROM Invitations WHERE user2_id = '" + currentUser.getId() + "';");
        while (rs.next()) {
            invitations.add(new Invitation(rs.getInt("id"), projectDB.getProject(rs.getInt("project_id")), currentUser, getUserInfo(rs.getInt("user1_id"))));
        }
        rs.close();
        return invitations;
    }

//    public Invitation getInvitation() throws SQLException {
//        Invitation invitation;
//        ResultSet rs = sqlQuery("SELECT id, project_id, user1_id, user2_id FROM Invitations WHERE id = '" + currentUser.getId() + "';");
//        invitation = new Invitation(rs.getInt("id"), rs.getInt("project_id"), rs.getInt("user1_id"), rs.getInt("user2_id"));
//        rs.close();
//        return invitation;
//    }

    /**
     * Queries the access token and clientID of the user's cloud service
     *
     * @return A HashMap containing accessToken and clientID
     * @throws SQLException
     */
    public HashMap<String, String> getCloudCredentials() throws SQLException {
        ResultSet res = sqlQuery("SELECT accessToken, clientID from users where id='" + currentUser.getId() + "'");
        HashMap<String, String> credentials = new HashMap<>();
        credentials.put("accessToken", res.getString("accessToken"));
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
        sqlUpdate("UPDATE users SET accessToken='" + token + "', clientID='" + clientID + "' where id='" + currentUser.getId() + "'");
        currentUser.setAccessToken(token);
        currentUser.setClientID(clientID);
    }

    public void addAccessToken(String token) throws SQLException {
        sqlUpdate("UPDATE users SET accessToken='" + token + "' where id='" + currentUser.getId() + "'");
        currentUser.setAccessToken(token);
    }

    public void addClientID(String clientID) throws SQLException {
        sqlUpdate("UPDATE users SET clientID='" + clientID + "' where id='" + currentUser.getId() + "'");
        currentUser.setClientID(clientID);
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
    public long availableDisk() throws SQLException {
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
    public long getDiskLimit() throws SQLException {
        ResultSet res = sqlQuery("SELECT diskLimit FROM admin");
        long limit = res.getLong("diskLimit");
        res.close();
        return limit;
    }


}