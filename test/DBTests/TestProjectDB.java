package DBTests;

import be.ac.ulb.infof307.g06.database.*;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestProjectDB {
    private static Connection db;
    @BeforeEach
    public void setup() throws SQLException, ClassNotFoundException {
        db = projects_database.init("jdbc:sqlite:test/DBTests/projectsDB.db");
    }

    @Test
    @DisplayName("basic project creation test")
    public void testProjectBasic() throws SQLException {
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

        int id0 = projects_database.createProject(ProjectTitle, ProjectDescription, ProjectTags, testDate, 0);
        int id1 = projects_database.getProjectID(ProjectTitle);

        int id2 = projects_database.createProject(Project2Title, Project2Description, Project2Tags, testDate2, 0);
        int id3 = projects_database.getProjectID(Project2Title);

        assertEquals(id0, id1);
        assertEquals(id2, id3);

        projects_database.editProject(id1, EditedProjectTitle, ProjectDescription, EditedProjectTags, testDate);
        assertEquals(projects_database.getProject(id1).getDescription(), ProjectDescription);
        assertEquals(projects_database.getProject(id1).getTitle(), EditedProjectTitle);

        projects_database.deleteProject(id1);
        assertNull(projects_database.getProject(id1));
        assertEquals(projects_database.getProject(id3).getTitle(), Project2Title);

    }

    @Test
    @DisplayName("Subproject creation test")
    public void Subproject_test() throws SQLException {
        String ProjectTitle = "Project 1";
        String ProjectDescription = "Description 1";
        String ProjectTags = "tag1,tag2";
        Date testDate = new Date(System.currentTimeMillis());

        String Project2Title = "Project 2";
        String Project2Description = "Description 2";
        String Project2Tags = "tag3";
        Date testDate2 = new Date(System.currentTimeMillis());

        int id0 = projects_database.createProject(ProjectTitle, ProjectDescription, ProjectTags, testDate, 0);

        int id1 = projects_database.createProject(Project2Title, Project2Description, Project2Tags, testDate2, 1);

        List<Integer> subProjects;
        subProjects = projects_database.getSubProjects(id0);

        // check if subproject is in project
        assertEquals(subProjects.get(0), id1);

        // Check if tags inherited
        assertEquals(projects_database.getProject(id1).getTags(), "tag1,tag2,tag3");

        // Check if deleting parent deletes subProject
        projects_database.deleteProject(id0);
        assertNull(projects_database.getProject(id0));
        assertNull(projects_database.getProject(id1));

    }

    @AfterEach
    public void clear() throws SQLException {
        Statement state = db.createStatement();
        state.execute("Delete from Project");
        state.execute("Delete from Collaborator");
        state.execute("Delete from Task");
        db.close();
    }
}
