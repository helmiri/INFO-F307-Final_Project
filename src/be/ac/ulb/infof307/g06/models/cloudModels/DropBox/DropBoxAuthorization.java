package be.ac.ulb.infof307.g06.models.cloudModels.DropBox;

import com.dropbox.core.*;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.oauth.DbxCredential;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An example command-line application that runs through the web-based OAuth
 * flow (using {@link DbxWebAuth}). It grabs short-live token as well as
 * refresh token from server. It stores all authentication related data into the new
 * DbxCredential object
 */
public class DropBoxAuthorization {
    private DbxAppInfo appInfo;
    private String authorizationUrl = "";
    private DbxWebAuth webAuth;


    public DropBoxAuthorization() throws JsonReader.FileLoadException {
        // Only display important log messages.
        Logger.getLogger("").setLevel(Level.WARNING);
        // Read app info file (contains app key and app secret)
        String argAppInfoFile = Objects.requireNonNull(DropBoxAuthorization.class.getResource("credentials.json")).getFile();
        appInfo = DbxAppInfo.Reader.readFromFile(argAppInfoFile);
    }

    public DbxCredential getAuthorization() throws IOException, DbxException {
        // Run through Dropbox API authorization process
        DbxAuthFinish authFinish = authorize();

        // Save auth information the new DbxCredential instance. It also contains app_key and
        // app_secret which is required to do refresh call.
        return new DbxCredential(Objects.requireNonNull(authFinish).getAccessToken(), authFinish
                .getExpiresAt(), authFinish.getRefreshToken(), appInfo.getKey(), appInfo.getSecret());
    }

    public String getUrl() {
        if (!authorizationUrl.isBlank()) {
            return authorizationUrl;
        }
        // Run through Dropbox API authorization process
        DbxRequestConfig requestConfig = new DbxRequestConfig("I(Should)PlanAll");
        webAuth = new DbxWebAuth(requestConfig, appInfo);

        // TokenAccessType.OFFLINE means refresh_token + access_token. ONLINE means access_token only.
        DbxWebAuth.Request webAuthRequest = DbxWebAuth.newRequestBuilder().withNoRedirect()
                .withTokenAccessType(TokenAccessType.OFFLINE).build();
        return webAuth.authorize(webAuthRequest);
    }

    private DbxAuthFinish authorize() throws IOException, DbxException {

        String code = new BufferedReader(new InputStreamReader(System.in)).readLine();
        if (code == null) {
            return null;
        }
        code = code.trim();
        return webAuth.finishFromCode(code);

    }
}