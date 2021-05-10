package be.ac.ulb.infof307.g06.models.database;

import be.ac.ulb.infof307.g06.models.User;

import java.sql.*;



/**
 * The database main abstract class
 */
public abstract class Database {
    protected static Connection db;
    protected String dbURL;
    private Statement state;
    protected static User currentUser;

    /**
     * Initializes the database
     *
     * @param dbName Path to database file
     * @throws ClassNotFoundException When the JDBC driver cannot be found
     * @throws SQLException           If a database access error occurs
     */
    public Database(String dbName) throws ClassNotFoundException, SQLException {
        dbURL = dbName;
        // Ensures that the same connection  is shared across all instances
        if (db == null) {
            db = DriverManager.getConnection("jdbc:sqlite:" + dbURL);
            Class.forName("org.sqlite.JDBC");
        }
        createTables();
    }


    /**
     * Implement this method to create the tables needed
     *
     * @throws SQLException If a database access error occurs or this method is called on a closed connection
     */
    protected abstract void createTables() throws SQLException;

    /**
     * Executes an sql query
     * @param query The query we want to execute
     * @return ResultSet
     * @throws SQLException if query fails
     */
    protected ResultSet sqlQuery(String query) throws SQLException {
        state = db.createStatement();
        state.close();
        return state.executeQuery(query);
    }

    /**
     * Executes an update in the database
     * @param query The query we want to execute
     * @throws SQLException if the query fails
     */
    protected void sqlUpdate(String query) throws SQLException {
        state = db.createStatement();
        state.executeUpdate(query);
        state.close();
    }

    /**
     * Returns the current user
     * @return The user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * closes the database
     * @throws SQLException if the closing fails
     */
    public void disconnectDB() throws SQLException {
        db.close();
    }
}
