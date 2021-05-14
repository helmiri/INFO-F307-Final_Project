package be.ac.ulb.infof307.g06.models.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 * The calendar database
 */
public class CalendarDB extends Database {

    /**
     * Initializes the database
     *
     * @throws SQLException If a database access error occurs
     */
    public CalendarDB() throws SQLException {
    }

    @Override
    protected void createTables() throws SQLException {
        sqlUpdate("CREATE TABLE IF NOT EXISTS Projects(title varchar(20), color varchar(20));");
    }

    /**
     * Adds a project in the calendar database.
     * @param projectName the name of the project
     * @param color the color of the project
     * @throws SQLException if query fails
     */
    public void addProject(String projectName, String color) throws SQLException {
        sqlUpdate("INSERT OR REPLACE INTO Projects (title, color) VALUES ('" + projectName + "','" + color + "');");
    }

    /**
     * Returns the color of a certain project
     * @param project the project we want the color from
     * @return the color
     * @throws SQLException if query fails
     */
    public String getColor(String project) throws SQLException {
        String res;
        try (ResultSet rs = prepareSqlQuery("SELECT color FROM Projects WHERE title = '" + project + "';")) {
            res = rs.getString("color");
        }
        return res;
    }

    /**
     * Removes a project from the calendar database
     * @param name name of the project
     * @throws SQLException if query fails
     */
    public void removeProject(String name) throws SQLException {
        sqlUpdate("DELETE FROM Projects WHERE title = '" + name + "';");
    }

    /**
     * Empties the project table
     * @throws SQLException if query fails
     */
    public void emptyTable() throws SQLException {
        sqlUpdate("DELETE FROM Projects");
    }

    /**
     * Returns every project and their color contained in the calendar db
     * @return All the projects
     * @throws SQLException if query fails
     */
    public Map<String, String> getProjects() throws SQLException {
        Map<String, String> res;
        try (ResultSet rs = prepareSqlQuery("SELECT title, color FROM Projects;")) {
            res = new HashMap<>();
            while (rs.next()) {
                res.put(rs.getString("title"), rs.getString("color"));
            }
        }
        return res;
    }

    /**
     * Check if a certain project is contained in the db
     *
     * @param title The title of the project
     * @return True if it exists, false otherwise
     * @throws SQLException On database access error
     */
    private boolean isInDB(String title) throws SQLException {
        int res;
        try (ResultSet rs = prepareSqlQuery("SELECT COUNT(*) FROM Projects WHERE title ='" + title + "';")) {
            res = rs.getInt("COUNT(*)");
        }
        return !(res == 0);
    }

    /**
     * Replace a project with another one.
     * @param prevName old project
     * @param newName new project
     * @throws SQLException if query fails
     */
    public void replaceProject(String prevName, String newName) throws SQLException {
        if (isInDB(prevName)) {
            String color = getColor(prevName);
            removeProject(prevName);
            addProject(newName, color);
        }
    }
}
