package be.ac.ulb.infof307.g06.controllers.projectControllers;

import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.models.encryption.EncryptedFile;
import be.ac.ulb.infof307.g06.views.projectViews.ProjectsViewController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.scene.control.TreeItem;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * File exportation/importation controller
 */
public class IOController {
    //--------------- ATTRIBUTES ----------------
    private ProjectsViewController viewController;
    private final String tempDir;
    private UserDB userDB;
    private ProjectDB projectDB;
    //--------------- METHODS ----------------

    /**
     * File exportation/importation controller
     *
     * @param userDB    The user database to be used to retrieve the information
     * @param projectDB The project database to be used to retrieve the information
     */

    public IOController(UserDB userDB, ProjectDB projectDB) {
        this.userDB = userDB;
        this.projectDB = projectDB;
        tempDir = System.getProperty("user.dir") + "/temp/";
        File directory = new File(tempDir);// Temporary file directory

        if (!directory.mkdir()) { // Delete content if not empty
            deleteTempFiles(directory);
        }
    }

    /**
     * Sets the view controller to the projects view controller.
     *
     * @param viewController ProjectsViewController, the projects view controller.
     */
    public void setViewController(ProjectsViewController viewController) {
        this.viewController = viewController;
    }

    /**
     * Write a project and its children in a json file.
     *
     * @param project    Project
     * @param fileWriter FileWriter
     */
    private void saveProjectAndChildrenJSON(Project project, FileWriter fileWriter) throws IOException, SQLException {
        int ID = project.getId();
        saveProjectJson(project, projectDB.getTasks(ID), projectDB.getTags(ID), fileWriter);
        List<Integer> subProjects = projectDB.getSubProjects(ID);
        if (subProjects.isEmpty()) {
            return;
        }
        for (Integer subProject : projectDB.getSubProjects(ID)) {
            fileWriter.write(",\n");
            saveProjectAndChildrenJSON(projectDB.getProject(subProject), fileWriter);
        }
    }

    /**
     * Export a complete project (root and all his children) in a "tar.gz" archive.
     *
     * @param password    Password to be used to encrypt the file
     * @param project     Project to be exported
     * @param archivePath Directory where the file will be saved
     * @throws IOException       on file write error
     * @throws DatabaseException when a database access error occurs
     */
    public void onExportProject(String password, Project project, String archivePath) throws IOException, DatabaseException {
        String jsonFile = tempDir + "/file.json";
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
        encrypt(password, archivePath + "/" + project.getTitle() + ".tar.gz");
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
     *
     * @param password The password to be used to decrypt the file
     * @param archivePath The path to the file
     * @return false if the project already exists in the database, true on success
     * @throws SQLException when a database access error occurs
     * @throws IOException  on file read error
     */
    public boolean onImportProject(String password, String archivePath) throws SQLException, IOException {
        String sourcePath = decrypt(password, archivePath);
        File file = new File(sourcePath);

        String directory = file.getAbsoluteFile().getParent();
        String jsonFile = tempDir + "/file.json";
        unzip(sourcePath, directory);
        if (isProjectInDb(jsonFile)) {
            deleteFile(jsonFile);
            return false;
        }
        parseJsonFile(jsonFile);
        userDB.updateDiskUsage(projectDB.getSizeOnDisk());
        deleteFile(jsonFile);
        return true;
    }

    /**
     * Decrypts a file
     *
     * @param password The password to be used
     * @param path     The path of the file to be decrypted
     * @return A path to the decrypted file
     * @throws IOException On error reading/writing the file
     */
    private String decrypt(String password, String path) throws IOException {
        String tempPath = tempDir + "/archive.tar.gz";
        EncryptedFile file = new EncryptedFile(password, path);
        file.decryptFile(tempPath);
        return tempPath;
    }

    /**
     * Encrypts a file
     *
     * @param password The password to be used
     * @param path     The path of the file to be encrypted
     * @throws IOException On error reading/writing the file
     */
    private void encrypt(String password, String path) throws IOException {
        String tempPath = tempDir + "/archive.tar.gz";
        EncryptedFile file = new EncryptedFile(password, path);
        file.encryptFile(tempPath);
        deleteFile(path);
        Files.move(Paths.get(tempPath), Paths.get(path));
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
                addProjectToTreeView(projectDB.getProject(id));
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
            tagID = projectDB.createTag(t.getDescription(), t.getColor());
            projectDB.addTag(tagID, id);
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
            projectDB.createTask(t.getDescription(), id, t.getStartDate(), t.getEndDate());
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
            id = projectDB.createProject(project.getTitle(), project.getDescription(), project.getStartDate(), project.getEndDate(), 0);
            projectMap.put(project.getParentId(), 0);
        } else {
            id = projectDB.createProject(project.getTitle(), project.getDescription(), project.getStartDate(), project.getEndDate(), projectMap.get(project.getParentId()));
            parentID = projectMap.get(project.getParentId());
        }
        projectMap.put(project.getId(), id);
        projectDB.addCollaborator(id, userDB.getCurrentUser().getId());
        return new int[]{id, parentID};
    }

    /**
     * Zip a file in a "tar.gz" archive.
     *
     * @param archiveName     Archive name
     * @param sourcePath      path to source file
     * @param destinationPath path to destination
     */
    public void zip(String archiveName, String sourcePath, String destinationPath) throws IOException {
        File source = new File(sourcePath);
        File destination = new File(destinationPath);
        Archiver archiver =
                ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
        archiver.create(archiveName, destination, source);
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
        try {
            archiver.extract(archive, dest); // WARNING OK
        } catch (IOException e) {
            // Catch and throw the exception back to notify the user of a potential cause because
            // there is no way of detecting whether the file has been encrypted by the application
            throw new IOException("Could not extract the file:\n\t- Is the password correct?\n\t- The file may not have originated from the application", e);
        } finally {
            archive.delete();
        }
    }

    /**
     * Check if one of the sub-project of a complete project
     * (root and his children) is in the Database.
     *
     * @param jsonFile Path to Json file
     * @return true if the project is in the database, false otherwise
     */
    public boolean isProjectInDb(String jsonFile) throws IOException, SQLException {

        String line;
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(jsonFile))) {
            while ((line = reader.readLine()) != null) {
                ++count;
                count %= 6;
                if (count == 2) {
                    Project project = new Gson().fromJson(line.substring(0, line.length() - 1), Project.class);
                    int id = projectDB.getProjectID(project.getTitle());
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
            // Ensure that the buffer reader is closed and throw back the exception
        } catch (IOException e) {
            throw new IOException(e);
        } catch (SQLException e) {
            throw new SQLException(e);
        }
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

    /**
     * Delete files in a folder
     *
     * @param folder Target folder
     */
    private void deleteTempFiles(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }
}