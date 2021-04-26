package be.ac.ulb.infof307.g06.models.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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

    public void addProject(String projectName, String color) throws SQLException {
        sqlUpdate("INSERT OR REPLACE INTO Projects (title, color) VALUES ('" + projectName + "','" + color + "');");
    }

    public String getColor(String project) throws SQLException {
        ResultSet rs = sqlQuery("SELECT color FROM Projects WHERE title = '" + project + "';");
        String res = rs.getString("color");
        rs.close();
        return res;
    }

    public void removeProject(String name) throws SQLException {
        sqlUpdate("DELETE FROM Projects WHERE title = '" + name + "';");
    }

    public void emptyTable() throws SQLException {
        sqlUpdate("DELETE FROM Projects");
    }

    public Map<String, String> getProjects() throws SQLException {
        ResultSet rs = sqlQuery("SELECT title, color FROM Projects;");
        Map<String, String> res = new HashMap<>();
        while (rs.next()) {
            res.put(rs.getString("title"), rs.getString("color"));
        }
        rs.close();
        return res;
    }

    private boolean isInDB(String title) throws SQLException {
        int res;
        ResultSet rs = sqlQuery("SELECT COUNT(*) FROM Projects WHERE title ='" + title + "';");
        res = rs.getInt("COUNT(*)");
        rs.close();
        return !(res == 0);
    }

    public void replaceProject(String prevName, String newName) throws SQLException {
        if (isInDB(prevName)) {
            String color = getColor(prevName);
            removeProject(prevName);
            addProject(newName, color);
        }
    }
}
