
package be.ac.ulb.infof307.g06.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class UserDB extends Database {

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
     * @return Map<String, String> where the key is the field's name containing fName, lName, and email
     * @throws SQLException On error accessing the database
     */
    public static Map<String, String> getUserInfo(String userName) throws SQLException {
        Map<String, String> res = new HashMap<>();
        if (!userExists(userName)) {
            return res;
        }

        Statement state = connect();
        ResultSet usrInfo = state.executeQuery("Select fName, lName, email, status from users where userName='" + userName + "'");

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

    public static String getAccToken(String userName) throws SQLException {
        Statement state = connect();
        String res = state.executeQuery("SELECT accToken from users where userName='" + userName + "'").getString("accToken");
        close(state);
        return res;
    }

    public static void addAccToken(String token, String userName) throws SQLException {
        Statement state = connect();
        state.executeUpdate("UPDATE users SET accToken='" + token + "' where userName='" + userName + "'");
        close(state);
    }
}