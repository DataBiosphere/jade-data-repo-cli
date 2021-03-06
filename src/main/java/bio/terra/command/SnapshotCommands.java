package bio.terra.command;

import bio.terra.datarepo.model.BillingProfileModel;
import bio.terra.datarepo.model.DeleteResponseModel;
import bio.terra.datarepo.model.PolicyResponse;
import bio.terra.datarepo.model.SnapshotRequestModel;
import bio.terra.datarepo.model.SnapshotSummaryModel;
import bio.terra.model.DRSnapshot;
import bio.terra.parser.Argument;
import bio.terra.parser.Command;
import bio.terra.parser.Option;
import bio.terra.parser.ParsedResult;
import bio.terra.parser.Syntax;
import bio.terra.tdrwrapper.exception.DataRepoClientException;

public final class SnapshotCommands {

  private SnapshotCommands() {}

  public static Syntax getSyntax() {
    return new Syntax()
        .addCommand(
            new Command()
                .primaryNames(new String[] {"snapshot", "create"})
                .commandId(CommandEnum.COMMAND_SNAPSHOT_CREATE.getCommandId())
                .help("Create a new snapshot")
                .addOption(
                    new Option()
                        .shortName("i")
                        .longName("input-json")
                        .hasArgument(true)
                        .optional(false)
                        .help(
                            "JSON definition of a snapshot; if the argument starts with @,"
                                + " it is interpreted as a file containing the json"))
                .addOption(
                    new Option()
                        .shortName("n")
                        .longName("name")
                        .hasArgument(true)
                        .optional(true)
                        .help("Snapshot name; if present, overrides the name in the JSON file"))
                .addOption(
                    new Option()
                        .shortName("p")
                        .longName("profile")
                        .hasArgument(true)
                        .optional(true)
                        .help(
                            "Profile name; if present, overrides the profile id in the JSON file")))
        .addCommand(
            new Command()
                .primaryNames(new String[] {"snapshot", "show"})
                .commandId(CommandEnum.COMMAND_SNAPSHOT_SHOW.getCommandId())
                .help("List one snapshot")
                .addArgument(
                    new Argument()
                        .name("snapshot-name")
                        .optional(false)
                        .help("name of the snapshot to show"))
                .addOption(
                    new Option()
                        .longName("format")
                        .hasArgument(true)
                        .optional(true)
                        .help("Choose format; 'text' is the default; 'json' is supported")))
        .addCommand(
            new Command()
                .primaryNames(new String[] {"snapshot", "delete"})
                .commandId(CommandEnum.COMMAND_SNAPSHOT_DELETE.getCommandId())
                .help("Delete a snapshot")
                .addArgument(
                    new Argument()
                        .name("snapshot-name")
                        .optional(false)
                        .help("Name of the snapshot to delete")))
        .addCommand(
            new Command()
                .primaryNames(new String[] {"snapshot", "policy", "show"})
                .commandId(CommandEnum.COMMAND_SNAPSHOT_POLICY_SHOW.getCommandId())
                .help("Show policies")
                .addArgument(
                    new Argument()
                        .name("snapshot-name")
                        .optional(false)
                        .help("Name of the snapshot")))
        .addCommand(
            new Command()
                .primaryNames(new String[] {"snapshot", "policy", "add"})
                .commandId(CommandEnum.COMMAND_SNAPSHOT_POLICY_ADD.getCommandId())
                .help("Add a member to a policy")
                .addArgument(
                    new Argument()
                        .name("snapshot-name")
                        .optional(false)
                        .help("Name of the snapshot"))
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
                .primaryNames(new String[] {"snapshot", "policy", "remove"})
                .commandId(CommandEnum.COMMAND_SNAPSHOT_POLICY_REMOVE.getCommandId())
                .help("Remove a member from a policy")
                .addArgument(
                    new Argument()
                        .name("snapshot-name")
                        .optional(false)
                        .help("Name of the snapshot"))
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
      case COMMAND_SNAPSHOT_CREATE:
        snapshotCreate(
            result.getArgument("input-json"),
            result.getArgument("name"),
            result.getArgument("profile"));
        break;
      case COMMAND_SNAPSHOT_SHOW:
        snapshotShow(result.getArgument("snapshot-name"), result.getArgument("format"));
        break;
      case COMMAND_SNAPSHOT_DELETE:
        snapshotDelete(result.getArgument("snapshot-name"));
        break;
      case COMMAND_SNAPSHOT_POLICY_ADD:
        snapshotPolicyAdd(
            result.getArgument("snapshot-name"),
            result.getArgument("policy"),
            result.getArgument("email"));
        break;
      case COMMAND_SNAPSHOT_POLICY_REMOVE:
        snapshotPolicyRemove(
            result.getArgument("snapshot-name"),
            result.getArgument("policy"),
            result.getArgument("email"));
        break;
      case COMMAND_SNAPSHOT_POLICY_SHOW:
        snapshotPolicyShow(result.getArgument("snapshot-name"));
        break;
      default:
        return false;
    }

    return true;
  }

