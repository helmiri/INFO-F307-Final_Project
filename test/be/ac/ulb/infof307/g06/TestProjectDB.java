package be.ac.ulb.infof307.g06;

import be.ac.ulb.infof307.g06.database.ProjectDB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestProjectDB extends TestDatabase {
    private static Connection db;

    public TestProjectDB() throws ClassNotFoundException {
    }

    @Test
    @DisplayName("Tag editing test")
    public void TestCreateProject() throws SQLException, ClassNotFoundException {
        ProjectDB database = new ProjectDB("test/DBTests/projectsDB.db");
        ProjectDB.createProject("project1", "desc1", "tag1,tag2", 0L, 0);
        ProjectDB.createProject("project2", "desc2", "tag3", 0L, 1);
        ProjectDB.createProject("project3", "desc3", "tag4", 0L, 2);
        ProjectDB.editProject(1, "project1", "desc1", "tag10", 0L);
        ProjectDB.editProject(2, "project2", "desc2", "tag11", 0L);
    }


}
