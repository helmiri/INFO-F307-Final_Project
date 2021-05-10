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
     * @param dbName Path to database file
     * @throws ClassNotFoundException When the JDBC driver cannot be found
     * @throws SQLException           If a database access error occurs
     */
    public CalendarDB(String dbName) throws ClassNotFoundException, SQLException {
        super(dbName);
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
        ResultSet rs = sqlQuery("SELECT color FROM Projects WHERE title = '" + project + "';");
        String res = rs.getString("color");
        rs.close();
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
        ResultSet rs = sqlQuery("SELECT title, color FROM Projects;");
        Map<String, String> res = new HashMap<>();
        while (rs.next()) {
            res.put(rs.getString("title"), rs.getString("color"));
        }
        rs.close();
        return res;
    }

    /**
     * Check if a certain project is contained in the db
     * @param title
     * @return
     * @throws SQLException
     */
    private boolean isInDB(String title) throws SQLException {
        int res;
        ResultSet rs = sqlQuery("SELECT COUNT(*) FROM Projects WHERE title ='" + title + "';");
        res = rs.getInt("COUNT(*)");
        rs.close();
        return !(res == 0);
    }

    /**
     * Replace a project with another one.
     * @param prevName old porject
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
