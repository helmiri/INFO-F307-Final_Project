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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The google drive API implementation
 */
public class GoogleDriveAPI {
    private final Drive service;

    /**
     * Initializes the connection with the GoogleCloud account.
     *
     * @param credentials    The user's credentials
     * @param HTTP_TRANSPORT Thread-safe HTTP low-level transport based on the java.net package.
     */
    public GoogleDriveAPI(Credential credentials, NetHttpTransport HTTP_TRANSPORT) {
        // Build a new authorized API client service.
        Logger.getLogger("").setLevel(Level.SEVERE);
        service = new Drive.Builder(HTTP_TRANSPORT, JacksonFactory.getDefaultInstance(), credentials)
                .setApplicationName("I(Should)PlanAll")
                .build();
    }

    /**
     * Returns the files contained in the GCloud account.
     *
     * @return A list of the files MetaDatas
     * @throws IOException on error accessing the files
     */
    public List<File> getFiles() throws IOException {
        FileList result = service.files().list().setFields("nextPageToken, files(id, name, appProperties)").execute();
        return result.getFiles();
    }

    /**
     * Downloads a file from a user's cloud storage
     *
     * @param localFilePath Path of the folder we want to download our file in.
     * @param fileID        Id of the file in the Gcloud account we want to download.
     * @throws IOException On error saving the file
     */
    public void downloadFile(String localFilePath, String fileID) throws IOException {
        OutputStream outputStream = new FileOutputStream(localFilePath);
        service.files().get(fileID).executeMediaAndDownloadTo(outputStream);
        outputStream.close();
    }

    /**
     * Uploading a file to the cloud services.
     *
     * @param localFilePath Path to the file we want to upload to the Gcloud account.
     * @param cloudFilePath Path to the folder in the Gcloud account we want to upload the file in.
     * @throws IOException On file access error
     */
    public void uploadFile(String localFilePath, String cloudFilePath) throws IOException {
        File fileMetadata = new File();
        java.io.File localFile = new java.io.File(localFilePath);
        FileContent mediaContent = new FileContent("tar/gz", localFile);
        setHashCode(cloudFilePath, fileMetadata, localFile);
        service.files().create(fileMetadata, mediaContent).setFields("id, appProperties").execute();
    }

    /**
     * Deletes an existing file and replaces it with a new one
     *
     * @param localFilePath Path to the file to be uploaded
     * @param cloudFilePath Path to the file to be updated
     * @param file          The cloud file's metadata
     * @throws IOException On error updating the file
     */
    public void updateFile(String localFilePath, String cloudFilePath, File file) throws IOException {
        uploadFile(localFilePath, cloudFilePath);
        service.files().delete(file.getId()).execute();
    }

    /**
     * Adds the hash code to the metadata of the file so it can be retrieved on download because the Google drive
     * API doesn't offer a way to easily retrieve it
     *
     * @param cloudFilePath Path to the file contained in the cloud
     * @param fileMetadata  The metadata file
     * @param localFile     The local file
     * @throws IOException On error reading the file
     */
    private void setHashCode(String cloudFilePath, File fileMetadata, java.io.File localFile) throws IOException {
        Map<String, String> properties = new HashMap<>();
        fileMetadata.setName(cloudFilePath);
        properties.put("hash", getHash(localFile));
        fileMetadata.setAppProperties(properties);
    }

    /**
     * Returns the hash code of a file.
     *
     * @param file The file that we want to get the hash content from.
     * @return The hash code
     * @throws IOException On error reading the file
     */
    @SuppressWarnings("UnstableApiUsage")
    public String getHash(java.io.File file) throws IOException {
        HashCode hash = com.google.common.io.Files.hash(file, Hashing.sha256());
        return hash.toString().toUpperCase();
    }
}
