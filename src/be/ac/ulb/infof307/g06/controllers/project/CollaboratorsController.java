package be.ac.ulb.infof307.g06.controllers.project;

import be.ac.ulb.infof307.g06.controllers.MainController;
import be.ac.ulb.infof307.g06.models.Global;
import be.ac.ulb.infof307.g06.models.Project;
import be.ac.ulb.infof307.g06.models.Task;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CollaboratorsController {
    public CollaboratorsController() {
    }

    /**
     * @param collaborators ObservableList<String>
     * @param selectedTask  Task
     */
    public void assignCollaborators(ObservableList<String> collaborators, Task selectedTask) {
        try {
            for (String collaborator : collaborators) {
                ProjectDB.addTaskCollaborator(selectedTask.getId(), Integer.parseInt(UserDB.getUserInfo(collaborator).get("id")));
            }
        } catch (SQLException e) {
            MainController.alertWindow(Alert.AlertType.ERROR, "Error", "Error in assigning collaborator to task: \n" + e);
        }
    }

    /**
     * Returns collaborators' id of a project.
     *
     * @param project TreeItem<Project>
     * @return ObservableList<String>
     */
    public ObservableList<String> getCollaborators(TreeItem<Project> project) {
        List<String> collaboratorsList = new ArrayList<String>();
        try {
            List<Integer> collaborators_id = ProjectDB.getCollaborators(project.getValue().getId());
            for (Integer integer : collaborators_id) {
                collaboratorsList.add(UserDB.getUserInfo(integer).get("uName"));
            }
        } catch (SQLException e) {
            MainController.alertWindow(Alert.AlertType.ERROR, "Error", "Error in fetching collaborators: \n" + e);
        }
        return FXCollections.observableArrayList(collaboratorsList);
    }

    /**
     * Adds a collaborator to a project and in the database.
     *
     * @param username String
     * @param project  int
     * @return Boolean
     */


    public boolean addCollaborator(String username, int project) {
        try {
            if (!UserDB.userExists(username)) {
                return false;
            }
            int receiverID = Integer.parseInt(UserDB.getUserInfo(username).get("id"));
            if (ProjectDB.getCollaborators(project).contains(receiverID)) {
                return true;
            }
            UserDB.sendInvitation(project, Global.userID, receiverID);
            UserDB.updateDiskUsage(ProjectDB.getSizeOnDisk());
            //MainController.alertWindow(Alert.AlertType.INFORMATION,"Alert","Invitation sent to " + collaboratorsName.getText() + ".");
        } catch (SQLException e) {
            //MainController.alertWindow(Alert.AlertType.ERROR, "Error", "Could not add the collaborator: \n" + e);
        }
        //MainController.alertWindow(Alert.AlertType.INFORMATION,"Alert","User " + collaboratorsName.getText() + " doesn't exist.");
        return false;
    }
}