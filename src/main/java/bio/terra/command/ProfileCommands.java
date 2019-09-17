package bio.terra.command;

import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.BillingProfileModel;
import bio.terra.datarepo.model.BillingProfileRequestModel;
import bio.terra.datarepo.model.DeleteResponseModel;
import bio.terra.datarepo.model.EnumerateBillingProfileModel;
import bio.terra.parser.Argument;
import bio.terra.parser.Command;
import bio.terra.parser.Option;
import bio.terra.parser.Syntax;

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

    public static Syntax getSyntax() {
        return new Syntax()
                .addCommand(new Command()
                        .primaryName("profile")
                        .secondaryName("create")
                        .commandId(CommandEnum.COMMAND_PROFILE_CREATE.getCommandId())
                        .help("Create a new profile")
                        .addOption(new Option()
                                .shortName("n")
                                .longName("name")
                                .hasArgument(true)
                                .optional(false)
                                .help("Profile name"))
                        .addOption(new Option()
                                .shortName("a")
                                .longName("account")
                                .hasArgument(true)
                                .optional(false)
                                .help("Billing account"))
                        .addOption(new Option()
                                .shortName("b")
                                .longName("biller")
                                .hasArgument(true)
                                .optional(true)
                                .help("Biller; defaults to 'direct'")))
                .addCommand(new Command()
                        .primaryName("profile")
                        .secondaryName("delete")
                        .commandId(CommandEnum.COMMAND_PROFILE_DELETE.getCommandId())
                        .help("Delete a profile")
                        .addArgument(new Argument()
                                .name("name")
                                .optional(false)
                                .help("Name of the profile to delete")))
                .addCommand(new Command()
                        .primaryName("profile")
                        .secondaryName("show")
                        .commandId(CommandEnum.COMMAND_PROFILE_SHOW.getCommandId())
                        .help("Show a profile")
                        .addArgument(new Argument()
                                .name("name")
                                .optional(true)
                                .help("Name of the profile to show. Defaults to showing all accessible profiles")));
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