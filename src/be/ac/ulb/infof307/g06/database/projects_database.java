package be.ac.ulb.infof307.g06.database;

import java.sql.*;

public class projects_database {
    private static Connection database;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        init("projects.db");
        //createTable();
        //createProject("test1", "decr1", "tags1");
        System.out.println(getProjectTitle(0));
    }

    private static void init(String dbName) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        database = DriverManager.getConnection("jdbc:sqlite:" + dbName);
    }

    private static void createTable() throws SQLException {
        Statement state = database.createStatement();
        //TODO: Check if table already exists
        state.execute("CREATE TABLE Project(id Integer,title varchar(20),description varchar(20),tags varchar(20),date Date,subProjects_id Integer,tasks_id Integer);");
        state.execute("CREATE TABLE Collaborator(id Integer, project_id Integer, user_id Integer);");
        state.execute("CREATE TABLE Task(id Integer, description varchar(20));");
    }

    private static void createProject(String title, String description, String tags) throws SQLException {
        Statement state = database.createStatement();
        int id;
        try {
            ResultSet rs = state.executeQuery("SELECT MAX(id) FROM Project;");
            id = rs.getInt("id");
            id++;
        } catch (Exception e) {
            id = 0;
        }
        state.execute("INSERT INTO Project (id, title, description, tags) VALUES('" + id + "','" + title + "','" + description + "','" + tags + "');");
    }

    private static String getProjectTitle(int id) throws SQLException {
        Statement state = database.createStatement();

        ResultSet rs = state.executeQuery("SELECT title FROM Project WHERE id='" + id + "';");
        String title = rs.getString("title");
        return title;
    }
}
