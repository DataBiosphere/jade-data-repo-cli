package bio.terra.endpoints;

import bio.terra.common.category.CLIIntegrated;
import bio.terra.context.Login;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(JUnit4.class)
@Category(CLIIntegrated.class)
public class CLICommandTests {

  private final Logger logger = LoggerFactory.getLogger(CLICommandTests.class);

  /**
   * Test method that calls commands in sequence so data gets setup in the right order.
   *
   * @throws IOException
   */
  @Test
  public void commandScript() throws IOException {
    // Make sure the session dataRepoURL is the same as the test datarepo URL
    setSessionBasepath();

    // fetch access token in the same way that the CLI does
    // this depends on the jadecli_client_secret.json file
    Login.authorize();
    String token = Login.getUserCredential().getAccessToken();

    String profileId = findOrCreateTestProfile();

    try {
      // jc dataset create --input-json inputDatasetCreate.txt
      Map<String, Object> datasetDetails =
          datasetCreateTest(token, profileId, "inputDatasetCreate.txt", "outputDatasetCreate.txt");

      // jc dataset show CLITestDataset
      datasetShowTest(datasetDetails, "outputDatasetShow.txt");

      // jc dr describe CLITestDataset
      drDescribeTest(datasetDetails, "outputDatasetShow.txt");

      // jc dataset delete CLITestDataset
      datasetDeleteTest(datasetDetails, "outputDatasetDelete.txt");
    } catch (Exception ex) {
      logger.error("CLICommandTests.commandScript exception", ex);
    } finally {
      deleteProfile();
    }
  }

  private void setSessionBasepath() throws IOException {
    List<String> cmdArgs =
        new ArrayList<>(
            Arrays.asList(
                "set", "session", "basepath", CLITestingConfig.config().getDataRepoURL()));
    CLITestingUtils.callCLICommand(cmdArgs);
  }

  private void deleteProfile() throws IOException {
    List<String> cmdArgs =
        new ArrayList<>(Arrays.asList("profile", "delete", CLITestingConfig.testProfileName));
    CLITestingUtils.callCLICommand(cmdArgs);
  }

  // Returns the profile id of the jadecli test profile
  private String findOrCreateTestProfile() throws IOException {
    ObjectMapper objMapper = new ObjectMapper();

    List<String> response = showTestProfile();
    if (response.size() == 0) {
      List<String> cmdArgs =
          new ArrayList<>(
              Arrays.asList(
                  "profile",
                  "create",
                  "--name",
                  CLITestingConfig.testProfileName,
                  "--account",
                  CLITestingConfig.config().getBillingAccount(),
                  "--format",
                  "json"));
      response = CLITestingUtils.callCLICommand(cmdArgs);
    }
    // Both the show and the create responses return the id, so we can map the JSON
    // and pick out the id.
    Map<String, Object> responseMap =
        objMapper.readValue(
            StringUtils.join(response, ' '), new TypeReference<Map<String, Object>>() {});

    return (String) responseMap.get("id");
  }

  private List<String> showTestProfile() throws IOException {
    List<String> cmdArgs =
        new ArrayList<>(
            Arrays.asList("profile", "show", "--format", "json", CLITestingConfig.testProfileName));
    return CLITestingUtils.callCLICommand(cmdArgs);
  }

