package bio.terra.tdrwrapper;

import bio.terra.datarepo.client.ApiException;

@FunctionalInterface
public interface ApiFunction<R> {
  R apply() throws ApiException;
}
