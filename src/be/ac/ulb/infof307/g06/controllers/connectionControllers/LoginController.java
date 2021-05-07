package be.ac.ulb.infof307.g06.controllers.connectionControllers;

import be.ac.ulb.infof307.g06.models.AlertWindow;
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

    //--------------- METHODS ----------------
    public LoginController(Stage stage, Listener listener) {
        this.stage = stage;
        this.listener = listener;
    }

    /**
     * Shows the login screen
     */
    public void show() {
        FXMLLoader loader = new FXMLLoader(LoginViewController.class.getResource("LoginView.fxml"));
        AnchorPane mainLayout;
        try {
            mainLayout = loader.load();
        } catch (IOException e) {
            new AlertWindow("Error", "Could not load the window", e.getMessage());
            return;
        }
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
        stage.setTitle("I(Should)PlanAll");

        stage.setResizable(true);
        stage.setHeight(465);
        stage.setWidth(715);
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }

    //--------------- LISTENER ----------------
    // Hand over control to the ConnectionHandler
    public interface Listener {
        void onLogin(String username, String password);
        void onSignup();
    }
}
