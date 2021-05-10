
package be.ac.ulb.infof307.g06.models.database;

import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.Task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * The database where all the project related info is stored
 */
public class ProjectDB extends Database {

    /**
     * same constructor as the main database abstract class
     * @param dbName the name of the database
     * @throws ClassNotFoundException if the super fails
     * @throws SQLException if query fails
     */
    public ProjectDB(String dbName) throws ClassNotFoundException, SQLException {
        super(dbName);
    }

    @Override
    protected void createTables() throws SQLException {
        sqlUpdate("CREATE TABLE IF NOT EXISTS Project(id Integer, title varchar(20), description varchar(20), startDate Long, endDate Long, parent_id Integer);");
        sqlUpdate("CREATE TABLE IF NOT EXISTS Collaborator(project_id Integer, user_id Integer);");
        sqlUpdate("CREATE TABLE IF NOT EXISTS Task(id Integer, description varchar(20), project_id Integer, startDate Long, endDate Long);");
        sqlUpdate("CREATE TABLE IF NOT EXISTS Tag(id Integer, description varchar(20), color varchar(20));");
        sqlUpdate("CREATE TABLE IF NOT EXISTS Tag_projects(tag_id Integer, project_id Integer);");
        sqlUpdate("CREATE TABLE IF NOT EXISTS Invitations(id Integer, project_id Integer, user1_id Integer, user2_id Integer);");
        sqlUpdate("CREATE TABLE IF NOT EXISTS tasks_users(task_id Integer, user_id Integer);");
    }

    /**
     * Creates a new project
     * @param title the title of the project
     * @param description the description of the project
     * @param startDate the starting date of the project
     * @param endDate the ending date of the project
     * @param parent_id the id of its parent (if it has one)
     * @return the id of the new project
     * @throws SQLException if the query fails
     */
    public int createProject(String title, String description, Long startDate, Long endDate, int parent_id) throws SQLException {
        ResultSet rs = null;
        int id;
        try {   // Generate id
            rs = sqlQuery("SELECT id, MAX(id) FROM Project;");
            id = rs.getInt("id");
            id++;
        } catch (Exception e) {
            id = 1;
        }
        Objects.requireNonNull(rs).close();
        sqlUpdate("INSERT INTO Project (id, title, description, startDate, endDate, parent_id) VALUES('" +
                id + "','" + title + "','" + description + "','" + startDate + "','" + endDate + "','" + parent_id + "');");
        if (parent_id != 0) {    // Add the parent tags to the current tags
            List<Tag> parent_tags = getTags(parent_id);
            for (Tag parent_tag : parent_tags) {
                addTag(parent_tag.getId(), id);
            }
        }

        return id;
    }

    /**
     * Edits a project
     * @param id the project id
     * @param title the title of the project
     * @param description the description of the project
     * @param startDate the starting date of the project
     * @param endDate the ending date of the project
     * @throws SQLException if the query fails 
     */
    public void editProject(int id, String title, String description, Long startDate, Long endDate) throws SQLException {
        sqlUpdate("UPDATE Project SET title = '" + title + "', description = '" + description + "', startDate = '" + startDate + "', endDate = '" + endDate + "' WHERE id = '" + id + "';");
    }

    /**
     * Edits the tags of a project.
     * @param project_id the id of the project
     * @param oldTags the previous tags
     * @param newTags the new tags
     * @throws SQLException if the query fail
     */
    private void editSubTags(int project_id, List<Tag> oldTags, List<Integer> newTags) throws SQLException {
        for (Tag oldTag : oldTags) {
            removeTag(oldTag.getId(), project_id);
        }
        addTags(project_id, oldTags, newTags);
    }

    /**
     * Adds tags to a project
     * @param project_id the id of the project
     * @param oldTags the previous tags
     * @param newTags the new tags
     * @throws SQLException if the query fail
     */
    private void addTags(int project_id, List<Tag> oldTags, List<Integer> newTags) throws SQLException {
        for (Integer newTag : newTags) {
            addTag(newTag, project_id);
        }
        for (int i = 0; i < getSubProjects(project_id).size(); i++) {
            editSubTags(getSubProjects(project_id).get(i), oldTags, newTags);
        }
    }

    /**
     * Delete a project in the database
     * @param id the id of the project
     * @throws SQLException if the query fail
     */
    public void deleteProject(int id) throws SQLException {

        for (Integer subProject : getSubProjects(id)) {
            deleteProject(subProject);
        }

        sqlUpdate("DELETE FROM Collaborator WHERE project_id = '" + id + "';");
        sqlUpdate("DELETE FROM Task WHERE project_id = '" + id + "';");
        sqlUpdate("DELETE FROM Project WHERE id = '" + id + "';");
    }

