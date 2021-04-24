package be.ac.ulb.infof307.g06.controllers.projectControllers;

import be.ac.ulb.infof307.g06.controllers.Controller;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.projectViews.popups.ProjectInputViewController;
import be.ac.ulb.infof307.g06.views.projectViews.ProjectsViewController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;
import org.rauschig.jarchivelib.*;

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
    public void show() {}

    /**
     * @param inputView ProjectInputViewController
     */
    public void initProjectExport(ProjectInputViewController inputView) {
        try {
            ObservableList<String> projectsTitleList = FXCollections.observableArrayList();
            List<Integer> ProjectIDList = project_db.getUserProjects(user_db.getCurrentUser().getId());
            for (Integer projectID : ProjectIDList) {
                projectsTitleList.add(project_db.getProject(projectID).getTitle());
            }
            //inputView.addProjectTitle(projectsTitleList);//i
        } catch (SQLException e) {
            // TODO Exception
        }
    }

    /**
     * Write a project and his children in a json file.
     *
     * @param project Project
     * @param fw      FileWriter
     */
    private void saveProjectAndChildsJson(Project project, FileWriter fw) throws IOException {
        try {
            int ID = project.getId();
            saveProjectJson(project, project_db.getTasks(ID), project_db.getTags(ID), fw);
            for (Integer subProject : project_db.getSubProjects(ID)) {
                fw.write(",\n");
                saveProjectAndChildsJson(project_db.getProject(subProject), fw);
            }
        } catch (Exception ignored) {
            fw.close();
        }
    }

    /**
     * Export a complete project (root and all his children) in a "tar.gz" archive.
     *
     * @param project     Project
     * @param archivePath String
     */
    public void onExportProject(Project project, String archivePath) {
        try {
            String jsonFile = archivePath + "/file.json";
            deleteFile(jsonFile);
            FileWriter fw = new FileWriter(jsonFile, true);
            fw.write("[\n");
            saveProjectAndChildsJson(project, fw);
            fw.write("\n]");
            fw.close();
            zip(project.getTitle(), jsonFile, archivePath);
            deleteFile(jsonFile);
            new AlertWindow("Success", "Success in project export." ).informationWindow();
        } catch (Exception e) {
            new AlertWindow("Failure", "Failure to export project : " + e).errorWindow();
        }
    }

    /**
     * Write a project, his tasks and his tags in a json file.
     *
     * @param project Project
     * @param tasks   List<Task>
     * @param tags    List<Tag>
     * @param fw      FileWriter
     */
    private void saveProjectJson(Project project, List<Task> tasks, List<Tag> tags, FileWriter fw) {
        try {
            Gson gson = new GsonBuilder().create();
            fw.write("[\n");
            gson.toJson(project, fw);
            fw.write(",\n");
            gson.toJson(tasks, fw);
            fw.write(",\n");
            gson.toJson(tags, fw);
            fw.write("\n]");
        } catch (Exception ignored) {
        }
    }

    /**
     * Import a complete project (root and all his children) from a archive "tar.gz".
     *
     * @param archivePath String
     */
    public void onImportProject(String archivePath) {
        try {
            File file = new File(archivePath);
            String directory = file.getAbsoluteFile().getParent();
            String jsonFile = directory + "/file.json";
            unzip(archivePath, directory);
            if (isProjectInDb(jsonFile)) return;
            BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
            int count = 0, parentID = 0 ,id = 0;
            String line = reader.readLine();
            Map<Integer, Integer> hm = new HashMap();
            while ((line = reader.readLine()) != null) {
                ++count;
                count %= 6;
                switch (count) {
                    case 2:
                        int[] listIdparentID=  parseProject(parentID, hm, line);
                        id = listIdparentID[0];
                        parentID = listIdparentID[1];
                        break;
                    case 3:
                        parseTasks(id, line);
                        break;
                    case 4:
                        parseTags(id, line);
                        // update la view
                        TreeItem<Project> child = new TreeItem<>(project_db.getProject(id));
                        setViewController(viewController);
                        viewController.insertProject(id, child, parentID);
                        break;
                    default:
                        if (line.equals("[")) {count = 1;}
                }
            }
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
            reader.close();
            deleteFile(jsonFile);
            new AlertWindow("Success", "Success in project import." ).informationWindow();
        } catch (IOException | SQLException e) {
            new AlertWindow("Failure", "Failure to import project : "+ e ).errorWindow();
            e.printStackTrace();
        }
    }

    /**
     *  Parse a line of json file containing a list of tags.
     *  Create the tags and add them to the project.
     * @param id int
     * @param line String
     * @throws SQLException
     */
    private void parseTags(int id, String line) throws SQLException {
        int tagID;
        Type listType = new TypeToken<ArrayList<Tag>>() {}.getType();
        List<Tag> tag = new Gson().fromJson(line, listType);
        for (Tag t : tag) {
            tagID = project_db.createTag(t.getDescription(), t.getColor());
            project_db.addTag(tagID, id);
        }
    }

    /**
     * Parse a line of json file containing a list of tasks.
     * Create the tasks and add them to the project.
     * @param id int
     * @param line String
     * @throws SQLException
     */
    private void parseTasks(int id, String line) throws SQLException {
        Type listType = new TypeToken<ArrayList<Task>>() {}.getType();
        List<Task> tasks = new Gson().fromJson(line.substring(0, line.length() - 1), listType);
        for (Task t : tasks) {
            project_db.createTask(t.getDescription(), id, t.getStartDate(), t.getEndDate());
        }
    }

    /**
     * Parse a line of json file containing a project (Object of type Project).
     * Create the project and add it to the database.
     * @param parentID int
     * @param hm Map<Integer,Integer>
     * @param line String
     * @return A list with of 2 integers, first one is the project id and
     *         second one is his parent's project id
     * @throws SQLException
     */
    private int[] parseProject(int parentID , Map<Integer,Integer> hm,String line) throws SQLException {
        Project project = new Gson().fromJson(line.substring(0, line.length() - 1), Project.class);
        int id;
        if (hm.size() == 0) {
            id = project_db.createProject(project.getTitle(), project.getDescription(), project.getStartDate(), project.getEndDate(), 0);
            hm.put(project.getParent_id(), 0);
        } else {
            id = project_db.createProject(project.getTitle(), project.getDescription(), project.getStartDate(), project.getEndDate(), hm.get(project.getParent_id()));
            parentID = hm.get(project.getParent_id());
        }
        hm.put(project.getId(), id);
        project_db.addCollaborator(id, user_db.getCurrentUser().getId());
        return new int []{id, parentID};
    }

    /**
     * Zip a file in a "tar.gz" archive.
     *
     * @param archiveName String
     * @param source      String
     * @param destination String
     */
    public void zip(String archiveName, String source, String destination) {
        try {
            File src = new File(source);
            File dest = new File(destination);
            Archiver archiver =
                    ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
            archiver.create(archiveName, dest, src);
            System.out.println("zip");
        } catch (Exception ignored) {
        }
    }

    /**
     * Unzip a tar.gz archive in a directory.
     *
     * @param source      String
     * @param destination String
     */
    public void unzip(String source, String destination) {
        try {
            Archiver archiver =
                    ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
            File archive = new File(source);
            File dest = new File(destination);
            System.out.println("WARNINGS are normal");
            archiver.extract(archive, dest); // WARNING OK
            System.out.println("unzip");
        } catch (Exception ignored) {
        }
    }

    /**
     * Check if one of the sub-project of a complete project
     * (root and his children) is in the Database.
     *
     * @param jsonFile String
     * @return boolean
     */
    public boolean isProjectInDb(String jsonFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
            String line = reader.readLine();
            int count = 0;
            while ((line = reader.readLine()) != null) {
                ++count;
                count %= 6;
                if (count == 2) {
                    System.out.println("projet " + line);
                    Project project = new Gson().fromJson(line.substring(0, line.length() - 1), Project.class);
                    int id = project_db.getProjectID(project.getTitle());
                    if (id != 0) return true;
                }
                else { if (line.equals("[")) count = 1; }
            }
            reader.close();
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Delete a file.
     *
     * @param fileName String
     */
    public void deleteFile(String fileName) {
        try {
            File myObj = new File(fileName);
            myObj.delete();
        } catch (Exception ignored) {
        }
    }
}
