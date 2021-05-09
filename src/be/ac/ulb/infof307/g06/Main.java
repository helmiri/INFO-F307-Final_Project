package be.ac.ulb.infof307.g06;

import be.ac.ulb.infof307.g06.controllers.connectionControllers.ConnectionHandler;
import be.ac.ulb.infof307.g06.exceptions.DatabaseException;
import be.ac.ulb.infof307.g06.models.database.ProjectDB;
import be.ac.ulb.infof307.g06.models.database.UserDB;
import be.ac.ulb.infof307.g06.models.encryption.EncryptedFile;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.sql.SQLException;

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
    public void start(Stage stage) throws IllegalBlockSizeException, IOException, BadPaddingException, InvalidKeyException {
        // Set main stage
        String DB_PATH = "Database.db";
        String DECRYPTED_DB_PATH = "Database_Decrypted.db";
        if (new File(DB_PATH).exists()) {
            EncryptedFile file = new EncryptedFile("QwAtb5wcgChC2u3@f,]/bnd\"", DB_PATH);
            file.decryptFile(DECRYPTED_DB_PATH);
        }
        try {
            UserDB userDB = new UserDB(DECRYPTED_DB_PATH);
            ProjectDB projectDB = new ProjectDB(DECRYPTED_DB_PATH);
            boolean isFirstBoot = userDB.isFirstBoot();
            ConnectionHandler handler = new ConnectionHandler(userDB, projectDB, stage, isFirstBoot);
            handler.showLogin();
        } catch (SQLException | ClassNotFoundException e) {
            new DatabaseException(e).show();
        }
    }
}
