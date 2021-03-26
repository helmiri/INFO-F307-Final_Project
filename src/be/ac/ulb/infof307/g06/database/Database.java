package be.ac.ulb.infof307.g06.database;

import java.sql.*;

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
                System.out.println("Error");
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
        state.execute("CREATE TABLE IF NOT EXISTS Collaborator(project_id Integer, user_id Integer);");
        state.execute("CREATE TABLE IF NOT EXISTS Task(id Integer, description varchar(20), project_id Integer);");
        state.execute("CREATE TABLE IF NOT EXISTS Tag(id Integer, description varchar(20), color varchar(20));");
        state.execute("CREATE TABLE IF NOT EXISTS Tag_projects(tag_id Integer, project_id Integer);");
        state.execute("CREATE TABLE IF NOT EXISTS Invitations(id Integer, project_id Integer, user1_id Integer, user2_id Integer);");
        state.execute("CREATE TABLE IF NOT EXISTS tasks_users(task_id Integer, user_id Integer);");
        state.execute("CREATE TABLE IF NOT EXISTS users(id Integer, fName varchar(20), lName varchar(20), userName varchar(20)," +
                "email varchar(40), password varchar(20), status boolean, accToken varchar(64), clientID varchar(64), diskUsage integer, primary key (id));");
        state.execute("CREATE TABLE IF NOT EXISTS admin(id integer, diskLimit integer)");
        close(state);
    }

    public static boolean isFirstBoot() throws SQLException {
        Statement state = connect();
        ResultSet res = state.executeQuery("SELECT * FROM users;");
        boolean empty = res.isClosed();
        close(state, res);
        return empty;
    }
}
