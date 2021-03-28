package be.ac.ulb.infof307.g06.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class Database {
    protected static Connection db;
    protected static String dbURL;

    /**
     * Initializes the database
     *
     * @param dbName Path to database file
     * @throws ClassNotFoundException When the JDBC driver cannot be found
     * @throws SQLException           If a database access error occurs
     */
    public Database(String dbName) throws ClassNotFoundException, SQLException {
        dbURL = dbName;
        Class.forName("org.sqlite.JDBC");
        createTables();
    }

    /**
     * Establishes a connection to the database
     *
     * @return Statement object
     * @throws SQLException If a database access error occurs
     */
    protected static Statement connect() throws SQLException {
        db = DriverManager.getConnection("jdbc:sqlite:" + dbURL);
        return db.createStatement();
    }

    /**
     * Closes the connections
     *
     * @param objects An arbitrary number of AutoClosable objects
     * @throws SQLException If a database access error occurs
     */
    protected static void close(AutoCloseable... objects) throws SQLException {
        db.close();
        for (AutoCloseable obj : objects) {
            try {
                obj.close();
            } catch (Exception e) {
                System.out.println("Error");
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

}
