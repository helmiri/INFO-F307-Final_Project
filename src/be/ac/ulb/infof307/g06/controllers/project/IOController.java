package be.ac.ulb.infof307.g06.controllers.project;

import be.ac.ulb.infof307.g06.models.Global;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Tag;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import org.rauschig.jarchivelib.*;

import java.io.*;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IOController {
    public IOController() {
    }

    /**
     * Write a project, his tasks and his tags in a json file.
     *
     * @param project Project
     * @param tasks   List<Task>
     * @param tags    List<Tag>
     * @param fw      FileWriter
     * @return boolean
     */
    public static boolean saveProjectJson(Project project, List<Task> tasks, List<Tag> tags, FileWriter fw) {
        try {
            Gson gson = new GsonBuilder().create();
            fw.write("[\n");
            gson.toJson(project, fw);
            fw.write(",\n");
            gson.toJson(tasks, fw);
            fw.write(",\n");
            gson.toJson(tags, fw);
            fw.write("\n]");
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Import a complete project (root and all his children) from a archive "tar.gz".
     *
     * @param archivePath String
     * @return boolean
     */
    public static boolean importProject(String archivePath) {
        try {
            File file = new File(archivePath);
            String directory = file.getAbsoluteFile().getParent();
            String jsonFile = directory + "/file.json";
            unzip(archivePath, directory);
            if (isProjectInDb(jsonFile)) return false;
            Gson gson = new Gson();
            BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
            String line = null;
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
                            id = ProjectDB.createProject(project.getTitle(), project.getDescription(), project.getDate(), 0);
                            hm.put(project.getParent_id(), 0);
                        } else {
                            id = ProjectDB.createProject(project.getTitle(), project.getDescription(), project.getDate(), hm.get(project.getParent_id()));
                            idParent = hm.get(project.getParent_id());
                        }
                        hm.put(project.getId(), id);
                        ProjectDB.addCollaborator(id, Global.userID);
                        break;
                    case 3:
                        System.out.println("tasks " + line);
                        Type listType = new TypeToken<ArrayList<Task>>() {
                        }.getType();
                        List<Task> tasks = new Gson().fromJson(line.substring(0, line.length() - 1), listType);
                        for (Task t : tasks) {
                            ProjectDB.createTask(t.getDescription(), id);
                        }
                        break;
                    case 4:
                        System.out.println("tag " + line);
                        Type listkind = new TypeToken<ArrayList<Tag>>() {
                        }.getType();
                        List<Tag> tag = new Gson().fromJson(line, listkind);
                        for (Tag t : tag) {
                            //verifier dans la bdd
                            idTag = ProjectDB.getTagID(t.getDescription());
                            if (idTag == 0) {
                                idTag = ProjectDB.createTag(t.getDescription(), t.getColor());
                            }
                            ProjectDB.addTag(idTag, id);
                        }
                        // update la view
                        TreeItem<Project> child = new TreeItem<Project>(ProjectDB.getProject(id));
                        Global.TreeMap.put(id, child);
                        if (idParent == 0) {
                            Global.projectsView.addChild(Global.root, child);
                        } else {
                            Global.projectsView.addChild(Global.TreeMap.get(idParent), child);
                        }
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
            UserDB.updateDiskUsage(ProjectDB.getSizeOnDisk());
            reader.close();
            deleteFile(jsonFile);
            return true;
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if an archive is valide ("tar.gz" contains a json file).
     *
     * @param archivePath String
     * @return boolean
     */
    public static boolean valideArchive(final String archivePath) {
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
     * @return boolean
     */
    public static boolean zip(String archiveName, String source, String destination) {
        try {
            File src = new File(source);
            File dest = new File(destination);
            Archiver archiver =
                    ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
            archiver.create(archiveName, dest, src);
            System.out.println("zip");
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Unzip a tar.gz archive in a directory.
     *
     * @param source      String
     * @param destination String
     * @return boolean
     */
    public static boolean unzip(final String source, final String destination) {
        try {
            Archiver archiver =
                    ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
            File archive = new File(source);
            File dest = new File(destination);
            System.out.println("WARNINGS are normal");
            archiver.extract(archive, dest); // WARNING OK
            System.out.println("unzip");
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Check if one of the sub-project of a complete project
     * (root and his children) is in the Database.
     *
     * @param jsonFile String
     * @return boolean
     */
    public static boolean isProjectInDb(String jsonFile) {
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
                        int id = ProjectDB.getProjectID(project.getTitle());
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
     * @return boolean
     */
    public static boolean deleteFile(final String fileName) {
        try {
            File myObj = new File(fileName);
            if (myObj.delete()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Raise an alert after an import of export.
     *
     * @param choice  String
     * @param succeed boolean
     */
    @FXML
    public static void alertExportImport(String choice, boolean succeed) {
        //TODO à mettre dans le main controller
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

    /**
     * Write a project and his children in a json file.
     *
     * @param project Project
     * @param fw      FileWriter
     */
    public void saveProjectAndChildsJson(Project project, FileWriter fw) throws IOException {
        try {
            final int ID = project.getId();
            saveProjectJson(project, ProjectDB.getTasks(ID), ProjectDB.getTags(ID), fw);
            for (Integer subProject : ProjectDB.getSubProjects(ID)) {
                fw.write(",\n");
                saveProjectAndChildsJson(ProjectDB.getProject(subProject), fw);
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
     * @param jsonFile    String
     * @return boolean
     */
    public boolean exportProject(Project project, String archivePath, String jsonFile) {
        try {
            final int ID = project.getId();
            deleteFile(jsonFile);
            FileWriter fw = new FileWriter(jsonFile, true);
            fw.write("[\n");
            saveProjectAndChildsJson(project, fw);
            fw.write("\n]");
            fw.close();
            zip(project.getTitle(), jsonFile, archivePath);
            deleteFile(jsonFile);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}