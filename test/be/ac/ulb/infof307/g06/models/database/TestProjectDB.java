package be.ac.ulb.infof307.g06.models.database;

import be.ac.ulb.infof307.g06.models.Project;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestProjectDB extends TestDatabase {
    String projectName = "test1";
    String projectDescription = "desc1";
    Long projectDate = 1000L;
    String taskDescription = "task1";
    String taskDescription2 = "task2";

    public TestProjectDB() throws ClassNotFoundException {
    }

    @AfterAll
    static void tearDown(){
    }

    @Test
    Project createProject() throws SQLException {
        int id = projectDB.createProject(projectName, projectDescription, projectDate, 0L, 0);
        Project project = projectDB.getProject(id);
        assertEquals(project.getTitle(), projectName);
        assertEquals(project.getDescription(), projectDescription);
        assertEquals(project.getStartDate(), projectDate);
        return project;
    }

    @Test
    void editProject() throws SQLException {
        Project project = createProject();
        String newProjectName = "newTitle1";
        String newProjectDescription = "newDesc1";
        long newProjectDate = 2000L;
        projectDB.editProject(project.getId(), newProjectName, newProjectDescription, newProjectDate, 0L);
        project = projectDB.getProject(project.getId());
        assertEquals(project.getTitle(), newProjectName);
        assertEquals(project.getDescription(), newProjectDescription);
        assertEquals(project.getStartDate(), newProjectDate);
    }

    @Test
    void deleteProject() throws SQLException {
        Project project = createProject();
        int projectID = project.getId();
        projectDB.deleteProject(projectID);
        assertNull(projectDB.getProject(projectID));
    }

    @Test
    void getSubProjects() throws SQLException {
        int id1 = projectDB.createProject(projectName, projectDescription, 0L, 0L, 0);
        int id2 = projectDB.createProject(projectName + "2", projectDescription + "2", 0L, 0L, id1);
        int id3 = projectDB.createProject(projectName + "3", projectDescription + "3", 0L, 0L, id1);

        List<Integer> subProjects = projectDB.getSubProjects(id1);
        List<Integer> expected = new ArrayList<>(Arrays.asList(id2, id3));
        assertTrue(expected.containsAll(subProjects) && subProjects.containsAll(expected));

    }

    @Test
    void addCollaborator() throws SQLException {
        projectDB.addCollaborator(1, 1);
        projectDB.addCollaborator(1, 2);
        projectDB.addCollaborator(1, 3);
        List<Integer> expected = new ArrayList<>(Arrays.asList(1, 2, 3));
        assertTrue(expected.containsAll(projectDB.getCollaborators(1)) && projectDB.getCollaborators(1).containsAll(expected));
    }

    @Test
    void deleteCollaborator() throws SQLException {
        addCollaborator();
        projectDB.deleteCollaborator(1, 2);
        List<Integer> expected = new ArrayList<>(Arrays.asList(1,3));
        assertTrue(expected.containsAll(projectDB.getCollaborators(1)) && projectDB.getCollaborators(1).containsAll(expected));
    }

    @Test
    void countCollaborators() throws SQLException {
        addCollaborator();
        assertEquals(projectDB.countCollaborators(1), 3);
    }

    @Test
    void getUserProjects() throws SQLException {
        addCollaborator();
        List<Integer> expected = new ArrayList<>(Arrays.asList(1));
        assertEquals(expected, projectDB.getUserProjects(2));
    }

    @Test
    void createTask() throws SQLException {
        projectDB.createTask(taskDescription, 1, 0L, 0L);
        projectDB.createTask(taskDescription2, 1, 0L, 0L);
        List<String> expected = new ArrayList<>(Arrays.asList(taskDescription, taskDescription2));
        List<String> actual = new ArrayList<>();
        for (int i = 0; i < projectDB.getTasks(1).size(); i++) {
            actual.add(projectDB.getTasks(1).get(i).getDescription());
        }
        assertEquals(expected, actual);
    }

    @Test
    void editTask() throws SQLException {
        projectDB.createTask(taskDescription, 1, 0L, 0L);
        projectDB.createTask(taskDescription2, 1, 0L, 0L);
        projectDB.editTask(taskDescription, "newDescription", 1, 0L, 0L);
        List<String> expected = new ArrayList<>(Arrays.asList("newDescription", taskDescription2));
        List<String> actual = new ArrayList<>();
        for (int i = 0; i < projectDB.getTasks(1).size(); i++) {
            actual.add(projectDB.getTasks(1).get(i).getDescription());
        }
        assertEquals(expected, actual);
    }

    @Test
    void deleteTask() throws SQLException, IOException, ClassNotFoundException {
        projectDB.createTask(taskDescription, 1, 0L, 0L);
        projectDB.createTask(taskDescription2, 1, 0L, 0L);
        projectDB.deleteTask(taskDescription, 1);
        List<String> expected = new ArrayList<>(Arrays.asList(taskDescription2));
        List<String> actual = new ArrayList<>();
        for (int i = 0; i < projectDB.getTasks(1).size(); i++) {
            actual.add(projectDB.getTasks(1).get(i).getDescription());
        }
        assertEquals(expected, actual);
    }

    @Test
    void countTasks() throws SQLException {
        projectDB.createTask(taskDescription, 1, 0L, 0L);
        projectDB.createTask(taskDescription2, 1, 0L, 0L);
        assertEquals(2, projectDB.countTasks(1));
    }

    @Test
    void createTag() throws SQLException {
        int id = projectDB.createTag("tag1", "#ffffff");
        assertEquals("tag1", projectDB.getTag(id).getDescription());
    }

    @Test
    void editTag() throws SQLException {
        int id = projectDB.createTag("tag1", "#ffffff");
        projectDB.editTag(id, "newtag1", "#ffffff");
        assertEquals("newtag1", projectDB.getTag(id).getDescription());
    }

    @Test
    void deleteTag() throws SQLException {
        int id = projectDB.createTag("tag1", "#ffffff");
        projectDB.deleteTag(id);
        assertNull(projectDB.getTag(id));
    }

    @Test
    void addTag() throws SQLException {
        int id1 = projectDB.createTag("tag1", "#ffffff");
        int id2 = projectDB.createTag("tag2", "#ffffff");
        projectDB.addTag(id1, 1);
        projectDB.addTag(id2, 1);

        List<String> expected = new ArrayList<>(Arrays.asList("tag1", "tag2"));
        List<String> actual = new ArrayList<>();
        for (int i = 0; i < projectDB.getTags(1).size(); i++) {
            actual.add(projectDB.getTags(1).get(i).getDescription());
        }
        assertEquals(expected, actual);
    }

    @Test
    void editTags() throws SQLException {
        int id1 = projectDB.createTag("tag1", "#ffffff");
        int id2 = projectDB.createTag("tag2", "#ffffff");
        int id3 = projectDB.createTag("tag3", "#ffffff");
        projectDB.addTag(id1, 1);
        projectDB.addTag(id2, 1);
        List<Integer> newTags = new ArrayList<>(Arrays.asList(id1, id3));
        projectDB.editTags(1, newTags);
        List<String> expected = new ArrayList<>(Arrays.asList("tag1", "tag3"));
        List<String> actual = new ArrayList<>();
        for (int i = 0; i < projectDB.getTags(1).size(); i++) {
            actual.add(projectDB.getTags(1).get(i).getDescription());
        }
        assertEquals(expected, actual);

    }

    @Test
    void removeTag() throws SQLException, IOException, ClassNotFoundException {
        int id1 = projectDB.createTag("tag1", "#ffffff");
        int id2 = projectDB.createTag("tag2", "#ffffff");
        projectDB.addTag(id1, 1);
        projectDB.addTag(id2, 1);
        projectDB.removeTag(1, id1);
        List<String> expected = new ArrayList<>(Arrays.asList("tag2"));
        List<String> actual = new ArrayList<>();
        for (int i = 0; i < projectDB.getTags(1).size(); i++) {
            actual.add(projectDB.getTags(1).get(i).getDescription());
        }
        assertEquals(expected, actual);
    }

    @Test
    void getAllTags() throws SQLException {
        int id1 = projectDB.createTag("tag1", "#ffffff");
        int id2 = projectDB.createTag("tag2", "#ffffff");
        projectDB.addTag(id1, 1);
        projectDB.addTag(id2, 1);
        List<String> expected = new ArrayList<>(Arrays.asList("tag1", "tag2"));
        List<String> actual = new ArrayList<>();
        for (int i = 0; i < projectDB.getAllTags().size(); i++) {
            actual.add(projectDB.getAllTags().get(i).getDescription());
        }
        assertEquals(expected, actual);
    }
}

