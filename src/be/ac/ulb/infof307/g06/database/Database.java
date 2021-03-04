package be.ac.ulb.infof307.g06.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class Database {
    protected static Connection db;
    protected static String dbURL;

    public Database(String dbName) throws ClassNotFoundException {
        dbURL = dbName;
        Class.forName("org.sqlite.JDBC");
    }

    protected static void connect() throws SQLException {
        db = DriverManager.getConnection("jdbc:sqlite:" + dbURL);
    }

    public static void close(AutoCloseable... objects) throws SQLException {
        db.close();
        for (AutoCloseable obj : objects) {
            try {
                obj.close();
            } catch (Exception e) {
                return;
            }
        }
    }
}
