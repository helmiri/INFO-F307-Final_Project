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


@SuppressWarnings("ALL")
public class Cloud {
    private static DbxClientV2 dboxClient;

    public static void init(String ACCESS_TOKEN, String clientidentifier) throws DbxException, IOException {
        // Create Dropbox client
        DbxRequestConfig config = new DbxRequestConfig(clientidentifier, "en_US");
        dboxClient = new DbxClientV2(config, ACCESS_TOKEN);

//        FullAccount account = Global.dboxClient.users().getCurrentAccount();
//        System.out.println(account.getName().getDisplayName());
    }

    public static List<Metadata> getFiles() throws DbxException {
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


    public static void uploadFile(String localFilePath, String cloudFilePath) throws IOException, DbxException {
        InputStream in = new FileInputStream(localFilePath);
        FileMetadata metadata = dboxClient.files().uploadBuilder(cloudFilePath)
                .uploadAndFinish(in);
        in.close();
    }

    public static void downloadFile(String localFilePath, String cloudFilePath) throws IOException, DbxException, NoSuchAlgorithmException {
        String tempPath = localFilePath;
        OutputStream outputStream = new FileOutputStream(tempPath);
        FileMetadata metadata = dboxClient.files()
                .downloadBuilder(cloudFilePath)
                .download(outputStream);

        outputStream.close();
    }


    public static String dropBoxHash(String file) {
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
