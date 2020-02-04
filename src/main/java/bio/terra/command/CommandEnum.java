package bio.terra.command;

// Enumeration of commands with their command ids.
public enum CommandEnum {
    COMMAND_DATASET_CREATE(0),
    COMMAND_DATASET_SHOW(1),
    COMMAND_DATASET_DELETE(2),
    COMMAND_DATASET_FILE(3),
    COMMAND_DATASET_FILES(4),
    COMMAND_DATASET_POLICY_SHOW(5),
    COMMAND_DATASET_POLICY_ADD(6),
    COMMAND_DATASET_POLICY_REMOVE(7),
    COMMAND_DATASET_TABLE(8),
    COMMAND_DATASET_FILE_SHOW(9),

    COMMAND_DR_LIST(20),
    COMMAND_DR_STREAM(21),
    COMMAND_DR_TREE(22),
    COMMAND_DR_DESCRIBE(23),

    COMMAND_SESSION_SHOW(40),
    COMMAND_SESSION_SET(41),
    COMMAND_SESSION_CD(42),
    COMMAND_SESSION_PWD(43),

    COMMAND_PROFILE_CREATE(60),
    COMMAND_PROFILE_SHOW(61),
    COMMAND_PROFILE_DELETE(62),

    COMMAND_SNAPSHOT_CREATE(80),
    COMMAND_SNAPSHOT_SHOW(81),
    COMMAND_SNAPSHOT_DELETE(82),
    COMMAND_SNAPSHOT_POLICY_SHOW(83),
    COMMAND_SNAPSHOT_POLICY_ADD(84),
    COMMAND_SNAPSHOT_POLICY_REMOVE(85),

    COMMAND_HELP(1000),

    COMMAND_ENUM_END(10000);

    private int commandId;

    CommandEnum(int commandId) {
        this.commandId = commandId;
    }

    public static CommandEnum commandIdToEnum(int id) {
        for (CommandEnum e : values()) {
            if (e.commandId == id) {
                return e;
            }
        }
        throw new IllegalArgumentException("No matching commandId");
    }

    public int getCommandId() {
        return commandId;
    }
}
