package bio.terra.endpoints;

import bio.terra.common.category.CLIIntegrated;
import bio.terra.context.Login;
import com.google.auth.oauth2.AccessToken;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@RunWith(JUnit4.class)
@Category(CLIIntegrated.class)
public class RepositoryEndpointTests {

    private static final String dataRepoURL = "http://localhost:8080/";
    //private static final String dataRepoURL = "https://jade-dd.datarepo-dev.broadinstitute.org/";

    /**
     * Repository API : GET : enumerateDatasets
     * @throws IOException
     */
    @Test
    public void enumerateDatasetsTest() throws IOException {
        // fetch access token in the same way that the CLI does
        // this depends on the jadecli_client_secret.json file
        Login.authorize();
        String token = Login.getUserCredential().getAccessToken();

        // endpoint information
        String endpointName = "api/repository/v1/datasets";
        String endpointType = "GET";

        // make request using Java HTTP library
        Map<String, Object> javaHttpResponse =
                EndpointUtils.sendJavaHttpRequest(dataRepoURL + endpointName, endpointType, token);

        // make request using curl in a separate process
        Map<String, Object> curlResponse =
                EndpointUtils.sendCurlRequest(dataRepoURL + endpointName, endpointType, token);

        // log the response maps to stdout
        System.out.println("javaHttpResponse: " + javaHttpResponse);
        System.out.println("curlResponse: " + curlResponse);

        // check that the responses match
        Assert.assertEquals(javaHttpResponse, curlResponse);

        // check that the status code is success
        Assert.assertEquals(200, curlResponse.get("statusCode"));
    }

}
