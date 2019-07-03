package bio.terra.datarepo.api;

import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.client.ApiClient;
import bio.terra.datarepo.client.Configuration;
import bio.terra.datarepo.client.Pair;

import javax.ws.rs.core.GenericType;

import bio.terra.datarepo.model.DRSAccessURL;
import bio.terra.datarepo.model.DRSBundle;
import bio.terra.datarepo.model.DRSError;
import bio.terra.datarepo.model.DRSObject;
import bio.terra.datarepo.model.DRSServiceInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-06-12T09:29:10.549-04:00")
public class DataRepositoryServiceApi {
  private ApiClient apiClient;

  public DataRepositoryServiceApi() {
    this(Configuration.getDefaultApiClient());
  }

  public DataRepositoryServiceApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  /**
   * Get a URL for fetching bytes.
   * Returns a URL that can be used to fetch the object bytes. This method only needs to be called when using an &#x60;AccessMethod&#x60; that contains an &#x60;access_id&#x60; (e.g., for servers that use signed URLs for fetching object bytes).
   * @param objectId An &#x60;id&#x60; of a Data Object (required)
   * @param accessId An &#x60;access_id&#x60; from the &#x60;access_methods&#x60; list of a Data Object (required)
   * @return DRSAccessURL
   * @throws ApiException if fails to make API call
   */
  public DRSAccessURL getAccessURL(String objectId, String accessId) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'objectId' is set
    if (objectId == null) {
      throw new ApiException(400, "Missing the required parameter 'objectId' when calling getAccessURL");
    }
    
    // verify the required parameter 'accessId' is set
    if (accessId == null) {
      throw new ApiException(400, "Missing the required parameter 'accessId' when calling getAccessURL");
    }
    
    // create path and map variables
    String localVarPath = "/ga4gh/drs/v1/objects/{object_id}/access/{access_id}"
      .replaceAll("\\{" + "object_id" + "\\}", apiClient.escapeString(objectId.toString()))
      .replaceAll("\\{" + "access_id" + "\\}", apiClient.escapeString(accessId.toString()));

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

    GenericType<DRSAccessURL> localVarReturnType = new GenericType<DRSAccessURL>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * Retrieve a Data Bundle
   * Returns bundle metadata, and a list of ids that can be used to fetch bundle contents.
   * @param bundleId  (required)
   * @return DRSBundle
   * @throws ApiException if fails to make API call
   */
  public DRSBundle getBundle(String bundleId) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'bundleId' is set
    if (bundleId == null) {
      throw new ApiException(400, "Missing the required parameter 'bundleId' when calling getBundle");
    }
    
    // create path and map variables
    String localVarPath = "/ga4gh/drs/v1/bundles/{bundle_id}"
      .replaceAll("\\{" + "bundle_id" + "\\}", apiClient.escapeString(bundleId.toString()));

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

    GenericType<DRSBundle> localVarReturnType = new GenericType<DRSBundle>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * Get info about a Data Object.
   * Returns object metadata, and a list of access methods that can be used to fetch object bytes.
   * @param objectId  (required)
   * @return DRSObject
   * @throws ApiException if fails to make API call
   */
  public DRSObject getObject(String objectId) throws ApiException {
    Object localVarPostBody = null;
    
    // verify the required parameter 'objectId' is set
    if (objectId == null) {
      throw new ApiException(400, "Missing the required parameter 'objectId' when calling getObject");
    }
    
    // create path and map variables
    String localVarPath = "/ga4gh/drs/v1/objects/{object_id}"
      .replaceAll("\\{" + "object_id" + "\\}", apiClient.escapeString(objectId.toString()));

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

    GenericType<DRSObject> localVarReturnType = new GenericType<DRSObject>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
  /**
   * Get information about this implementation.
   * May return service version and other information.
   * @return DRSServiceInfo
   * @throws ApiException if fails to make API call
   */
  public DRSServiceInfo getServiceInfo() throws ApiException {
    Object localVarPostBody = null;
    
    // create path and map variables
    String localVarPath = "/ga4gh/drs/v1/service-info";

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

    GenericType<DRSServiceInfo> localVarReturnType = new GenericType<DRSServiceInfo>() {};
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
      }
}
