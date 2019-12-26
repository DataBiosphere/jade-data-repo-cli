package bio.terra.endpoints;

import bio.terra.common.category.CLIIntegrated;
import bio.terra.context.Login;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.*;

@RunWith(JUnit4.class)
@Category(CLIIntegrated.class)
public class CLICommandTests {

    private static final String dataRepoURL = "http://localhost:8080/";
    private static final String clientSecretsFilePath = null;
    //private static final String dataRepoURL = "https://jade.datarepo-dev.broadinstitute.org/";
    //private static final String clientSecretsFilePath = "/tmp/jadecli_client_secret.json";

    @BeforeClass
    public static void setup() {
        Login.setClientSecretsFilePath(clientSecretsFilePath);
    }

    @AfterClass
    public static void teardown() {
        Login.setClientSecretsFilePath(null);
    }

    /**
     * CLI command : jc dataset show
     * Repository API : GET : enumerateDatasets
     * @throws IOException
     */
    @Test
    public void datasetShowTest() throws IOException {
        // TODO: need to pull this from the JSON payload
        String datasetName = "MMdataset";

        // fetch access token in the same way that the CLI does
        // this depends on the jadecli_client_secret.json file
        Login.authorize();
        String token = Login.getUserCredential().getAccessToken();

        // endpoint information
        String endpointName = "api/repository/v1/datasets";
        String endpointType = "GET";

        // build request parameter map
        Map<String, String> params = new HashMap<>();
        params.put("filter",datasetName);

        // make request using Java HTTP library
        Map<String, Object> javaHttpResponse =
                EndpointUtils.sendJavaHttpRequest(dataRepoURL + endpointName, endpointType, token, params);

        // check that the status code is success
        Assert.assertEquals(200, javaHttpResponse.get("statusCode"));

        // call CLI command in a separate process
        List<String> cmdArgs = new ArrayList<String>(new ArrayList<>(Arrays.asList(
                new String[]{"dataset", "show", datasetName})));
        List<String> cliCmdResponse = EndpointUtils.callCLICommand(cmdArgs);

        // read in the expect CLI command output from a file
        List<String> cliCmdExpectedResponse = EndpointUtils.readCLIExpectedOutput("outputDatasetShow.txt");

        // log the responses to stdout
        System.out.println("javaHttpResponse: " + javaHttpResponse + "\n");
        System.out.println("cliCmdResponse: " + cliCmdResponse + "\n");

        // need to replace %xyz% variables in the CLI expected response with values from the Java HTTP response
        Assert.assertEquals(1, javaHttpResponse.get("total"));
        Map<String, Object> items0 = (Map<String, Object>)((ArrayList<Object>) javaHttpResponse.get("items")).get(0);
        String id = items0.get("id").toString();
        String createdDate = items0.get("createdDate").toString();

        // check that the responses match
        for (int ctr=0; ctr<cliCmdResponse.size(); ctr++) {
            String expectedLine = cliCmdExpectedResponse.get(ctr);
            expectedLine = expectedLine.replace("%id%", id);
            expectedLine = expectedLine.replace("%createdDate%", createdDate);

            Assert.assertEquals(expectedLine, cliCmdResponse.get(ctr));
        }
    }

}
