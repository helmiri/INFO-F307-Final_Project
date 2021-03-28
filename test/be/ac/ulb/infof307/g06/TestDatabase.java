package be.ac.ulb.infof307.g06;

import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDatabase {
    protected static Connection db;
    protected static List<Map<String, String>> testData;
    protected static List<String> dbFields;

    public TestDatabase() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
    }

    @BeforeAll
    public static void setup() throws SQLException, ClassNotFoundException {
        db = DriverManager.getConnection("jdbc:sqlite:test/be/ac/ulb/infof307/g06/testDB.db");
        dbFields = new ArrayList<>(5);
        dbFields.add("fName");
        dbFields.add("lName");
        dbFields.add("userName");
        dbFields.add("email");
        dbFields.add("password");
        dbFields.add("accToken");
        dbFields.add("clientID");

        testData = new ArrayList<>(10);
        Map<String, String> userData;
        for (int i = 0; i < 10; i++) {
            userData = new HashMap<>();
            for (String dbField : dbFields) {
                userData.put(dbField, "User_" + (i + 1) + '_' + dbField);
            }
            testData.add(i, userData);
        }
        new UserDB("test/be/ac/ulb/infof307/g06/testDB.db");
        new ProjectDB("test/be/ac/ulb/infof307/g06/testDB.db");
    }

    @BeforeEach
    private void prepareUserData() throws SQLException {
        /*
          Populate tesDB with testData
         */
        db = DriverManager.getConnection("jdbc:sqlite:test/be/ac/ulb/infof307/g06/testDB.db");
        PreparedStatement state;
        for (int i = 0; i < 10; i++) {
            state = db.prepareStatement("INSERT INTO users(fName, lName, userName, email, password, accToken, clientID) VALUES (?,?,?,?,?,?,?)");
            for (int j = 0; j < 7; j++) {
                state.setString(j + 1, testData.get(i).get(dbFields.get(j)));
            }
            state.execute();
            state.close();
        }
    }

    @SuppressWarnings("SqlWithoutWhere")
    @AfterEach
    public void clear() throws SQLException {
        /*
          Clear testDB for a fresh start after each Test
         */
        Statement state = db.createStatement();
        state.executeUpdate("DELETE FROM users");
        state.executeUpdate("DELETE FROM Project");
        state.executeUpdate("DELETE FROM Collaborator");
        state.executeUpdate("DELETE FROM Task");
        state.close();
        db.close();
    }
}
