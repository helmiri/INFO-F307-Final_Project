package be.ac.ulb.infof307.g06.models.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * The database main abstract class
 */
public abstract class Database {
    private Statement state;
    private final DatabaseConnection connection;


    /**
     * Initializes the database
     *
     * @throws SQLException If a database access error occurs
     */
    public Database() throws SQLException {
        // Ensures that the same connection is shared across all database instances
        connection = DatabaseConnection.getInstance();
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
    protected ResultSet prepareSqlQuery(String query) throws SQLException {
        state = connection.createStatement();
        state.close();
        return state.executeQuery(query);
    }

    /**
     * Executes an update in the database
     * @param query The query we want to execute
     * @throws SQLException if the query fails
     */
    protected void sqlUpdate(String query) throws SQLException {
        state = connection.createStatement();
        state.executeUpdate(query);
        state.close();
    }

    /**
     * Creates a prepared statement
     * @param query The query to be prepared
     * @param keys The keys to be retrieved
     * @return The prepared statement
     * @throws SQLException on error creating the statement
     */
    protected PreparedStatement prepareSqlQuery(String query, String[] keys) throws SQLException {
        return connection.prepareStatement(query, keys);
    }
}
