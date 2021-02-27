package be.ac.ulb.infof307.g6.database;

import java.sql.*;
public class Database {
    private static Connection database;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        init("data.db");
        createTable();
    }
    private static void init(String dbName) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        database = DriverManager.getConnection("jdbc:sqlite:" + dbName);
    }

    private static void createTable() throws SQLException {
        Statement state = database.createStatement();

        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='Users'");
        if (res.next()){
            System.out.println("Found");
            return;
        }
        state.execute("CREATE TABLE Users(id Integer," + "fName varchar(20),"
                + "lName varchar(20)," + "userName varchar(20),"
                + "email varchar(40)," + "password varchar(20),"
                + "primary key (id));");
    }
}
