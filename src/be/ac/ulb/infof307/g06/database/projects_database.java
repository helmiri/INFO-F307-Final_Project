// TODO: collaborators, edit project

package be.ac.ulb.infof307.g06.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class Project{
    int id;
    String title;
    String description;
    String tags;
    Date date;
    int parent_id;

    public Project(int id, String title, String description, String tags, Date date, int parent_id){
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.date = date;
        this.parent_id = parent_id;
    }

    public int getId(){return id;}
    public String getTitle(){return title;}
    public String getDescription(){return description;}
    public String getTags(){return tags;}
    public Date getDate(){return date;}
    public int getParent_id(){return parent_id;}
}
class Task{
    String description;
    public Task(String description){this.description = description;}
    public String getDescription() {return description; }
}
public class projects_database {
    private static Connection database;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        init("projects.db");
        Date today = new Date(System.currentTimeMillis());
        //createTask("test1", 1);
        //createTask("test2", 1);
        //createTask("test3", 1);
        //createTask("test4", 2);

        System.out.println(getTasks(1).toString());
    }

    public static void init(String dbName) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        database = DriverManager.getConnection("jdbc:sqlite:" + dbName);
        createTable();
    }

    private static void createTable() throws SQLException {
        Statement state = database.createStatement();

        state.execute("CREATE TABLE IF NOT EXISTS Project(id Integer, title varchar(20), description varchar(20), tags varchar(20), date Long, parent_id Integer);");
        state.execute("CREATE TABLE IF NOT EXISTS Collaborator(id Integer, project_id Integer, user_id Integer);");
        state.execute("CREATE TABLE IF NOT EXISTS Task(id Integer, description varchar(20), project_id Integer);");

    }

    public static int createProject(String title, String description, String tags, Date date, int parent_id) throws SQLException {
        Statement state = database.createStatement();
        int id;
        try {
            ResultSet rs = state.executeQuery("SELECT id, MAX(id) FROM Project;");
            id = rs.getInt("id");
            id++;
        } catch (Exception e) {
            System.out.println(e);
            id = 1;
        }

        state.execute("INSERT INTO Project (id, title, description, tags, date, parent_id) VALUES('" +
                id + "','" + title + "','" + description + "','" + tags + "','" + date.getTime() + "','" + parent_id + "');");

        return id;
    }

    public static int getProjectID(String title) throws SQLException{
        Statement state = database.createStatement();
        ResultSet rs = state.executeQuery("SELECT id FROM Project WHERE title='" + title + "';");

        int id = rs.getInt("id");

        return id;
    }

    public static Project getProject(int id) throws SQLException {
        Statement state = database.createStatement();
        ResultSet rs = state.executeQuery("SELECT title,description,tags,date,parent_id FROM Project WHERE id='" + id + "';");

        String title = rs.getString("title");
        String description = rs.getString("description");
        String tags = rs.getString("tags");
        Date date =  new Date(rs.getLong("date"));
        int parent_id = rs.getInt("parent_id");
        Project res = new Project(id,title,description,tags,date,parent_id);

        return res;
    }

    public static int createTask(String description, int project_id) throws SQLException{
        Statement state = database.createStatement();
        int id;
        try {
            ResultSet rs = state.executeQuery("SELECT id, MAX(id) FROM Task;");
            id = rs.getInt("id");
            id++;
        } catch (Exception e) {
            System.out.println(e);
            id = 1;
        }
        state.execute("INSERT INTO Task (id, description, project_id) VALUES('" +
                id + "','" + description + "','" + project_id+ "');");
        return id;
    }

    public static List<Task> getTasks(int project_id) throws SQLException{
        Statement state = database.createStatement();
        ResultSet rs = state.executeQuery("SELECT description FROM Task WHERE project_id='" + project_id + "';");
        List<Task> res = new ArrayList<Task>();

        while (rs.next()){
            res.add(new Task(rs.getString("description")));
        }
        return res;
    }

}
