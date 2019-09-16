package bio.terra.command;

import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.BillingProfileModel;
import bio.terra.datarepo.model.BillingProfileRequestModel;
import bio.terra.datarepo.model.DeleteResponseModel;
import bio.terra.datarepo.model.EnumerateBillingProfileModel;

import java.util.Collections;
import java.util.List;

public class ProfileCommands {


    private static ProfileCommands theProfileCommands;

    private ProfileCommands() {
    }

    public static ProfileCommands getInstance() {
        if (theProfileCommands == null) {
            theProfileCommands = new ProfileCommands();
        }
        return theProfileCommands;
    }

    public void profileCreate(String name, String account, String biller) {
        if (biller == null) {
            biller = "direct";
        }
        BillingProfileRequestModel profileRequest = new BillingProfileRequestModel()
                .billingAccountId(account)
                .profileName(name)
                .biller(biller);

        try {
            BillingProfileModel profile = DRApis.getResourcesApi().createProfile(profileRequest);
            printProfile(profile);
        } catch (ApiException ex) {
            System.out.println("Error processing profile create:");
            CommandUtils.printError(ex);
        }
    }

    public void profileDelete(String profileName) {
        BillingProfileModel profile = CommandUtils.findProfileByName(profileName);

        try {
            DeleteResponseModel deleteResponse = DRApis.getResourcesApi().deleteProfile(profile.getId());
            System.out.printf("Profile deleted: %s (%s)\n", profile.getProfileName(), deleteResponse.getObjectState().getValue());
        } catch (ApiException ex) {
            System.out.println("Error processing profile delete:");
            CommandUtils.printError(ex);
        }
    }

    public void profileShow(String profileName) {
        try {
            List<BillingProfileModel> profiles;
            if (profileName == null) {
                EnumerateBillingProfileModel enumerateProfiles = DRApis.getResourcesApi().enumerateProfiles(0, 100000);
                profiles = enumerateProfiles.getItems();
            } else {
                BillingProfileModel profile = CommandUtils.findProfileByName(profileName);
                profiles = Collections.singletonList(profile);
            }
            for (BillingProfileModel profile : profiles) {
                printProfile(profile);
                System.out.println();
            }
        } catch (ApiException ex) {
            System.out.println("Error processing profile show:");
            CommandUtils.printError(ex);
        }
    }

    private void printProfile(BillingProfileModel profile) {
        System.out.println("Profile '" + profile.getProfileName() + "'");
        System.out.println("  id        : " + profile.getId());
        System.out.println("  account   : " + profile.getBillingAccountId());
        System.out.println("  accessible: " + profile.isAccessible());
    }
}
