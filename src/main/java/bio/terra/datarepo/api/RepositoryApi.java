package bio.terra.datarepo.api;

import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.client.ApiClient;
import bio.terra.datarepo.client.Configuration;
import bio.terra.datarepo.client.Pair;

import javax.ws.rs.core.GenericType;

import bio.terra.datarepo.model.DatasetModel;
import bio.terra.datarepo.model.DatasetRequestModel;
import bio.terra.datarepo.model.DeleteResponseModel;
import bio.terra.datarepo.model.EnumerateDatasetModel;
import bio.terra.datarepo.model.EnumerateStudyModel;
import bio.terra.datarepo.model.ErrorModel;
import bio.terra.datarepo.model.FSObjectModel;
import bio.terra.datarepo.model.FileLoadModel;
import bio.terra.datarepo.model.IngestRequestModel;
import bio.terra.datarepo.model.JobModel;
import bio.terra.datarepo.model.PolicyMemberRequest;
import bio.terra.datarepo.model.PolicyResponse;
import bio.terra.datarepo.model.StudyModel;
import bio.terra.datarepo.model.StudyRequestModel;
import bio.terra.datarepo.model.StudySummaryModel;
import bio.terra.datarepo.model.UserStatusInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-06-12T09:29:10.549-04:00")
public class RepositoryApi {
  private ApiClient apiClient;

  public RepositoryApi() {
    this(Configuration.getDefaultApiClient());
  }

