package be.ac.ulb.infof307.g06.JavaUI.sample;

import be.ac.ulb.infof307.g06.database.*;
import be.ac.ulb.infof307.g06.database.ProjectDB.Project;

import be.ac.ulb.infof307.g06.JavaUI.sample.Global;
import be.ac.ulb.infof307.g06.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;


import javax.swing.*;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class Controller {
    // --------------------- ATTRIBUTS -------------------------

    // ---------CONNECTION---------
    @FXML
    private TextField logInUsernameField;

    @FXML
    private PasswordField logInPasswordField;

    @FXML
    private Button connectionBtn;

    @FXML
    private Button registerBtn;

    // ---------SIGN UP---------
    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField signUpUsernameField;

    @FXML
    private TextField signUpPasswordField;

    @FXML
    private TextField passwordConfirmationField;

    @FXML
    private Button SignUpBtn;

    @FXML
    private Button goToConnection;

    // ---------TERMS AND CONDITIONS---------

    @FXML
    private Button acceptConditionsBtn;

    @FXML
    private CheckBox acceptConditionsBox;

    // ---------STATISTICS---------

    @FXML
    private Button visualiseBtn;

    @FXML
    private Button BackToMainMenu;

    // --------- MAIN MENU ------------

    @FXML
    private Button ProfileAccessBtn;

    @FXML
    private Button ProjectAccessBtn;    //ATTENTION METTRE DES MINUSCULES AU DEBUT DES LES NOMS D'ATTRIBUTS SVPPP (Aline)

    @FXML
    private Button CalendarAccessBtn;

    @FXML
    private Button SettingsAccessBtn;

    @FXML
    private Button HelpBtn;

    @FXML
    private Button LogOutBtn;

    @FXML
    private Button goToStats;

    // ---------PROJECTS MENu------

    @FXML
    private Button addBtn;

    @FXML
    private Button loadBtn;

    @FXML
    private TextField nameProject;

    @FXML
    private TextField descriptionProject;

    @FXML
    private DatePicker dateProject;

    @FXML
    private TextField tagsProject;

    @FXML
    private TextField parentProject;

    @FXML
    private TableView<Project> projectsTable;

    @FXML
    private TableView<Project> subProjectsTable;

    @FXML
    private TableColumn<Project,String> projectsColumn;

    @FXML
    private TableColumn<Project,String> descriptionColumn;

    @FXML
    private TableColumn<Project,String> tagsColumn;

    @FXML
    private TableColumn<Project, String> dateColumn;

    @FXML
    private TableColumn<Project, String> subprojectsColumn;

    @FXML
    private TableColumn<Project, String> subdescriptionColumn;

    @FXML
    private TableColumn<Project, String> subtagsColumn;

    @FXML
    private TableColumn<Project, String> subdateColumn;
    // ----------------TASK---------------
    @FXML
    private TextField taskName;

    @FXML
    private Button addTask;

    @FXML
    private TextArea taskDescription;

    // ----------------DATABASE---------------

    private ProjectDB projectsDB = new ProjectDB("Database.db");
    private UserDB userDB = new UserDB("Database.db");
    public Controller() throws SQLException, ClassNotFoundException {
    }

    // --------------------- METHODES -------------------------

    @FXML
    private void buttonEvents(ActionEvent event) throws Exception {
        //TODO: Rename cette méthode en LogInEvents?

        if( event.getSource()== connectionBtn      ){ logInConditions(); }
        else if( event.getSource()== registerBtn   )  { Main.showRegisterScene()  ;}
        else if( event.getSource()== SignUpBtn     )  { signUpConditions()        ;}
        else if( event.getSource()== goToConnection)  { Main.showConnectionScene();}

        else if( event.getSource() == loadBtn)        {
            projectsColumn.setCellValueFactory(new PropertyValueFactory<Project, String>("title"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<Project, String>("description"));
            tagsColumn.setCellValueFactory(new PropertyValueFactory<Project, String>("tags"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<Project, String>("date"));
            loadProjects();}



        else if( event.getSource()== addBtn)          {
            projectsColumn.setCellValueFactory(new PropertyValueFactory<Project, String>("title"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<Project, String>("description"));
            tagsColumn.setCellValueFactory(new PropertyValueFactory<Project, String>("tags"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<Project, String>("date"));
            addProject(); }

        else if( event.getSource() == LogOutBtn) { Main.showConnectionScene();}
        else if( event.getSource() == ProjectAccessBtn) { Main.showStatisticsScene(); }
        else if( event.getSource() == goToStats) { Main.showStatisticsScene(); }
        else if( event.getSource() == BackToMainMenu) {Main.ShowMainMenu();}
        else if( event.getSource()== acceptConditionsBtn){
            if(acceptConditionsBox.isSelected()){
                Main.ShowMainMenu();
                Main.closeConditionsStage();
            }
        }
        else if(event.getSource() ==visualiseBtn){
            Main.showVisualScene();
        }
    }



    private void loadProjects() throws SQLException{
        List<Integer> projectsArray = projectsDB.getUserProjects(Global.userID);
        projectsTable.setItems(getprojects(projectsArray));
    }

    @FXML
    private void logInConditions() throws Exception{
        //TODO: remplacer dans le if pour le mot de passe par validatePassword
        /*if(        validateUsername(logInUsernameField)
                && logInPasswordField.getText().length() <= 16
                && logInPasswordField.getText().length() >=  8){
            String passwd = logInPasswordField.getText();
            String user = logInUsernameField.getText();
            //checkData(user,passwd)}
            */
        String passwd = logInPasswordField.getText();
        String user = logInUsernameField.getText();

        //DB
        Global.userID=userDB.validateData(user,passwd);
        if(Global.userID!=0){ Main.ShowMainMenu(); }
        //else alert box
    }

    @FXML
    private void signUpConditions() throws Exception{
        if(     validateFirstName()
                && validateLastName()
                && validateUsername(signUpUsernameField)
                && validatePassword(signUpPasswordField)
                && validateEmail()
                && passwordConfirmationField.getText().equals(signUpPasswordField.getText())){
            String firstName= firstNameField.getText();
            String lastName= lastNameField.getText();
            String email= emailField.getText();
            String username= signUpUsernameField.getText();
            String passwd= signUpPasswordField.getText();
            //DB
            if (!(userDB.userExists(username))){
                Global.userID=userDB.addUser(firstName,lastName,username,email,passwd);
                Main.showConditionsScene();
            }
            //else alert box
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wrong enters");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinWidth(900);
            alert.setContentText("One of the sign up options is wrong:\n" +
                    "   - The last name and the first name must not contain any special characters\n" +
                    "   - The email Address must be a valid one: 'an.example@gmail.com'\n" +
                    "   - The username must not contain any special characters or spaces (8 to 16 chars)\n" +
                    "   - The password must have at least one lowercase character, one uppercase character and one special character from '@#_$%!' (6 to 16 chars)\n");
            alert.showAndWait();
        }
    }

    @FXML
    private boolean validateLastName(){
        Pattern p = Pattern.compile("^[^±!@£$%^&*_+§¡€#¢§¶•ªº«\\/<>?:;|=.,]{1,64}$");
        Matcher m = p.matcher(lastNameField.getText());
        return m.matches();
    }

    @FXML
    private boolean validateFirstName(){
        Pattern p = Pattern.compile("^[^±!@£$%^&*_+§¡€#¢§¶•ªº«\\/<>?:;|=.,]{1,64}$");
        Matcher m = p.matcher(firstNameField.getText());
        return m.matches();
    }

    @FXML
    private boolean validateEmail(){
        Pattern p = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
        Matcher m = p.matcher(emailField.getText());
        return m.matches();
    }

    @FXML
    private boolean validateUsername(TextField field){
        Pattern p = Pattern.compile("^[^±!@£$%^&*+§¡€#¢§¶•ª º«\\/<>?:;|=.,]{1,16}$");
        Matcher m = p.matcher(field.getText());
        return m.matches();
    }

    @FXML
    private boolean validatePassword(TextField field){
        Pattern p = Pattern.compile("((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#_$%!]).{6,16})");
        Matcher m = p.matcher(field.getText());
        return m.matches();
    }

    public ObservableList<Project> getprojects(List<Integer> projects) throws SQLException{
        ObservableList<Project> projectsList = FXCollections.observableArrayList();
        for(Integer project : projects){

            projectsList.add(projectsDB.getProject(project));
        }

        return projectsList;
    }

    @FXML
    private void addProject() throws SQLException{

        int parentID=0;
        //TODO:Créer une condition si le parent existe pas
        if (parentProject.getText() == "" || projectsDB.getProjectID(parentProject.getText())!=0){
            if(parentProject.getText() != ""){ parentID= projectsDB.getProjectID(parentProject.getText());}

            int newProjectID = projectsDB.createProject(nameProject.getText(),descriptionProject.getText(),tagsProject.getText(),dateProject.getValue().toEpochDay(),parentID);
            projectsDB.addCollaborator(newProjectID, Global.userID);
            projectsTable.getItems().add(projectsDB.getProject(newProjectID));
        }
    }

    @FXML
    private void deleteProject(ActionEvent event) throws SQLException{
        //régler le prob si aucun item est sélectionné
        //TODO: delete la 2e table
        String projectName = projectsTable.getSelectionModel().getSelectedItem().getTitle();
        int projectID = projectsDB.getProjectID(projectName);
        projectsDB.deleteProject(projectID);
        projectsTable.getItems().removeAll(projectsTable.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void showSubProjects(ActionEvent event) throws SQLException{
        subprojectsColumn.setCellValueFactory(new PropertyValueFactory<Project, String>("title"));
        subdescriptionColumn.setCellValueFactory(new PropertyValueFactory<Project, String>("description"));
        subtagsColumn.setCellValueFactory(new PropertyValueFactory<Project, String>("tags"));
        subdateColumn.setCellValueFactory(new PropertyValueFactory<Project, String>("date"));

        //faire de la récursivité mais po le temps
        String projectName = projectsTable.getSelectionModel().getSelectedItem().getTitle();
        int projectID=projectsDB.getProjectID(projectName);
        List<Integer> subProjects = projectsDB.getSubProjects(projectID);
        subProjectsTable.setItems(getprojects(subProjects));


    }

    @FXML
    private void showDetailsProject(ActionEvent event) throws SQLException{
        String projectName = projectsTable.getSelectionModel().getSelectedItem().getTitle();
        int projectID=projectsDB.getProjectID(projectName);
        Project showProject= projectsDB.getProject(projectID);
        String description= showProject.getDescription();
        String tags=showProject.getTags();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Details");
        alert.setHeaderText(null);
        alert.getDialogPane().setMinWidth(600);
        alert.setResizable(false);
        alert.setContentText("DESCRIPTION:\n" +description+"\n\n"+"TAGS:\n"+tags);
        alert.showAndWait();
    }

    /*
    @FXML
    private void addTask(ActionEvent event) throws Exception, SQLException {
        String taskDes="";
        if(event.getSource() == addTask){
            taskDes= taskDescription.getText();
            Main.closeTaskStage();}

        else{Main.showTaskScene(); }

        String projectName = projectsTable.getSelectionModel().getSelectedItem().getTitle();
        int projectID=projectsDB.getProjectID(projectName);
        //String taskNm = taskName.getText();
        int task = projectsDB.createTask(taskDes,projectID);
    }*/

}

