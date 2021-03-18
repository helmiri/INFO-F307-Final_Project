package be.ac.ulb.infof307.g06.models;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

public class Cloud {
    private static final String ACCESS_TOKEN = "s9ifuzucCEEAAAAAAAAAAStAeLKov4CFRnm8kfR9sgN00Knv0PTVdU2yXo7IzLgW";

    public static void main(String args[]) throws DbxException, IOException {
        // Create Dropbox client
        DbxRequestConfig config = new DbxRequestConfig("Group6Project", "en_US");
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        // Get current account info
        FullAccount account = client.users().getCurrentAccount();
        System.out.println(account.getName().getDisplayName());

        // Get files and folder metadata from Dropbox root directory
        ListFolderResult result = client.files().listFolder("");
        while (true) {
            for (Metadata metadata : result.getEntries()) {
                System.out.println(metadata.getPathLower());
            }

            if (!result.getHasMore()) {
                break;
            }

            result = client.files().listFolderContinue(result.getCursor());
        }

        // Upload "happy.png" to Dropbox
        try (InputStream in = new FileInputStream("/Users/horatiul/Desktop/happy.png")) {
            FileMetadata metadata = client.files().uploadBuilder("/happy-uploaded-java.png")
                    .uploadAndFinish(in);
        }

        // ------
        // Downloading a file using the Dropbox Java library
        String localPath = "/Users/horatiul/Desktop/happy-downloaded-java.png";
        OutputStream outputStream = new FileOutputStream(localPath);
        FileMetadata metadata = client.files()
                .downloadBuilder("/happy.png")
                .download(outputStream);

    }
}
