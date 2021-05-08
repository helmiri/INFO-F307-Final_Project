package be.ac.ulb.infof307.g06.controllers.projectControllers;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.cloudModels.GoogleDrive.GoogleDriveAPI;
import be.ac.ulb.infof307.g06.models.cloudModels.GoogleDrive.GoogleDriveAuthorization;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GoogleDriveController {
    private final GoogleDriveAPI gDriveClient;
    private List<File> gDriveFiles;

    public GoogleDriveController(String userName) throws IOException, GeneralSecurityException {
        GoogleDriveAuthorization authorization = new GoogleDriveAuthorization(userName);
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        gDriveClient = new GoogleDriveAPI(authorization.getCredentials(httpTransport), httpTransport);
    }

    /**
     * Downloads a file from the user's Google Drive account
     *
     * @param cloudPath The list of paths to the target files in the cloud storage
     * @param localPath The directory where to save the files on disk
     */
    public List<String> downloadFiles(List<String> cloudPath, String localPath) throws IOException {
        List<String> downloadedFilePaths = new ArrayList<>();
        List<File> fileMetas = getFileMetaDatas(cloudPath);
        for (File fileMeta : fileMetas) {
            String localFilePath = getPathIfNotExists(localPath, fileMeta);
            if (localFilePath == null) {
                new AlertWindow("Identical files", "The file " + fileMeta.getName() + " already exists").showInformationWindow();
                continue;
            }
            gDriveClient.downloadFile(localFilePath, Objects.requireNonNull(fileMeta).getId());
            downloadedFilePaths.add(localFilePath);
        }
        return downloadedFilePaths;
    }

    private String getPathIfNotExists(String localPath, File fileMeta) throws IOException {
        String localFilePath = localPath + "/" + fileMeta.getName();
        java.io.File localFile = new java.io.File(localFilePath);
        if (isFileIdentical(localFile, Objects.requireNonNull(fileMeta))) {
            return null;
        }
        return localFilePath;
    }

    /**
     * Compares files using the Google Drive hashing algorithm
     *
     * @param localFile Path to the local file to be compared
     * @param cloudFile Metadata of the file in the cloud storage
     * @return true if identical, false otherwise
     */
    private boolean isFileIdentical(java.io.File localFile, com.google.api.services.drive.model.File cloudFile) throws IOException {
        String localChecksum = gDriveClient.getHash(localFile);
        return localChecksum.equals(cloudFile.getAppProperties().get("hash"));
    }

    /**
     * Extract the names of the files from the Google Drive metadata
     *
     * @return A list containing the names of the files
     */
    public List<String> fetchFiles() throws IOException {
        gDriveFiles = gDriveClient.getFiles();
        List<String> res = new ArrayList<>();
        for (com.google.api.services.drive.model.File entry : gDriveFiles) {
            res.add(entry.getName());
        }
        return res;
    }

    /**
     * Retrieves the metadata of the selected files
     *
     * @param cloudPaths The list of paths to the target files in the cloud storage
     * @return A list of the target files' metadata
     */
    private List<File> getFileMetaDatas(List<String> cloudPaths) {
        List<File> files = new ArrayList<>();
        for (String cloudPath : cloudPaths) {
            for (File file : gDriveFiles) {
                if (file.getName().equals(cloudPath)) {
                    files.add(file);
                    break;
                }
            }
        }
        return files;
    }

    /**
     * Uploads a file to the drive account
     *
     * @param localFilePath The path to the file to be uploaded
     * @param fileName      The name of the file to be saved
     * @throws IOException On error accessing the file
     */
    public void uploadFile(String localFilePath, String fileName) throws IOException {
        gDriveFiles = gDriveClient.getFiles();
        List<String> fileNameList = new ArrayList<>();
        fileNameList.add(fileName);
        List<File> file = getFileMetaDatas(fileNameList);
        if (file.isEmpty()) {
            gDriveClient.uploadFile(localFilePath, fileName);
        } else {
            gDriveClient.updateFile(localFilePath, fileName, file.get(0));
        }
    }
}
