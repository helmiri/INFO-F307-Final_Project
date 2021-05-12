package be.ac.ulb.infof307.g06.models.database;

import be.ac.ulb.infof307.g06.models.Invitation;
import be.ac.ulb.infof307.g06.models.User;
import be.ac.ulb.infof307.g06.models.encryption.Hash;
import com.dropbox.core.oauth.DbxCredential;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * The user database.
 */
public class UserDB extends Database {
    private Hash hash;

    /**
     * Same constructor as the main database abstract class (except for the hash).
     *
     * @param dbName the name of the db
     * @throws ClassNotFoundException if it doesnt find the parent class
     * @throws SQLException           if the query fails
     */
    public UserDB(String dbName) throws ClassNotFoundException, SQLException {
        super(dbName);
        hash = new Hash();
    }

    /**
     * Checks whether the current user has administrator privileges
     *
     * @return true if it's the case, false otherwise
     * @throws SQLException On database access error
     */
    public boolean isAdmin() throws SQLException {
        ResultSet res = sqlQuery("SELECT id FROM admin where id='" + currentUser.getId() + "'");
        boolean check = res.next();
        res.close();
        return check;
    }

    /**
     * This method should only be called at the first boot
     * It should be used to set the first user to sign up as the system's administrator
     *
     * @param diskLimit (Only parameter for now) Memory usage limit to be applied to all users in bytes
     * @throws SQLException When a database access error occurs
     */
    public void setAdmin(int diskLimit) throws SQLException {
        sqlUpdate("INSERT INTO admin(id, diskLimit) VALUES(" + 1 + "," + diskLimit + ")");
    }