    /**
     * Returns all the subproject from a parent project
     * @param id the id of the parent project
     * @return all the subprojects
     * @throws SQLException if the query fails
     */
    public List<Integer> getSubProjects(int id) throws SQLException {
        List<Integer> subProjects = new ArrayList<>();
        ResultSet rs = sqlQuery("SELECT id FROM Project WHERE parent_id = '" + id + "';");
        while (rs.next()) {
            subProjects.add(rs.getInt("id"));
        }
        rs.close();
        return subProjects;
    }

    /**
     * Returns the id of a project
     * @param title the title of the project
     * @return the id of the project
     * @throws SQLException if the query fails
     */
    public int getProjectID(String title) throws SQLException {
        ResultSet rs = null;
        int id = 0;
        try {
            rs = sqlQuery("SELECT id FROM Project WHERE title='" + title + "';");

            id = rs.getInt("id");
        } catch (Exception ignored) {
        }

        Objects.requireNonNull(rs).close();
        return id;
    }

    /**
     * Returns the space available on the disk
     * @return the size on disk available
     * @throws SQLException if the query fails
     */
    public int getSizeOnDisk() throws SQLException {
        int total = 0;
        for (Integer projectID : getUserProjects(currentUser.getId())) {
            total += getProjectInfoSize(projectID);
        }
        return total;
    }

    /**
     * Returns the total size of a project
     * @param projectID the id of the project
     * @return the size of a project
     * @throws SQLException if the query fails
     */
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

    /**
     * Returns a project
     * @param id the id of a project
     * @return the project
     * @throws SQLException if the query fails
     */
    public Project getProject(int id) throws SQLException {
        ResultSet rs = null;
        Project res = null;
        try {
            rs = sqlQuery("SELECT title,description,startDate,endDate,parent_id FROM Project WHERE id='" + id + "';");

            String title = rs.getString("title");
            String description = rs.getString("description");
            Long startDate = rs.getLong("startDate");
            Long endDate = rs.getLong("endDate");
            int parent_id = rs.getInt("parent_id");
            res = new Project(id, title, description, startDate, endDate, parent_id);
        } catch (Exception ignored) {
        }
        if (rs != null) {
            rs.close();
        }
        return res;
    }

    // ---------------- COLLABORATORS ----------------

    /**
     * Adds a collaborator to a project
     * @param project_id the id of the project
     * @param user_id the id of the user
     * @throws SQLException if the query fails
     */
    public void addCollaborator(int project_id, int user_id) throws SQLException {
        if (!getCollaborators(project_id).contains(user_id)) {
            sqlUpdate("INSERT INTO Collaborator (project_id, user_id) VALUES ('" + project_id + "','" + user_id + "');");
        }
        for (Integer subProject : getSubProjects(project_id)) {
            addCollaborator(subProject, user_id);
        }
    }

    /**
     * Deletes a collaborator from a project
     * @param project_id the id of the project
     * @param user_id the id of the user
     * @throws SQLException if the query fails
     */
    public void deleteCollaborator(int project_id, int user_id) throws SQLException {
        sqlUpdate("DELETE FROM Collaborator WHERE project_id = '" + project_id + "' and user_id = '" + user_id + "';");
        for (Integer subProject : getSubProjects(project_id)) {
            deleteCollaborator(subProject, user_id);
        }
    }

    /**
     * Returns every collaborator from a project
     * @param project_id the id of the project
     * @return the list of the ids of the collaborators
     * @throws SQLException if the query fails
     */
    public List<Integer> getCollaborators(int project_id) throws SQLException {
        ResultSet rs = sqlQuery("SELECT user_id FROM Collaborator WHERE project_id='" + project_id + "';");
        List<Integer> res = new ArrayList<>();
        while (rs.next()) {
            res.add(rs.getInt("user_id"));
        }
        rs.close();
        return res;
    }

    /**
     * Returns the number of collaborators a project has
     * @param project_id the id of the project
     * @return the number of collaborators
     * @throws SQLException if the query fails
     */
    public int countCollaborators(int project_id) throws SQLException {
        int res;
        ResultSet rs = sqlQuery("SELECT COUNT(*) FROM Collaborator WHERE project_id='" + project_id + "';");
        res = rs.getInt("COUNT(*)");
        rs.close();
        return res;
    }

    /**
     * Returns all project id's that a user has
     * @param user_id the id of the user
     * @return returns the id's of the projects
     * @throws SQLException if the query fails
     */
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

