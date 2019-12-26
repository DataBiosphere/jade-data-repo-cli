package bio.terra.endpoints;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpStatusCodes;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Utility methods for sending HTTP requests and parsing the responses from a JSON string into a Java object.
 *   - sendJavaHttpRequest uses Java's HTTPURL library
 *   - sendCurlRequest uses curl
 *
 * Utility methods for running a CLI command in a separate process and reading in the expected response from a file.
 *   - callCLICommand uses Java's ProcessBuilder class
 *   - readCLIExpectedOutput read from a file in the resources/CLICommandTests directory
 */
public final class CLITestingUtils {

    private CLITestingUtils() { }

    /**
     * Call a CLI command in a separate process.
     * @param cmdArgs a list of the command line arguments=
     * @return a List of the lines written to stdout
     * @throws IOException
     */
    public static List<String> callCLICommand(List<String> cmdArgs) throws IOException {
        // build and run process
        cmdArgs.add(0, "./build/install/jadecli/bin/jadecli");
        ProcessBuilder procBuilder = new ProcessBuilder(cmdArgs);
        Process proc = procBuilder.start();

        // read in all lines written to stdout
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(proc.getInputStream(), Charset.defaultCharset()));
        String outputLine;
        List<String> outputLines = new ArrayList<>();
        while ((outputLine = bufferedReader.readLine()) != null) {
            outputLines.add(outputLine);
        }
        bufferedReader.close();

