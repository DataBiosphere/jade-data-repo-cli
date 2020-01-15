package bio.terra.endpoints;

import bio.terra.common.category.CLIIntegrated;
import bio.terra.context.Login;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@RunWith(JUnit4.class)
@Category(CLIIntegrated.class)
/**
 * These tests do NOT use the CLI. They are included here to check that the Data Repo instance is responding to basic
 * unauthenticated and authenticated HTTP requests. These tests are just intended to help with debugging CLI failures.
 */
public class JavaHTTPRequestTests {

    private final Logger logger = LoggerFactory.getLogger(JavaHTTPRequestTests.class);

    @BeforeClass
    public static void setup() {
        Login.setClientSecretsFilePath(CLITestingConfig.clientSecretsFilePath);
    }

    @AfterClass
    public static void teardown() {
        Login.setClientSecretsFilePath(null);
    }

    /**
     * Unauthenticated API : GET : serviceStatus
     * @throws IOException
     */
    @Test
    public void serviceStatusTest() throws IOException {
        // endpoint information
        String endpointName = "status";
        String endpointType = "GET";

        // make request using Java HTTP library
        Map<String, Object> javaHttpResponse = CLITestingUtils.sendJavaHttpRequest(
                CLITestingConfig.dataRepoURL + endpointName, endpointType, null, null);

        // make request using curl in a separate process
        //Map<String, Object> curlResponse =
        //        EndpointUtils.sendCurlRequest(dataRepoURL + endpointName, endpointType, null);

        // log the response to stdout
        logger.info("javaHttpResponse: " + javaHttpResponse);

        // check that the status code is success
        Assert.assertEquals(200, javaHttpResponse.get("statusCode"));

        // there should only be one field (statusCode) because there is no response body
        Assert.assertEquals(1, javaHttpResponse.size());
    }

    /**
     * Unauthenticated API : GET : retrieveRepositoryConfig
     * @throws IOException
     */
    @Test
    public void retrieveRepositoryConfigTest() throws IOException {
        // endpoint information
        String endpointName = "configuration";
        String endpointType = "GET";

        // make request using Java HTTP library
        Map<String, Object> javaHttpResponse = CLITestingUtils.sendJavaHttpRequest(
                CLITestingConfig.dataRepoURL + endpointName, endpointType, null, null);

        // make request using curl in a separate process
        //Map<String, Object> curlResponse =
        //        EndpointUtils.sendCurlRequest(dataRepoURL + endpointName, endpointType, null);

        // log the response to stdout
        logger.info("javaHttpResponse: " + javaHttpResponse);

        // check that the status code is success
        Assert.assertEquals(200, javaHttpResponse.get("statusCode"));

        // clientId should not be null or empty string
        String javaHttpClientId = (String)javaHttpResponse.get("clientId");
        Assert.assertNotNull(javaHttpClientId);
        Assert.assertNotEquals("", javaHttpClientId);

        // activeProfiles should not be null or empty
        ArrayList<String> activeProfiles = (ArrayList<String>)javaHttpResponse.get("activeProfiles");
        Assert.assertNotNull(activeProfiles);
        Assert.assertFalse(activeProfiles.size() == 0);
    }

    /**
     * Repository API : GET : enumerateDatasets
     * @throws IOException
     */
    @Ignore
    public void enumerateDatasetsTest() throws IOException {
        // fetch access token in the same way that the CLI does
        // this depends on the jadecli_client_secret.json file
        Login.authorize();
        String token = Login.getUserCredential().getAccessToken();

        // endpoint information
        String endpointName = "api/repository/v1/datasets";
        String endpointType = "GET";

        // make request using Java HTTP library
        Map<String, Object> javaHttpResponse = CLITestingUtils.sendJavaHttpRequest(
                CLITestingConfig.dataRepoURL + endpointName, endpointType, token, null);

        // make request using curl in a separate process
        //Map<String, Object> curlResponse =
        //        CLITestingUtils.sendCurlRequest(CLITestingConfig.dataRepoURL + endpointName, endpointType, token);

        // log the response to stdout
        logger.info("javaHttpResponse: " + javaHttpResponse);

        // check that the status code is success
        Assert.assertEquals(200, javaHttpResponse.get("statusCode"));
    }

}
