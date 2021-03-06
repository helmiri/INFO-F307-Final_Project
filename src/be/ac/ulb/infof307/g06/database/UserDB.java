
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
     * @param fName    First name
     * @param lName    Last name
     * @param userName User name
     * @param email    email
     * @param password password
     * @return true on success
     * @throws SQLException Error accessing the database
     */
    public static int addUser(String fName, String lName, String userName, String email, String password) throws SQLException {
        connect();
        String[] key = {"id"};
        PreparedStatement state1 = db.prepareStatement("INSERT INTO users(fName, lName, userName, email, password) VALUES (?,?,?,?,?)", key);
        state1.setString(1, fName);
        state1.setString(2, lName);
        state1.setString(3, userName);
        state1.setString(4, email);
        state1.setString(5, password);
        state1.execute();
        ResultSet rs = state1.getGeneratedKeys();
        int res = rs.getInt(1);
        close(state1, rs);
        return res;
    }

    public static boolean userExists(String userName) throws SQLException {
        Statement state = connect();
        ResultSet res = state.executeQuery("SELECT userName FROM users WHERE userName='" + userName + "'");
        boolean found = res.next();
        close(state, res);
        return found;
    }

    public static int validateData(String userName, String password) throws SQLException {
        if (!userExists(userName)) {
            return 0;
        }
        Statement state = connect();
        ResultSet res = state.executeQuery("SELECT id, password FROM main.users WHERE userName='" + userName + "'");
        boolean valid = res.getString("password").equals(password);
        int key = 0;
        if (valid) {
            key = res.getInt("id");
        }
        close(state, res);
        return key;
    }

    public static Map<String, String> getUserInfo(String userName) throws SQLException {
        Map<String, String> res = new HashMap<>();
        if (!userExists(userName)) {
            return res;
        }

        Statement state = connect();
        ResultSet usrInfo = state.executeQuery("Select fName, lName, email from users where userName='" + userName + "'");

        res.put("fName", usrInfo.getString("fName"));
        res.put("lName", usrInfo.getString("lName"));
        res.put("email", usrInfo.getString("email"));
        close(state, usrInfo);
        return res;
    }
}