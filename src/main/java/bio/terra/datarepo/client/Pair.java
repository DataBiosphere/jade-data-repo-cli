/*
 * Data Repository API
 * This document defines the REST API for Data Repository. **Status: design in progress**  There are four top-level endpoints (besides some used by swagger):  * /       - generated by swagger: swagger API page that provides this documentation and a live UI for submitting REST requests  * /status - provides the operational status of the service  * /api    - is the authenticated and authorized Data Repository API  * /ga4gh/drs/v1 - is a transcription of the Data Repository Service API  The overall API (/api) currently supports one interface:  * Repository - a general and default interface for initial setup  The API endpoints are organized by interface. Each interface is separately versioned.  ## Notes on Naming All of the reference items are suffixed with \"Model\". Those names are used as the class names in the generated Java code. It is helpful to distinguish these model classes from other related classes, like the DAO classes and the operation classes.  ## Editing and debugging I have found it best to edit this file directly to make changes and then use the swagger-editor to validate. The errors out of swagger-codegen are not that helpful. In the swagger-editor, it gives you nice errors and links to the place in the YAML where the errors are.  But... the swagger-editor has been a bit of a pain for me to run. I tried the online website and was not able to load my YAML. Instead, I run it locally in a docker container, like this: ``` docker pull swaggerapi/swagger-editor docker run -p 9090:8080 swaggerapi/swagger-editor ``` Then navigate to localhost:9090 in your browser.  I have not been able to get the file upload to work. It is a bit of a PITA, but I copy-paste the source code, replacing what is in the editor. Then make any fixes. Then copy-paste the resulting, valid file back into our source code. Not elegant, but easier than playing detective with the swagger-codegen errors.  This might be something about my browser or environment, so give it a try yourself and see how it goes.  ## Merging the DRS standard swagger into this swagger ##  The merging is done in three sections:  1. Merging the security definitions into our security definitions  2. This section of paths. We make all paths explicit (prefixed with /ga4gh/drs/v1)     All standard DRS definitions and parameters are prefixed with 'DRS' to separate them     from our native definitions and parameters. We remove the x-swagger-router-controller lines.  3. A separate part of the definitions section for the DRS definitions 
 *
 * OpenAPI spec version: 0.1.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package bio.terra.datarepo.client;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-06-12T09:29:10.549-04:00")
public class Pair {
    private String name = "";
    private String value = "";

    public Pair (String name, String value) {
        setName(name);
        setValue(value);
    }

    private void setName(String name) {
        if (!isValidString(name)) return;

        this.name = name;
    }

    private void setValue(String value) {
        if (!isValidString(value)) return;

        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    private boolean isValidString(String arg) {
        if (arg == null) return false;
        if (arg.trim().isEmpty()) return false;

        return true;
    }
}
