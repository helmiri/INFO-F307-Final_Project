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
    @DisplayName("basic project creation test")
    public void TestCreateProject() throws SQLException {
        /* TODO: Split this test into multiple methods
         * TestCreateProject(): tests ProjectDB.createProject() ONLY
         * TestGetProjectID(): tests ProjectDB.getProjectID()
         * TestEditProject(): tests ProjectDB.editProject()
         * TestDeleteProject(): tests ProjectDB.deleteProject()
         *
         * HINT: Insert data manually into the database to test the individual methods
         *       see TestDatabase @BeforeALL setup(), @BeforeEach prepareUserData() and @AfterEach clear() methods
         *
         *       Insert data before each method call and clear it after each so that every test is isolated
         */
        String ProjectTitle = "Project 1";
        String EditedProjectTitle = "Project Edited";
        String ProjectDescription = "Description 1";
        String ProjectTags = "tag1, tag2";
        String EditedProjectTags = "tag1, tag Edited";
        Date testDate = new Date(System.currentTimeMillis());


        String Project2Title = "Project 2";
        String Project2Description = "Description 2";
        String Project2Tags = "tag1, tag2";
        Date testDate2 = new Date(System.currentTimeMillis());

        int id0 = ProjectDB.createProject(ProjectTitle, ProjectDescription, ProjectTags, testDate, 0);
        int id1 = ProjectDB.getProjectID(ProjectTitle);

        int id2 = ProjectDB.createProject(Project2Title, Project2Description, Project2Tags, testDate2, 0);
        int id3 = ProjectDB.getProjectID(Project2Title);

        assertEquals(id0, id1);
        assertEquals(id2, id3);

        ProjectDB.editProject(id1, EditedProjectTitle, ProjectDescription, EditedProjectTags, testDate);
        assertEquals(ProjectDB.getProject(id1).getDescription(), ProjectDescription);
        assertEquals(ProjectDB.getProject(id1).getTitle(), EditedProjectTitle);

        ProjectDB.deleteProject(id1);
        assertNull(ProjectDB.getProject(id1));
        assertEquals(ProjectDB.getProject(id3).getTitle(), Project2Title);

    }

    @Test
    @DisplayName("Subproject creation test")
    public void TestGetSubProjects() throws SQLException {
        String ProjectTitle = "Project 1";
        String ProjectDescription = "Description 1";
        String ProjectTags = "tag1,tag2";
        Date testDate = new Date(System.currentTimeMillis());

        String Project2Title = "Project 2";
        String Project2Description = "Description 2";
        String Project2Tags = "tag3";
        Date testDate2 = new Date(System.currentTimeMillis());

        int id0 = ProjectDB.createProject(ProjectTitle, ProjectDescription, ProjectTags, testDate, 0);

        int id1 = ProjectDB.createProject(Project2Title, Project2Description, Project2Tags, testDate2, 1);

        List<Integer> subProjects;
        subProjects = ProjectDB.getSubProjects(id0);

        // check if subproject is in project
        assertEquals(subProjects.get(0), id1);

        // Check if tags inherited
        assertEquals(ProjectDB.getProject(id1).getTags(), "tag1,tag2,tag3");

        // Check if deleting parent deletes subProject
        ProjectDB.deleteProject(id0);
        assertNull(ProjectDB.getProject(id0));
        assertNull(ProjectDB.getProject(id1));

    }

}
