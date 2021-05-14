package be.ac.ulb.infof307.g06.models.database;

import be.ac.ulb.infof307.g06.models.Project;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestProjectDB extends TestDatabase {
    private final String testTag1 = "tag1";
    private final String testColor = "#ffffff";
    private final String testTag2 = "tag2";
    private String projectName = "test1";
    private String projectDescription = "desc1";
    private Long projectDate = 1000L;
    private String taskDescription = "task1";
    private String taskDescription2 = "task2";

    TestProjectDB() throws ClassNotFoundException, SQLException {
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
        List<Integer> expected = new ArrayList<>(Collections.singletonList(1));
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
    void deleteTask() throws SQLException {
        projectDB.createTask(taskDescription, 1, 0L, 0L);
        projectDB.createTask(taskDescription2, 1, 0L, 0L);
        projectDB.deleteTask(taskDescription, 1);
        List<String> expected = new ArrayList<>(Collections.singletonList(taskDescription2));
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
        int id = projectDB.createTag(testTag1, testColor);
        assertEquals(testTag1, projectDB.getTag(id).getDescription());
    }

    @Test
    void editTag() throws SQLException {
        int id = projectDB.createTag(testTag1, testColor);
        projectDB.editTag(id, "newtag1", testColor);
        assertEquals("newtag1", projectDB.getTag(id).getDescription());
    }

    @Test
    void deleteTag() throws SQLException {
        int id = projectDB.createTag(testTag1, testColor);
        projectDB.deleteTag(id);
        assertNull(projectDB.getTag(id));
    }

    @Test
    void addTag() throws SQLException {
        int id1 = projectDB.createTag(testTag1, testColor);
        int id2 = projectDB.createTag(testTag2, testColor);
        projectDB.addTag(id1, 1);
        projectDB.addTag(id2, 1);

        List<String> expected = new ArrayList<>(Arrays.asList(testTag1, testTag2));
        List<String> actual = new ArrayList<>();
        for (int i = 0; i < projectDB.getTags(1).size(); i++) {
            actual.add(projectDB.getTags(1).get(i).getDescription());
        }
        assertEquals(expected, actual);
    }

    @Test
    void editTags() throws SQLException {
        int id1 = projectDB.createTag(testTag1, testColor);
        int id2 = projectDB.createTag(testTag2, testColor);
        int id3 = projectDB.createTag("tag3", testColor);
        projectDB.addTag(id1, 1);
        projectDB.addTag(id2, 1);
        List<Integer> newTags = new ArrayList<>(Arrays.asList(id1, id3));
        projectDB.editTags(1, newTags);
        List<String> expected = new ArrayList<>(Arrays.asList(testTag1, "tag3"));
        List<String> actual = new ArrayList<>();
        for (int i = 0; i < projectDB.getTags(1).size(); i++) {
            actual.add(projectDB.getTags(1).get(i).getDescription());
        }
        assertEquals(expected, actual);
    }

    @Test
    void removeTag() throws SQLException {
        int id1 = projectDB.createTag(testTag1, testColor);
        int id2 = projectDB.createTag(testTag2, testColor);
        projectDB.addTag(id1, 1);
        projectDB.addTag(id2, 1);
        projectDB.removeTag(1, id1);
        List<String> expected = new ArrayList<>(Collections.singletonList(testTag2));
        List<String> actual = new ArrayList<>();
        for (int i = 0; i < projectDB.getTags(1).size(); i++) {
            actual.add(projectDB.getTags(1).get(i).getDescription());
        }
        assertEquals(expected, actual);
    }

    @Test
    void getAllTags() throws SQLException {
        int id1 = projectDB.createTag(testTag1, testColor);
        int id2 = projectDB.createTag(testTag2, testColor);
        projectDB.addTag(id1, 1);
        projectDB.addTag(id2, 1);
        List<String> expected = new ArrayList<>(Arrays.asList(testTag1, testTag2));
        List<String> actual = new ArrayList<>();
        for (int i = 0; i < projectDB.getAllTags().size(); i++) {
            actual.add(projectDB.getAllTags().get(i).getDescription());
        }
        assertEquals(expected, actual);
    }
}