    /**
     * Add a new task
     * @param description the description of the task
     * @param project_id the id of the project
     * @param startDate the starting date of the tast
     * @param endDate the ending date of the task
     * @throws SQLException if the query fails
     */
    public void createTask(String description, int project_id, Long startDate, Long endDate) throws SQLException {
        ResultSet rs = null;
        for (int i = 0; i < getTasks(project_id).size(); i++) {
            if (getTasks(project_id).get(i).getDescription().equals(description)) {
                return;
            }
        }
        int id;
        try {   // Generate id
            rs = sqlQuery("SELECT id, MAX(id) FROM Task;");
            id = rs.getInt("id");
            id++;
        } catch (Exception e) {
            id = 1;
        }
        sqlUpdate("INSERT INTO Task (id, description, project_id, startDate, endDate) VALUES('" +
                id + "','" + description + "','" + project_id + "','" + startDate + "','" + endDate + "');");
        Objects.requireNonNull(rs).close();
    }

    /**
     * Edit an existing task
     * @param prev_description the old description
     * @param new_description the new description
     * @param project_id the id of the project
     * @param startDate the starting date of the tast
     * @param endDate the ending date of the task
     * @throws SQLException if the query fails
     */
    public void editTask(String prev_description, String new_description, int project_id, Long startDate, Long endDate) throws SQLException {
        List<Task> tasks = getTasks(project_id);
        List<String> taskNames = new ArrayList<>();
        for (Task task : tasks) {
            taskNames.add(task.getDescription());
        }
        if (!taskNames.contains(new_description) || new_description.equals(prev_description)) {
            sqlUpdate("UPDATE Task SET description = '" + new_description + "', startDate = '" + startDate + "', endDate = '" + endDate + "' WHERE project_id = '" + project_id + "' AND description = '" + prev_description + "';");
        }
    }

    /**
     * Delete a task
     * @param description the description of the tag
     * @param project_id the id of the project
     * @throws SQLException if the query fails
     */
    public void deleteTask(String description, int project_id) throws SQLException {
        sqlUpdate("DELETE FROM Task WHERE project_id = '" + project_id + "' and description = '" + description + "';");
    }

    /**
     * Returns the task that corresponds to the id
     * @param id the id of the task
     * @return the task
     * @throws SQLException if the query fails
     */
    public Task getTask(int id) throws SQLException {
        ResultSet rs = null;
        Task res;
        try {
            rs = sqlQuery("SELECT id, description, startDate, endDate FROM Task WHERE id='" + id + "';");
            res = new Task(rs.getInt("id"), rs.getString("description"), id, rs.getLong("startDate"), rs.getLong("endDate"));
        } catch (Exception e) {
            res = null;
        }
        Objects.requireNonNull(rs).close();
        return res;
    }

    /**
     * Returns all the tasks from a project
     * @param project_id the id of the project
     * @return the id's of the tasks
     * @throws SQLException if the query fails
     */
    public List<Task> getTasks(int project_id) throws SQLException {
        ResultSet rs = sqlQuery("SELECT id, description, startDate, endDate FROM Task WHERE project_id='" + project_id + "';");
        List<Task> res = new ArrayList<>();

        while (rs.next()) {
            res.add(new Task(rs.getInt("id"), rs.getString("description"), project_id, rs.getLong("startDate"), rs.getLong("endDate")));
        }
        rs.close();
        return res;
    }

    /**
     * Returns the number of tasks of a project
     * @param project_id the id of the project
     * @return the number of tasks
     * @throws SQLException if the query fails
     */
    public int countTasks(int project_id) throws SQLException {
        int res;
        ResultSet rs = sqlQuery("SELECT COUNT(*) FROM Task WHERE project_id='" + project_id + "';");
        res = rs.getInt("COUNT(*)");
        rs.close();
        return res;
    }

    /**
     * Adds a collaborator to a task
     * @param task_id the id of a task
     * @param user_id the user id
     * @throws SQLException if the query fails
     */
    public void addTaskCollaborator(int task_id, int user_id) throws SQLException {
        if (!getTaskCollaborator(task_id).contains(user_id)) {
            sqlUpdate("INSERT INTO tasks_users (task_id, user_id) VALUES ('" + task_id + "','" + user_id + "');");
        }
    }

    /**
     * Deletes a collaborator from a task
     * @param task_id the id of a task
     * @param user_id the user id
     * @throws SQLException if the query fails
     */
    public void deleteTaskCollaborator(int task_id, int user_id) throws SQLException {
        sqlUpdate("DELETE FROM tasks_users WHERE task_id = '" + task_id + "' and user_id = '" + user_id + "';");
    }

