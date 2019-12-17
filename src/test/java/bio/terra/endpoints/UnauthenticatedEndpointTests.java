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

    /**
     * Unauthenticated API : GET : serviceStatus
     * @throws IOException
     */
    @Test
    public void serviceStatusTest() throws IOException, InterruptedException {
        // endpoint

        // make request using Java HTTP library
        Map<String, Object> javaHttpResponse =
                EndpointUtils.sendJavaHttpRequest("http://localhost:8080/status","GET");

        // make request using curl in a separate process
        Map<String, Object> curlResponse =
                EndpointUtils.sendCurlRequest("http://localhost:8080/status", "GET");

        // check that the responses match
        Assert.assertEquals(javaHttpResponse,curlResponse);

        // there should only be one field (statusCode) because there is no response body
        Assert.assertEquals(javaHttpResponse.size(),1);

        // log the response map to stdout
        System.out.println("responses match");
        System.out.println(javaHttpResponse);
    }

    /**
     * Unauthenticated API : GET : retrieveRepositoryConfig
     * @throws IOException
     */
    @Test
    public void retrieveRepositoryConfigTest() throws IOException {
        // make request using Java HTTP library
        Map<String, Object> javaHttpResponse =
                EndpointUtils.sendJavaHttpRequest("http://localhost:8080/configuration","GET");

        // make request using curl in a separate process
        Map<String, Object> curlResponse =
                EndpointUtils.sendCurlRequest("http://localhost:8080/configuration", "GET");

        // check that the responses match
        Assert.assertEquals(javaHttpResponse, curlResponse);

        // clientId should not be null or empty string
        String javaHttpClientId = (String)javaHttpResponse.get("clientId");
        Assert.assertNotNull(javaHttpClientId);
        Assert.assertNotEquals(javaHttpClientId,"");

        // activeProfiles should contain one item, "google"
        ArrayList<String> activeProfiles = (ArrayList<String>)javaHttpResponse.get("activeProfiles");
        Assert.assertNotNull(activeProfiles);
        Assert.assertEquals(activeProfiles.size(),1);
        Assert.assertEquals(activeProfiles.get(0),"google");

        // log the response map to stdout
        System.out.println("responses match");
        System.out.println(javaHttpResponse);
    }

}