        return outputLines;
    }

    /**
     * Read from a file in the resources/CLICommandTests directory.
     * @param fileName does not include the directory path
     * @return a List of the lines in the file
     * @throws IOException
     */
    public static List<String> readCLIExpectedOutput(String fileName) throws IOException {
        // open file
        File expectedOutputFile = new File(CLITestingConfig.dirName, fileName);

        // read in all lines
        BufferedReader bufferedReader = new BufferedReader(new FileReader(expectedOutputFile));
        String outputLine;
        List<String> outputLines = new ArrayList<>();
        while ((outputLine = bufferedReader.readLine()) != null) {
            outputLines.add(outputLine);
        }
        bufferedReader.close();

        return outputLines;
    }

    /**
     * Read from a JSON format file in the resources/CLICommandTests directory.
     * @param fileName does not include the directory path
     * @return a Map of the input object
     * @throws IOException
     */
    public static Map<String, Object> readCLIInput(String fileName) throws IOException {
        // open file
        File inputFile = new File(CLITestingConfig.dirName, fileName);

        // read in all lines
        BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
        StringBuilder inputFileStr = new StringBuilder();
        String inputLine;
        while ((inputLine = bufferedReader.readLine()) != null) {
            inputFileStr.append(inputLine);
        }
        bufferedReader.close();

        return buildMapFromJSON(inputFileStr.toString(), null);
    }

    /**
     * Sends an HTTP request using Java's HTTPURLConnection class.
     * @param urlStr where to direct the request
     * @param requestType the type of request, GET/PUT/POST/DELETE
     * @param accessToken the bearer token to include in the request, null if not required
     * @param params map of request parameters
     * @return a Java Map that includes the HTTP status code (under statusCode key) and the parsed JSON response
     * @throws IOException
     */
    public static Map<String, Object> sendJavaHttpRequest(
            String urlStr, String requestType, String accessToken, Map<String, String> params) throws IOException {
        // build parameter string
        String paramsStr = "";
        if (params != null && params.size() > 0) {
            StringBuilder paramsStrBuilder = new StringBuilder();
            for (Map.Entry<String, String> mapEntry : params.entrySet()) {
                paramsStrBuilder.append(URLEncoder.encode(mapEntry.getKey(), "UTF-8"));
                paramsStrBuilder.append("=");
                paramsStrBuilder.append(URLEncoder.encode(mapEntry.getValue(), "UTF-8"));
                paramsStrBuilder.append("&");
            }
            paramsStr = paramsStrBuilder.toString();
        }

        // for GET requests, append the parameters to the URL
        if (requestType == "GET") {
            urlStr += "?" + paramsStr;
        }

        // open HTTP connection
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        // set header properties
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod(requestType);
        if (accessToken != null) {
            con.setRequestProperty("Authorization", "Bearer " + accessToken);
        }

        // for other request types, write the parameters to the request body
        if (requestType != "GET") {
            con.setDoOutput(true);
            DataOutputStream outputStream = new DataOutputStream(con.getOutputStream());
            outputStream.writeBytes(paramsStr);
            outputStream.flush();
            outputStream.close();
        }

        // send the request and read the returned status code
        int statusCode = con.getResponseCode();

        // select the appropriate input stream depending on the status code
        InputStream inputStream;
        if (HttpStatusCodes.isSuccess(statusCode)) {
            inputStream = con.getInputStream();
        } else {
            inputStream = con.getErrorStream();
        }

        // read the response body
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));
        String inputLine;
        StringBuffer responseBody = new StringBuffer();
        while ((inputLine = bufferedReader.readLine()) != null) {
            responseBody.append(inputLine);
        }
        bufferedReader.close();

        // close HTTP connection
        con.disconnect();

        // build and return the response map
        return buildMapFromJSON(responseBody.toString(), statusCode);
    }

    /**
     * Sends an HTTP request using curl running in a separate process.
     * @param urlStr where to direct the request
     * @param requestType the type of request, GET/PUT/POST/DELETE
     * @param accessToken the bearer token to include in the request, null if not required
     * @return a Java Map that includes the HTTP status code (under statusCode key) and the parsed JSON response
     * @throws IOException
     */
    public static Map<String, Object> sendCurlRequest(String urlStr, String requestType, String accessToken)
            throws IOException {
        // build and run process
        List<String> cmdArgs = new ArrayList<String>(Arrays.asList(
                new String[]{"curl", "-i", "-H", requestType, urlStr, "-H", "accept:application/json"}));
        if (accessToken != null) {
            cmdArgs.add("-H");
            cmdArgs.add("Authorization: Bearer " + accessToken);
        }
        ProcessBuilder procBuilder = new ProcessBuilder(cmdArgs);
        Process proc = procBuilder.start();

        // first line of the header output contains the status code. it looks like:
        // HTTP/1.1 200
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(proc.getInputStream(), Charset.defaultCharset()));
        String statusCodeLine = bufferedReader.readLine();
        if (statusCodeLine == null) {
            throw new RuntimeException("no process output");
        }

        // the status code is the integer after the space in the first line
        String[] statusCodeLineSplit = statusCodeLine.split(" ");
        if (statusCodeLineSplit.length < 2) {
            throw new RuntimeException("status code line not decoded correctly: " + statusCodeLine);
        }
        int statusCode = Integer.parseInt(statusCodeLineSplit[1]);

        // until the empty line are the rest of the header output. they look like:
        // Content-Type: application/json;charset=UTF-8
        // Transfer-Encoding: chunked
        // Date: Tue, 17 Dec 2019 15:58:03 GMT
        //
        String inputLine;
        while ((inputLine = bufferedReader.readLine()) != null) {
            if (inputLine.equals("")) {
                break;
            }
        }

        // remaining lines are the response body
        StringBuffer responseBody = new StringBuffer();
        while ((inputLine = bufferedReader.readLine()) != null) {
            responseBody.append(inputLine);
        }
        bufferedReader.close();

        // build and return the response map
        return buildMapFromJSON(responseBody.toString(), statusCode);
    }

    /**
     * Parses the HTTP response body from a Java String that uses JSON format into a Java key/value Map.
     * This method uses the Jackson JSON mapping library (ObjectMapper).
     * @param responseBody the response body as a String that uses JSON format
     * @param statusCode the HTTP status code as an Integer, ignored if null
     * @return a Java Map that includes the HTTP status code (under statusCode key) and the parsed JSON response
     * @throws JsonProcessingException
     */
    private static Map<String, Object> buildMapFromJSON(String responseBody, Integer statusCode)
            throws JsonProcessingException {
        // JSON parse the response body into a Java Map
        Map<String, Object> map;
        if (responseBody.equals("")) {
            // create an empty map if no response body
            map = new HashMap<String, Object>();
        } else {
            // create and populate a map using the Jackson JSON mapping library
            ObjectMapper objMapper = new ObjectMapper();
            map = objMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {
            });
        }

        if (statusCode != null) {
            // put the status code into the response map
            map.put("statusCode", statusCode);
        }

        return map;
    }

}
