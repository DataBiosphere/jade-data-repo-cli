package bio.terra.endpoints;

public final class CLITestingConfig {
    //public static final String dataRepoURL = "http://localhost:8080/";
    //public static final String clientSecretsFilePath = null;
    //public static final String dataRepoURL = "https://jade.datarepo-dev.broadinstitute.org/";
    //public static final String clientSecretsFilePath = "/tmp/jadecli_client_secret.json";
    public static final String dataRepoURL = "https://jade-dd.datarepo-dev.broadinstitute.org/";
    public static final String clientSecretsFilePath = "/tmp/jadecli_client_secret.json";

    public static final String dirName = "src/test/resources/CLICommandTests/";

    private CLITestingConfig() { }
}
