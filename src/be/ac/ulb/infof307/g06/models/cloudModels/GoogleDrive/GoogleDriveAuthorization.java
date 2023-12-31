package be.ac.ulb.infof307.g06.models.cloudModels.GoogleDrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * The class that establishes the connection with the google drive cloud services.
 */
public class GoogleDriveAuthorization {
    private final String userName;
    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";

    /**
     * constructor
     *
     * @param userName the Google drive username
     */
    public GoogleDriveAuthorization(String userName) {
        this.userName = userName;
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    public Credential getCredentials(NetHttpTransport HTTP_TRANSPORT) throws IOException {
        Logger.getLogger("").setLevel(Level.SEVERE);
        // Load client secrets.
        AuthorizationCodeInstalledApp authorization;
        try (InputStream in = GoogleDriveAuthorization.class.getResourceAsStream(CREDENTIALS_FILE_PATH)) {
            if (in == null) {
                throw new IOException("Resource not found: " + CREDENTIALS_FILE_PATH);
            }
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new InputStreamReader(in));

            // Build flow and trigger user authorization request.
            String TOKENS_DIRECTORY_PATH = "tokens";
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JacksonFactory.getDefaultInstance(), clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            authorization = new AuthorizationCodeInstalledApp(flow, receiver);
        }
        return authorization.authorize(userName);
    }
}