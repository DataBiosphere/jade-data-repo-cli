package bio.terra.command;

import bio.terra.context.Context;
import bio.terra.context.ContextEnum;
import bio.terra.datarepo.api.RepositoryApi;
import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.BillingProfileModel;
import bio.terra.datarepo.model.DatasetSummaryModel;
import bio.terra.datarepo.model.EnumerateBillingProfileModel;
import bio.terra.datarepo.model.EnumerateDatasetModel;
import bio.terra.datarepo.model.EnumerateSnapshotModel;
import bio.terra.datarepo.model.ErrorModel;
import bio.terra.datarepo.model.JobModel;
import bio.terra.datarepo.model.PolicyModel;
import bio.terra.datarepo.model.PolicyResponse;
import bio.terra.datarepo.model.SnapshotSummaryModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
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
                        // The object from retrieve is a LinkedHashMap (always AFAIK), so to get
                        // the right type, I cast it and then convert it back into a JSON string.
                        // TODO: this should handle ErrorModel and it doesn't!
                        Object object = api.retrieveJobResult(jobId);
                        LinkedHashMap<String, Object> hashMap = (LinkedHashMap<String, Object>) object;
                        String jsonString = objectMapper.writeValueAsString(hashMap);
                        return objectMapper.readValue(jsonString, tClass);

                    default:
                        throw new IllegalStateException("We shouldn't be here");
                }
            }
        } catch (InterruptedException ex) {
            System.err.println("Exit due to interrupt");
        } catch (ApiException ex) {
            printErrorAndExit("Failed to retrieve job status or result: " + ex.getMessage());
        } catch (IOException ex) {
            printErrorAndExit("Failed to parse job result: " + ex.getMessage());
        }

        return null;
    }

    public static void printErrorAndExit(String message) {
        System.err.println(message);
        System.exit(1);
    }

    public static DatasetSummaryModel findDatasetByName(String datasetName) {
        try {
            EnumerateDatasetModel enumerateDataset = DRApis.getRepositoryApi().enumerateDatasets(0, 100000, null, null, datasetName);

            List<DatasetSummaryModel> studies = enumerateDataset.getItems();
            for (DatasetSummaryModel summary : studies) {
                if (StringUtils.equals(summary.getName(), datasetName)) {
                    return summary;
                }
            }
            CommandUtils.printErrorAndExit("Dataset not found: " + datasetName);
            return null;

        } catch (ApiException ex) {
            throw new IllegalArgumentException("Error processing find dataset by name");
        }
    }

    public static SnapshotSummaryModel findSnapshotByName(String snapshotName) {
        try {
            EnumerateSnapshotModel enumerateSnapshot = DRApis.getRepositoryApi().enumerateSnapshots(0, 100000, null, null, snapshotName);

            List<SnapshotSummaryModel> studies = enumerateSnapshot.getItems();
            for (SnapshotSummaryModel summary : studies) {
                if (StringUtils.equals(summary.getName(), snapshotName)) {
                    return summary;
                }
            }
            CommandUtils.printErrorAndExit("Snapshot not found: " + snapshotName);
            return null;

        } catch (ApiException ex) {
            throw new IllegalArgumentException("Error processing find snapshot by name");
        }
    }

    public static BillingProfileModel findProfileByName(String profileName) {
        try {
            EnumerateBillingProfileModel enumerateProfiles = DRApis.getResourcesApi().enumerateProfiles(0, 100000);

            List<BillingProfileModel> profiles = enumerateProfiles.getItems();
            for (BillingProfileModel profile : profiles) {
                if (StringUtils.equals(profile.getProfileName(), profileName)) {
                    return profile;
                }
            }
            CommandUtils.printErrorAndExit("Profile not found: " + profileName);
            return null;

        } catch (ApiException ex) {
            throw new IllegalArgumentException("Error processing find dataset by name");
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


    public static void printPolicyResponse(PolicyResponse policyResponse) {
        for (PolicyModel policyModel : policyResponse.getPolicies()) {
            System.out.println("Policy " + policyModel.getName());
            for (String member : policyModel.getMembers()) {
                System.out.println("  " + member);
            }
            System.out.println();
        }
    }

}