    /**
     * Get all the collaborators id that are associated with a task
     * @param task_id the id of a task
     * @return the collaborators of the task
     * @throws SQLException if the query fails
     */
    public List<Integer> getTaskCollaborator(int task_id) throws SQLException {
        ResultSet rs = sqlQuery("SELECT user_id FROM tasks_users WHERE task_id='" + task_id + "';");
        List<Integer> res = new ArrayList<>();
        while (rs.next()) {
            res.add(rs.getInt("user_id"));
        }
        rs.close();
        return res;
    }

    /**
     * Returns all the task that a user has
     * @param user_id the id of the user
     * @return the tasks of the user
     * @throws SQLException if the query fails
     */
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

    /**
     * Creates a new tag
     * @param description the description of the tag
     * @param color the color of the tag
     * @return the id of the tag
     * @throws SQLException if the query fails
     */
    public int createTag(String description, String color) throws SQLException {
        ResultSet rs = null;
        int id;
        if (getTagID(description) == 0) {
            try {   // Generate id
                rs = sqlQuery("SELECT id, MAX(id) FROM Tag;");
                id = rs.getInt("id");
                id++;
            } catch (Exception e) {
                id = 1;
            }
            sqlUpdate("INSERT INTO Tag (id, description, color) VALUES('" +
                    id + "','" + description + "','" + color + "');");
            Objects.requireNonNull(rs).close();
        } else {
            return getTagID(description);
        }
        return id;
    }

    /**
     * Edits an existing tag
     * @param id the id of the tag
     * @param description the description of the tag
     * @param color the color of the tag
     * @throws SQLException if the query fails
     */
    public void editTag(int id, String description, String color) throws SQLException {
        sqlUpdate("UPDATE Tag SET description = '" + description + "', color = '" + color + "' WHERE id = '" + id + "';");
    }

    /**
     * Deletes a tag
     * @param id the id of the tag
     * @throws SQLException if the query fails
     */
    public void deleteTag(int id) throws SQLException {
        sqlUpdate("DELETE FROM Tag WHERE id = '" + id + "';");
    }

    /**
     * Returns a tag by its id
     * @param id the id of the tag
     * @return the tag
     * @throws SQLException if the query fails
     */
    public Tag getTag(int id) throws SQLException {
        ResultSet rs = null;
        Tag res;
        try {
            rs = sqlQuery("SELECT description, color FROM Tag WHERE id='" + id + "';");
            res = new Tag(id, rs.getString("description"), rs.getString("color"));

        } catch (Exception e) {
            res = null;
        }
        assert rs != null;
        rs.close();
        return res;
    }

    /**
     * Returns all the tags that exist
     * @return the tags
     * @throws SQLException if the query fails
     */
    public List<Tag> getAllTags() throws SQLException {
        ResultSet rs = sqlQuery("SELECT id, description, color FROM Tag;");
        List<Tag> res = new ArrayList<>();
        while (rs.next()) {
            res.add(new Tag(rs.getInt("id"), rs.getString("description"), rs.getString("color")));
        }
        rs.close();
        return res;
    }

    /**
     * Adds a tag to a project
     * @param tag_id the id of the tag
     * @param project_id the id of the project
     * @throws SQLException if the query fails
     */
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

    /**
     * Removes a tag from a project
     * @param tag_id the id of the tag
     * @param project_id the id of the project
     * @throws SQLException if the query fails
     */
    public void removeTag(int tag_id, int project_id) throws SQLException {
        sqlUpdate("DELETE FROM Tag_projects WHERE tag_id = '" + tag_id + "' AND project_id = '" + project_id + "';");
    }

    /**
     * Edit multiple tags from a project
     * @param project_id the id of the project
     * @param tags_id the id's of the tags
     * @throws SQLException if the query fails
     */
    public void editTags(int project_id, List<Integer> tags_id) throws SQLException {
        List<Tag> oldTags = getTags(project_id);

        for (Tag oldTag : oldTags) {
            removeTag(oldTag.getId(), project_id);
        }

        addTags(project_id, oldTags, tags_id);
    }

    /**
     * Returns all the tags of a project
     * @param project_id the id of the project
     * @return the tags
     * @throws SQLException if the query fails
     */
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

    /**
     * Returns the id of a tag with its name
     * @param title the title of the tag
     * @return the id of the returned tag
     * @throws SQLException if the query fails
     */
    public int getTagID(String title) throws SQLException {
        ResultSet rs;
        int id;
        try {
            rs = sqlQuery("SELECT id FROM Tag WHERE description ='" + title + "';");
            id = rs.getInt("id");
        } catch (Exception e) {
            return 0;
        }
        rs.close();
        rs.close();
        return id;
    }
}
