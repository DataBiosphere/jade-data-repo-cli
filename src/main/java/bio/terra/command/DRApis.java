package bio.terra.command;

import bio.terra.context.Login;
import bio.terra.datarepo.api.RepositoryApi;

// Singleton container for pointers to the DR APIs
public class DRApis {
    private static RepositoryApi repositoryApi = null;

    public static RepositoryApi getRepositoryApi() {
        if (repositoryApi == null) {
            repositoryApi = new RepositoryApi();
        }

        Login.requiresLogin();
        return repositoryApi;
    }
}
