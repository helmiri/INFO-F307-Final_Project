package be.ac.ulb.infof307.g06.models.database;

import java.sql.*;

/**
 * Ensures that the same database connection is used among all instances of the databases using a Singleton-inspired design pattern
 * This is to avoid having to pass the database path as parameter across all controllers that need access to the database
 * It also ensures that a controller does not decide to create its own database separate from the rest which has security
 * risk implications because the database has to be encrypted when the application closes.
 * Allowing controllers to create their own databases could leak sensitive data
 * <p>
 * Furthermore, a single connection also ensures that no trailing connections are left open when the application closes
 * Once the connection is closed (preferably by the class that initialized it), it cannot be opened again signaling the
 * end of the execution of the program
 */
public class DatabaseConnection {
    @SuppressWarnings("StaticVariableOfConcreteClass") // Justified in this case to ensure a single instance
    private static DatabaseConnection connection;
    private Connection sqliteConnection;

    private DatabaseConnection(String databasePath) throws SQLException, ClassNotFoundException {
        sqliteConnection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
        Class.forName("org.sqlite.JDBC");
    }

    /**
     * Initializes a connection. It also ensures that only the first path provided to initialize the connection
     * is the only one used even if this method is called multiple times
     *
     * @param databasePath Path to the database
     * @throws SQLException           On error creating the connection
     * @throws ClassNotFoundException On error locating the sqlite library
     */
    public static void connect(String databasePath) throws SQLException, ClassNotFoundException {
        if (connection == null) {
            connection = new DatabaseConnection(databasePath);
        }
    }

    /**
     * @return The single instance of the connection
     */
    public static DatabaseConnection getInstance() {
        return connection;
    }

    /**
     * Creates and returns a statement
     *
     * @return The statement
     * @throws SQLException on error
     */
    public Statement createStatement() throws SQLException {
        return sqliteConnection.createStatement();
    }

    /**
     * Creates and returns a sqlite prepared statement
     *
     * @param query The query to be prepared
     * @param keys  The keys to be retrieved
     * @return The statement
     * @throws SQLException On error
     */
    public PreparedStatement prepareStatement(String query, String[] keys) throws SQLException {
        return sqliteConnection.prepareStatement(query, keys);
    }

    /**
     * Closes the database connection. Once closed it cannot be opened again to avoid funky business because all
     * database instances should access the same database
     *
     * @throws SQLException On error closing the connection
     */
    public void closeConnection() throws SQLException {
        sqliteConnection.close();
    }
}
