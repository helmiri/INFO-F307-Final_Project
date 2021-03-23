package be.ac.ulb.infof307.g06;

import be.ac.ulb.infof307.g06.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.Task;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestProjectDB {
    static String dbPath = "test/be/ac/ulb/infof307/g06/testDB.db";
    String projectName = "test1";
    String projectDescription = "desc1";
    Long projectDate = 1000L;

    String taskDescription = "task1";
    String taskDescription2 = "task2";

    @BeforeAll
    static void setUp() throws SQLException, ClassNotFoundException, IOException {
        File db = new File(dbPath);
        FileOutputStream fos = new FileOutputStream(dbPath);
        fos.close();
        if (db.delete()){
            System.out.println("Deleted the file: " + db.getName());
        } else {
            System.out.println("Failed to delete the file.");
        }
        new ProjectDB(dbPath);
    }

    @AfterAll
    static void tearDown(){
    }

    @Test
    Project createProject() throws SQLException {
        int id = ProjectDB.createProject(projectName,projectDescription,projectDate, 0);
        Project project = ProjectDB.getProject(id);
        assertEquals(project.getTitle(), projectName);
        assertEquals(project.getDescription(), projectDescription);
        assertEquals(project.getDate(), projectDate);
        return project;
    }

    @Test
    void editProject() throws SQLException {
        Project project = createProject();
        String newProjectName = "newTitle1";
        String newProjectDescription = "newDesc1";
        long newProjectDate = 2000L;
        ProjectDB.editProject(project.getId(), newProjectName, newProjectDescription, newProjectDate);
        project = ProjectDB.getProject(project.getId());
        assertEquals(project.getTitle(), newProjectName);
        assertEquals(project.getDescription(), newProjectDescription);
        assertEquals(project.getDate(), newProjectDate);
    }

    @Test
    void deleteProject() throws SQLException {
        Project project = createProject();
        int projectID = project.getId();
        ProjectDB.deleteProject(projectID);
        assertNull(ProjectDB.getProject(projectID));
    }

    @Test
    void getSubProjects() throws SQLException {
        int id1 = ProjectDB.createProject(projectName,projectDescription,projectDate, 0);
        int id2 = ProjectDB.createProject(projectName +"2",projectDescription + "2",projectDate, id1);
        int id3 = ProjectDB.createProject(projectName +"3",projectDescription + "3",projectDate, id1);

        List<Integer> subProjects = ProjectDB.getSubProjects(id1);
        List<Integer> expected = new ArrayList<>(Arrays.asList(id2, id3));
        assertTrue(expected.containsAll(subProjects) && subProjects.containsAll(expected));

    }

    @Test
    void addCollaborator() throws SQLException {
        ProjectDB.addCollaborator(1,1);
        ProjectDB.addCollaborator(1,2);
        ProjectDB.addCollaborator(1,3);
        List<Integer> expected = new ArrayList<>(Arrays.asList(1,2,3));
        assertTrue(expected.containsAll(ProjectDB.getCollaborators(1)) && ProjectDB.getCollaborators(1).containsAll(expected));
    }

    @Test
    void deleteCollaborator() throws SQLException {
        addCollaborator();
        ProjectDB.deleteCollaborator(1,2);
        List<Integer> expected = new ArrayList<>(Arrays.asList(1,3));
        assertTrue(expected.containsAll(ProjectDB.getCollaborators(1)) && ProjectDB.getCollaborators(1).containsAll(expected));
    }

    @Test
    void countCollaborators() throws SQLException {
        addCollaborator();
        assertEquals(ProjectDB.countCollaborators(1), 3);
    }

    @Test
    void getUserProjects() throws SQLException {
        addCollaborator();
        List<Integer> expected = new ArrayList<>(Arrays.asList(1));
        assertEquals(expected, ProjectDB.getUserProjects(2));
    }

    @Test
    void createTask() throws SQLException {
        ProjectDB.createTask(taskDescription, 1);
        ProjectDB.createTask(taskDescription2, 1);
        List<String> expected = new ArrayList<>(Arrays.asList(taskDescription, taskDescription2));
        List<String> actual = new ArrayList<>();
        for (int i=0;i<ProjectDB.getTasks(1).size();i++){
            actual.add(ProjectDB.getTasks(1).get(i).getDescription());
        }
        assertEquals(expected, actual);
    }

    @Test
    void editTask() throws SQLException {
        ProjectDB.createTask(taskDescription, 1);
        ProjectDB.createTask(taskDescription2, 1);
        ProjectDB.editTask(taskDescription, "newDescription", 1);
        List<String> expected = new ArrayList<>(Arrays.asList("newDescription", taskDescription2));
        List<String> actual = new ArrayList<>();
        for (int i=0;i<ProjectDB.getTasks(1).size();i++){
            actual.add(ProjectDB.getTasks(1).get(i).getDescription());
        }
        assertEquals(expected, actual);
    }

    @Test
    void deleteTask() throws SQLException, IOException, ClassNotFoundException {
        setUp();
        ProjectDB.createTask(taskDescription, 1);
        ProjectDB.createTask(taskDescription2, 1);
        ProjectDB.deleteTask(taskDescription, 1);
        List<String> expected = new ArrayList<>(Arrays.asList(taskDescription2));
        List<String> actual = new ArrayList<>();
        for (int i=0;i<ProjectDB.getTasks(1).size();i++){
            actual.add(ProjectDB.getTasks(1).get(i).getDescription());
        }
        assertEquals(expected, actual);
    }

    @Test
    void countTasks() throws SQLException {
        ProjectDB.createTask(taskDescription, 1);
        ProjectDB.createTask(taskDescription2, 1);
        assertEquals(2, ProjectDB.countTasks(1));
    }

    @Test
    void createTag() throws SQLException {
        int id = ProjectDB.createTag("tag1", 0);
        assertEquals("tag1", ProjectDB.getTag(id).getDescription());
    }

    @Test
    void editTag() throws SQLException {
        int id = ProjectDB.createTag("tag1", 0);
        ProjectDB.editTag(id, "newtag1", 1);
        assertEquals("newtag1", ProjectDB.getTag(id).getDescription());
    }

    @Test
    void deleteTag() throws SQLException {
        int id = ProjectDB.createTag("tag1", 0);
        ProjectDB.deleteTag(id);
        assertNull(ProjectDB.getTag(id));
    }

    @Test
    void addTag() throws SQLException {
        int id1 = ProjectDB.createTag("tag1", 0);
        int id2 = ProjectDB.createTag("tag2", 0);
        ProjectDB.addTag(id1, 1);
        ProjectDB.addTag(id2, 1);

        List<String> expected = new ArrayList<>(Arrays.asList("tag1", "tag2"));
        List<String> actual = new ArrayList<>();
        for (int i=0;i<ProjectDB.getTags(1).size();i++){
            actual.add(ProjectDB.getTags(1).get(i).getDescription());
        }
        assertEquals(expected, actual);
    }

    @Test
    void editTags() throws SQLException {
        int id1 = ProjectDB.createTag("tag1", 0);
        int id2 = ProjectDB.createTag("tag2", 0);
        int id3 = ProjectDB.createTag("tag3", 0);
        ProjectDB.addTag(id1, 1);
        ProjectDB.addTag(id2, 1);
        List<Integer> newTags = new ArrayList<>(Arrays.asList(id1,id3));
        ProjectDB.editTags(1, newTags);
        List<String> expected = new ArrayList<>(Arrays.asList("tag1", "tag3"));
        List<String> actual = new ArrayList<>();
        for (int i=0;i<ProjectDB.getTags(1).size();i++){
            actual.add(ProjectDB.getTags(1).get(i).getDescription());
        }
        assertEquals(expected, actual);

    }

    @Test
    void removeTag() throws SQLException, IOException, ClassNotFoundException {
        setUp();
        int id1 = ProjectDB.createTag("tag1", 0);
        int id2 = ProjectDB.createTag("tag2", 0);
        ProjectDB.addTag(id1, 1);
        ProjectDB.addTag(id2, 1);
        ProjectDB.removeTag(1, id1);
        List<String> expected = new ArrayList<>(Arrays.asList("tag2"));
        List<String> actual = new ArrayList<>();
        for (int i=0;i<ProjectDB.getTags(1).size();i++){
            actual.add(ProjectDB.getTags(1).get(i).getDescription());
        }
        assertEquals(expected, actual);
    }

    @Test
    void getAllTags() throws SQLException, IOException, ClassNotFoundException {
        setUp();
        int id1 = ProjectDB.createTag("tag1", 0);
        int id2 = ProjectDB.createTag("tag2", 0);
        int id3 = ProjectDB.createTag("tag3", 0);
        ProjectDB.addTag(id1, 1);
        ProjectDB.addTag(id2, 1);
        List<String> expected = new ArrayList<>(Arrays.asList("tag1", "tag2", "tag3"));
        List<String> actual = new ArrayList<>();
        for (int i=0;i<ProjectDB.getAllTags().size();i++){
            actual.add(ProjectDB.getAllTags().get(i).getDescription());
        }
        assertEquals(expected, actual);
    }
}