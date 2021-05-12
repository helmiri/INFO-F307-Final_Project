package be.ac.ulb.infof307.g06;

import be.ac.ulb.infof307.g06.controllers.connectionControllers.ConnectionHandler;
import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.models.encryption.EncryptedFile;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * The application launcher
 */
public class Main extends Application {
    /**
     * Launches the main application
     *
     * @param args String[] Arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * Starts the main stage
     *
     * @param stage Stage
     */
    @Override
    public void start(Stage stage) {
        // Decrypt the database if it exists
        new File("data").mkdir();
        String DB_PATH = "data/Database.db";
        String DECRYPTED_DB_PATH = "data/Database_Decrypted.db";

        try {
            if (new File(DB_PATH).exists()) {
                EncryptedFile file = new EncryptedFile("QwAtb5wcgChC2u3@f,]/bnd\"", DB_PATH);
                file.decryptFile(DECRYPTED_DB_PATH);
            }
            ConnectionHandler handler;
            handler = new ConnectionHandler(stage, DECRYPTED_DB_PATH, DB_PATH);
            handler.showLogin();
        } catch (DatabaseException e) {
            e.show();
        } catch (IOException e) {
            new DatabaseException(e, "Could not load database").show();
        }
    }
}
