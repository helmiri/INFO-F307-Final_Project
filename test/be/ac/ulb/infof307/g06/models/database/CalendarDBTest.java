package be.ac.ulb.infof307.g06.models.database;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CalendarDBTest extends TestDatabase {
    String project1 = "project1";
    String color1 = "color1";
    String project2 = "project2";
    String color2 = "color2";

    public CalendarDBTest() throws ClassNotFoundException, SQLException {
    }


    @Test
    void addProject() throws SQLException {
        calendarDB.addProject(project1, color1);
        calendarDB.addProject(project2, color2);
        Map<String, String> projects = calendarDB.getProjects();
        Map<String, String> expected = new HashMap<>();
        expected.put(project1, color1);
        expected.put(project2, color2);
        assertEquals(expected, projects);
    }

    @Test
    void getColor() throws SQLException {
        calendarDB.addProject(project1, color1);
        String color = calendarDB.getColor(project1);
        assertEquals(color1, color);
    }

    @Test
    void removeProject() throws SQLException {
        calendarDB.addProject(project1, color1);
        calendarDB.addProject(project2, color2);
        calendarDB.removeProject(project1);
        Map<String, String> projects = calendarDB.getProjects();
        Map<String, String> expected = new HashMap<>();
        expected.put(project2, color2);
        assertEquals(expected, projects);
    }

    @Test
    void emptyTable() throws SQLException {
        calendarDB.addProject(project1, color1);
        calendarDB.addProject(project2, color2);
        calendarDB.emptyTable();
        Map<String, String> projects = calendarDB.getProjects();
        Map<String, String> expected = new HashMap<>();
        assertEquals(expected, projects);
    }

    @Test
    void replaceProject() throws SQLException {
        calendarDB.addProject(project1, color1);
        calendarDB.replaceProject(project1, "project1 edited");
        Map<String, String> projects = calendarDB.getProjects();
        Map<String, String> expected = new HashMap<>();
        expected.put("project1 edited", color1);
        assertEquals(expected, projects);
    }
}