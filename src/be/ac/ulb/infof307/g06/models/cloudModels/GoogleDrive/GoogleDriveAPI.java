package be.ac.ulb.infof307.g06.models.cloudModels.GoogleDrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GoogleDriveAPI {
    private final Drive service;

    public GoogleDriveAPI(Credential credentials, NetHttpTransport HTTP_TRANSPORT) throws GeneralSecurityException, IOException {
        // Build a new authorized API client service.
        Logger.getLogger("").setLevel(Level.SEVERE);
        service = new Drive.Builder(HTTP_TRANSPORT, JacksonFactory.getDefaultInstance(), credentials)
                .setApplicationName("I(Should)PlanAll")
                .build();
    }

    public List<File> getFiles() throws IOException {
        FileList result = service.files().list().setFields("nextPageToken, files(id, name, appProperties)").execute();
        return result.getFiles();
    }

    public void downloadFile(String localFilePath, String fileID) throws IOException {
        OutputStream outputStream = new FileOutputStream(localFilePath);
        service.files().get(fileID).executeMediaAndDownloadTo(outputStream);
        outputStream.close();
    }


    public void uploadFile(String localFilePath, String cloudFilePath) throws IOException {
        File fileMetadata = new File();
        java.io.File localFile = new java.io.File(localFilePath);
        FileContent mediaContent = new FileContent("tar/gz", localFile);
        setHashCode(cloudFilePath, fileMetadata, localFile);
        service.files().create(fileMetadata, mediaContent).setFields("id, appProperties").execute();
    }

    private void setHashCode(String cloudFilePath, File fileMetadata, java.io.File localFile) throws IOException {
        Map<String, String> properties = new HashMap<>();
        fileMetadata.setName(cloudFilePath);
        properties.put("hash", getHash(localFile));
        fileMetadata.setAppProperties(properties);
    }

    @SuppressWarnings("UnstableApiUsage")
    public String getHash(java.io.File file) throws IOException {
        HashCode hash = com.google.common.io.Files.hash(file, Hashing.sha256());
        return hash.toString().toUpperCase();
    }

    public void updateFile(String fileID, String localPath) {

    }
}
