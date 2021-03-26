package be.ac.ulb.infof307.g06.models;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderBuilder;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class Cloud {

    public static void main(String[] args) throws IOException, DbxException, NoSuchAlgorithmException {
        connect("1dmwFHhIn68AAAAAAAAAAWnTZgaV7cg_T72aWassPUk4EC7TFZr0iU4lW2tFhpqC", "INFOF307");
        downloadFile("README.md", "/README.md");
    }

    public static DbxClientV2 connect(String ACCESS_TOKEN, String clientidentifier) throws DbxException, IOException {
        // Create Dropbox client
        DbxRequestConfig config = new DbxRequestConfig(clientidentifier, "en_US");
        Global.dboxClient = new DbxClientV2(config, ACCESS_TOKEN);

//        FullAccount account = Global.dboxClient.users().getCurrentAccount();
//        System.out.println(account.getName().getDisplayName());


        return Global.dboxClient;
    }

    public static List<String> getFiles() throws DbxException {
        ListFolderBuilder listFolderBuilder = Global.dboxClient.files().listFolderBuilder("");
        ListFolderResult result = listFolderBuilder.withRecursive(true).start();
        List<String> res = new ArrayList<>();
        while (true) {
            for (Metadata metadata : result.getEntries()) {
                res.add(metadata.getPathLower());
            }

            if (!result.getHasMore()) {
                break;
            }

            result = Global.dboxClient.files().listFolderContinue(result.getCursor());
        }
        return res;
    }

    public static void uploadFile(String localFilePath, String cloudFilePath) throws IOException, DbxException {
        InputStream in = new FileInputStream(localFilePath);
        FileMetadata metadata = Global.dboxClient.files().uploadBuilder(cloudFilePath)
                .uploadAndFinish(in);
        in.close();
    }

    public static int downloadFile(String localFilePath, String cloudFilePath) throws IOException, DbxException, NoSuchAlgorithmException {
        String tempPath = localFilePath + ".temp";
        OutputStream outputStream = new FileOutputStream(tempPath);
        FileMetadata metadata = Global.dboxClient.files()
                .downloadBuilder(cloudFilePath)
                .download(outputStream);

        outputStream.close();
        File tempFile = new File(tempPath);
        File localFile = new File(localFilePath);

        if (metadata.getContentHash().equals(dropBoxHash(localFilePath))) {
            tempFile.delete();
            return 0;
        } else {
            localFile.delete();
            tempFile.renameTo(localFile);
            return 1;
        }
    }


    private static String dropBoxHash(String file) {
        MessageDigest hasher = new DropBoxContentHasher();
        byte[] buf = new byte[1024];
        try (InputStream in = new FileInputStream(file)) {
            while (true) {
                int n = in.read(buf);
                if (n < 0) break;  // EOF
                hasher.update(buf, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return DropBoxContentHasher.hex(hasher.digest());
    }

}