  /**
   * CLI command : jc dataset create Repository API : POST : createDataset
   *
   * @throws IOException
   */
  public Map<String, Object> datasetCreateTest(
      String token, String profileId, String inputJSONFilename, String expectedOutputFilename)
      throws IOException {
    logger.info("***********************************************");
    logger.info("jc dataset create --input-json inputDatasetCreate.txt");

    Map<String, String> inputVariablesMap = new HashMap<>();
    inputVariablesMap.put("%profileId%", profileId);

    // Generate temp input file with the right profile id
    String inputJSON = CLITestingUtils.generateInputFile(inputJSONFilename, inputVariablesMap);

    // call CLI command in a separate process
    List<String> cmdArgs =
        new ArrayList<>(Arrays.asList("dataset", "create", "--input-json", inputJSON));
    List<String> cliCmdResponse = CLITestingUtils.callCLICommand(cmdArgs);

    // read in the expect CLI command output from a file
    List<String> cliCmdExpectedResponse =
        CLITestingUtils.readCLIExpectedOutput(expectedOutputFilename);

    // log the response to stdout
    logger.info("cliCmdResponse: " + cliCmdResponse + "\n");

    // TODO: sometimes the dataset isn't returned if you query immediately after creation. add
    // polling/retry here.
    logger.info("sleeping for 3 seconds...");
    try {
      Thread.sleep(3000);
    } catch (InterruptedException ex) {
    }

    // fetch the dataset summary and details over HTTP
    Map<String, Object> inputJSONMap = CLITestingUtils.readCLIInput(inputJSONFilename);
    Map<String, Object> datasetSummary =
        enumerateDatasetHttpGET(token, inputJSONMap.get("name").toString());
    Map<String, Object> datasetDetails =
        retrieveDatasetHttpGET(token, datasetSummary.get("id").toString());

    // need to replace %xyz% variables in the CLI expected response with values from the Java HTTP
    // response
    Map<String, String> variablesMap = new HashMap<>();
    variablesMap.put("%id%", datasetSummary.get("id").toString());
    variablesMap.put("%profileId%", profileId);
    variablesMap.put("%createdDate%", datasetSummary.get("createdDate").toString());

    // then do the expected to actual comparison, line by line
    CLITestingUtils.compareCLIExpectedOutput(cliCmdResponse, cliCmdExpectedResponse, variablesMap);

    return datasetDetails;
  }

  /**
   * HTTP GET request : GET : enumerateDatasets
   *
   * @param token access token to send with request
   * @return dataset summary as a property map
   * @throws IOException
   */
  public Map<String, Object> enumerateDatasetHttpGET(String token, String datasetName)
      throws IOException {
    String endpointName = "api/repository/v1/datasets";
    String endpointType = "GET";

    logger.info("***********************************************");
    logger.info("HTTP request " + endpointType + " " + endpointName);

    // build request parameter map
    Map<String, String> params = new HashMap<>();
    params.put("filter", datasetName);

    // make request using Java HTTP library
    Map<String, Object> javaHttpResponse =
        CLITestingUtils.sendJavaHttpRequest(
            CLITestingConfig.config().getDataRepoURL() + endpointName, endpointType, token, params);

    // log the response to stdout
    logger.info("javaHttpResponse: " + javaHttpResponse + "\n");

    // check that the status code is success
    Assert.assertEquals(200, javaHttpResponse.get("statusCode"));

    // check that we fetched exactly one dataset
    // note that this is not the same as checking the total property, which includes all datasets,
    // even those that do not satisfy the filter.
    ArrayList<Object> items = (ArrayList<Object>) javaHttpResponse.get("items");
    Assert.assertEquals(1, items.size());

    // return the dataset summary as a map
    Map<String, Object> items0 = (Map<String, Object>) items.get(0);
    return items0;
  }

  /**
   * HTTP GET request : GET : retrieveDataset
   *
   * @param token access token to send with request
   * @return dataset details as a property map
   * @throws IOException
   */
  public Map<String, Object> retrieveDatasetHttpGET(String token, String datasetId)
      throws IOException {
    logger.info("***********************************************");
    logger.info("HTTP request api/repository/v1/datasets/{id}");

    // endpoint information
    String endpointName = "api/repository/v1/datasets/" + datasetId;
    String endpointType = "GET";

    // make request using Java HTTP library
    Map<String, Object> javaHttpResponse =
        CLITestingUtils.sendJavaHttpRequest(
            CLITestingConfig.config().getDataRepoURL() + endpointName, endpointType, token, null);

    // log the response to stdout
    logger.info("javaHttpResponse: " + javaHttpResponse + "\n");

    // check that the status code is success
    Assert.assertEquals(200, javaHttpResponse.get("statusCode"));

    // return the dataset details as a map
    return javaHttpResponse;
  }

