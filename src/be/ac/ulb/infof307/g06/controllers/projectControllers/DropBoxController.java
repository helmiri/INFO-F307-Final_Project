package be.ac.ulb.infof307.g06.controllers.projectControllers;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import be.ac.ulb.infof307.g06.models.cloudModels.DropBox.DropBoxAPI;
import com.dropbox.core.DbxException;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Communicates with the DropBox api
 */
public class DropBoxController {
    private final DropBoxAPI dbxClient;
    private List<Metadata> dropBoxFiles;

    public DropBoxController(DbxCredential credential) throws DbxException {
        if (credential == null) {
            throw new DbxException("Credentials not found. Make sure that you granted access in settings");
        } else {
            dbxClient = new DropBoxAPI(credential.getAccessToken(), credential.getAppKey());
        }
    }

    /**
     * Downloads files from the user's DropBox account
     *
     * @param cloudPaths The list of paths to the target files in the cloud storage
     * @param localPath  The directory where to save the files on disk
     * @return Paths to the downloaded files
     */
    public List<String> downloadFiles(List<String> cloudPaths, String localPath) throws IOException, NoSuchAlgorithmException, DbxException {
        List<Metadata> fileMetas = getFileMetaDatas(cloudPaths);
        List<String> downloadedFilePaths = new ArrayList<>();
        for (Metadata fileMeta : fileMetas) {
            String downloadedFile = getPathIfNotExists(localPath, fileMeta);
            if (downloadedFile == null) {
                new AlertWindow("Identical files", "The file " + fileMeta.getName() + " already exists").showInformationWindow();
                continue;
            }
            dbxClient.downloadFile(downloadedFile, fileMeta.getPathDisplay());
            downloadedFilePaths.add(downloadedFile);
        }
        return downloadedFilePaths;
    }


    /**
     * Checks whether the file to be downloaded already exists and returns the path where the file will be stored
     *
     * @param localPath Destination folder
     * @param fileMeta  The file's metadata
     * @return The path where the file will be saved, null if it already exists
     */
    private String getPathIfNotExists(String localPath, Metadata fileMeta) {
        String downloadedFile = localPath + "/" + fileMeta.getName();
        if (new File(downloadedFile).exists() && isFileIdentical(downloadedFile, (FileMetadata) fileMeta)) {
            return null;
        }
        return downloadedFile;
    }

    /**
     * Retrieves the metadata of the selected files
     *
     * @param cloudPaths The list of paths to the target files in the cloud storage
     * @return A list of the target files' metadata
     */
    private List<Metadata> getFileMetaDatas(List<String> cloudPaths) {
        List<Metadata> fileMetas = new ArrayList<>();
        for (String cloudPath : cloudPaths) {
            for (Metadata metadata : dropBoxFiles) {
                if (metadata.getPathDisplay().equals(cloudPath)) {
                    fileMetas.add(metadata);
                    break;
                }
            }
        }
        return fileMetas;
    }

    /**
     * Compares files using the Drop Box hashing algorithm
     *
     * @param localPath Path to the local file to be compared
     * @param fileMeta  Metadata of the file in the cloud storage
     * @return true if identical, false otherwise
     */
    private boolean isFileIdentical(String localPath, FileMetadata fileMeta) {
        String hash = dbxClient.getHash(localPath);
        if (hash == null) {
            return false;
        }
        return fileMeta.getContentHash().equals(hash);
    }

    /**
     * Extract the paths of the files from the DropBox metadata
     *
     * @return A list containing the paths of the files
     */
    public List<String> fetchFiles() throws DbxException {
        dropBoxFiles = dbxClient.getFiles();
        List<String> res = new ArrayList<>();
        for (Metadata entry : dropBoxFiles) {
            res.add(entry.getPathDisplay());
        }
        return res;
    }

    /**
     * Uploads a file
     *
     * @param localFilePath file path
     * @param fileName      file name
     * @throws IOException  exception
     * @throws DbxException exception
     */
    public void uploadFile(String localFilePath, String fileName) throws IOException, DbxException {
        dbxClient.uploadFile(localFilePath, fileName);
    }
}
