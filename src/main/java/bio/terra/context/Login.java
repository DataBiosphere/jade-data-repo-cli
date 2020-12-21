package bio.terra.context;

import static bio.terra.context.ContextEnum.AUTH_KEY_FILE;

import bio.terra.command.CommandUtils;
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
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public final class Login {
  private static Credential userCredential;
  private static GoogleCredentials saCredential;
  private static ContextAuthTypeEnum authType;
  private static GoogleClientSecrets clientSecrets;
  private static final String CLIENT_SECRET_FILE = "jadecli_client_secret.json";
  private static final String APPLICATION_NAME = "jadecli";
  private static final List<String> userLoginScopes =
      Arrays.asList(
          "openid",
          "email",
          "profile",
          "https://www.googleapis.com/auth/devstorage.read_only",
          "https://www.googleapis.com/auth/bigquery.readonly");

  private Login() {}

  public static void requiresLogin() {
    authType = getAuthType();
    if (authType == ContextAuthTypeEnum.AUTH_TYPE_USER) {
      authorizeUser();
    } else {
      authorizeSA();
    }

    // Set the data repo api client access
    Configuration.getDefaultApiClient()
        .setUserAgent(APPLICATION_NAME)
        .setBasePath(Context.getInstance().getContextItem(ContextEnum.BASE_PATH))
        .setAccessToken(getAccessToken());
  }

  public static String getAccessToken() {
    if (authType == ContextAuthTypeEnum.AUTH_TYPE_USER) {
      return userCredential.getAccessToken();
    }
    AccessToken accessToken = saCredential.getAccessToken();
    return accessToken.getTokenValue();
  }

  public static Credential getUserCredential() {
    return userCredential;
  }

  // Authenticate using service account credentials
  static void authorizeSA() {
    String keyFile = Context.getInstance().getContextItem(AUTH_KEY_FILE);
    if (StringUtils.isEmpty(keyFile)) {
      CommandUtils.printErrorAndExit(
          "You must set a key file to authenticate as a service account");
    }

    try {
      saCredential =
          GoogleCredentials.fromStream(new FileInputStream(keyFile)).createScoped(userLoginScopes);
      saCredential.refreshIfExpired();
    } catch (IOException e) {
      CommandUtils.printErrorAndExit("Error processing key file: " + keyFile);
    }
  }

  // Google magic to authenticate the user and return the access token
  // Sets userCredential private member
  static void authorizeUser() {
    File dataStoreDir = getDataStoreDir();

    for (int retryCount = 0; retryCount < 2; retryCount++) {
      try {
        authorizeWorker(dataStoreDir);
        return;
      } catch (TokenResponseException tex) {
        System.err.println("Error using existing token: " + tex.getMessage());
        if (retryCount == 0) {

          if (!cleanDirectory(dataStoreDir)) {
            System.err.println(
                "No retry: unable to remove old credential from " + dataStoreDir.getAbsolutePath());
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

  private static void authorizeWorker(File dataStoreDir) throws Exception {
    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(dataStoreDir);

    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, getClientSecrets(), userLoginScopes)
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

  @SuppressFBWarnings(
      value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE",
      justification = "Spotbugs bug: not properly understanding the resource try")
  private static GoogleClientSecrets getClientSecrets() throws Exception {
    if (clientSecrets == null) {
      JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
      try (InputStream stream =
          Login.class.getClassLoader().getResourceAsStream(CLIENT_SECRET_FILE)) {
        clientSecrets =
            GoogleClientSecrets.load(
                jsonFactory, new InputStreamReader(stream, Charset.defaultCharset()));
      }
    }
    return clientSecrets;
  }

  private static File getDataStoreDir() {
    String homePath = System.getProperty("user.home");
    return new File(homePath, ".jadecli/creds");
  }

  public static boolean clearCredentialDirectory() {
    File dir = getDataStoreDir();
    return cleanDirectory(dir);
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

  private static ContextAuthTypeEnum getAuthType() {
    String contextValue = Context.getInstance().getContextItem(ContextEnum.AUTH_TYPE);
    return ContextAuthTypeEnum.fromContextValue(contextValue);
  }
}
