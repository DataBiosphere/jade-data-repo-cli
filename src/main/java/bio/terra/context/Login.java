package bio.terra.context;

import bio.terra.datarepo.client.Configuration;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public final class Login {
    private static boolean isLoggedIn;
    private static Credential userCredential;
    private static String clientSecretsFilePath;

    private Login() { }

    public static void requiresLogin() {
        if (!isLoggedIn) {
            authorize();
            Configuration.getDefaultApiClient()
                    .setUserAgent(Context.getInstance().getContextItem(ContextEnum.APPLICATION_NAME))
                    .setBasePath(Context.getInstance().getContextItem(ContextEnum.BASE_PATH))
                    .setAccessToken(userCredential.getAccessToken());
        }
    }

    public static Credential getUserCredential() {
        return userCredential;
    }

    // Google magic to authenticate the user and return the access token
    // Sets userCredential private member
    public static void authorize() {
        try {
            List<String> userLoginScopes = Arrays.asList(
                    "openid",
                    "email",
                    "profile",
                    "https://www.googleapis.com/auth/devstorage.read_only");
            String homePath = System.getProperty("user.home");
            File dataStoreDir = new File(homePath, ".jadecli/creds");
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(dataStoreDir);

            // load client secrets
            File clientSecretsFile;
            if (clientSecretsFilePath == null) {
                clientSecretsFile = new File(homePath, ".jadecli/client/jadecli_client_secret.json");
            } else {
                clientSecretsFile = new File(clientSecretsFilePath);
            }
            // TODO: for reviewers, should this be the default charset or just hardcode to UTF-8?
            // practically, I don't think this will make a difference on Mac or Linux, only Windows
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,
                    new InputStreamReader(new FileInputStream(clientSecretsFile), Charset.defaultCharset()));

            // set up authorization code flow
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, jsonFactory, clientSecrets, userLoginScopes)
                    .setDataStoreFactory(dataStoreFactory)
                    .setApprovalPrompt("force")
                    .build();
            // authorize
            userCredential =
                    new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

            if (userCredential.getExpiresInSeconds() < 30) {
                if (!userCredential.refreshToken()) {
                    // if we fail to get a refresh token, what should we do?
                    System.err.println("Oh no! Failed to refresh token!");
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Setter for path to client secrets JSON file.
     * A null value means to use the default path ~/.jadecli/client/jadecli_client_secret.json
     * @param newPath new file path, may be null
     */
    public static void setClientSecretsFilePath(String newPath) {
        clientSecretsFilePath = newPath;
    }

}
