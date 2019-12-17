package bio.terra.endpoints;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpStatusCodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Utility methods for sending HTTP requests and parsing the responses from a JSON string into a Java object.
 *   - sendJavaHttpRequest uses Java's HTTPURL library
 *   - sendCurlRequest uses curl
 */
public final class EndpointUtils {

    private EndpointUtils() { }

    /**
     * Sends an HTTP request using Java's HTTPURLConnection class.
     * @param urlStr where to direct the request
     * @param requestType the type of request, GET/PUT/POST/DELETE
     * @param accessToken the bearer token to include in the request, null if not required
     * @return a Java Map that includes the HTTP status code (under statusCode key) and the parsed JSON response
     * @throws IOException
     */
    public static Map<String, Object> sendJavaHttpRequest(String urlStr, String requestType, String accessToken)
            throws IOException {
        // open HTTP connection, make request
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod(requestType);
        if (accessToken != null) {
            con.setRequestProperty("Authorization", "Bearer " + accessToken);
        }

        // read the status code
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
        return buildResponseMap(responseBody.toString(), statusCode);
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
        return buildResponseMap(responseBody.toString(), statusCode);
    }

    /**
     * Parses the HTTP response body from a Java String that uses JSON format into a Java key/value Map.
     * This method uses the Jackson JSON mapping library (ObjectMapper).
     * @param responseBody the response body as a String that uses JSON format
     * @param statusCode the HTTP status code as an int
     * @return a Java Map that includes the HTTP status code (under statusCode key) and the parsed JSON response
     * @throws JsonProcessingException
     */
    private static Map<String, Object> buildResponseMap(String responseBody, int statusCode)
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

        // put the status code into the response map
        map.put("statusCode", statusCode);

        return map;
    }

}
