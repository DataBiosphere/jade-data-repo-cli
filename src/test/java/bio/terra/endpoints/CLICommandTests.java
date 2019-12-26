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

    @BeforeClass
    public static void setup() {
        Login.setClientSecretsFilePath(CLITestingConfig.clientSecretsFilePath);
    }

    @AfterClass
    public static void teardown() {
        Login.setClientSecretsFilePath(null);
    }

    /**
     * Test method that calls commands in sequence so data gets setup in the right order.
     * @throws IOException
     */
    @Test
    public void commandScript() throws IOException {
        // fetch access token in the same way that the CLI does
        // this depends on the jadecli_client_secret.json file
        Login.authorize();
        String token = Login.getUserCredential().getAccessToken();

        // TODO: create and delete profile, need to modify input JSON file for create dataset and write to /tmp

        // jc dataset create --input-json inputDatasetCreate.txt
        Map<String, Object> datasetSummary =
                datasetCreateTest(token, "inputDatasetCreate.txt", "outputDatasetCreate.txt");

        // jc dataset show CLITestDataset
        datasetShowTest(datasetSummary, "outputDatasetShow.txt");

        // jc dr describe CLITestDataset
        drDescribeTest(datasetSummary, "outputDatasetShow.txt");

        // jc dataset delete CLITestDataset
        datasetDeleteTest(datasetSummary, "outputDatasetDelete.txt");
    }

    /**
     * CLI command : jc dataset create
     * Repository API : POST : createDataset
     * @throws IOException
     */
    public Map<String, Object> datasetCreateTest(String token, String inputJSONFilename, String expectedOutputFilename)
            throws IOException {
        System.out.println("***********************************************");
        System.out.println("jc dataset create --input-json inputDatasetCreate.txt");

        // build path to input file
        String inputJSON = CLITestingConfig.dirName + inputJSONFilename;

        // call CLI command in a separate process
        List<String> cmdArgs = new ArrayList<String>(new ArrayList<>(Arrays.asList(
                new String[]{"dataset", "create", "--input-json", inputJSON})));
        List<String> cliCmdResponse = CLITestingUtils.callCLICommand(cmdArgs);

        // read in the expect CLI command output from a file
        List<String> cliCmdExpectedResponse = CLITestingUtils.readCLIExpectedOutput(expectedOutputFilename);

        // log the response to stdout
        System.out.println("cliCmdResponse: " + cliCmdResponse + "\n");

        // fetch the dataset summary over HTTP
        Map<String, Object> inputJSONMap = CLITestingUtils.readCLIInput(inputJSONFilename);
        Map<String, Object> datasetSummary = datasetHttpGET(token, inputJSONMap.get("name").toString());

        // need to replace %xyz% variables in the CLI expected response with values from the Java HTTP response
        String id = datasetSummary.get("id").toString();
        String createdDate = datasetSummary.get("createdDate").toString();

        // check that the responses match
        for (int ctr = 0; ctr < cliCmdResponse.size(); ctr++) {
            String expectedLine = cliCmdExpectedResponse.get(ctr);
            expectedLine = expectedLine.replace("%id%", id);
            expectedLine = expectedLine.replace("%createdDate%", createdDate);

            Assert.assertEquals(expectedLine, cliCmdResponse.get(ctr));
        }

        return datasetSummary;
    }

    /**
     * HTTP GET request : GET : retrieveDataset
     * @param token access token to send with request
     * @return dataset summary as a property map
     * @throws IOException
     */
    public Map<String, Object> datasetHttpGET(String token, String datasetName) throws IOException {
        System.out.println("***********************************************");
        System.out.println("HTTP request api/repository/v1/datasets");

        // endpoint information
        String endpointName = "api/repository/v1/datasets";
        String endpointType = "GET";

        // build request parameter map
        Map<String, String> params = new HashMap<>();
        params.put("filter", datasetName);

        // make request using Java HTTP library
        Map<String, Object> javaHttpResponse = CLITestingUtils.sendJavaHttpRequest(
                CLITestingConfig.dataRepoURL + endpointName, endpointType, token, params);

        // log the response to stdout
        System.out.println("javaHttpResponse: " + javaHttpResponse + "\n");

        // check that the status code is success
        Assert.assertEquals(200, javaHttpResponse.get("statusCode"));

        // check that we fetched exactly one dataset
        Assert.assertEquals(1, javaHttpResponse.get("total"));

        // return the dataset summary as a map
        Map<String, Object> items0 = (Map<String, Object>)((ArrayList<Object>) javaHttpResponse.get("items")).get(0);
        return items0;
    }

    /**
     * CLI command : jc dataset show
     * Repository API : GET : enumerateDatasets
     * @throws IOException
     */
    public void datasetShowTest(Map<String, Object> datasetSummary, String expectedOutputFilename)
            throws IOException {
        String datasetName = datasetSummary.get("name").toString();
        System.out.println("***********************************************");
        System.out.println("jc dataset show " + datasetName);

        // call CLI command in a separate process
        List<String> cmdArgs = new ArrayList<String>(new ArrayList<>(Arrays.asList(
                new String[]{"dataset", "show", datasetName})));
        List<String> cliCmdResponse = CLITestingUtils.callCLICommand(cmdArgs);

        // read in the expect CLI command output from a file
        List<String> cliCmdExpectedResponse = CLITestingUtils.readCLIExpectedOutput(expectedOutputFilename);

        // log the response to stdout
        System.out.println("cliCmdResponse: " + cliCmdResponse + "\n");

        // need to replace %xyz% variables in the CLI expected response with values from the Java HTTP response
        String id = datasetSummary.get("id").toString();
        String createdDate = datasetSummary.get("createdDate").toString();

        // check that the responses match
        for (int ctr = 0; ctr < cliCmdResponse.size(); ctr++) {
            String expectedLine = cliCmdExpectedResponse.get(ctr);
            expectedLine = expectedLine.replace("%id%", id);
            expectedLine = expectedLine.replace("%createdDate%", createdDate);

            Assert.assertEquals(expectedLine, cliCmdResponse.get(ctr));
        }
    }

    /**
     * CLI command : jc dr describe
     * Repository API : GET : enumerateDatasets
     * @throws IOException
     */
    public void drDescribeTest(Map<String, Object> datasetSummary, String expectedOutputFilename)
            throws IOException {
        String datasetName = datasetSummary.get("name").toString();
        System.out.println("***********************************************");
        System.out.println("jc dr describe " + datasetName);

        // call CLI command in a separate process
        List<String> cmdArgs = new ArrayList<String>(new ArrayList<>(Arrays.asList(
                new String[]{"dr", "describe", datasetName})));
        List<String> cliCmdResponse = CLITestingUtils.callCLICommand(cmdArgs);

        // read in the expect CLI command output from a file
        List<String> cliCmdExpectedResponse = CLITestingUtils.readCLIExpectedOutput(expectedOutputFilename);

        // log the response to stdout
        System.out.println("cliCmdResponse: " + cliCmdResponse + "\n");

        // need to replace %xyz% variables in the CLI expected response with values from the Java HTTP response
        String id = datasetSummary.get("id").toString();
        String createdDate = datasetSummary.get("createdDate").toString();

        // check that the responses match
        for (int ctr = 0; ctr < cliCmdResponse.size(); ctr++) {
            String expectedLine = cliCmdExpectedResponse.get(ctr);
            expectedLine = expectedLine.replace("%id%", id);
            expectedLine = expectedLine.replace("%createdDate%", createdDate);

            Assert.assertEquals(expectedLine, cliCmdResponse.get(ctr));
        }
    }

    /**
     * CLI command : jc dataset delete
     * Repository API : POST : deleteDataset
     * @throws IOException
     */
    public void datasetDeleteTest(Map<String, Object> datasetSummary, String expectedOutputFilename)
            throws IOException {
        String datasetName = datasetSummary.get("name").toString();
        System.out.println("***********************************************");
        System.out.println("jc dataset delete " + datasetName);

        // call CLI command in a separate process
        List<String> cmdArgs = new ArrayList<String>(new ArrayList<>(Arrays.asList(
                new String[]{"dataset", "delete", datasetName})));
        List<String> cliCmdResponse = CLITestingUtils.callCLICommand(cmdArgs);

        // read in the expect CLI command output from a file
        List<String> cliCmdExpectedResponse = CLITestingUtils.readCLIExpectedOutput(expectedOutputFilename);

        // log the response to stdout
        System.out.println("cliCmdResponse: " + cliCmdResponse + "\n");

        // check that the responses match
        for (int ctr = 0; ctr < cliCmdResponse.size(); ctr++) {
            String expectedLine = cliCmdExpectedResponse.get(ctr);
            Assert.assertEquals(expectedLine, cliCmdResponse.get(ctr));
        }
    }

}
