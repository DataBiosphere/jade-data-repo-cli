package bio.terra.command;

import bio.terra.context.Login;
import bio.terra.datarepo.api.RepositoryApi;
import bio.terra.datarepo.api.ResourcesApi;
import bio.terra.tdrwrapper.DataRepoWrap;

// Singleton container for pointers to the DR APIs
public final class DRApi {
  private static DataRepoWrap dataRepoWrap;

  private DRApi() {}

  public static DataRepoWrap get() {
    if (dataRepoWrap == null) {
      dataRepoWrap = new DataRepoWrap(new RepositoryApi(), new ResourcesApi());
    }

    Login.requiresLogin();
    return dataRepoWrap;
  }
}
