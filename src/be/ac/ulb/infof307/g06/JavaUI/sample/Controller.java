package be.ac.ulb.infof307.g06.JavaUI.sample;


import be.ac.ulb.infof307.g06.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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
    private TableView<Project> projectsTable;

    @FXML
    private Button addBtn;

    @FXML
    private Button loadBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private TableColumn<Project,String> projectsColumn;

    @FXML
    private TextField nameProject;

    @FXML
    private TextField descriptionProject;

    @FXML
    private DatePicker dateProject;

    @FXML
    private TextField tagsProject;

    @FXML
    private TableColumn<Project,String> descriptionColumn;

    @FXML
    private TableColumn<Project,String> tagsColumn;

    // --------------------- METHODES -------------------------

    @FXML
    private void buttonEvents(ActionEvent event) throws Exception {
        //TODO: Rename cette méthode en LogInEvents?

        if( event.getSource()== connectionBtn){ logInConditions(); }
        else if( event.getSource()== registerBtn   )  { Main.showRegisterScene()  ;}
        else if( event.getSource()== SignUpBtn     )  { signUpConditions()        ;}
        else if( event.getSource()== goToConnection)  { Main.showConnectionScene();}

        else if( event.getSource() == loadBtn)        {
            projectsColumn.setCellValueFactory(new PropertyValueFactory<Project, String>("name"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<Project, String>("description"));
            tagsColumn.setCellValueFactory(new PropertyValueFactory<Project, String>("tags"));
            loadProjects();}
        else if( event.getSource()== addBtn)          {
            projectsColumn.setCellValueFactory(new PropertyValueFactory<Project, String>("name"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<Project, String>("description"));
            tagsColumn.setCellValueFactory(new PropertyValueFactory<Project, String>("tags"));
            addProject(); }

        else if( event.getSource()== deleteBtn){deleteProject();}

        else if( event.getSource() == LogOutBtn) { Main.showConnectionScene();}
        else if( event.getSource() == ProjectAccessBtn) {Main.showStatisticsScene();
        }
        else if( event.getSource() == goToStats) {Main.showStatisticsScene();}
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


    private void loadProjects(){
        projectsTable.setItems(getprojects());
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
            Main.ShowMainMenu();
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
            //setData(firstName,lastName,email,username,passwd)
            Main.showConditionsScene();
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
        Pattern p = Pattern.compile("^[^±!@£$%^&*+§¡€#¢§¶•ª º«\\/<>?:;|=.,]{8,16}$");
        Matcher m = p.matcher(field.getText());
        return m.matches();
    }

    @FXML
    private boolean validatePassword(TextField field){
        Pattern p = Pattern.compile("((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#_$%!]).{6,16})");
        Matcher m = p.matcher(field.getText());
        return m.matches();
    }

    public ObservableList<Project> getprojects() {
        ObservableList<Project> projects = FXCollections.observableArrayList();
        projects.add(new Project("projet1", "test", "cool"));
        return projects;
    }

    @FXML
    private void addProject(){
        Project project = new Project(nameProject.getText(), descriptionProject.getText(),  tagsProject.getText());
        //addToDatabase()
        projectsTable.getItems().add(project);
    }

    @FXML
    private void deleteProject() {
        projectsTable.getItems().removeAll(projectsTable.getSelectionModel().getSelectedItem());
        /*
        Project project = projectsTable.getSelectionModel().getSelectedItem();
        project.getName()
         */
        //delFromData()
    }
}