  /**
   * CLI command : jc dataset show Repository API : GET : enumerateDatasets
   *
   * @throws IOException
   */
  public void datasetShowTest(Map<String, Object> datasetDetails, String expectedOutputFilename)
      throws IOException {
    String datasetName = datasetDetails.get("name").toString();
    logger.info("***********************************************");
    logger.info("jc dataset show " + datasetName);

    // call CLI command in a separate process
    List<String> cmdArgs = new ArrayList<>(Arrays.asList("dataset", "show", datasetName));
    List<String> cliCmdResponse = CLITestingUtils.callCLICommand(cmdArgs);

    // read in the expect CLI command output from a file
    List<String> cliCmdExpectedResponse =
        CLITestingUtils.readCLIExpectedOutput(expectedOutputFilename);

    // log the response to stdout
    logger.info("cliCmdResponse: " + cliCmdResponse + "\n");

    // need to replace %xyz% variables in the CLI expected response with values from the Java HTTP
    // response
    Map<String, String> variablesMap =
        CLITestingUtils.buildMapOfDatasetVariablesToSwap(datasetDetails);

    // then do the expected to actual comparison, line by line
    CLITestingUtils.compareCLIExpectedOutput(cliCmdResponse, cliCmdExpectedResponse, variablesMap);
  }

  /**
   * CLI command : jc dr describe Repository API : GET : enumerateDatasets
   *
   * @throws IOException
   */
  public void drDescribeTest(Map<String, Object> datasetDetails, String expectedOutputFilename)
      throws IOException {
    String datasetName = datasetDetails.get("name").toString();
    logger.info("***********************************************");
    logger.info("jc dr describe " + datasetName);

    // call CLI command in a separate process
    List<String> cmdArgs = new ArrayList<>(Arrays.asList("dr", "describe", datasetName));
    List<String> cliCmdResponse = CLITestingUtils.callCLICommand(cmdArgs);

    // read in the expect CLI command output from a file
    List<String> cliCmdExpectedResponse =
        CLITestingUtils.readCLIExpectedOutput(expectedOutputFilename);

    // log the response to stdout
    logger.info("cliCmdResponse: " + cliCmdResponse + "\n");

    // need to replace %xyz% variables in the CLI expected response with values from the Java HTTP
    // response
    Map<String, String> variablesMap =
        CLITestingUtils.buildMapOfDatasetVariablesToSwap(datasetDetails);

    // then do the expected to actual comparison, line by line
    CLITestingUtils.compareCLIExpectedOutput(cliCmdResponse, cliCmdExpectedResponse, variablesMap);
  }

  /**
   * CLI command : jc dataset delete Repository API : POST : deleteDataset
   *
   * @throws IOException
   */
  public void datasetDeleteTest(Map<String, Object> datasetDetails, String expectedOutputFilename)
      throws IOException {
    String datasetName = datasetDetails.get("name").toString();
    logger.info("***********************************************");
    logger.info("jc dataset delete " + datasetName);

    // call CLI command in a separate process
    List<String> cmdArgs = new ArrayList<>(Arrays.asList("dataset", "delete", datasetName));
    List<String> cliCmdResponse = CLITestingUtils.callCLICommand(cmdArgs);

    // read in the expect CLI command output from a file
    List<String> cliCmdExpectedResponse =
        CLITestingUtils.readCLIExpectedOutput(expectedOutputFilename);

    // log the response to stdout
    logger.info("cliCmdResponse: " + cliCmdResponse + "\n");

    // no %xyz% variables to replace in the expected response before doing the comparison
    // so just build an empty map
    Map<String, String> variablesMap = new HashMap<>();

    // then do the expected to actual comparison, line by line
    CLITestingUtils.compareCLIExpectedOutput(cliCmdResponse, cliCmdExpectedResponse, variablesMap);
  }
}
