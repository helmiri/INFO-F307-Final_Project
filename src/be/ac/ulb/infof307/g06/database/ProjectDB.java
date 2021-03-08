
package be.ac.ulb.infof307.g06.database;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProjectDB extends Database {

    public ProjectDB(String dbName) throws ClassNotFoundException, SQLException {
        super(dbName);
    }


    public static int createProject(String title, String description, String tags, Long date, int parent_id) throws SQLException {
        Statement state = connect();
        ResultSet rs = null;
        int id;
        try {   // Generate id
            rs = state.executeQuery("SELECT id, MAX(id) FROM Project;");
            id = rs.getInt("id");
            id++;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            id = 1;
        }

        if (parent_id != 0){    // Add the parent tags to the current tags
            String parent_tags = getProject(parent_id).getTags();
            tags = parent_tags + "/" + tags;
        }
        state.execute("INSERT INTO Project (id, title, description, tags, date, parent_id) VALUES('" +
                id + "','" + title + "','" + description + "','" + tags + "','" + date + "','" + parent_id + "');");
        close(state, rs);
        return id;
    }

    public static void editProject(int id, String title, String description, String tags, Long date) throws SQLException{
        Statement state = connect();
        state.execute("UPDATE Project SET title = '" + title + "', description = '" + description + "', tags = '" + tags + "', date = '" + date + "' WHERE id = '" + id + "';");
        for (Integer subProject : getSubProjects(id)) {
            editTags(subProject, tags);
        }
        close(state);
    }

    public static void editTags(int id, String tags) throws SQLException{
        Statement state = connect();
        String newTags = tags.replace("/", ",") + getProject(id).getTags().split("/")[1];
        state.execute("UPDATE Project SET tags = '" + newTags + "' WHERE id = '" + id + "';");
        for (Integer subProject : getSubProjects(id)) {
            editTags(subProject, newTags);
        }
        close(state);
    }

    public static void deleteProject(int id) throws SQLException{
        Statement state = connect();
        for (Integer subProject : getSubProjects(id)) {
            deleteProject(subProject);
        }
        state.execute("DELETE FROM Collaborator WHERE project_id = '" + id + "';");
        state.execute("DELETE FROM Task WHERE project_id = '" + id + "';");
        state.execute("DELETE FROM Project WHERE id = '" + id + "';");
        close(state);
    }


    public static List<Integer> getSubProjects(int id) throws SQLException {
        Statement state = connect();
        List<Integer> subProjects = new ArrayList<>();
        ResultSet rs = state.executeQuery("SELECT id FROM Project WHERE parent_id = '" + id + "';");
        while (rs.next()) {
            subProjects.add(rs.getInt("id"));
        }
        close(state, rs);
        return subProjects;
    }


    public static int getProjectID(String title) throws SQLException {
        Statement state = connect();
        int id = 0;
        try{
            ResultSet rs = state.executeQuery("SELECT id FROM Project WHERE title='" + title + "';");

            id = rs.getInt("id");
        }
        catch (Exception ignored){};

        close(state);
        return id;
    }

    public static Project getProject(int id) throws SQLException {
        Statement state = connect();
        ResultSet rs = null;
        Project res = null;
        try {
            rs = state.executeQuery("SELECT title,description,tags,date,parent_id FROM Project WHERE id='" + id + "';");

            String title = rs.getString("title");
            String description = rs.getString("description");
            String tags = rs.getString("tags");
            Long date = rs.getLong("date");
            int parent_id = rs.getInt("parent_id");
            res = new Project(id, title, description, tags, date, parent_id);
        } catch (Exception ignored) {
        }

        close(state, rs);
        return res;
    }

    public static int addCollaborator(int project_id, int user_id) throws SQLException {
        Statement state = connect();
        ResultSet rs = null;
        int id;
        try {   // Generate id
            rs = state.executeQuery("SELECT id, MAX(id) FROM Collaborator;");
            id = rs.getInt("id");
            id++;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            id = 1;
        }
        state.execute("INSERT INTO Collaborator (id, project_id, user_id) VALUES ('" + id + "','" + project_id + "','" + user_id + "');");
        close(rs, state);
        return id;
    }


    public static void deleteCollaborator(int project_id, int user_id) throws SQLException{
        Statement state = connect();
        state.execute("DELETE FROM Collaborator WHERE project_id = '" + project_id + "' and user_id = '" + user_id + "';");
        close(state);
    }

    public static List<Integer> getCollaborators(int project_id) throws SQLException{
        Statement state = connect();
        ResultSet rs = state.executeQuery("SELECT user_id FROM Collaborator WHERE project_id='" + project_id + "';");
        List<Integer> res = new ArrayList<>();
        while (rs.next()) {
            res.add(rs.getInt("user_id"));
        }
        close(rs);
        return res;
    }

    public static int countCollaborators(int project_id) throws SQLException{
        Statement state = connect();
        int res;
        ResultSet rs = state.executeQuery("SELECT COUNT(*) FROM Collaborator WHERE project_id='" + project_id + "';");
        res = rs.getInt("COUNT(*)");
        close(rs);
        return res;
    }

    public static List<Integer> getUserProjects(int user_id) throws SQLException {
        Statement state = connect();
        ResultSet rs = state.executeQuery("SELECT project_id FROM Collaborator WHERE user_id='" + user_id + "';");
        List<Integer> res = new ArrayList<>();
        while (rs.next()) {
            res.add(rs.getInt("project_id"));
        }
        close(rs);
        return res;
    }

    public static int createTask(String description, int project_id) throws SQLException {
        Statement state = connect();
        ResultSet rs = null;
        int id;
        try {   // Generate id
            rs = state.executeQuery("SELECT id, MAX(id) FROM Task;");
            id = rs.getInt("id");
            id++;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            id = 1;
        }
        state.execute("INSERT INTO Task (id, description, project_id) VALUES('" +
                id + "','" + description + "','" + project_id + "');");
        close(rs, state);
        return id;
    }

    public static void editTask(String prev_description, String new_description, int project_id) throws SQLException{
        Statement state = connect();
        state.execute("UPDATE Task SET description = '" + new_description + "' WHERE project_id = '" + project_id + "' AND description = '" + prev_description + "';");
        close(state);
    }

    public static void deleteTask(String description , int project_id) throws SQLException{
        Statement state = connect();
        state.execute("DELETE Task WHERE project_id = '" + project_id + "' and description = '" + description + "';");
        close(state);
    }

    public static List<Task> getTasks(int project_id) throws SQLException{
        Statement state = connect();
        ResultSet rs = state.executeQuery("SELECT description FROM Task WHERE project_id='" + project_id + "';");
        List<Task> res = new ArrayList<>();

        while (rs.next()){
            res.add(new Task(rs.getString("description"), project_id));
        }
        close(state, rs);
        return res;
    }

    public static int countTasks(int project_id) throws SQLException{
        Statement state = connect();
        int res;
        ResultSet rs = state.executeQuery("SELECT COUNT(*) FROM Tasks WHERE project_id='" + project_id + "';");
        res = rs.getInt("COUNT(*)");
        close(rs);
        return res;
    }

    public static class Project {
        int id;
        String title;
        String description;
        String tags;
        Long date;
        int parent_id;

        public Project(int id, String title, String description, String tags, Long date, int parent_id) {
            this.id = id;
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

        public Long getDate() {
            return date;
        }

        public int getParent_id() {
            return parent_id;
        }
    }

    public static class Task {
        String description;
        int projectID;

        public Task(String description, int id) {
            this.description = description;
            this.projectID = id;
        }

        public String getDescription() {return description;}
        public int getProjectID(){return projectID;}
    }
}
