package be.ac.ulb.infof307.g06.models.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CalendarDBTest {
    static String dbPath = "test/be/ac/ulb/infof307/g06/models/database/testDB.db";
    CalendarDB database;
    String project1 = "project1";
    String color1 = "color1";
    String project2 = "project2";
    String color2 = "color2";

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException, IOException {
        File db = new File(dbPath);
        FileOutputStream fos = new FileOutputStream(dbPath);
        fos.close();
        if (db.delete()) {
            System.out.println("Deleted the file: " + db.getName());
        } else {
            System.out.println("Failed to delete the file.");
        }
        database = new CalendarDB(dbPath);
    }

    @Test
    void addProject() throws SQLException {
        database.addProject(project1, color1);
        database.addProject(project2, color2);
        Map<String, String> projects = database.getProjects();
        Map<String, String> expected = new HashMap<>();
        expected.put(project1, color1);
        expected.put(project2, color2);
        assertEquals(expected, projects);
    }

    @Test
    void getColor() throws SQLException {
        database.addProject(project1, color1);
        String color = database.getColor(project1);
        assertEquals(color1, color);
    }

    @Test
    void removeProject() throws SQLException {
        database.addProject(project1, color1);
        database.addProject(project2, color2);
        database.removeProject(project1);
        Map<String, String> projects = database.getProjects();
        Map<String, String> expected = new HashMap<>();
        expected.put(project2, color2);
        assertEquals(expected, projects);
    }

    @Test
    void emptyTable() throws SQLException {
        database.addProject(project1, color1);
        database.addProject(project2, color2);
        database.emptyTable();
        Map<String, String> projects = database.getProjects();
        Map<String, String> expected = new HashMap<>();
        assertEquals(expected, projects);
    }

}