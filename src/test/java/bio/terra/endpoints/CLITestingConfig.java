package bio.terra.endpoints;

public final class CLITestingConfig {
    private static CLITestingConfig theConfig;
    private String dataRepoURL;
    private String clientSecretsFilePath;
    private String billingAccount;

    public static final String dirName = "src/test/resources/CLICommandTests/";
    public static final String testProfileName = "jadecli_test_profile";

    private CLITestingConfig() {
        dataRepoURL = System.getenv("JADECLI_TEST_DATAREPO_URL");
        if (dataRepoURL == null) {
            dataRepoURL = "http://localhost:8080/";
        }

        // Secrets default is null; default is supplied in the main code
        clientSecretsFilePath = System.getenv("JADECLI_TEST_CLIENT_SECRETS");

        // Billing account is required
        billingAccount = System.getenv("JADECLI_TEST_BILLING_ACCOUNT");
        if (billingAccount == null) {
            throw new IllegalArgumentException("Envvar JADECLI_TEST_BILLING_ACCOUNT is required");
        }
    }

    public static CLITestingConfig config() {
        if (theConfig == null) {
            theConfig = new CLITestingConfig();
        }
        return theConfig;
    }

    public String getDataRepoURL() {
        return dataRepoURL;
    }

    public String getClientSecretsFilePath() {
        return clientSecretsFilePath;
    }

    public String getBillingAccount() {
        return billingAccount;
    }
}
