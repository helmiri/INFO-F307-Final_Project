package be.ac.ulb.infof307.g06.database;

import java.sql.*;

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

    protected static void closeConnection() throws SQLException {
        db.close();
    }

    protected static ResultSet run(String sql) throws SQLException {
        connect();
        Statement state = db.createStatement();
        ResultSet res = state.executeQuery(sql);
        closeConnection();
        return res;
    }
}