  public static void snapshotCreate(String json, String name, String profileName) {
    try {
      SnapshotRequestModel snapshotRequestModel =
          CommandUtils.makeRequestFromJson(json, SnapshotRequestModel.class);
      if (snapshotRequestModel != null) {
        // Override the name and profile if requested
        if (name != null) {
          snapshotRequestModel.name(name);
        }
        if (profileName != null) {
          BillingProfileModel profileModel = CommandUtils.findProfileByName(profileName);
          snapshotRequestModel.profileId(profileModel.getId());
        }

        DRApi.get().createSnapshot(snapshotRequestModel);
      }
    } catch (DataRepoClientException ex) {
      System.out.println("Error processing snapshot create:");
      CommandUtils.printError(ex);
    }
  }

  public static void snapshotDelete(String snapshotName) {
    SnapshotSummaryModel summary = CommandUtils.findSnapshotByName(snapshotName);
    try {
      DeleteResponseModel deleteResponse = DRApi.get().deleteSnapshot(summary.getId());
      System.out.printf(
          "Snapshot deleted: %s (%s)%n", snapshotName, deleteResponse.getObjectState().getValue());
    } catch (DataRepoClientException ex) {
      System.out.println("Error processing snapshot delete:");
      CommandUtils.printError(ex);
    }
  }

  public static void snapshotShow(String snapshotName, String format) {
    format = CommandUtils.validateFormat(format);

    // Show snapshot is the same as describe
    SnapshotSummaryModel summary = CommandUtils.findSnapshotByName(snapshotName);
    DRSnapshot snapshotElement = new DRSnapshot(summary);
    snapshotElement.describe(format);
  }

  public static void snapshotPolicyShow(String snapshotName) {
    SnapshotSummaryModel summary = CommandUtils.findSnapshotByName(snapshotName);
    try {
      PolicyResponse policyResponse = DRApi.get().retrieveSnapshotPolicies(summary.getId());
      CommandUtils.printPolicyResponse(policyResponse);
    } catch (DataRepoClientException ex) {
      System.out.println("Error processing show policy:");
      CommandUtils.printError(ex);
    }
  }

  public static void snapshotPolicyAdd(String snapshotName, String policyName, String email) {
    SnapshotSummaryModel summary = CommandUtils.findSnapshotByName(snapshotName);
    try {
      PolicyResponse policyResponse =
          DRApi.get().addSnapshotPolicyMember(summary.getId(), policyName, email);
      CommandUtils.printPolicyResponse(policyResponse);
    } catch (DataRepoClientException ex) {
      System.out.println("Error adding policy member:");
      CommandUtils.printError(ex);
    }
  }

  public static void snapshotPolicyRemove(String snapshotName, String policyName, String email) {
    SnapshotSummaryModel summary = CommandUtils.findSnapshotByName(snapshotName);
    try {
      PolicyResponse policyResponse =
          DRApi.get().deleteSnapshotPolicyMember(summary.getId(), policyName, email);
      CommandUtils.printPolicyResponse(policyResponse);
    } catch (DataRepoClientException ex) {
      System.out.println("Error removing policy member:");
      CommandUtils.printError(ex);
    }
  }
}
