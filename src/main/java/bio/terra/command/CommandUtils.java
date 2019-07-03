package bio.terra.command;

import bio.terra.context.Context;
import bio.terra.context.ContextEnum;
import bio.terra.datarepo.api.RepositoryApi;
import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.EnumerateStudyModel;
import bio.terra.datarepo.model.ErrorModel;
import bio.terra.datarepo.model.JobModel;
import bio.terra.datarepo.model.StudySummaryModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CommandUtils {
    public static final String SLASH = "/";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static void printError(ApiException ex) {
        try {
            ErrorModel errorModel = objectMapper.readValue(ex.getResponseBody(), ErrorModel.class);
            System.out.printf("[%d] %s\n", ex.getCode(), errorModel.getMessage());
            if (errorModel.getErrorDetail() != null) {
                for (String detail : errorModel.getErrorDetail()) {
                    System.out.printf("  %s\n", detail);
                }
            }

        } catch (Exception omex) {
            System.out.printf("[%d] %s\n", ex.getCode(), ex.getMessage());
        }
    }

    public static <T> T waitForResponse(RepositoryApi api,
                                        JobModel job,
                                        int sleepSeconds,
                                        Class<T> tClass) {
        try {
            while (true) {
                String jobId = job.getId();
                int statusCode = api.getApiClient().getStatusCode();
                Map<String, List<String>> responseHeaders = api.getApiClient().getResponseHeaders();

                switch (statusCode) {
                    case 202:
                        // Not done case: sleep and check
                        TimeUnit.SECONDS.sleep(sleepSeconds);
                        job = api.retrieveJob(jobId);
                        break;

                    case 200:
                        // Done case: get the result with the header URL and return the response;
                        // let the caller interpret the response
                        Object object = api.retrieveJobResult(jobId);
                        return (T)object;

                    default:
                        throw new IllegalStateException("We shouldn't be here");
                }
            }
        } catch (InterruptedException ex) {
            System.err.println("Exit due to interrupt");
        } catch (ApiException ex) {
            printErrorAndExit("Failed to retrieve job status or result: " + ex.getMessage());
        }

        return null;
    }

    public static void printErrorAndExit(String message) {
        System.err.println(message);
        System.exit(1);
    }

    public static StudySummaryModel findStudyByName(String studyName) {
        try {
            EnumerateStudyModel enumerateStudy = DRApis.getRepositoryApi().enumerateStudies(0, 100000, null, null, studyName);

            List<StudySummaryModel> studies = enumerateStudy.getItems();
            for (StudySummaryModel summary : studies) {
                if (StringUtils.equals(summary.getName(), studyName)) {
                    return summary;
                }
            }
            CommandUtils.printErrorAndExit("Study not found: " + studyName);
            return null;

        } catch (ApiException ex) {
            throw new IllegalArgumentException("Error processing find study by name");
        }
    }

    public static String getObjectName(String path) {
        String[] pathParts = StringUtils.split(path, '/');
        return pathParts[pathParts.length - 1];
    }

    public static String makeFullPath(String inPath) {
        String pwd = Context.getInstance().getContextItem(ContextEnum.PWD);

        if (inPath == null) {
            inPath = pwd;
        }

        if (StringUtils.startsWith(inPath, SLASH)) {
            return inPath;
        }

        if (StringUtils.endsWith(pwd, SLASH)) {
            return pwd + inPath;
        }
        return pwd + SLASH + inPath;
    }

}
