package be.ac.ulb.infof307.g06.database;

import java.sql.*;
class Project{
    int id;
    String title;
    String description;
    String tags;
    Date date;
    int[] subProjects;
    int[] tasks;

    public Project(int id, String title, String description, String tags, Date date){
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.date = date;
    }
    public int getId(){return id;}
    public String getTitle(){return title;}
    public String getDescription(){return description;}
    public String getTags(){return tags;}
    public Date getDate(){return date;}
}
public class projects_database {
    private static Connection database;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        init("projects.db");
        //createTable();
        //createProject("test1", "decr1", "tags1");
        System.out.println(getProject(0).getTitle());
        System.out.println(getProjectID("test1"));
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

    private static int createProject(String title, String description, String tags) throws SQLException {
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

        return id;
    }

    private static int getProjectID(String title)throws  SQLException{
        Statement state = database.createStatement();
        ResultSet rs = state.executeQuery("SELECT id FROM Project WHERE title='" + title + "';");

        int id = rs.getInt("id");

        return id;
    }

    private static Project getProject(int id) throws SQLException {
        Statement state = database.createStatement();
        ResultSet rs = state.executeQuery("SELECT title,description,tags, date FROM Project WHERE id='" + id + "';");

        String title = rs.getString("title");
        String description = rs.getString("description");
        String tags = rs.getString("tags");
        Date date =  rs.getDate("date");
        Project res = new Project(id,title,description,tags,date);
        return res;
    }
}
