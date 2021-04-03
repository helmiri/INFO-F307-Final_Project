
package be.ac.ulb.infof307.g06.models.database;

import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.Task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectDB extends Database {

    public ProjectDB(String dbName) throws ClassNotFoundException, SQLException {
        super(dbName);
    }

    @Override
    protected void createTables() throws SQLException {
        sqlUpdate("CREATE TABLE IF NOT EXISTS Project(id Integer, title varchar(20), description varchar(20), date Long, parent_id Integer);");
        sqlUpdate("CREATE TABLE IF NOT EXISTS Collaborator(project_id Integer, user_id Integer);");
        sqlUpdate("CREATE TABLE IF NOT EXISTS Task(id Integer, description varchar(20), project_id Integer);");
        sqlUpdate("CREATE TABLE IF NOT EXISTS Tag(id Integer, description varchar(20), color varchar(20));");
        sqlUpdate("CREATE TABLE IF NOT EXISTS Tag_projects(tag_id Integer, project_id Integer);");
        sqlUpdate("CREATE TABLE IF NOT EXISTS Invitations(id Integer, project_id Integer, user1_id Integer, user2_id Integer);");
        sqlUpdate("CREATE TABLE IF NOT EXISTS tasks_users(task_id Integer, user_id Integer);");
    }

    /**
     * @param title       Title
     * @param description Description
     * @param date        Date
     * @param parent_id   Parent project ID if it's a sub porject
     * @return The newly created project's ID
     * @throws SQLException
     */
    public int createProject(String title, String description, Long date, int parent_id) throws SQLException {
        ResultSet rs = null;
        int id;
        try {   // Generate id
            rs = sqlQuery("SELECT id, MAX(id) FROM Project;");
            id = rs.getInt("id");
            id++;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            id = 1;
        }

        sqlUpdate("INSERT INTO Project (id, title, description, date, parent_id) VALUES('" +
                id + "','" + title + "','" + description + "','" + date + "','" + parent_id + "');");
        if (parent_id != 0){    // Add the parent tags to the current tags
            List<Tag> parent_tags = getTags(parent_id);
            for (Tag parent_tag : parent_tags) {
                addTag(parent_tag.getId(), id);
            }
        }
        rs.close();
        return id;
    }

    public void editProject(int id, String title, String description, Long date) throws SQLException {
        sqlUpdate("UPDATE Project SET title = '" + title + "', description = '" + description + "', date = '" + date + "' WHERE id = '" + id + "';");
    }

    private void editSubTags(int project_id, List<Tag> oldTags, List<Integer> newTags) throws SQLException {
        for (int i = 0; i < oldTags.size(); i++) {
            removeTag(oldTags.get(i).getId(), project_id);
        }
        for (int i = 0; i < newTags.size(); i++) {
            addTag(newTags.get(i), project_id);
        }
        for (int i = 0; i < getSubProjects(project_id).size(); i++) {
            editSubTags(getSubProjects(project_id).get(i), oldTags, newTags);
        }
    }

    public void deleteProject(int id) throws SQLException {

        for (Integer subProject : getSubProjects(id)) {
            deleteProject(subProject);
        }

        sqlUpdate("DELETE FROM Collaborator WHERE project_id = '" + id + "';");
        sqlUpdate("DELETE FROM Task WHERE project_id = '" + id + "';");
        sqlUpdate("DELETE FROM Project WHERE id = '" + id + "';");
    }

    public List<Integer> getSubProjects(int id) throws SQLException {
        List<Integer> subProjects = new ArrayList<>();
        ResultSet rs = sqlQuery("SELECT id FROM Project WHERE parent_id = '" + id + "';");
        while (rs.next()) {
            subProjects.add(rs.getInt("id"));
        }
        rs.close();
        return subProjects;
    }


    public int getProjectID(String title) throws SQLException {
        ResultSet rs = null;
        int id = 0;
        try {
            rs = sqlQuery("SELECT id FROM Project WHERE title='" + title + "';");

            id = rs.getInt("id");
        } catch (Exception ignored) {
        }


        rs.close();
        return id;
    }

    public int getSizeOnDisk() throws SQLException {
        // TODO
        Project current;
        int total = 0;
        for (Integer projectID : getUserProjects(/*Global.userID*/ 1)) {
            total += getProjectInfoSize(projectID);
        }
        return total;
    }

    private int getProjectInfoSize(Integer projectID) throws SQLException {
        int total = getProject(projectID).getSize();

        // Collaborators size id => int => 4 bytes
        total += getCollaborators(projectID).size() * 4;

        for (Tag tag : getTags(projectID)) {
            total += tag.getSize();
        }

        for (Task task : getTasks(projectID)) {
            total += task.getSize();
        }

        for (Integer subID : getSubProjects(projectID)) {
            total += getProjectInfoSize(subID);
        }
        return total;
    }


    public Project getProject(int id) throws SQLException {
        ResultSet rs = null;
        Project res = null;
        try {
            rs = sqlQuery("SELECT title,description,date,parent_id FROM Project WHERE id='" + id + "';");

            String title = rs.getString("title");
            String description = rs.getString("description");
            Long date = rs.getLong("date");
            int parent_id = rs.getInt("parent_id");
            res = new Project(id, title, description, date, parent_id);
        } catch (Exception ignored) {
        }

        rs.close();
        return res;
    }

    // ---------------- COLLABORATORS ----------------

    public void addCollaborator(int project_id, int user_id) throws SQLException {
        ResultSet rs = null;
        if (!getCollaborators(project_id).contains(user_id)) {
            sqlUpdate("INSERT INTO Collaborator (project_id, user_id) VALUES ('" + project_id + "','" + user_id + "');");
        }
        for (Integer subProject : getSubProjects(project_id)) {
            addCollaborator(subProject, user_id);
        }
        rs.close();
    }

    public void deleteCollaborator(int project_id, int user_id) throws SQLException {
        sqlUpdate("DELETE FROM Collaborator WHERE project_id = '" + project_id + "' and user_id = '" + user_id + "';");
        for (Integer subProject : getSubProjects(project_id)) {
            deleteCollaborator(subProject, user_id);
        }
    }

    public List<Integer> getCollaborators(int project_id) throws SQLException {
        ResultSet rs = sqlQuery("SELECT user_id FROM Collaborator WHERE project_id='" + project_id + "';");
        List<Integer> res = new ArrayList<>();
        while (rs.next()) {
            res.add(rs.getInt("user_id"));
        }
        rs.close();
        return res;
    }

    public int countCollaborators(int project_id) throws SQLException {
        int res;
        ResultSet rs = sqlQuery("SELECT COUNT(*) FROM Collaborator WHERE project_id='" + project_id + "';");
        res = rs.getInt("COUNT(*)");
        rs.close();
        return res;
    }

    public List<Integer> getUserProjects(int user_id) throws SQLException {
        ResultSet rs = sqlQuery("SELECT project_id FROM Collaborator WHERE user_id='" + user_id + "';");
        List<Integer> res = new ArrayList<>();
        while (rs.next()) {
            res.add(rs.getInt("project_id"));
        }
        rs.close();
        return res;
    }

    // ---------------TASKS --------------

    public int createTask(String description, int project_id) throws SQLException {
        ResultSet rs = null;
        for (int i = 0; i < getTasks(project_id).size(); i++) {
            if (getTasks(project_id).get(i).getDescription().equals(description)) {
                return 0;
            }
        }
        int id;
        try {   // Generate id
            rs = sqlQuery("SELECT id, MAX(id) FROM Task;");
            id = rs.getInt("id");
            id++;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            id = 1;
        }
        sqlUpdate("INSERT INTO Task (id, description, project_id) VALUES('" +
                id + "','" + description + "','" + project_id + "');");
        rs.close();
        return id;
    }

    public void editTask(String prev_description, String new_description, int project_id) throws SQLException {
        List<Task> tasks = getTasks(project_id);
        List<String> taskNames = new ArrayList<>();
        for (Task task : tasks) {
            taskNames.add(task.getDescription());
        }
        if (!taskNames.contains(new_description)) {
            sqlUpdate("UPDATE Task SET description = '" + new_description + "' WHERE project_id = '" + project_id + "' AND description = '" + prev_description + "';");
        }
    }

    public void deleteTask(String description, int project_id) throws SQLException {
        sqlUpdate("DELETE FROM Task WHERE project_id = '" + project_id + "' and description = '" + description + "';");
    }

    public Task getTask(int id) throws SQLException {
        ResultSet rs = null;
        Task res;
        try {
            rs = sqlQuery("SELECT id, description FROM Task WHERE id='" + id + "';");
            res = new Task(rs.getInt("id"), rs.getString("description"), id);
        } catch (Exception e) {
            res = null;
        }
        rs.close();
        return res;
    }

    public List<Task> getTasks(int project_id) throws SQLException {
        ResultSet rs = sqlQuery("SELECT id, description FROM Task WHERE project_id='" + project_id + "';");
        List<Task> res = new ArrayList<>();

        while (rs.next()) {
            res.add(new Task(rs.getInt("id"), rs.getString("description"), project_id));
        }
        rs.close();
        return res;
    }

    public int countTasks(int project_id) throws SQLException {
        int res;
        ResultSet rs = sqlQuery("SELECT COUNT(*) FROM Task WHERE project_id='" + project_id + "';");
        res = rs.getInt("COUNT(*)");
        rs.close();
        return res;
    }

    public void addTaskCollaborator(int task_id, int user_id) throws SQLException {
        ResultSet rs = null;
        if (!getTaskCollaborator(task_id).contains(user_id)) {
            sqlUpdate("INSERT INTO tasks_users (task_id, user_id) VALUES ('" + task_id + "','" + user_id + "');");
        }
        rs.close();
    }

    public void deleteTaskCollaborator(int task_id, int user_id) throws SQLException {
        sqlUpdate("DELETE FROM tasks_users WHERE task_id = '" + task_id + "' and user_id = '" + user_id + "';");
    }

    public List<Integer> getTaskCollaborator(int task_id) throws SQLException {
        ResultSet rs = sqlQuery("SELECT user_id FROM tasks_users WHERE task_id='" + task_id + "';");
        List<Integer> res = new ArrayList<>();
        while (rs.next()) {
            res.add(rs.getInt("user_id"));
        }
        rs.close();
        return res;
    }

    public List<Task> getUserTasks(int user_id) throws SQLException {
        ResultSet rs = sqlQuery("SELECT task_id FROM tasks_users WHERE user_id='" + user_id + "';");
        List<Task> res = new ArrayList<>();
        while (rs.next()) {
            res.add(getTask(rs.getInt("task_id")));
        }
        rs.close();
        return res;
    }

    // ----------- TAGS ---------------

    public int createTag(String description, String color) throws SQLException {
        ResultSet rs = null;
        int id = 0;
        if (getTagID(description) == 0) {
            try {   // Generate id
                rs = sqlQuery("SELECT id, MAX(id) FROM Tag;");
                id = rs.getInt("id");
                id++;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                id = 1;
            }
            sqlUpdate("INSERT INTO Tag (id, description, color) VALUES('" +
                    id + "','" + description + "','" + color + "');");
            rs.close();
        } else {
            return getTagID(description);
        }
        return id;
    }

    public void editTag(int id, String description, String color) throws SQLException {
        sqlUpdate("UPDATE Tag SET description = '" + description + "', color = '" + color + "' WHERE id = '" + id + "';");
    }

    public void deleteTag(int id) throws SQLException {
        sqlUpdate("DELETE FROM Tag WHERE id = '" + id + "';");
    }

    public Tag getTag(int id) throws SQLException {
        ResultSet rs = null;
        Tag res;
        try {
            rs = sqlQuery("SELECT description, color FROM Tag WHERE id='" + id + "';");
            res = new Tag(id, rs.getString("description"), rs.getString("color"));

        } catch (Exception e) {
            res = null;
            System.out.println(e);
        }
        assert rs != null;
        rs.close();
        return res;
    }

    public List<Tag> getAllTags() throws SQLException {
        ResultSet rs = sqlQuery("SELECT id, description, color FROM Tag;");
        List<Tag> res = new ArrayList<>();
        while (rs.next()) {
            res.add(new Tag(rs.getInt("id"), rs.getString("description"), rs.getString("color")));
        }
        rs.close();
        return res;
    }

    public void addTag(int tag_id, int project_id) throws SQLException {
        List<Tag> tags = getTags(project_id);
        List<String> tagsString = new ArrayList<>();
        for (Tag tag : tags) {
            tagsString.add(tag.getDescription());
        }
        if (!tagsString.contains(getTag(tag_id).getDescription())) {
            sqlUpdate("INSERT INTO Tag_projects(tag_id, project_id) VALUES('" +
                    tag_id + "','" + project_id + "');");
        }
    }

    public void removeTag(int tag_id, int project_id) throws SQLException {
        sqlUpdate("DELETE FROM Tag_projects WHERE tag_id = '" + tag_id + "' AND project_id = '" + project_id + "';");
    }

    public void editTags(int project_id, List<Integer> tags_id) throws SQLException {
        List<Tag> oldTags = getTags(project_id);

        for (int i = 0; i < oldTags.size(); i++) {
            System.out.println(oldTags.get(i).getId());
            removeTag(oldTags.get(i).getId(), project_id);
        }

        for (int i = 0; i < tags_id.size(); i++) {
            addTag(tags_id.get(i), project_id);
        }
        for (int i=0; i<getSubProjects(project_id).size(); i++){
            editSubTags(getSubProjects(project_id).get(i), oldTags, tags_id);
        }
    }

    public List<Tag> getTags(int project_id) throws SQLException {
        ResultSet rs = sqlQuery("SELECT tag_id FROM Tag_projects WHERE project_id='" + project_id + "';");
        List<Tag> res = new ArrayList<>();
        while (rs.next()) {
            if (getTag(rs.getInt("tag_id")) != null) {
                res.add(getTag(rs.getInt("tag_id")));
            }
        }
        rs.close();
        return res;
    }

    public int getTagID(String title) throws SQLException {
        ResultSet rs = null;
        int id = 0;
        try {
            rs = sqlQuery("SELECT id FROM Tag WHERE description ='" + title + "';");
            id = rs.getInt("id");
        } catch (Exception e) {
        }
        assert rs != null;
        rs.close();
        return id;
    }
}
