package be.ac.ulb.infof307.g06.controllers.projectControllers;

import be.ac.ulb.infof307.g06.controllers.Controller;
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
            final int ID = project.getId();
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
            final int ID = project.getId();
            deleteFile(jsonFile);
            FileWriter fw = new FileWriter(jsonFile, true);
            fw.write("[\n");
            saveProjectAndChildsJson(project, fw);
            fw.write("\n]");
            fw.close();
            zip(project.getTitle(), jsonFile, archivePath);
            deleteFile(jsonFile);
        } catch (Exception e) {
            // TODO Exception
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
            Gson gson = new Gson();
            BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
            String line;
            int count = 0;
            reader.readLine();
            int idParent = 0;
            int id = 0;
            int idTag = 0;
            Map<Integer, Integer> hm = new HashMap();
            while ((line = reader.readLine()) != null) {
                ++count;
                count %= 6;
                switch (count) {
                    case 1:
                        System.out.println("ouvrante " + line);
                        break;
                    case 2:
                        System.out.println("projet " + line);
                        Project project = gson.fromJson(line.substring(0, line.length() - 1), Project.class);
                        if (hm.size() == 0) {
                            id = project_db.createProject(project.getTitle(), project.getDescription(), project.getStartDate(), project.getEndDate(), 0);
                            hm.put(project.getParent_id(), 0);
                        } else {
                            id = project_db.createProject(project.getTitle(), project.getDescription(), project.getStartDate(), project.getEndDate(), hm.get(project.getParent_id()));
                            idParent = hm.get(project.getParent_id());
                        }
                        hm.put(project.getId(), id);
                        project_db.addCollaborator(id, user_db.getCurrentUser().getId());
                        break;
                    case 3:
                        System.out.println("tasks " + line);
                        Type listType = new TypeToken<ArrayList<Task>>() {
                        }.getType();
                        List<Task> tasks = new Gson().fromJson(line.substring(0, line.length() - 1), listType);
                        for (Task t : tasks) {
                            project_db.createTask(t.getDescription(), id, t.getStartDate(), t.getEndDate());
                        }
                        break;
                    case 4:
                        System.out.println("tag " + line);
                        Type listkind = new TypeToken<ArrayList<Tag>>() {
                        }.getType();
                        List<Tag> tag = new Gson().fromJson(line, listkind);
                        for (Tag t : tag) {
                            //verifier dans la bdd
                            idTag = project_db.getTagID(t.getDescription());
                            if (idTag == 0) {
                                idTag = project_db.createTag(t.getDescription(), t.getColor());
                            }
                            project_db.addTag(idTag, id);
                        }
                        // update la view
                        TreeItem<Project> child = new TreeItem<>(project_db.getProject(id));
                        System.out.println("AAAAAAAA  " + viewController);
                        viewController.insertProject(id, child, idParent);
                        break;
                    case 5:
                        System.out.println("fermante " + line);
                        break;
                    default:
                        if (line.equals("[")) {
                            count = 1;
                        } else {
                            System.out.println("fin " + line);
                        }
                }
            }
            user_db.updateDiskUsage(project_db.getSizeOnDisk());
            reader.close();
            deleteFile(jsonFile);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if an archive is valide ("tar.gz" contains a json file).
     *
     * @param archivePath String
     * @return boolean
     */
    public boolean valideArchive(final String archivePath) {
        try {
            Archiver archiver =
                    ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
            File archive = new File(archivePath);
            ArchiveStream stream = archiver.stream(archive);
            ArchiveEntry entry;
            int count = 0;
            while ((entry = stream.getNextEntry()) != null) {
                ++count;
                String name = entry.getName();
                if (count > 1 || !name.substring(name.length() - 4).equals(".json")) {
                    return false;
                }
            }
            stream.close();
            return true;
        } catch (Exception ignored) {
            return false;
        }
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
    public void unzip(final String source, final String destination) {
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
            Gson gson = new Gson();
            BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
            String line = null;
            int count = 0;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                ++count;
                count %= 6;
                switch (count) {
                    case 1:
                        System.out.println("ouvrante " + line);
                        break;
                    case 2:
                        System.out.println("projet " + line);
                        Project project = gson.fromJson(line.substring(0, line.length() - 1), Project.class);
                        int id = project_db.getProjectID(project.getTitle());
                        if (id != 0) return true;
                        break;
                    case 3:
                        System.out.println("tasks " + line);
                        Type listType = new TypeToken<ArrayList<Task>>() {
                        }.getType();
                        List<Task> tasks = new Gson().fromJson(line.substring(0, line.length() - 1), listType);
                        break;
                    case 4:
                        System.out.println("tag " + line);
                        Type listkind = new TypeToken<ArrayList<Tag>>() {
                        }.getType();
                        List<Tag> tag = new Gson().fromJson(line, listkind);
                        break;
                    case 5:
                        System.out.println("fermante " + line);
                        break;
                    default:
                        if (line.equals("[")) {
                            count = 1;
                        } else {
                            System.out.println("fin " + line);
                        }
                }
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
    public void deleteFile(final String fileName) {
        try {
            File myObj = new File(fileName);
            if (myObj.delete()) {
            } else {
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Raise an alert after an import of export.
     *
     * @param choice  String
     * @param succeed boolean
     */
    @FXML
    public void alertExportImport(String choice, boolean succeed) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(choice);
        alert.setHeaderText(null);
        alert.getDialogPane().setMinWidth(900);
        if (succeed) {
            alert.setContentText(choice + "ed successfully.");
            alert.showAndWait();
        } else {
            alert.setContentText("Failed to " + choice + " your project");
            alert.showAndWait();
        }
    }
}
