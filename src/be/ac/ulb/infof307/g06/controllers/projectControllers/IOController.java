package be.ac.ulb.infof307.g06.controllers.projectControllers;

import be.ac.ulb.infof307.g06.controllers.Controller;
import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.projectViews.ProjectsViewController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import java.io.*;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IOController extends Controller {
    //--------------- ATTRIBUTES ----------------
    private ProjectsViewController viewController;

    //--------------- METHODS ----------------
    public IOController(UserDB user_db, ProjectDB project_db, Stage stage, Scene scene) {
        super(user_db, project_db, stage, scene);
    }

    public void setViewController(ProjectsViewController viewController) {
        this.viewController = viewController;
    }

    @Override
    public void show() {
    }

    /**
     * Write a project and its children in a json file.
     *
     * @param project    Project
     * @param fileWriter FileWriter
     */
    private void saveProjectAndChildrenJSON(Project project, FileWriter fileWriter) throws IOException, SQLException {
        int ID = project.getId();
        saveProjectJson(project, project_db.getTasks(ID), project_db.getTags(ID), fileWriter);
        List<Integer> subProjects = project_db.getSubProjects(ID);
        if (subProjects.isEmpty()) {
            return;
        }
        for (Integer subProject : project_db.getSubProjects(ID)) {
            fileWriter.write(",\n");
            saveProjectAndChildrenJSON(project_db.getProject(subProject), fileWriter);
        }
    }

    /**
     * Export a complete project (root and all his children) in a "tar.gz" archive.
     *
     * @param project     Project
     * @param archivePath String
     * @throws IOException       on file write error
     * @throws DatabaseException when a database access error occurs
     */
    public void onExportProject(Project project, String archivePath) throws IOException, DatabaseException {
        String jsonFile = archivePath + "/file.json";
        deleteFile(jsonFile);
        try (FileWriter fileWriter = new FileWriter(jsonFile, true)) {
            saveProjectAndChildrenJSON(project, fileWriter);
        } catch (IOException error) {
            // Close file writer and throw exception back
            throw new IOException(error);
        } catch (SQLException error) {
            throw new DatabaseException(error);
        }
        zip(project.getTitle(), jsonFile, archivePath);
        deleteFile(jsonFile);
    }

    /**
     * Write a project, his tasks and his tags in a json file.
     *
     * @param project    The project
     * @param tasks      The list of tasks
     * @param tags       The list of tags
     * @param fileWriter FileWriter
     * @throws IOException On file write error
     */
    private void saveProjectJson(Project project, List<Task> tasks, List<Tag> tags, FileWriter fileWriter) throws IOException {
        Gson gson = new GsonBuilder().create();
        fileWriter.write("[\n");
        gson.toJson(project, fileWriter);
        fileWriter.write(",\n");
        gson.toJson(tasks, fileWriter);
        fileWriter.write(",\n");
        gson.toJson(tags, fileWriter);
        fileWriter.write("\n]");
    }

    /**
     * Import a complete project (root and all his children) from a archive "tar.gz".
     *
     * @param archivePath The path to the file
     * @return false if the project already exists in the database, true on success
     * @throws SQLException when a database access error occurs
     * @throws IOException  on file read error
     */
    public boolean onImportProject(String archivePath) throws SQLException, IOException {
        File file = new File(archivePath);
        String directory = file.getAbsoluteFile().getParent();
        String jsonFile = directory + "/file.json";
        unzip(archivePath, directory);
        if (isProjectInDb(jsonFile)) {
            deleteFile(jsonFile);
            return false;
        }
        parseJsonFile(jsonFile);
        user_db.updateDiskUsage(project_db.getSizeOnDisk());
        deleteFile(jsonFile);
        return true;
    }

    /**
     * Parse a project json file and import the project into the database
     *
     * @param jsonFile Path to json file
     * @throws IOException  On read error
     * @throws SQLException when a database error occurs
     */
    private void parseJsonFile(String jsonFile) throws IOException, SQLException {
        BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
        int count = 0, parentID = 0, id = 0;
        String line;
        Map<Integer, Integer> projectMap = new HashMap<>();
        while ((line = reader.readLine()) != null) {
            count = ++count % 6;
            if (count == 2) {
                int[] parentIDList = parseProject(parentID, projectMap, line);
                id = parentIDList[0];
                parentID = parentIDList[1];
            } else if (count == 3) {
                parseTasks(id, line);
            } else if (count == 4) {
                parseTags(id, line);
                addProjectToTreeView(project_db.getProject(id));
            } else {
                if (line.equals("[")) {
                    count = 1;
                }
            }
        }
        reader.close();
    }

    /**
     * Add the project to the tree view, update the view.
     *
     * @param project Project
     */
    private void addProjectToTreeView(Project project) {
        TreeItem<Project> child = new TreeItem<>(project);
        setViewController(viewController);
        viewController.insertProject(project.getId(), child, project.getParentId());
    }

    /**
     * Parse a line of json file containing a list of tags.
     * Create the tags and add them to the project.
     *
     * @param id   Tag's ID
     * @param line tag name
     * @throws SQLException When a database error occurs
     */
    private void parseTags(int id, String line) throws SQLException {
        int tagID;
        Type listType = new TypeToken<ArrayList<Tag>>() {
        }.getType();
        List<Tag> tag = new Gson().fromJson(line, listType);
        for (Tag t : tag) {
            tagID = project_db.createTag(t.getDescription(), t.getColor());
            project_db.addTag(tagID, id);
        }
    }

    /**
     * Parse a line of json file containing a list of tasks.
     * Create the tasks and add them to the project.
     *
     * @param id   Task's ID
     * @param line Task name
     * @throws SQLException exception
     */
    private void parseTasks(int id, String line) throws SQLException {
        Type listType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> tasks = new Gson().fromJson(line.substring(0, line.length() - 1), listType);
        for (Task t : tasks) {
            project_db.createTask(t.getDescription(), id, t.getStartDate(), t.getEndDate());
        }
    }

    /**
     * Parse a line of json file containing a project (Object of type Project).
     * Create the project and add it to the database.
     *
     * @param parentID   ID of the parent project
     * @param projectMap Hashmap containing parent project ID as key
     * @param line       String
     * @return A list with of 2 integers, first one is the project id and
     * second one is his parent's project id
     * @throws SQLException When a database access error occurs
     */
    private int[] parseProject(int parentID, Map<Integer, Integer> projectMap, String line) throws SQLException {
        Project project = new Gson().fromJson(line.substring(0, line.length() - 1), Project.class);
        int id;
        if (projectMap.size() == 0) {
            id = project_db.createProject(project.getTitle(), project.getDescription(), project.getStartDate(), project.getEndDate(), 0);
            projectMap.put(project.getParentId(), 0);
        } else {
            id = project_db.createProject(project.getTitle(), project.getDescription(), project.getStartDate(), project.getEndDate(), projectMap.get(project.getParentId()));
            parentID = projectMap.get(project.getParentId());
        }
        projectMap.put(project.getId(), id);
        project_db.addCollaborator(id, user_db.getCurrentUser().getId());
        return new int[]{id, parentID};
    }

    /**
     * Zip a file in a "tar.gz" archive.
     *
     * @param archiveName Archive name
     * @param source      path to source file
     * @param destination path to destination
     */
    public void zip(String archiveName, String source, String destination) throws IOException {
        File src = new File(source);
        File dest = new File(destination);
        Archiver archiver =
                ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
        archiver.create(archiveName, dest, src);
    }

    /**
     * Unzip a tar.gz archive in a directory.
     *
     * @param source      Source file
     * @param destination Destination file
     */
    public void unzip(String source, String destination) throws IOException {
        Archiver archiver =
                ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
        File archive = new File(source);
        File dest = new File(destination);
        archiver.extract(archive, dest); // WARNING OK
    }

    /**
     * Check if one of the sub-project of a complete project
     * (root and his children) is in the Database.
     *
     * @param jsonFile Path to Json file
     * @return true if the project is in the database, false otherwise
     */
    public boolean isProjectInDb(String jsonFile) throws IOException, SQLException {
        BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
        String line;
        int count = 0;
        while ((line = reader.readLine()) != null) {
            ++count;
            count %= 6;
            if (count == 2) {
                Project project = new Gson().fromJson(line.substring(0, line.length() - 1), Project.class);
                int id = project_db.getProjectID(project.getTitle());
                if (id != 0) {
                    reader.close();
                    return true;
                }
            } else {
                if (line.equals("[")) {
                    count = 1;
                }
            }
        }
        reader.close();
        return false;
    }

    /**
     * Delete a file.
     *
     * @param fileName path to target
     */
    public void deleteFile(String fileName) {
        File myObj = new File(fileName);
        myObj.delete();
    }
}