package be.ac.ulb.infof307.g06;

import be.ac.ulb.infof307.g06.controllers.connectionControllers.LoginController;
import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.exceptions.WindowLoadException;
import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.database.DatabaseConnection;
import be.ac.ulb.infof307.g06.models.encryption.EncryptedFile;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * The application launcher
 */
public class Main extends Application {

    private static final String dbPath = "data/Database.db";
    private static final String decryptedDBPath = "data/Database_Decrypted.db";
    private static final String encryptionPassword = "QwAtb5wcgChC2u3@f,]/bnd\"";

    /**
     * Launches the main application
     *
     * @param args String[] Arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
        encryptDatabase();
    }

    /**
     * Encrypts the database after the application is closed
     */
    private static void encryptDatabase() {
        EncryptedFile databaseFile = new EncryptedFile(encryptionPassword, decryptedDBPath);
        try {
            databaseFile.encryptFile(dbPath);
            new File(decryptedDBPath).delete();
        } catch (IOException ioException) {
            new AlertWindow("Error", "Could not commit the database", ioException.getMessage()).showErrorWindow();
            System.exit(-1);
        }
    }

    /**
     * Starts the main stage
     *
     * @param stage Stage
     */
    @Override
    public void start(Stage stage) {
        //Set stage
        stage.setTitle("I(Should)PlanAll");
        stage.centerOnScreen();
        stage.setResizable(false);

        // Decrypt the database if it exists
        new File("data").mkdir();
        try {
            if (!new File(decryptedDBPath).exists() && new File(dbPath).exists()) { // Application not running. Decrypt before launching
                EncryptedFile file = new EncryptedFile(encryptionPassword, dbPath);
                file.decryptFile(decryptedDBPath);
            }
            DatabaseConnection.connect(decryptedDBPath);
            LoginController controller = new LoginController(stage);
            controller.show();
        } catch (DatabaseException e) {
            e.show();
        } catch (WindowLoadException e) {
            e.show();
        } catch (IOException e) {
            new AlertWindow("Error", "Could not decrypt database").showErrorWindow();
        } catch (SQLException | ClassNotFoundException error) {
            new AlertWindow("Error", "Could not connect to the database").showErrorWindow();
        }
    }
}
