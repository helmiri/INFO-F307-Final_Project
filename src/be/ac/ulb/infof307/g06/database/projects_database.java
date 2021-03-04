// TODO: collaborators, edit project

package be.ac.ulb.infof307.g06.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class projects_database {
    private static Connection database;

    public static Connection init(String dbName) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        database = DriverManager.getConnection(dbName);
        createTable();
        return database;
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
        try {   // Generate id
            ResultSet rs = state.executeQuery("SELECT id, MAX(id) FROM Project;");
            id = rs.getInt("id");
            id++;
        } catch (Exception e) {
            System.out.println(e);
            id = 1;
        }

        if (parent_id != 0){    // Add the parent tags to the current tags
            String parent_tags = getProject(parent_id).getTags();
            tags = parent_tags + "," + tags;
        }
        state.execute("INSERT INTO Project (id, title, description, tags, date, parent_id) VALUES('" +
                id + "','" + title + "','" + description + "','" + tags + "','" + date.getTime() + "','" + parent_id + "');");
        return id;
    }

    public static void editProject(int id, String title, String description, String tags, Date date) throws SQLException{
        Statement state = database.createStatement();
        state.execute("UPDATE Project SET title = '" + title + "', description = '" + description + "', tags = '" + tags + "', date = '" + date + "' WHERE id = '" + id + "';");
    }

    public static void deleteProject(int id) throws SQLException{
        Statement state = database.createStatement();
        List<Integer> subProjects = getSubProjects(id);
        for (Integer subProject : subProjects) {
            deleteProject(subProject);
        }
        state.execute("DELETE FROM Collaborator WHERE project_id = '" + id + "';");
        state.execute("DELETE FROM Task WHERE project_id = '" + id + "';");
        state.execute("DELETE FROM Project WHERE id = '" + id + "';");
    }


    public static List<Integer> getSubProjects(int id) throws SQLException{
        Statement state = database.createStatement();
        List<Integer> subProjects = new ArrayList<Integer>();
        ResultSet rs = state.executeQuery("SELECT id FROM Project WHERE parent_id = '" + id + "';");
        while (rs.next()){
            subProjects.add(rs.getInt("id"));
        }
        return subProjects;
    }


    public static int getProjectID(String title) throws SQLException{
        Statement state = database.createStatement();
        ResultSet rs = state.executeQuery("SELECT id FROM Project WHERE title='" + title + "';");

        int id = rs.getInt("id");
        return id;
    }

    public static Project getProject(int id) throws SQLException {
        Statement state = database.createStatement();
        try {
            ResultSet rs = state.executeQuery("SELECT title,description,tags,date,parent_id FROM Project WHERE id='" + id + "';");

            String title = rs.getString("title");
            String description = rs.getString("description");
            String tags = rs.getString("tags");
            Date date = new Date(rs.getLong("date"));
            int parent_id = rs.getInt("parent_id");
            Project res = new Project(id, title, description, tags, date, parent_id);
            return res;
        } catch (Exception e){
            return null;
        }

    }

    public static int addCollaborator(int project_id, int user_id) throws SQLException{
        Statement state = database.createStatement();
        int id;
        try {   // Generate id
            ResultSet rs = state.executeQuery("SELECT id, MAX(id) FROM Collaborator;");
            id = rs.getInt("id");
            id++;
        } catch (Exception e) {
            System.out.println(e);
            id = 1;
        }
        state.execute("INSERT INTO Collaborator (id, project_id, user_id) VALUES ('" + id + "','" + project_id + "','" + user_id + "';");
        return id;
    }

    public static void deleteCollaborator(int project_id, int user_id) throws SQLException{
        Statement state = database.createStatement();
        state.execute("DELETE FROM Collaborator WHERE project_id = '" + project_id + "' and user_id = '" + user_id + "';");
    }

    public static List<Integer> getCollaborators(int project_id) throws SQLException{
        Statement state = database.createStatement();
        ResultSet rs = state.executeQuery("SELECT user_id FROM Collaborator WHERE project_id='" + project_id + "';");
        List<Integer> res = new ArrayList<Integer>();
        while (rs.next()){
            res.add(rs.getInt("user_id"));
        }
        return res;
    }

    public static List<Integer> getUserProjects(int user_id) throws SQLException{
        Statement state = database.createStatement();
        ResultSet rs = state.executeQuery("SELECT project_id FROM Collaborator WHERE user_id='" + user_id + "';");
        List<Integer> res = new ArrayList<Integer>();
        while (rs.next()){
            res.add(rs.getInt("project_id_id"));
        }
        return res;
    }

    public static int createTask(String description, int project_id) throws SQLException{
        Statement state = database.createStatement();
        int id;
        try {   // Generate id
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

    public static void editTask(String prev_description, String new_description, int project_id) throws SQLException{
        Statement state = database.createStatement();
        state.execute("UPDATE Task SET description = '" + new_description + "' WHERE project_id = '" + project_id + "' AND description = '" + prev_description + "';");
    }

    public static void deleteTask(String description , int project_id) throws SQLException{
        Statement state = database.createStatement();
        state.execute("DELETE Task WHERE project_id = '" + project_id + "' and description = '" + description + "';");
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

    public static class Project {
        int id;
        String title;
        String description;
        String tags;
        Date date;
        int parent_id;

        public Project(int id, String title, String description, String tags, Date date, int parent_id) {
            this.title = title;
            this.description = description;
            this.tags = tags;
            this.date = date;
            this.parent_id = parent_id;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getTags() {
            return tags;
        }

        public Date getDate() {
            return date;
        }

        public int getParent_id() {
            return parent_id;
        }
    }

    public static class Task {
        String description;

        public Task(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
