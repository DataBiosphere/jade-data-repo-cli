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

    COMMAND_DR_LIST(10),
    COMMAND_DR_STREAM(11),
    COMMAND_DR_TREE(13),
    COMMAND_DR_DESCRIBE(14),

    COMMAND_SESSION_SHOW(20),
    COMMAND_SESSION_SET(21),
    COMMAND_SESSION_CD(22),
    COMMAND_SESSION_PWD(23),

    COMMAND_PROFILE_CREATE(30),
    COMMAND_PROFILE_SHOW(31),
    COMMAND_PROFILE_DELETE(32),

    COMMAND_SNAPSHOT_CREATE(40),
    COMMAND_SNAPSHOT_SHOW(41),
    COMMAND_SNAPSHOT_DELETE(42),

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
