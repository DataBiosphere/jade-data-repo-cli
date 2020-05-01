# Jade CLI

The Jade CLI provides a command line interface to the Terra Data Repository.
It is in a prototype state right now, but it will be productized moving forward.
It is not complete. There are commands that do not work or are only partially
implemented. Your patience is appreciated...

## Usage
The CLI is designed to be used in a shell. It accepts a command on stdin, generates
output on stdout and errors on stderr. It sets a proper return status based on the
success or failure of the command.

The CLI presents the data repository as a pseudo-directory structure. The logical "root"
directory contains datasets and snapshots. Each dataset or snapshot has two subdirectories
called `files` and `tables`. Enumerating under the `files` directory will display the
files in the dataset or snapshot. Enumerating under the `tables` directory will display
the tables. You can `session cd` to a directory to simplify navigating.

This README does not provide full command documentation. In large part, that is because
the tool is under active development. Any documentation here would fall out of date
quickly. You can use the `help` command to see what the syntax is today.

## Login
The CLI is not a Google-approved application so login can be a bit of an ordeal. 
Depending on your relationship with GCP, the first time you run the tool it may run through the Google
authentication process. You will get the usual selection of what user you want to authenticate as. You
will also get scary screens saying this is not an approved application and it isn't safe. You have to
navigate through the scary parts and accept some scopes; it does request scope for GCS (and eventually
 will request scope for BigQuery). When the application is further along, we will
get an approval from Google and make the scary parts go away.

### Debugging
You can debug the CLI by creating an IntelliJ configuration like this:
1. Add an Application configuration
2. Main class: `bio.terra.Main`
3. Program arguments: `dr list` (or whatever command you want to test...)
4. Use classpath of module: `jadecli.main`

By changing the program arguments, you can test any command.

### Running
You can run the code on a command line like this:
1. Install a distribution into your build directory: `./gradlew installDist`
2. In your IntelliJ terminal, make a convenience alias: `alias jc='./build/install/jadecli/bin/jadecli'`
3. Run a command such as `jc help`

### Testing
There are two test targets for verifying the CLI:
- `./gradlew :testCLIUnit --console=verbose --info`
- `./gradlew :testCLIIntegrated --console=verbose --info`
Note that you must do  `./gradlew installDist` before running the testCLIIntegrated task.

### Distribution
The CLI is distributed in a directory structure. There is a `bin` directory containing scripts to
launch the application. There is a `lib` directory containing all of the related jars. So it is
not packaged as one fat jar.

You can generate a distribution as tar or zip with the commands:
* `./gradlew distTar`
* `./gradlew distZip`

## Implementation Notes

### Parsing
I looked for an off-the-shelf parser, but didn't find one that worked for this application.
There *must* be one, but I didn't find it. The parser is pretty quick and dirty. There is
documentation about it in `src/main/java/bio/terra/parser/package-info.java`

The syntax is all defined in `Main.java`. I think is is pretty self-explanatory. It is pretty
to add new constructs.




