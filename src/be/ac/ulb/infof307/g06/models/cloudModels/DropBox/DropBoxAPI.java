package be.ac.ulb.infof307.g06.models.cloudModels.DropBox;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class DropBoxAPI {
    private final DbxClientV2 dboxClient;

    /**
     * Initializes the connection with the dropbox account.
     *
     * @param accessToken The user's access token
     * @param clientID    The appID
     */
    public DropBoxAPI(String accessToken, String clientID) {
        // Create Dropbox client
        DbxRequestConfig config = new DbxRequestConfig(clientID, "en_US");
        dboxClient = new DbxClientV2(config, accessToken);
    }

    /**
     * Returns the list of all the files contained in the dropbox account
     *
     * @return
     * @throws DbxException
     */
    public List<Metadata> getFiles() throws DbxException {
        ListFolderBuilder listFolderBuilder = dboxClient.files().listFolderBuilder("");
        ListFolderResult result = listFolderBuilder.withRecursive(true).start();
        List<Metadata> res = new ArrayList<>();

        while (true) {
            res.addAll(result.getEntries());

            if (!result.getHasMore()) {
                break;
            }

            result = dboxClient.files().listFolderContinue(result.getCursor());
        }

        return res;
    }

    /**
     * Uploading a file to the cloud services.
     *
     * @param localFilePath Path to the file we want to upload to the dropbox account
     * @param cloudFilePath Path to the folder in the dropbox account we want to upload the file in.
     * @throws IOException
     * @throws DbxException
     */

    public void uploadFile(String localFilePath, String cloudFilePath) throws IOException, DbxException {
        InputStream in = new FileInputStream(localFilePath);
        UploadBuilder uploadBuilder = dboxClient.files().uploadBuilder(cloudFilePath);
        uploadBuilder.withMode(WriteMode.OVERWRITE);
        FileMetadata metadata = uploadBuilder.uploadAndFinish(in);
        in.close();
    }

    /**
     * Downloads a file from a user's cloud storage
     *
     * @param localFilePath Path of the folder we want to download our file in.
     * @param cloudFilePath Path to the file in the dropbox account where the file we want to download is.
     * @throws IOException
     * @throws DbxException
     * @throws NoSuchAlgorithmException
     */
    public void downloadFile(String localFilePath, String cloudFilePath) throws IOException, DbxException, NoSuchAlgorithmException {
        OutputStream outputStream = new FileOutputStream(localFilePath);
        FileMetadata metadata = dboxClient.files()
                .downloadBuilder(cloudFilePath)
                .download(outputStream);

        outputStream.close();
    }

    /**
     * Returns the hash value of a file.
     *
     * @param file
     * @return
     */
    public String getHash(String file) {
        MessageDigest hasher = new DropBoxContentHasher();
        byte[] buf = new byte[1024];
        try (InputStream in = new FileInputStream(file)) {
            while (true) {
                int n = in.read(buf);
                if (n < 0) break;  // EOF
                hasher.update(buf, 0, n);
            }
        } catch (IOException e) {
            return null;
        }
        DropBoxContentHasher dropBoxContentHasher = new DropBoxContentHasher();
        return dropBoxContentHasher.hex(hasher.digest());
    }

}
