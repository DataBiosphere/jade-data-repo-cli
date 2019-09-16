package bio.terra.command;

import bio.terra.context.Login;
import bio.terra.datarepo.api.RepositoryApi;
import bio.terra.datarepo.api.ResourcesApi;

// Singleton container for pointers to the DR APIs
public class DRApis {
    private static RepositoryApi repositoryApi = null;
    private static ResourcesApi resourcesApi = null;

    public static RepositoryApi getRepositoryApi() {
        if (repositoryApi == null) {
            repositoryApi = new RepositoryApi();
        }

        Login.requiresLogin();
        return repositoryApi;
    }

    public static ResourcesApi getResourcesApi() {
        if (resourcesApi == null) {
            resourcesApi = new ResourcesApi();
        }

        Login.requiresLogin();
        return resourcesApi;
    }
}
