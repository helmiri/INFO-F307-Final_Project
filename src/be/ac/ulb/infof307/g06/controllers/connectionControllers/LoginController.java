package be.ac.ulb.infof307.g06.controllers.connectionControllers;

import be.ac.ulb.infof307.g06.views.connectionViews.LoginViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    //--------------- ATTRIBUTES ----------------
    private final Stage stage;
    private final Listener listener;
    private AnchorPane mainLayout;

    //--------------- METHODS ----------------
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
        mainLayout = loader.load();
        Scene scene = new Scene(mainLayout);
        stage.setScene(scene);
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

        stage.setResizable(true);
        stage.setHeight(465);
        stage.setWidth(715);
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }

    //--------------- LISTENER ----------------
    public interface Listener {
        void onLogin(String username, String password);
        void onSignup();
    }
}
