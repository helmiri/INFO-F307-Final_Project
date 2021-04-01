package be.ac.ulb.infof307.g06.controllers.connection;

import be.ac.ulb.infof307.g06.controllers.MainController;
import be.ac.ulb.infof307.g06.models.Global;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.views.ConnectionsViews.LoginViewController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    private final Stage stage;
    private final Listener listener;

    public LoginController(Stage stage, Listener listener) {
        this.stage = stage;
        this.listener = listener;
    }

    /**
     * Sets the loader to show the Log In scene.
     */
    public void show() throws IOException {
        // Load the fxml
        FXMLLoader loader = new FXMLLoader(LoginViewController.class.getResource("LoginView.fxml"));
        loader.load();
        LoginViewController controller = loader.getController();
        controller.setListener(new LoginViewController.ViewListener() {
            @Override
            public void login(String username, String password) {
                listener.onLogin(username, password);
            }

            @Override
            public void signup() {
                listener.onSignup();
            }
        });
        stage.setTitle("Projet génie logiciel");
        // Disconnect user before closing
        stage.setOnCloseRequest(e -> {
            try {
                if (Global.userID != 0) UserDB.disconnectUser();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            Platform.exit();
            System.exit(0);
        });
        stage.setResizable(true);
        stage.setHeight(465);
        stage.setWidth(715);
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }


    public interface Listener {
        void onLogin(String username, String password);

        void onSignup();
    }
}
