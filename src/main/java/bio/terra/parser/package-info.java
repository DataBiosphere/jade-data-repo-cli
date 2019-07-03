package bio.terra.parser;

/**
 * Very simple parser. The form of syntax is:
 *  command  options  arguments
 * The command determines what options and arguments we accept. Options have long (--) and short (-) forms.
 * We can make this more complex by adding complexity to the Command parsing.
 *
 * The "help" command is built in and uses the parsing structure to generate the help.
 *
 * The Syntax structure is static. Separate elements are generated for the parsed result.
 *
 * Options are accepted in any order.
 *
 * Argument declarations must be defined in order and are parsed in order. Optional arguments
 * must be at the end to get the right behavior.
 * TODO: No check for that yet.
 *
 * Commands have numeric ids. The numbers must be positive integers.
 * -1 is used to indicate the command is not known.
 */