    /**
     * Sets the disk usage limit value
     *
     * @param value The limit value
     * @throws SQLException On database access error
     */
    public void setLimit(long value) throws SQLException {
        //noinspection SqlWithoutWhere
        sqlUpdate("UPDATE admin SET diskLimit='" + value + "'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createTables() throws SQLException {
        sqlUpdate("CREATE TABLE IF NOT EXISTS users(id Integer, fName varchar(20), lName varchar(20), userName varchar(20)," +
                "email varchar(40), password varchar(20), status boolean, diskUsage integer, primary key (id));");
        sqlUpdate("CREATE TABLE IF NOT EXISTS admin(id integer, diskLimit long)");
        sqlUpdate("CREATE TABLE IF NOT EXISTS DropBoxCredentials(id integer, accessToken varchar(256), expiration long, refreshToken varchar(256), appKey varchar(256), appSecret varchar(256))");
    }

    /**
     * Inserts the user's dropbox credentials into the database
     *
     * @param credential The credential object
     * @throws SQLException On database access error
     */
    public void addDropBoxCredentials(DbxCredential credential) throws SQLException {
        sqlUpdate("INSERT INTO DropBoxCredentials VALUES ('" + currentUser.getId() + "','"
                + credential.getAccessToken() + "','" + credential.getExpiresAt() + "','" + credential.getRefreshToken() + "','"
                + credential.getAppKey() + "','" + credential.getAppSecret() + "');");
    }

    /**
     * Updates the user's dropbox credentials
     *
     * @param credential The credential object
     * @throws SQLException on database access error
     */
    public void updateDropBoxCredentials(DbxCredential credential) throws SQLException {
        sqlUpdate("UPDATE DropBoxCredentials set accessToken='" + credential.getAccessToken() + "', refreshToken='"
                + credential.getRefreshToken() + "', appKey='" + credential.getAppKey() + "', expiration='"
                + credential.getExpiresAt() + "', appSecret='" + credential.getAppSecret() + "' where id='" + currentUser.getId() + "'");
    }

    /**
     * Retrieves the user's dropbox credentials
     *
     * @return The credentials object
     * @throws SQLException On database access error
     */
    public DbxCredential getDropBoxCredentials() throws SQLException {
        ResultSet res;
        res = sqlQuery("SELECT accessToken, expiration, refreshToken, appKey, appSecret from DropBoxCredentials where id='" + currentUser.getId() + "'");
        if (res.isClosed()) {
            return null;
        }
        DbxCredential credential = new DbxCredential(res.getString("accessToken"),
                res.getLong("expiration"), res.getString("refreshToken"), res.getString("appKey"), res.getString("appSecret"));
        res.close();
        return credential;
    }

    /**
     * Checks whether the app has been booted for the first time
     *
     * @return true if the database is empty, false otherwise
     * @throws SQLException On database access error
     */
    public boolean isFirstBoot() throws SQLException {
        ResultSet res = sqlQuery("SELECT * FROM admin;");
        boolean empty = res.isClosed();
        currentUser.setAdmin(empty);
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
        PreparedStatement state = db.prepareStatement("INSERT INTO users(fName, lName, userName, email, password, status, diskUsage) VALUES (?,?,?,?,?,?,?)", key);
        state.setString(1, fName);
        state.setString(2, lName);
        state.setString(3, userName);
        state.setString(4, email);
        String hashPassword = hash.hashPassword(password, userName); // change salt
        state.setString(5, hashPassword);
        state.setBoolean(6, true);
        state.setInt(7, 0);
        state.execute();
        ResultSet rs = state.getGeneratedKeys();
        int res = rs.getInt(1);
        rs.close();
        state.close();
        setCurrentUser(fName, lName, userName, email, res);
        return res;
    }

    /**
     * Sets the current user for query purposes
     *
     * @param fName    The user's first name
     * @param lName    The user's last name
     * @param userName The user's username
     * @param email    The user's email
     * @param id       The ID of the user
     * @throws SQLException on error accessing the database
     */
    private void setCurrentUser(String fName, String lName, String userName, String email, int id) throws SQLException {
        Database.currentUser = new User(userName, fName, lName, email, false);
        Database.currentUser.setId(id);
        boolean isAdmin;
        isAdmin = isAdmin();
        Database.currentUser.setAdmin(isAdmin);
    }

    /**
     * Queries the database for the requested userName
     *
     * @param userName userName to be searched
     * @return true if found, false if not
     * @throws SQLException When a database access error occurs
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
     * @return The unique identifier of the user if the password matches, 0 if the user does not exist, -1 if the data is invalid
     * @throws SQLException When a database access error occurs
     */
    public int validateData(String userName, String password) throws SQLException {
        if (!userExists(userName)) {
            return 0;
        }
        ResultSet res = sqlQuery("SELECT id, password, status FROM main.users WHERE userName='" + userName + "'");
        String hashPassword = hash.hashPassword(password, userName); // change salt
        Integer key = validate(hashPassword, res);
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

    /**
     * Check whether the user has administrator privileges
     *
     * @param id The id of the user
     * @return True if it's the case, false otherwise
     * @throws SQLException On database access error
     */
    public boolean isAdmin(int id) throws SQLException {
        ResultSet res = sqlQuery("SELECT id from admin where id='" + id + "'");

        boolean bool = !res.isClosed();

        if (!bool) {
            res.close();
        }
        return bool;
    }

    /**
     * Checks whether the username and password match those in the database
     *
     * @param password The password
     * @param res      ResultSet object containing the username and password
     * @return The ID of the user on success, null if the user is already connected, 0 if the credentials aren't valid
     * @throws SQLException On database error
     */
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
     * @return The user's information the key is the field's name containing fName, lName, email, uName
     * @throws SQLException If a database access error occurs
     */

    public User getUserInfo(int id) throws SQLException {
        String idField = "from users where id='" + id + "'";
        return queryUserInfo(idField);
    }

    /**
     * Queries the database for the user's information
     *
     * @param userName The user's username
     * @return The user's information
     * @throws SQLException If a database access error occurs
     */
    public User getUserInfo(String userName) throws SQLException {
        if (!userExists(userName)) {
            return null;
        }
        String usernameField = "from users where userName='" + userName + "'";
        return queryUserInfo(usernameField);
    }

    /**
     * Retrieves the user's information
     *
     * @param idField The user's ID
     * @return A User object with the user's information
     * @throws SQLException On error accessing the database
     */
    private User queryUserInfo(String idField) throws SQLException {
        ResultSet usrInfo = sqlQuery("Select id, userName, fName, lName, email " + idField);
        if (usrInfo.isClosed()) {
            return null;
        }
        User user = new User(usrInfo.getString("userName"),
                usrInfo.getString("fName"),
                usrInfo.getString("lName"),
                usrInfo.getString("email"),
                isAdmin(usrInfo.getInt("id")));
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

    /**
     * Adds an invitation to the database
     *
     * @param projectId  The ID of the project the user is invited to participate on
     * @param senderId   The sender's ID
     * @param receiverId The destination's ID
     * @throws SQLException On error accessing the database
     */
    public void sendInvitation(int projectId, int senderId, int receiverId) throws SQLException {
        ResultSet rs = null;
        int id;
        try {   // Generate id
            rs = sqlQuery("SELECT id, MAX(id) FROM Invitations;");
            id = rs.getInt("id");
            id++;
        } catch (Exception e) {
            id = 1;
        }
        sqlUpdate("INSERT INTO Invitations(id, project_id, user1_id, user2_id) VALUES('" + id + "','" + projectId + "', '" + senderId + "','" + receiverId + "');");
        assert rs != null;
        rs.close();
    }

    /**
     * Removes an invitation
     *
     * @param inviteID The invitation ID
     * @throws SQLException on error accessing the database
     */
    public void removeInvitation(int inviteID) throws SQLException {
        sqlUpdate("DELETE FROM Invitations WHERE id = '" + inviteID + "';");
    }

    /**
     * returns the invitations for collaboration of a user
     * @param projectDB the project database
     * @return all the invitations
     * @throws SQLException if the query fails
     */
    public List<Invitation> getInvitations(ProjectDB projectDB) throws SQLException {
        List<Invitation> invitations = new ArrayList<>();
        ResultSet rs = sqlQuery("SELECT id, project_id, user1_id FROM Invitations WHERE user2_id = '" + currentUser.getId() + "';");
        while (rs.next()) {
            invitations.add(new Invitation(rs.getInt("id"), projectDB.getProject(rs.getInt("project_id")), currentUser, getUserInfo(rs.getInt("user1_id"))));
        }
        rs.close();
        return invitations;
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
        if (!newPassword.isBlank()) {
            setField(newPassword, "password");
        }
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
     * @throws SQLException When a database access error occurs
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

    /**
     * Updates the user's information
     *
     * @param newUser     The new user information. If null, it won't be updated
     * @param newPassword The new password. If null, the password won't be updated
     * @throws SQLException On database access error
     */
    public void userSettingsSync(User newUser, String newPassword) throws SQLException {
        if (newUser != null) {
            currentUser = newUser;
            setUserInfo(currentUser.getFirstName(), currentUser.getLastName(), currentUser.getEmail(), "");
            return;
        }
        String hashPassword = hash.hashPassword(newPassword, currentUser.getUserName()); // change salt
        sqlUpdate("UPDATE users set password='" + hashPassword + "' where id='" + currentUser.getId() + "'");
    }

}