package bio.terra.context;

import bio.terra.datarepo.client.Configuration;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public final class Login {
  private static boolean isLoggedIn;
  private static Credential userCredential;
  private static final String CLIENT_SECRET_FILE = "jadecli_client_secret.json";

  private Login() {}

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
    String homePath = System.getProperty("user.home");
    File dataStoreDir = new File(homePath, ".jadecli/creds");

    for (int retryCount = 0; retryCount < 2; retryCount++) {
      try {
        authorizeWorker(dataStoreDir);
        return;
      } catch (TokenResponseException tex) {
        System.err.println("Error using existing token: " + tex.getMessage());
        if (retryCount == 0) {
          System.err.println("Attempting to clear old credential and retry");
          if (!cleanDirectory(dataStoreDir)) {
            System.err.println("No retry: unable to remove old credential from " + dataStoreDir.getAbsolutePath());
          }
        }
      } catch (IOException e) {
        System.err.println(e.getMessage());
        e.printStackTrace();
        return;
      } catch (Throwable t) {
        t.printStackTrace();
        return;
      }
    }
  }

  // Google magic to authenticate the user and return the access token
  // Sets userCredential private member
  private static void authorizeWorker(File dataStoreDir) throws Exception {
    List<String> userLoginScopes =
        Arrays.asList(
            "openid",
            "email",
            "profile",
            "https://www.googleapis.com/auth/devstorage.read_only",
            "https://www.googleapis.com/auth/bigquery.readonly");
    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(dataStoreDir);

    // load client secrets
    // Note that the use of dataStoreFactory below is just a way to get the class loader.
    // We could have used any object instance.
    GoogleClientSecrets clientSecrets;
    try (InputStream stream =
        dataStoreFactory.getClass().getClassLoader().getResourceAsStream(CLIENT_SECRET_FILE)) {
      if (stream == null) {
        throw new IllegalStateException("Did not find client secret file in installation");
      }
      clientSecrets =
          GoogleClientSecrets.load(
              jsonFactory, new InputStreamReader(stream, Charset.defaultCharset()));
    }

    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, userLoginScopes)
            .setDataStoreFactory(dataStoreFactory)
            .setApprovalPrompt("force")
            .build();
    // authorize
    userCredential =
        new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

    Long expireSeconds = userCredential.getExpiresInSeconds();
    if (expireSeconds == null || expireSeconds < 30) {
      if (!userCredential.refreshToken()) {
        // if we fail to get a refresh token, what should we do?
        System.err.println("Oh no! Failed to refresh token!");
      }
    }
  }

  private static boolean cleanDirectory(File dir) {
    File[] fileList = dir.listFiles();
    if (fileList == null) {
      return true;
    }
    for (File file : fileList) {
      if (file.isDirectory()) {
        if (!cleanDirectory(file)) {
          return false;
        }
      }
      if (!file.delete()) {
        return false;
      }
    }
    return true;
  }
}
