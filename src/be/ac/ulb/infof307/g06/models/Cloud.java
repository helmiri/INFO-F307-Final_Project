package be.ac.ulb.infof307.g06.models;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
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

        // Get current account info
//        FullAccount account = Global.dboxClient.users().getCurrentAccount();
//        System.out.println(account.getName().getDisplayName());

        // TODO: faudra trouver un moyen de renvoyer les fichiers contenus dans la dropbox pour que l'utilisateur sache ce qu'il s'y contient.

        return Global.dboxClient;
    }

    public static List<String> getFiles() throws DbxException {
        ListFolderResult result = Global.dboxClient.files().listFolder("");
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

    public static void uploadFile(DbxClientV2 client, String path, String filename) throws IOException, DbxException {
        try (InputStream in = new FileInputStream(path)) {
            FileMetadata metadata = client.files().uploadBuilder(filename)
                    .uploadAndFinish(in);
        }
    }

    public static void downloadFile(DbxClientV2 client, String path, String filename) throws IOException, DbxException {
        OutputStream outputStream = new FileOutputStream(path);
        FileMetadata metadata = client.files()
                .downloadBuilder(filename)
                .download(outputStream);
    }
}
