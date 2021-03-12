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
     * @throws SQLException           On error creating tables
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
     * @throws SQLException
     */
    protected static Statement connect() throws SQLException {
        db = DriverManager.getConnection("jdbc:sqlite:" + dbURL);
        return db.createStatement();
    }

    /**
     * Closes the connections
     *
     * @param objects An arbitrary number of AutoClosable objects
     * @throws SQLException
     */
    protected static void close(AutoCloseable... objects) throws SQLException {
        db.close();
        for (AutoCloseable obj : objects) {
            try {
                obj.close();
            } catch (Exception e) {
                return;
            }
        }
    }

    /**
     * Creates the database tables if they don't exist
     *
     * @throws SQLException
     */
    protected void createTables() throws SQLException {
        Statement state = connect();
        state.execute("CREATE TABLE IF NOT EXISTS Project(id Integer, title varchar(20), description varchar(20), date Long, parent_id Integer);");
        state.execute("CREATE TABLE IF NOT EXISTS Collaborator(id Integer, project_id Integer, user_id Integer);");
        state.execute("CREATE TABLE IF NOT EXISTS Task(id Integer, description varchar(20), project_id Integer);");
        state.execute("CREATE TABLE IF NOT EXISTS Tag(id Integer, description varchar(20), color Integer);");
        state.execute("CREATE TABLE IF NOT EXISTS Tag_projects(tag_id Integer, project_id Integer);");
        state.execute("CREATE TABLE IF NOT EXISTS users(id Integer, fName varchar(20), lName varchar(20), userName varchar(20),email varchar(40),password varchar(20), primary key (id));");
        close(state);
    }
}
