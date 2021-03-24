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
        String tempHash = getFileChecksum(MessageDigest.getInstance("SHA-256"), tempFile);
        String hash = getFileChecksum(MessageDigest.getInstance("SHA-256"), localFile);

        if (hash.equals(tempHash)) {
            tempFile.delete();
            return 0;
        } else {
            localFile.delete();
            tempFile.renameTo(localFile);
            return 1;
        }
    }

    private static String getFileChecksum(MessageDigest digest, File file) throws IOException {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[4096];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        ;

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }

}
