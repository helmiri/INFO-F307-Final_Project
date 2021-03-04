
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

    public UserDB(String dbName) throws ClassNotFoundException {
        super(dbName);
    }

    private static void createTable() throws SQLException {
        Statement state = db.createStatement();
        state.execute("CREATE TABLE IF NOT EXISTS users(id Integer," + "fName varchar(20),"
                + "lName varchar(20)," + "userName varchar(20),"
                + "email varchar(40)," + "password varchar(20),"
                + "primary key (id));");
    }

    /**
     * @param fName First name
     * @param lName Last name
     * @param userName User name
     * @param email email
     * @param password password
     * @return true on success
     * @throws SQLException Error accessing the database
     */
    public static boolean addUser(String fName, String lName, String userName, String email, String password) throws SQLException {

        connect();
        createTable();
        PreparedStatement state1 = db.prepareStatement("INSERT INTO users(fName, lName, userName, email, password) VALUES (?,?,?,?,?)");
        state1.setString(1, fName);
        state1.setString(2, lName);
        state1.setString(3, userName);
        state1.setString(4, email);
        state1.setString(5, password);
        state1.execute();
        closeConnection();
        return true;
    }

    public static boolean userExists(String userName) throws SQLException {
        connect();
        Statement state = db.createStatement();
        ResultSet res = state.executeQuery("SELECT userName FROM users WHERE userName='" + userName + "'");
        boolean found = res.next();
        closeConnection();
        return found;
    }

    public static boolean validateData(String userName, String password) throws SQLException {
        if (!userExists(userName)) {
            return false;
        }
        connect();
        Statement state = db.createStatement();
        ResultSet res = state.executeQuery("SELECT password FROM main.users WHERE userName='" + userName + "'");
        boolean valid = res.getString("password").equals(password);
        closeConnection();
        return valid;
    }

    public static Map<String, String> getUserInfo(String userName) throws SQLException {
        Map<String, String> res = new HashMap<>();
        if (!userExists(userName)) {
            return res;
        }
        connect();

        Statement state = db.createStatement();
        ResultSet usrInfo = state.executeQuery("Select fName, lName, email from users where userName='" + userName + "'");

        res.put("fName", usrInfo.getString("fName"));
        res.put("lName", usrInfo.getString("lName"));
        res.put("email", usrInfo.getString("email"));
        closeConnection();
        return res;
    }
}