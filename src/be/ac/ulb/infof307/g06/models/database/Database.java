package be.ac.ulb.infof307.g06.models.database;

import java.sql.*;

public abstract class Database {
    protected Connection db;
    protected String dbURL;
    private Statement state;

    /**
     * Initializes the database
     *
     * @param dbName Path to database file
     * @throws ClassNotFoundException When the JDBC driver cannot be found
     * @throws SQLException           If a database access error occurs
     */
    public Database(String dbName) throws ClassNotFoundException, SQLException {
        dbURL = dbName;
        db = DriverManager.getConnection("jdbc:sqlite:" + dbURL);
        Class.forName("org.sqlite.JDBC");
        createTables();
    }


    /**
     * Closes the connections
     *
     * @param objects An arbitrary number of AutoClosable objects
     * @throws SQLException If a database access error occurs
     */
    protected static void close(AutoCloseable... objects) throws SQLException {
        for (AutoCloseable obj : objects) {
            try {
                obj.close();
            } catch (Exception e) {
                return;
            }
        }
    }

    /**
     * Implement this method to create the tables needed
     *
     * @throws SQLException If a database access error occurs or this method is called on a closed connection
     */
    protected abstract void createTables() throws SQLException;


    protected ResultSet sqlQuery(String query) throws SQLException {
        state = db.createStatement();
        state.close();
        return state.executeQuery(query);
    }

    protected void sqlUpdate(String query) throws SQLException {
        state = db.createStatement();
        state.executeUpdate(query);
        state.close();
    }
}
