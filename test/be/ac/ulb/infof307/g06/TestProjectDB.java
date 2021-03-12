package be.ac.ulb.infof307.g06;

import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestProjectDB extends TestDatabase {
    private static Connection db;

    public TestProjectDB() throws ClassNotFoundException {
    }

    @Test
    @DisplayName("Tag editing test")
    public void TestTags() throws SQLException, ClassNotFoundException {
        new ProjectDB("test/DBTests/projectsDB.db");
        ProjectDB.createProject("project1", "desc1", 0L, 0);
        ProjectDB.createTag("tag1", 0);
        ProjectDB.createTag("tag2", 1);
        ProjectDB.createTag("tag3", 2);
        ProjectDB.addTag(ProjectDB.getTagID("tag1"), ProjectDB.getProjectID("project1"));
        ProjectDB.addTag(ProjectDB.getTagID("tag2"), ProjectDB.getProjectID("project1"));
        ProjectDB.removeTag(ProjectDB.getTagID("tag1"), ProjectDB.getProjectID("project1"));

    }


}
