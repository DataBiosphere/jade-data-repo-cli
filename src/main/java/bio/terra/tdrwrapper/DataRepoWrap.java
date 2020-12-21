package bio.terra.tdrwrapper;

// This class wraps the TDR client, providing these features:
// 1. Raw ApiExceptions are mapped to specific DataRepoClient exceptions based on the http error
// status. Those exceptions have the ErrorModel deserialized.
// 2. Futures for waiting for and retrieving results of async calls.
// 3. Methods that automatically wait.

import bio.terra.datarepo.api.RepositoryApi;
import bio.terra.datarepo.api.ResourcesApi;
import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.BillingProfileModel;
import bio.terra.datarepo.model.BillingProfileRequestModel;
import bio.terra.datarepo.model.BulkLoadArrayRequestModel;
import bio.terra.datarepo.model.BulkLoadArrayResultModel;
import bio.terra.datarepo.model.DatasetModel;
import bio.terra.datarepo.model.DatasetRequestModel;
import bio.terra.datarepo.model.DatasetSummaryModel;
import bio.terra.datarepo.model.DeleteResponseModel;
import bio.terra.datarepo.model.EnumerateBillingProfileModel;
import bio.terra.datarepo.model.EnumerateDatasetModel;
import bio.terra.datarepo.model.EnumerateSnapshotModel;
import bio.terra.datarepo.model.ErrorModel;
import bio.terra.datarepo.model.FileModel;
import bio.terra.datarepo.model.IngestRequestModel;
import bio.terra.datarepo.model.IngestResponseModel;
import bio.terra.datarepo.model.JobModel;
import bio.terra.datarepo.model.PolicyMemberRequest;
import bio.terra.datarepo.model.PolicyResponse;
import bio.terra.datarepo.model.SnapshotModel;
import bio.terra.datarepo.model.SnapshotRequestModel;
import bio.terra.datarepo.model.SnapshotSummaryModel;
import bio.terra.tdrwrapper.exception.DataRepoBadRequestClientException;
import bio.terra.tdrwrapper.exception.DataRepoClientException;
import bio.terra.tdrwrapper.exception.DataRepoConflictClientException;
import bio.terra.tdrwrapper.exception.DataRepoForbiddenClientException;
import bio.terra.tdrwrapper.exception.DataRepoInternalServiceClientException;
import bio.terra.tdrwrapper.exception.DataRepoNotFoundClientException;
import bio.terra.tdrwrapper.exception.DataRepoNotImplementedClientException;
import bio.terra.tdrwrapper.exception.DataRepoServiceUnavailableClientException;
import bio.terra.tdrwrapper.exception.DataRepoUnauthorizedClientException;
import bio.terra.tdrwrapper.exception.DataRepoUnknownClientException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpStatusCodes;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DataRepoWrap {
  private static final Logger logger = LoggerFactory.getLogger(DataRepoWrap.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();

  private final RepositoryApi repositoryApi;
  private final ResourcesApi resourcesApi;

  public DataRepoWrap(RepositoryApi repositoryApi, ResourcesApi resourcesApi) {
    this.repositoryApi = repositoryApi;
    this.resourcesApi = resourcesApi;
  }

  /**
   * Function wrapper that converts openapi ApiException into DataRepoClient exceptions
   *
   * @param function a datarepo client call
   * @param <T> success return type
   * @return T returned on success
   * @throws DataRepoClientException usually a DataRepoClientException subclass
   */
  public static <T> T apiCallThrow(ApiFunction<T> function) {
    try {
      return function.apply();
    } catch (ApiException apiException) {
      throw fromApiException(apiException);
    }
  }

  private static DataRepoClientException fromApiException(ApiException apiException) {
    int statusCode = apiException.getCode();
    List<String> errorDetails = null;
    String message = StringUtils.EMPTY;
    // We don't expect to see this case, but don't want to barf if it happens
    if (!HttpStatusCodes.isSuccess(statusCode)) {
      String responseBody = apiException.getResponseBody();
      if (responseBody != null) {
        try {
          ErrorModel errorModel = objectMapper.readValue(responseBody, ErrorModel.class);
          errorDetails = errorModel.getErrorDetail();
          message = errorModel.getMessage();
        } catch (JsonProcessingException ex) {
          message = responseBody;
        }
      }
    }
    switch (statusCode) {
      case HttpStatusCodes.STATUS_CODE_BAD_REQUEST:
        return new DataRepoBadRequestClientException(
            message, statusCode, errorDetails, apiException);
      case HttpStatusCodes.STATUS_CODE_CONFLICT:
        return new DataRepoConflictClientException(message, statusCode, errorDetails, apiException);
      case HttpStatusCodes.STATUS_CODE_FORBIDDEN:
        return new DataRepoForbiddenClientException(
            message, statusCode, errorDetails, apiException);
      case HttpStatusCodes.STATUS_CODE_NOT_FOUND:
        return new DataRepoNotFoundClientException(message, statusCode, errorDetails, apiException);
      case HttpStatusCodes.STATUS_CODE_SERVER_ERROR:
        return new DataRepoInternalServiceClientException(
            message, statusCode, errorDetails, apiException);
      case HttpStatusCodes.STATUS_CODE_SERVICE_UNAVAILABLE:
        return new DataRepoServiceUnavailableClientException(
            "Service Unavailable", statusCode, errorDetails, apiException);
      case HttpStatusCodes.STATUS_CODE_UNAUTHORIZED:
        return new DataRepoUnauthorizedClientException(
            message, statusCode, errorDetails, apiException);
      case 501: // not implemented - no HttpStatusCodes for that
        return new DataRepoNotImplementedClientException(
            message, statusCode, errorDetails, apiException);
      default:
        return new DataRepoUnknownClientException(
            "Unknown Exception", statusCode, errorDetails, apiException);
    }
  }

  // -- billing profile alphabetically --

  public PolicyResponse addProfilePolicyMember(
      String profileId, String policyName, String userEmail) {
    PolicyMemberRequest addRequest = new PolicyMemberRequest().email(userEmail);
    return apiCallThrow(
        () -> resourcesApi.addProfilePolicyMember(addRequest, profileId, policyName));
  }

  public BillingProfileModel createProfile(BillingProfileRequestModel createProfileRequest) {

    WrapFuture<BillingProfileModel> wrapFuture = createProfileFuture(createProfileRequest);

    return wrapFuture.getResult();
  }

  public WrapFuture<BillingProfileModel> createProfileFuture(
      BillingProfileRequestModel createProfileRequest) {
    JobModel jobResponse = apiCallThrow(() -> resourcesApi.createProfile(createProfileRequest));
    return new WrapFuture<>(jobResponse.getId(), repositoryApi, BillingProfileModel.class);
  }

  public DeleteResponseModel deleteProfile(String profileId) {
    WrapFuture<DeleteResponseModel> wrapFuture = deleteProfileFuture(profileId);
    return wrapFuture.getResult();
  }

  public WrapFuture<DeleteResponseModel> deleteProfileFuture(String profileId) {
    JobModel jobResponse = apiCallThrow(() -> resourcesApi.deleteProfile(profileId));
    return new WrapFuture<>(jobResponse.getId(), repositoryApi, DeleteResponseModel.class);
  }

  public PolicyResponse deleteProfilePolicyMember(
      String profileId, String policyName, String userEmail) {

    return apiCallThrow(
        () -> resourcesApi.deleteProfilePolicyMember(profileId, policyName, userEmail));
  }

  public EnumerateBillingProfileModel enumerateProfiles(Integer offset, Integer limit) {
    return DataRepoWrap.apiCallThrow(() -> resourcesApi.enumerateProfiles(offset, limit));
  }

  public BillingProfileModel retrieveProfile(String profileId) {
    return DataRepoWrap.apiCallThrow(() -> resourcesApi.retrieveProfile(profileId));
  }

  public PolicyResponse retrieveProfilePolicies(String id) {
    return apiCallThrow(() -> resourcesApi.retrieveProfilePolicies(id));
  }

  // -- dataset --

  public PolicyResponse addDatasetPolicyMember(String id, String policyName, String userEmail) {
    PolicyMemberRequest addRequest = new PolicyMemberRequest().email(userEmail);
    return apiCallThrow(() -> repositoryApi.addDatasetPolicyMember(id, policyName, addRequest));
  }

  public DatasetSummaryModel createDataset(DatasetRequestModel createDatasetRequest) {

    WrapFuture<DatasetSummaryModel> wrapFuture = createDatasetFuture(createDatasetRequest);

    return wrapFuture.getResult();
  }

  public WrapFuture<DatasetSummaryModel> createDatasetFuture(
      DatasetRequestModel createDatasetRequest) {

    JobModel jobResponse = apiCallThrow(() -> repositoryApi.createDataset(createDatasetRequest));

    return new WrapFuture<>(jobResponse.getId(), repositoryApi, DatasetSummaryModel.class);
  }

  public DeleteResponseModel deleteDataset(String id) {
    WrapFuture<DeleteResponseModel> wrapFuture = deleteDatasetFuture(id);
    return wrapFuture.getResult();
  }

  public WrapFuture<DeleteResponseModel> deleteDatasetFuture(String id) {
    JobModel jobResponse = apiCallThrow(() -> repositoryApi.deleteDataset(id));

    return new WrapFuture<>(jobResponse.getId(), repositoryApi, DeleteResponseModel.class);
  }

  public PolicyResponse deleteDatasetPolicyMember(String id, String policyName, String email) {
    return apiCallThrow(() -> repositoryApi.deleteDatasetPolicyMember(id, policyName, email));
  }

  public EnumerateDatasetModel enumerateDatasets(
      int offset, int limit, String sort, String direction, String filter) {
    return apiCallThrow(
        () -> repositoryApi.enumerateDatasets(offset, limit, sort, direction, filter));
  }

  public IngestResponseModel ingestDataset(String id, IngestRequestModel ingestRequest) {
    WrapFuture<IngestResponseModel> wrapFuture = ingestDatasetFuture(id, ingestRequest);
    return wrapFuture.getResult();
  }

  public WrapFuture<IngestResponseModel> ingestDatasetFuture(
      String id, IngestRequestModel ingestRequest) {
    JobModel jobResponse = apiCallThrow(() -> repositoryApi.ingestDataset(id, ingestRequest));
    return new WrapFuture<>(jobResponse.getId(), repositoryApi, IngestResponseModel.class);
  }

  public DatasetModel retrieveDataset(String id) {
    return apiCallThrow(() -> repositoryApi.retrieveDataset(id));
  }

  public PolicyResponse retrieveDatasetPolicies(String id) {
    return apiCallThrow(() -> repositoryApi.retrieveDatasetPolicies(id));
  }

  // -- file --

  public BulkLoadArrayResultModel bulkFileLoadArray(
      String id, BulkLoadArrayRequestModel loadRequest) {
    WrapFuture<BulkLoadArrayResultModel> wrapFuture = bulkFileLoadArrayFuture(id, loadRequest);
    return wrapFuture.getResult();
  }

  public WrapFuture<BulkLoadArrayResultModel> bulkFileLoadArrayFuture(
      String id, BulkLoadArrayRequestModel loadRequest) {
    JobModel jobResponse = apiCallThrow(() -> repositoryApi.bulkFileLoadArray(id, loadRequest));
    return new WrapFuture<>(jobResponse.getId(), repositoryApi, BulkLoadArrayResultModel.class);
  }

  public FileModel lookupFileById(String id, String fileId, int depth) {
    return apiCallThrow(() -> repositoryApi.lookupFileById(id, fileId, depth));
  }

  public FileModel lookupFileByPath(String id, String filePath, int depth) {
    return apiCallThrow(() -> repositoryApi.lookupFileByPath(id, filePath, depth));
  }

  public FileModel lookupSnapshotFileById(String id, String fileId, int depth) {
    return apiCallThrow(() -> repositoryApi.lookupSnapshotFileById(id, fileId, depth));
  }

  public FileModel lookupSnapshotFileByPath(String id, String filePath, int depth) {
    return apiCallThrow(() -> repositoryApi.lookupSnapshotFileByPath(id, filePath, depth));
  }

  // -- snapshot --

  public PolicyResponse addSnapshotPolicyMember(String id, String policyName, String userEmail) {
    PolicyMemberRequest addRequest = new PolicyMemberRequest().email(userEmail);
    return apiCallThrow(() -> repositoryApi.addSnapshotPolicyMember(id, policyName, addRequest));
  }

  public SnapshotSummaryModel createSnapshot(SnapshotRequestModel snapshotRequest) {
    WrapFuture<SnapshotSummaryModel> wrapFuture = createSnapshotFuture(snapshotRequest);
    return wrapFuture.getResult();
  }

  public WrapFuture<SnapshotSummaryModel> createSnapshotFuture(
      SnapshotRequestModel snapshotRequest) {
    JobModel jobResponse = apiCallThrow(() -> repositoryApi.createSnapshot(snapshotRequest));
    return new WrapFuture<>(jobResponse.getId(), repositoryApi, SnapshotSummaryModel.class);
  }

  public DeleteResponseModel deleteSnapshot(String id) {
    WrapFuture<DeleteResponseModel> wrapFuture = deleteSnapshotFuture(id);
    return wrapFuture.getResult();
  }

  public WrapFuture<DeleteResponseModel> deleteSnapshotFuture(String id) {
    JobModel jobResponse = apiCallThrow(() -> repositoryApi.deleteSnapshot(id));

    return new WrapFuture<>(jobResponse.getId(), repositoryApi, DeleteResponseModel.class);
  }

  public PolicyResponse deleteSnapshotPolicyMember(String id, String policyName, String userEmail) {
    return apiCallThrow(() -> repositoryApi.deleteDatasetPolicyMember(id, policyName, userEmail));
  }

  public EnumerateSnapshotModel enumerateSnapshots(
      int offset, int limit, String sort, String direction, String filter) {
    return apiCallThrow(
        () -> repositoryApi.enumerateSnapshots(offset, limit, sort, direction, filter));
  }

  public SnapshotModel retrieveSnapshot(String id) {
    return apiCallThrow(() -> repositoryApi.retrieveSnapshot(id));
  }

  public PolicyResponse retrieveSnapshotPolicies(String id) {
    return apiCallThrow(() -> repositoryApi.retrieveSnapshotPolicies(id));
  }
}
