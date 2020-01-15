package bio.terra.endpoints;

import bio.terra.common.category.CLIIntegrated;
import bio.terra.context.Login;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(JUnit4.class)
@Category(CLIIntegrated.class)
public class CLICommandTests {

    private final Logger logger = LoggerFactory.getLogger(CLICommandTests.class);

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
        Map<String, Object> datasetDetails =
                datasetCreateTest(token, "inputDatasetCreate.txt", "outputDatasetCreate.txt");

        // jc dataset show CLITestDataset
        datasetShowTest(datasetDetails, "outputDatasetShow.txt");

        // jc dr describe CLITestDataset
        drDescribeTest(datasetDetails, "outputDatasetShow.txt");

        // jc dataset delete CLITestDataset
        datasetDeleteTest(datasetDetails, "outputDatasetDelete.txt");
    }

    /**
     * CLI command : jc dataset create
     * Repository API : POST : createDataset
     * @throws IOException
     */
    public Map<String, Object> datasetCreateTest(String token, String inputJSONFilename, String expectedOutputFilename)
            throws IOException {
        logger.info("***********************************************");
        logger.info("jc dataset create --input-json inputDatasetCreate.txt");

        // build path to input file
        String inputJSON = CLITestingConfig.dirName + inputJSONFilename;

        // call CLI command in a separate process
        List<String> cmdArgs = new ArrayList<String>(new ArrayList<>(Arrays.asList(
                new String[]{"dataset", "create", "--input-json", inputJSON})));
        List<String> cliCmdResponse = CLITestingUtils.callCLICommand(cmdArgs);

        // read in the expect CLI command output from a file
        List<String> cliCmdExpectedResponse = CLITestingUtils.readCLIExpectedOutput(expectedOutputFilename);

        // log the response to stdout
        logger.info("cliCmdResponse: " + cliCmdResponse + "\n");

        // TODO: sometimes the dataset isn't returned if you query immediately after creation. add polling/retry here.
        logger.info("sleeping for 1 second...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) { }

        // fetch the dataset summary and details over HTTP
        Map<String, Object> inputJSONMap = CLITestingUtils.readCLIInput(inputJSONFilename);
        Map<String, Object> datasetSummary = enumerateDatasetHttpGET(token, inputJSONMap.get("name").toString());
        Map<String, Object> datasetDetails = retrieveDatasetHttpGET(token, datasetSummary.get("id").toString());

        // need to replace %xyz% variables in the CLI expected response with values from the Java HTTP response
        Map<String, String> variablesMap = new HashMap<>();
        variablesMap.put("%id%", datasetSummary.get("id").toString());
        variablesMap.put("%createdDate%", datasetSummary.get("createdDate").toString());

        // then do the expected to actual comparison, line by line
        CLITestingUtils.compareCLIExpectedOutput(cliCmdResponse, cliCmdExpectedResponse, variablesMap);

        return datasetDetails;
    }

    /**
     * HTTP GET request : GET : enumerateDatasets
     * @param token access token to send with request
     * @return dataset summary as a property map
     * @throws IOException
     */
    public Map<String, Object> enumerateDatasetHttpGET(String token, String datasetName) throws IOException {
        logger.info("***********************************************");
        logger.info("HTTP request api/repository/v1/datasets");

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
        logger.info("javaHttpResponse: " + javaHttpResponse + "\n");

        // check that the status code is success
        Assert.assertEquals(200, javaHttpResponse.get("statusCode"));

        // check that we fetched exactly one dataset
        Assert.assertEquals(1, javaHttpResponse.get("total"));

        // return the dataset summary as a map
        Map<String, Object> items0 = (Map<String, Object>)((ArrayList<Object>) javaHttpResponse.get("items")).get(0);
        return items0;
    }

    /**
     * HTTP GET request : GET : retrieveDataset
     * @param token access token to send with request
     * @return dataset details as a property map
     * @throws IOException
     */
    public Map<String, Object> retrieveDatasetHttpGET(String token, String datasetId) throws IOException {
        logger.info("***********************************************");
        logger.info("HTTP request api/repository/v1/datasets/{id}");

        // endpoint information
        String endpointName = "api/repository/v1/datasets/" + datasetId;
        String endpointType = "GET";

        // make request using Java HTTP library
        Map<String, Object> javaHttpResponse = CLITestingUtils.sendJavaHttpRequest(
                CLITestingConfig.dataRepoURL + endpointName, endpointType, token, null);

        // log the response to stdout
        logger.info("javaHttpResponse: " + javaHttpResponse + "\n");

        // check that the status code is success
        Assert.assertEquals(200, javaHttpResponse.get("statusCode"));

        // return the dataset details as a map
        return javaHttpResponse;
    }

    /**
     * CLI command : jc dataset show
     * Repository API : GET : enumerateDatasets
     * @throws IOException
     */
    public void datasetShowTest(Map<String, Object> datasetDetails, String expectedOutputFilename)
            throws IOException {
        String datasetName = datasetDetails.get("name").toString();
        logger.info("***********************************************");
        logger.info("jc dataset show " + datasetName);

        // call CLI command in a separate process
        List<String> cmdArgs = new ArrayList<String>(new ArrayList<>(Arrays.asList(
                new String[]{"dataset", "show", datasetName})));
        List<String> cliCmdResponse = CLITestingUtils.callCLICommand(cmdArgs);

        // read in the expect CLI command output from a file
        List<String> cliCmdExpectedResponse = CLITestingUtils.readCLIExpectedOutput(expectedOutputFilename);

        // log the response to stdout
        logger.info("cliCmdResponse: " + cliCmdResponse + "\n");

        // need to replace %xyz% variables in the CLI expected response with values from the Java HTTP response
        Map<String, String> variablesMap = CLITestingUtils.buildMapOfDatasetVariablesToSwap(datasetDetails);

        // then do the expected to actual comparison, line by line
        CLITestingUtils.compareCLIExpectedOutput(cliCmdResponse, cliCmdExpectedResponse, variablesMap);
    }

    /**
     * CLI command : jc dr describe
     * Repository API : GET : enumerateDatasets
     * @throws IOException
     */
    public void drDescribeTest(Map<String, Object> datasetDetails, String expectedOutputFilename)
            throws IOException {
        String datasetName = datasetDetails.get("name").toString();
        logger.info("***********************************************");
        logger.info("jc dr describe " + datasetName);

        // call CLI command in a separate process
        List<String> cmdArgs = new ArrayList<String>(new ArrayList<>(Arrays.asList(
                new String[]{"dr", "describe", datasetName})));
        List<String> cliCmdResponse = CLITestingUtils.callCLICommand(cmdArgs);

        // read in the expect CLI command output from a file
        List<String> cliCmdExpectedResponse = CLITestingUtils.readCLIExpectedOutput(expectedOutputFilename);

        // log the response to stdout
        logger.info("cliCmdResponse: " + cliCmdResponse + "\n");

        // need to replace %xyz% variables in the CLI expected response with values from the Java HTTP response
        Map<String, String> variablesMap = CLITestingUtils.buildMapOfDatasetVariablesToSwap(datasetDetails);

        // then do the expected to actual comparison, line by line
        CLITestingUtils.compareCLIExpectedOutput(cliCmdResponse, cliCmdExpectedResponse, variablesMap);
    }

    /**
     * CLI command : jc dataset delete
     * Repository API : POST : deleteDataset
     * @throws IOException
     */
    public void datasetDeleteTest(Map<String, Object> datasetDetails, String expectedOutputFilename)
            throws IOException {
        String datasetName = datasetDetails.get("name").toString();
        logger.info("***********************************************");
        logger.info("jc dataset delete " + datasetName);

        // call CLI command in a separate process
        List<String> cmdArgs = new ArrayList<String>(new ArrayList<>(Arrays.asList(
                new String[]{"dataset", "delete", datasetName})));
        List<String> cliCmdResponse = CLITestingUtils.callCLICommand(cmdArgs);

        // read in the expect CLI command output from a file
        List<String> cliCmdExpectedResponse = CLITestingUtils.readCLIExpectedOutput(expectedOutputFilename);

        // log the response to stdout
        logger.info("cliCmdResponse: " + cliCmdResponse + "\n");

        // no %xyz% variables to replace in the expected response before doing the comparison
        // so just build an empty map
        Map<String, String> variablesMap = new HashMap<>();

        // then do the expected to actual comparison, line by line
        CLITestingUtils.compareCLIExpectedOutput(cliCmdResponse, cliCmdExpectedResponse, variablesMap);
    }

}
