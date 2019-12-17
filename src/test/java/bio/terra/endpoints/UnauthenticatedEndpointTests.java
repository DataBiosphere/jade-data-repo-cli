package bio.terra.endpoints;

import bio.terra.common.category.CLIIntegrated;
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
public class UnauthenticatedEndpointTests {

    private static final String dataRepoURL = "http://localhost:8080/";
    //private static final String dataRepoURL = "https://jade-dd.datarepo-dev.broadinstitute.org/";

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
        Map<String, Object> javaHttpResponse =
                EndpointUtils.sendJavaHttpRequest(dataRepoURL + endpointName, endpointType, null);

        // make request using curl in a separate process
        Map<String, Object> curlResponse =
                EndpointUtils.sendCurlRequest(dataRepoURL + endpointName, endpointType, null);

        // log the response maps to stdout
        System.out.println("javaHttpResponse: " + javaHttpResponse);
        System.out.println("curlResponse: " + curlResponse);

        // check that the responses match
        Assert.assertEquals(javaHttpResponse, curlResponse);

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
        Map<String, Object> javaHttpResponse =
                EndpointUtils.sendJavaHttpRequest(dataRepoURL + endpointName, endpointType, null);

        // make request using curl in a separate process
        Map<String, Object> curlResponse =
                EndpointUtils.sendCurlRequest(dataRepoURL + endpointName, endpointType, null);

        // log the response maps to stdout
        System.out.println("javaHttpResponse: " + javaHttpResponse);
        System.out.println("curlResponse: " + curlResponse);

        // check that the responses match
        Assert.assertEquals(javaHttpResponse, curlResponse);

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

}
