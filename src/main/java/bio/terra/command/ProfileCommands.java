package bio.terra.command;

import static bio.terra.command.CommandEnum.COMMAND_PROFILE_CREATE;
import static bio.terra.command.CommandEnum.COMMAND_PROFILE_POLICY_ADD;
import static bio.terra.command.CommandUtils.outputPrettyJson;

import bio.terra.datarepo.model.BillingProfileModel;
import bio.terra.datarepo.model.BillingProfileRequestModel;
import bio.terra.datarepo.model.DeleteResponseModel;
import bio.terra.datarepo.model.EnumerateBillingProfileModel;
import bio.terra.datarepo.model.PolicyResponse;
import bio.terra.parser.Argument;
import bio.terra.parser.Command;
import bio.terra.parser.Option;
import bio.terra.parser.ParsedResult;
import bio.terra.parser.Syntax;
import bio.terra.tdrwrapper.exception.DataRepoClientException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ProfileCommands {

  private ProfileCommands() {}

  public static Syntax getSyntax() {
    return new Syntax()
        .addCommand(
            new Command()
                .primaryNames(new String[] {"profile", "create"})
                .commandId(COMMAND_PROFILE_CREATE.getCommandId())
                .help("Create a new profile")
                .addOption(
                    new Option()
                        .shortName("n")
                        .longName("name")
                        .hasArgument(true)
                        .optional(false)
                        .help("Profile name"))
                .addOption(
                    new Option()
                        .shortName("a")
                        .longName("account")
                        .hasArgument(true)
                        .optional(false)
                        .help("Billing account"))
                .addOption(
                    new Option()
                        .shortName("b")
                        .longName("biller")
                        .hasArgument(true)
                        .optional(true)
                        .help("Biller; defaults to 'direct'"))
                .addOption(
                    new Option()
                        .shortName("d")
                        .longName("description")
                        .hasArgument(true)
                        .optional(true)
                        .help("Description of the billing account"))
                .addOption(CommandUtils.formatOption))
        .addCommand(
            new Command()
                .primaryNames(new String[] {"profile", "delete"})
                .commandId(CommandEnum.COMMAND_PROFILE_DELETE.getCommandId())
                .help("Delete a profile")
                .addArgument(
                    new Argument()
                        .name("name")
                        .optional(false)
                        .help("Name of the profile to delete")))
        .addCommand(
            new Command()
                .primaryNames(new String[] {"profile", "show"})
                .commandId(CommandEnum.COMMAND_PROFILE_SHOW.getCommandId())
                .help("Show a profile")
                .addOption(CommandUtils.formatOption)
                .addArgument(
                    new Argument()
                        .name("name")
                        .optional(true)
                        .help(
                            "Name of the profile to show. Defaults to showing all accessible profiles")))
        .addCommand(
            new Command()
                .primaryNames(new String[] {"profile", "policy", "show"})
                .commandId(CommandEnum.COMMAND_PROFILE_POLICY_SHOW.getCommandId())
                .help("Show policies")
                .addArgument(
                    new Argument().name("policy-name").optional(false).help("Name of the policy")))
        .addCommand(
            new Command()
                .primaryNames(new String[] {"profile", "policy", "add"})
                .commandId(COMMAND_PROFILE_POLICY_ADD.getCommandId())
                .help("Add a member to a policy")
                .addArgument(
                    new Argument().name("profile-name").optional(false).help("Name of the profile"))
                .addOption(
                    new Option()
                        .shortName("p")
                        .longName("policy")
                        .hasArgument(true)
                        .optional(false)
                        .help("The policy to add member to"))
                .addOption(
                    new Option()
                        .shortName("e")
                        .longName("email")
                        .hasArgument(true)
                        .optional(false)
                        .help("Email of the member to be added")))
        .addCommand(
            new Command()
                .primaryNames(new String[] {"dataset", "policy", "remove"})
                .commandId(CommandEnum.COMMAND_DATASET_POLICY_REMOVE.getCommandId())
                .help("Remove a member from a policy")
                .addArgument(
                    new Argument().name("profile-name").optional(false).help("Name of the profile"))
                .addOption(
                    new Option()
                        .shortName("p")
                        .longName("policy")
                        .hasArgument(true)
                        .optional(false)
                        .help("The policy to remove member from"))
                .addOption(
                    new Option()
                        .shortName("e")
                        .longName("email")
                        .hasArgument(true)
                        .optional(false)
                        .help("Email of the member to be removed")));
  }

  public static boolean dispatchCommand(CommandEnum command, ParsedResult result) {
    switch (command) {
      case COMMAND_PROFILE_CREATE:
        ProfileCommands.profileCreate(
            result.getArgument("name"),
            result.getArgument("account"),
            result.getArgument("biller"),
            result.getArgument("description"),
            result.getArgument("format"));
        break;
      case COMMAND_PROFILE_DELETE:
        ProfileCommands.profileDelete(result.getArgument("name"));
        break;
      case COMMAND_PROFILE_SHOW:
        ProfileCommands.profileShow(result.getArgument("name"), result.getArgument("format"));
        break;
      case COMMAND_PROFILE_POLICY_SHOW:
        profilePolicyShow(result.getArgument("profile-name"));
        break;
      case COMMAND_PROFILE_POLICY_ADD:
        profilePolicyAdd(
            result.getArgument("profile-name"),
            result.getArgument("policy"),
            result.getArgument("email"));
        break;
      case COMMAND_PROFILE_POLICY_REMOVE:
        profilePolicyRemove(
            result.getArgument("profile-name"),
            result.getArgument("policy"),
            result.getArgument("email"));
        break;
      default:
        return false;
    }

    return true;
  }

  private static void profileCreate(
      String name, String account, String biller, String description, String format) {
    if (biller == null) {
      biller = "direct";
    }
    BillingProfileRequestModel profileRequest =
        new BillingProfileRequestModel()
            .id(UUID.randomUUID().toString())
            .billingAccountId(account)
            .profileName(name)
            .biller(biller)
            .description(description);

    try {
      BillingProfileModel profile = DRApi.get().createProfile(profileRequest);
      printProfile(profile, format);
    } catch (DataRepoClientException ex) {
      System.out.println("Error processing profile create:");
      CommandUtils.printError(ex);
    }
  }

  private static void profileDelete(String profileName) {
    BillingProfileModel profile = CommandUtils.findProfileByName(profileName);

    try {
      DeleteResponseModel deleteResponse = DRApi.get().deleteProfile(profile.getId());
      System.out.printf(
          "Profile deleted: %s (%s)%n",
          profile.getProfileName(), deleteResponse.getObjectState().getValue());
    } catch (DataRepoClientException ex) {
      System.out.println("Error processing profile delete:");
      CommandUtils.printError(ex);
    }
  }

  private static void profileShow(String profileName, String format) {
    try {
      List<BillingProfileModel> profiles;
      if (profileName == null) {
        EnumerateBillingProfileModel enumerateProfiles = DRApi.get().enumerateProfiles(0, 100000);
        profiles = enumerateProfiles.getItems();
      } else {
        BillingProfileModel profile = CommandUtils.findProfileByName(profileName);
        profiles = Collections.singletonList(profile);
      }
      for (BillingProfileModel profile : profiles) {
        printProfile(profile, format);
        System.out.println();
      }
    } catch (DataRepoClientException ex) {
      System.out.println("Error processing profile show:");
      CommandUtils.printError(ex);
    }
  }

  private static void profilePolicyShow(String profileName) {
    BillingProfileModel profile = CommandUtils.findProfileByName(profileName);
    try {
      PolicyResponse policyResponse = DRApi.get().retrieveProfilePolicies(profile.getId());
      CommandUtils.printPolicyResponse(policyResponse);
    } catch (DataRepoClientException ex) {
      System.out.println("Error processing show policy:");
      CommandUtils.printError(ex);
    }
  }

  private static void profilePolicyAdd(String profileName, String policyName, String email) {
    BillingProfileModel profile = CommandUtils.findProfileByName(profileName);
    try {
      PolicyResponse policyResponse =
          DRApi.get().addProfilePolicyMember(profile.getId(), policyName, email);
      CommandUtils.printPolicyResponse(policyResponse);
    } catch (DataRepoClientException ex) {
      System.out.println("Error adding policy member:");
      CommandUtils.printError(ex);
    }
  }

  private static void profilePolicyRemove(String profileName, String policyName, String email) {
    BillingProfileModel profile = CommandUtils.findProfileByName(profileName);
    try {
      PolicyResponse policyResponse =
          DRApi.get().deleteProfilePolicyMember(profile.getId(), policyName, email);
      CommandUtils.printPolicyResponse(policyResponse);
    } catch (DataRepoClientException ex) {
      System.out.println("Error removing policy member:");
      CommandUtils.printError(ex);
    }
  }

  private static void printProfile(BillingProfileModel profile, String format) {
    switch (CommandUtils.CLIFormatFlags.lookup(format)) {
      case CLI_FORMAT_TEXT:
        System.out.println("Profile '" + profile.getProfileName() + "'");
        System.out.println("  id         : " + profile.getId());
        System.out.println("  account    : " + profile.getBillingAccountId());
        System.out.println("  description: " + profile.getDescription());
        System.out.println("  created    : " + profile.getCreatedDate());
        System.out.println("  created by : " + profile.getCreatedBy());
        break;
      case CLI_FORMAT_JSON:
        Map<String, Object> objectValues = new HashMap<>();
        objectValues.put("name", profile.getProfileName());
        objectValues.put("id", profile.getId());
        objectValues.put("account", profile.getBillingAccountId());
        objectValues.put("description", profile.getDescription());
        objectValues.put("createdDate", profile.getCreatedDate());
        objectValues.put("createdBy", profile.getCreatedBy());
        outputPrettyJson(objectValues);
        break;
    }
  }
}
