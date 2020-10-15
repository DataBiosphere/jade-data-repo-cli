package bio.terra.command;

import bio.terra.context.Login;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.IOUtils;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.channels.Channels;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

public final class StreamFile {

  private StreamFile() {}

  public static void streamFile(String gspath) {
    Credential userCredential = Login.getUserCredential();
    String accessToken = userCredential.getAccessToken();
    Date expirationTime = new Date(userCredential.getExpirationTimeMilliseconds());

    Storage storage =
        StorageOptions.newBuilder()
            .setCredentials(GoogleCredentials.create(new AccessToken(accessToken, expirationTime)))
            .build()
            .getService();

    URI sourceUri = URI.create(gspath);
    if (!StringUtils.equals(sourceUri.getScheme(), "gs")) {
      throw new IllegalArgumentException("Source path is not a gs path: '" + gspath + "'");
    }
    if (sourceUri.getPort() != -1) {
      throw new IllegalArgumentException(
          "Source path must not have a port specified: '" + gspath + "'");
    }
    String sourceBucket = sourceUri.getAuthority();
    String sourcePath = StringUtils.removeStart(sourceUri.getPath(), "/");

    try (InputStream input = Channels.newInputStream(storage.reader(sourceBucket, sourcePath))) {
      IOUtils.copy(input, System.out);
    } catch (IOException ex) {
      throw new IllegalArgumentException("Caught IO exception: " + ex.getMessage());
    }
  }
}