  public RepositoryApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  /**
   * 
   * Adds a member to the specified policy for the dataset
   * @param id A study or dataset id (required)
   * @param policyName The relevant policy (required)
   * @param policyMember Dataset to change the policy of (optional)
   * @return PolicyResponse
   * @throws ApiException if fails to make API call
   */
  public PolicyResponse addDatasetPolicyMember(String id, String policyName, PolicyMemberRequest policyMember) throws ApiException {
    Object localVarPostBody = policyMember;
    
    // verify the required parameter 'id' is set
    if (id == null) {
      throw new ApiException(400, "Missing the required parameter 'id' when calling addDatasetPolicyMember");
    }
    
    // verify the required parameter 'policyName' is set
    if (policyName == null) {
      throw new ApiException(400, "Missing the required parameter 'policyName' when calling addDatasetPolicyMember");
    }
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/datasets/{id}/policies/{policyName}/members"
      .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()))
      .replaceAll("\\{" + "policyName" + "\\}", apiClient.escapeString(policyName.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      "application/json"
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<PolicyResponse> localVarReturnType = new GenericType<PolicyResponse>() {};
    return apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Adds a member to the specified policy for the dataset
   * @param id A study or dataset id (required)
   * @param policyName The relevant policy (required)
   * @param policyMember Dataset to change the policy of (optional)
   * @return PolicyResponse
   * @throws ApiException if fails to make API call
   */
  public PolicyResponse addStudyPolicyMember(String id, String policyName, PolicyMemberRequest policyMember) throws ApiException {
    Object localVarPostBody = policyMember;
    
    // verify the required parameter 'id' is set
    if (id == null) {
      throw new ApiException(400, "Missing the required parameter 'id' when calling addStudyPolicyMember");
    }
    
    // verify the required parameter 'policyName' is set
    if (policyName == null) {
      throw new ApiException(400, "Missing the required parameter 'policyName' when calling addStudyPolicyMember");
    }
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/studies/{id}/policies/{policyName}/members"
      .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()))
      .replaceAll("\\{" + "policyName" + "\\}", apiClient.escapeString(policyName.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      "application/json"
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<PolicyResponse> localVarReturnType = new GenericType<PolicyResponse>() {};
    return apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Create a new dataset
   * @param dataset Dataset to create (optional)
   * @return JobModel
   * @throws ApiException if fails to make API call
   */
  public JobModel createDataset(DatasetRequestModel dataset) throws ApiException {
    Object localVarPostBody = dataset;
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/datasets";

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      "application/json"
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<JobModel> localVarReturnType = new GenericType<JobModel>() {};
    return apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Create a new study
   * @param study Study to create (optional)
   * @return StudySummaryModel
   * @throws ApiException if fails to make API call
   */
  public StudySummaryModel createStudy(StudyRequestModel study) throws ApiException {
    Object localVarPostBody = study;
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/studies";

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      "application/json"
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<StudySummaryModel> localVarReturnType = new GenericType<StudySummaryModel>() {};
    return apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Delete a dataset by id
   * @param id A study or dataset id (required)
   * @return JobModel
   * @throws ApiException if fails to make API call
   */
  public JobModel deleteDataset(String id) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'id' is set
    if (id == null) {
      throw new ApiException(400, "Missing the required parameter 'id' when calling deleteDataset");
    }
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/datasets/{id}"
      .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<JobModel> localVarReturnType = new GenericType<JobModel>() {};
    return apiClient.invokeAPI(localVarPath, "DELETE", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Adds a member to the specified policy for the dataset
   * @param id A study or dataset id (required)
   * @param policyName The relevant policy (required)
   * @param memberEmail The email of the user to remove (required)
   * @return PolicyResponse
   * @throws ApiException if fails to make API call
   */
  public PolicyResponse deleteDatasetPolicyMember(String id, String policyName, String memberEmail) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'id' is set
    if (id == null) {
      throw new ApiException(400, "Missing the required parameter 'id' when calling deleteDatasetPolicyMember");
    }
    
    // verify the required parameter 'policyName' is set
    if (policyName == null) {
      throw new ApiException(400, "Missing the required parameter 'policyName' when calling deleteDatasetPolicyMember");
    }
    
    // verify the required parameter 'memberEmail' is set
    if (memberEmail == null) {
      throw new ApiException(400, "Missing the required parameter 'memberEmail' when calling deleteDatasetPolicyMember");
    }
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/datasets/{id}/policies/{policyName}/members/{memberEmail}"
      .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()))
      .replaceAll("\\{" + "policyName" + "\\}", apiClient.escapeString(policyName.toString()))
      .replaceAll("\\{" + "memberEmail" + "\\}", apiClient.escapeString(memberEmail.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<PolicyResponse> localVarReturnType = new GenericType<PolicyResponse>() {};
    return apiClient.invokeAPI(localVarPath, "DELETE", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Hard delete of a file by id. The file is deleted even if it is in use by a study. Subsequent lookups will give not found errors. 
   * @param id A study or dataset id (required)
   * @param fileid A file id (required)
   * @return JobModel
   * @throws ApiException if fails to make API call
   */
  public JobModel deleteFile(String id, String fileid) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'id' is set
    if (id == null) {
      throw new ApiException(400, "Missing the required parameter 'id' when calling deleteFile");
    }
    
    // verify the required parameter 'fileid' is set
    if (fileid == null) {
      throw new ApiException(400, "Missing the required parameter 'fileid' when calling deleteFile");
    }
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/studies/{id}/files/{fileid}"
      .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()))
      .replaceAll("\\{" + "fileid" + "\\}", apiClient.escapeString(fileid.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<JobModel> localVarReturnType = new GenericType<JobModel>() {};
    return apiClient.invokeAPI(localVarPath, "DELETE", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Delete a study by id
   * @param id A study or dataset id (required)
   * @return DeleteResponseModel
   * @throws ApiException if fails to make API call
   */
  public DeleteResponseModel deleteStudy(String id) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'id' is set
    if (id == null) {
      throw new ApiException(400, "Missing the required parameter 'id' when calling deleteStudy");
    }
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/studies/{id}"
      .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<DeleteResponseModel> localVarReturnType = new GenericType<DeleteResponseModel>() {};
    return apiClient.invokeAPI(localVarPath, "DELETE", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Adds a member to the specified policy for the dataset
   * @param id A study or dataset id (required)
   * @param policyName The relevant policy (required)
   * @param memberEmail The email of the user to remove (required)
   * @return PolicyResponse
   * @throws ApiException if fails to make API call
   */
  public PolicyResponse deleteStudyPolicyMember(String id, String policyName, String memberEmail) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'id' is set
    if (id == null) {
      throw new ApiException(400, "Missing the required parameter 'id' when calling deleteStudyPolicyMember");
    }
    
    // verify the required parameter 'policyName' is set
    if (policyName == null) {
      throw new ApiException(400, "Missing the required parameter 'policyName' when calling deleteStudyPolicyMember");
    }
    
    // verify the required parameter 'memberEmail' is set
    if (memberEmail == null) {
      throw new ApiException(400, "Missing the required parameter 'memberEmail' when calling deleteStudyPolicyMember");
    }
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/studies/{id}/policies/{policyName}/members/{memberEmail}"
      .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()))
      .replaceAll("\\{" + "policyName" + "\\}", apiClient.escapeString(policyName.toString()))
      .replaceAll("\\{" + "memberEmail" + "\\}", apiClient.escapeString(memberEmail.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<PolicyResponse> localVarReturnType = new GenericType<PolicyResponse>() {};
    return apiClient.invokeAPI(localVarPath, "DELETE", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Returns a list of all of the datasets the caller has access to 
   * @param offset The number of items to skip before starting to collect the result set. (optional, default to 0)
   * @param limit The numbers of items to return. (optional, default to 10)
   * @param sort The field to use for sorting. (optional, default to created_date)
   * @param direction The direction to sort. (optional, default to desc)
   * @param filter Filter the results where this string is a case insensitive match in the name or description. (optional)
   * @return EnumerateDatasetModel
   * @throws ApiException if fails to make API call
   */
  public EnumerateDatasetModel enumerateDatasets(Integer offset, Integer limit, String sort, String direction, String filter) throws ApiException {
    Object localVarPostBody = null;
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/datasets";

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();

    localVarQueryParams.addAll(apiClient.parameterToPairs("", "offset", offset));
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "limit", limit));
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "sort", sort));
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "direction", direction));
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "filter", filter));

    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<EnumerateDatasetModel> localVarReturnType = new GenericType<EnumerateDatasetModel>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Returns a list of all of the jobs the caller has access to 
   * @param offset The number of items to skip before starting to collect the result set. (optional, default to 0)
   * @param limit The numbers of items to return. (optional, default to 10)
   * @return List&lt;JobModel&gt;
   * @throws ApiException if fails to make API call
   */
  public List<JobModel> enumerateJobs(Integer offset, Integer limit) throws ApiException {
    Object localVarPostBody = null;
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/jobs";

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();

    localVarQueryParams.addAll(apiClient.parameterToPairs("", "offset", offset));
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "limit", limit));

    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<List<JobModel>> localVarReturnType = new GenericType<List<JobModel>>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Returns a list of all of the studies the caller has access to 
   * @param offset The number of studies to skip before when retrieving the next page (optional, default to 0)
   * @param limit The numbers studies to retrieve and return. (optional, default to 10)
   * @param sort The field to use for sorting. (optional, default to created_date)
   * @param direction The direction to sort. (optional, default to desc)
   * @param filter Filter the results where this string is a case insensitive match in the name or description. (optional)
   * @return EnumerateStudyModel
   * @throws ApiException if fails to make API call
   */
  public EnumerateStudyModel enumerateStudies(Integer offset, Integer limit, String sort, String direction, String filter) throws ApiException {
    Object localVarPostBody = null;
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/studies";

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();

    localVarQueryParams.addAll(apiClient.parameterToPairs("", "offset", offset));
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "limit", limit));
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "sort", sort));
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "direction", direction));
    localVarQueryParams.addAll(apiClient.parameterToPairs("", "filter", filter));

    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<EnumerateStudyModel> localVarReturnType = new GenericType<EnumerateStudyModel>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Ingest one file into the study file system; async returns a FSObjectModel
   * @param id A study or dataset id (required)
   * @param ingestFile Ingest file request (optional)
   * @return JobModel
   * @throws ApiException if fails to make API call
   */
  public JobModel ingestFile(String id, FileLoadModel ingestFile) throws ApiException {
    Object localVarPostBody = ingestFile;
    
    // verify the required parameter 'id' is set
    if (id == null) {
      throw new ApiException(400, "Missing the required parameter 'id' when calling ingestFile");
    }
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/studies/{id}/files"
      .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      "application/json"
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<JobModel> localVarReturnType = new GenericType<JobModel>() {};
    return apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Ingest data into a study table
   * @param id A study or dataset id (required)
   * @param ingest Ingest request (optional)
   * @return JobModel
   * @throws ApiException if fails to make API call
   */
  public JobModel ingestStudy(String id, IngestRequestModel ingest) throws ApiException {
    Object localVarPostBody = ingest;
    
    // verify the required parameter 'id' is set
    if (id == null) {
      throw new ApiException(400, "Missing the required parameter 'id' when calling ingestStudy");
    }
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/studies/{id}/ingest"
      .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      "application/json"
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<JobModel> localVarReturnType = new GenericType<JobModel>() {};
    return apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Lookup metadata for one file
   * @param id A study or dataset id (required)
   * @param fileid A file id (required)
   * @return FSObjectModel
   * @throws ApiException if fails to make API call
   */
  public FSObjectModel lookupFileObjectById(String id, String fileid) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'id' is set
    if (id == null) {
      throw new ApiException(400, "Missing the required parameter 'id' when calling lookupFileObjectById");
    }
    
    // verify the required parameter 'fileid' is set
    if (fileid == null) {
      throw new ApiException(400, "Missing the required parameter 'fileid' when calling lookupFileObjectById");
    }
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/studies/{id}/files/{fileid}"
      .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()))
      .replaceAll("\\{" + "fileid" + "\\}", apiClient.escapeString(fileid.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<FSObjectModel> localVarReturnType = new GenericType<FSObjectModel>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Lookup metadata for one file
   * @param id A study or dataset id (required)
   * @param path Full path to a file or directory (required)
   * @return FSObjectModel
   * @throws ApiException if fails to make API call
   */
  public FSObjectModel lookupFileObjectByPath(String id, String path) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'id' is set
    if (id == null) {
      throw new ApiException(400, "Missing the required parameter 'id' when calling lookupFileObjectByPath");
    }
    
    // verify the required parameter 'path' is set
    if (path == null) {
      throw new ApiException(400, "Missing the required parameter 'path' when calling lookupFileObjectByPath");
    }
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/studies/{id}/filesystem/objects"
      .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();

    localVarQueryParams.addAll(apiClient.parameterToPairs("", "path", path));

    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<FSObjectModel> localVarReturnType = new GenericType<FSObjectModel>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Retrieve a dataset by id
   * @param id A study or dataset id (required)
   * @return DatasetModel
   * @throws ApiException if fails to make API call
   */
  public DatasetModel retrieveDataset(String id) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'id' is set
    if (id == null) {
      throw new ApiException(400, "Missing the required parameter 'id' when calling retrieveDataset");
    }
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/datasets/{id}"
      .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<DatasetModel> localVarReturnType = new GenericType<DatasetModel>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Retrieve the read and discover policies for the dataset
   * @param id A study or dataset id (required)
   * @return PolicyResponse
   * @throws ApiException if fails to make API call
   */
  public PolicyResponse retrieveDatasetPolicies(String id) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'id' is set
    if (id == null) {
      throw new ApiException(400, "Missing the required parameter 'id' when calling retrieveDatasetPolicies");
    }
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/datasets/{id}/policies"
      .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<PolicyResponse> localVarReturnType = new GenericType<PolicyResponse>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Retrieve a job&#39;s status by id
   * @param id A study or dataset id (required)
   * @return JobModel
   * @throws ApiException if fails to make API call
   */
  public JobModel retrieveJob(String id) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'id' is set
    if (id == null) {
      throw new ApiException(400, "Missing the required parameter 'id' when calling retrieveJob");
    }
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/jobs/{id}"
      .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<JobModel> localVarReturnType = new GenericType<JobModel>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Retrieve a job&#39;s result by id
   * @param id A study or dataset id (required)
   * @return Object
   * @throws ApiException if fails to make API call
   */
  public Object retrieveJobResult(String id) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'id' is set
    if (id == null) {
      throw new ApiException(400, "Missing the required parameter 'id' when calling retrieveJobResult");
    }
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/jobs/{id}/result"
      .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<Object> localVarReturnType = new GenericType<Object>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Retrieve a study by id
   * @param id A study or dataset id (required)
   * @return StudyModel
   * @throws ApiException if fails to make API call
   */
  public StudyModel retrieveStudy(String id) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'id' is set
    if (id == null) {
      throw new ApiException(400, "Missing the required parameter 'id' when calling retrieveStudy");
    }
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/studies/{id}"
      .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<StudyModel> localVarReturnType = new GenericType<StudyModel>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Retrieve the read and discover policies for the dataset
   * @param id A study or dataset id (required)
   * @return PolicyResponse
   * @throws ApiException if fails to make API call
   */
  public PolicyResponse retrieveStudyPolicies(String id) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'id' is set
    if (id == null) {
      throw new ApiException(400, "Missing the required parameter 'id' when calling retrieveStudyPolicies");
    }
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/studies/{id}/policies"
      .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<PolicyResponse> localVarReturnType = new GenericType<PolicyResponse>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * 
   * Returns whether the user is registered with terra 
   * @return UserStatusInfo
   * @throws ApiException if fails to make API call
   */
  public UserStatusInfo user() throws ApiException {
    Object localVarPostBody = null;
    
    // create path and map variables
    String localVarPath = "/api/repository/v1/register/user";

    // query params
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();


    
    
    final String[] localVarAccepts = {
      "application/json"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);

    final String[] localVarContentTypes = {
      
    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

    String[] localVarAuthNames = new String[] { "googleoauth" };

    GenericType<UserStatusInfo> localVarReturnType = new GenericType<UserStatusInfo>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
}
