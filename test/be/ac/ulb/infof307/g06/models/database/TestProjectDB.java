package be.ac.ulb.infof307.g06.models.database;

import be.ac.ulb.infof307.g06.models.Project;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class TestProjectDB {
    static String dbPath = "test/be/ac/ulb/infof307/g06/models/database/testDB.db";
    String projectName = "test1";
    String projectDescription = "desc1";
    Long projectDate = 1000L;
    ProjectDB database;
    String taskDescription = "task1";
    String taskDescription2 = "task2";

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
        database = new ProjectDB(dbPath);
    }

    @AfterAll
    static void tearDown(){
    }

    @Test
    Project createProject() throws SQLException {
        int id = database.createProject(projectName, projectDescription, projectDate, 0L, 0);
        Project project = database.getProject(id);
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
        database.editProject(project.getId(), newProjectName, newProjectDescription, newProjectDate, 0L);
        project = database.getProject(project.getId());
        assertEquals(project.getTitle(), newProjectName);
        assertEquals(project.getDescription(), newProjectDescription);
        assertEquals(project.getStartDate(), newProjectDate);
    }

    @Test
    void deleteProject() throws SQLException {
        Project project = createProject();
        int projectID = project.getId();
        database.deleteProject(projectID);
        assertNull(database.getProject(projectID));
    }

    @Test
    void getSubProjects() throws SQLException {
        int id1 = database.createProject(projectName, projectDescription, 0L, 0L, 0);
        int id2 = database.createProject(projectName + "2", projectDescription + "2", 0L, 0L, id1);
        int id3 = database.createProject(projectName + "3", projectDescription + "3", 0L, 0L, id1);

        List<Integer> subProjects = database.getSubProjects(id1);
        List<Integer> expected = new ArrayList<>(Arrays.asList(id2, id3));
        assertTrue(expected.containsAll(subProjects) && subProjects.containsAll(expected));

    }

    @Test
    void addCollaborator() throws SQLException {
        database.addCollaborator(1, 1);
        database.addCollaborator(1, 2);
        database.addCollaborator(1, 3);
        List<Integer> expected = new ArrayList<>(Arrays.asList(1, 2, 3));
        assertTrue(expected.containsAll(database.getCollaborators(1)) && database.getCollaborators(1).containsAll(expected));
    }

    @Test
    void deleteCollaborator() throws SQLException {
        addCollaborator();
        database.deleteCollaborator(1, 2);
        List<Integer> expected = new ArrayList<>(Arrays.asList(1,3));
        assertTrue(expected.containsAll(database.getCollaborators(1)) && database.getCollaborators(1).containsAll(expected));
    }

    @Test
    void countCollaborators() throws SQLException {
        addCollaborator();
        assertEquals(database.countCollaborators(1), 3);
    }

    @Test
    void getUserProjects() throws SQLException {
        addCollaborator();
        List<Integer> expected = new ArrayList<>(Arrays.asList(1));
        assertEquals(expected, database.getUserProjects(2));
    }

    @Test
    void createTask() throws SQLException {
        database.createTask(taskDescription, 1, 0L, 0L);
        database.createTask(taskDescription2, 1, 0L, 0L);
        List<String> expected = new ArrayList<>(Arrays.asList(taskDescription, taskDescription2));
        List<String> actual = new ArrayList<>();
        for (int i = 0; i < database.getTasks(1).size(); i++) {
            actual.add(database.getTasks(1).get(i).getDescription());
        }
        assertEquals(expected, actual);
    }

    @Test
    void editTask() throws SQLException {
        database.createTask(taskDescription, 1, 0L, 0L);
        database.createTask(taskDescription2, 1, 0L, 0L);
        database.editTask(taskDescription, "newDescription", 1, 0L, 0L);
        List<String> expected = new ArrayList<>(Arrays.asList("newDescription", taskDescription2));
        List<String> actual = new ArrayList<>();
        for (int i = 0; i < database.getTasks(1).size(); i++) {
            actual.add(database.getTasks(1).get(i).getDescription());
        }
        assertEquals(expected, actual);
    }

    @Test
    void deleteTask() throws SQLException, IOException, ClassNotFoundException {
        setUp();
        database.createTask(taskDescription, 1, 0L, 0L);
        database.createTask(taskDescription2, 1, 0L, 0L);
        database.deleteTask(taskDescription, 1);
        List<String> expected = new ArrayList<>(Arrays.asList(taskDescription2));
        List<String> actual = new ArrayList<>();
        for (int i = 0; i < database.getTasks(1).size(); i++) {
            actual.add(database.getTasks(1).get(i).getDescription());
        }
        assertEquals(expected, actual);
    }

    @Test
    void countTasks() throws SQLException {
        database.createTask(taskDescription, 1, 0L, 0L);
        database.createTask(taskDescription2, 1, 0L, 0L);
        assertEquals(2, database.countTasks(1));
    }

    @Test
    void createTag() throws SQLException {
        int id = database.createTag("tag1", "#ffffff");
        assertEquals("tag1", database.getTag(id).getDescription());
    }

    @Test
    void editTag() throws SQLException {
        int id = database.createTag("tag1", "#ffffff");
        database.editTag(id, "newtag1", "#ffffff");
        assertEquals("newtag1", database.getTag(id).getDescription());
    }

    @Test
    void deleteTag() throws SQLException {
        int id = database.createTag("tag1", "#ffffff");
        database.deleteTag(id);
        assertNull(database.getTag(id));
    }

    @Test
    void addTag() throws SQLException {
        int id1 = database.createTag("tag1", "#ffffff");
        int id2 = database.createTag("tag2", "#ffffff");
        database.addTag(id1, 1);
        database.addTag(id2, 1);

        List<String> expected = new ArrayList<>(Arrays.asList("tag1", "tag2"));
        List<String> actual = new ArrayList<>();
        for (int i = 0; i < database.getTags(1).size(); i++) {
            actual.add(database.getTags(1).get(i).getDescription());
        }
        assertEquals(expected, actual);
    }

    @Test
    void editTags() throws SQLException {
        int id1 = database.createTag("tag1", "#ffffff");
        int id2 = database.createTag("tag2", "#ffffff");
        int id3 = database.createTag("tag3", "#ffffff");
        database.addTag(id1, 1);
        database.addTag(id2, 1);
        List<Integer> newTags = new ArrayList<>(Arrays.asList(id1, id3));
        database.editTags(1, newTags);
        List<String> expected = new ArrayList<>(Arrays.asList("tag1", "tag3"));
        List<String> actual = new ArrayList<>();
        for (int i = 0; i < database.getTags(1).size(); i++) {
            actual.add(database.getTags(1).get(i).getDescription());
        }
        assertEquals(expected, actual);

    }

    @Test
    void removeTag() throws SQLException, IOException, ClassNotFoundException {
        setUp();
        int id1 = database.createTag("tag1", "#ffffff");
        int id2 = database.createTag("tag2", "#ffffff");
        database.addTag(id1, 1);
        database.addTag(id2, 1);
        database.removeTag(1, id1);
        List<String> expected = new ArrayList<>(Arrays.asList("tag2"));
        List<String> actual = new ArrayList<>();
        for (int i = 0; i < database.getTags(1).size(); i++) {
            actual.add(database.getTags(1).get(i).getDescription());
        }
        assertEquals(expected, actual);
    }

    @Test
    void getAllTags() throws SQLException, IOException, ClassNotFoundException {
        setUp();
        int id1 = database.createTag("tag1", "#ffffff");
        int id2 = database.createTag("tag2", "#ffffff");
        database.addTag(id1, 1);
        database.addTag(id2, 1);
        List<String> expected = new ArrayList<>(Arrays.asList("tag1", "tag2"));
        List<String> actual = new ArrayList<>();
        for (int i = 0; i < database.getAllTags().size(); i++) {
            actual.add(database.getAllTags().get(i).getDescription());
        }
        assertEquals(expected, actual);
    }
}

