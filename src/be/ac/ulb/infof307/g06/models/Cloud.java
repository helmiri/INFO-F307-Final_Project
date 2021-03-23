package be.ac.ulb.infof307.g06.models;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderBuilder;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.io.*;
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

    }

    public static void downloadFile(String localFilePath, String cloudFilePath) throws IOException, DbxException {
        OutputStream outputStream = new FileOutputStream(localFilePath);
        FileMetadata metadata = Global.dboxClient.files()
                .downloadBuilder(cloudFilePath)
                .download(outputStream);
    }
}
