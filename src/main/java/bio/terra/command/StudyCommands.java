package bio.terra.command;

import bio.terra.datarepo.client.ApiException;
import bio.terra.datarepo.model.DeleteResponseModel;
import bio.terra.datarepo.model.StudyRequestModel;
import bio.terra.datarepo.model.StudySummaryModel;
import bio.terra.model.DRDataset;

import java.io.File;
import java.io.IOException;

public class StudyCommands {
    private static StudyCommands theStudyCommands;

    private StudyCommands() {
    }

    public static StudyCommands getInstance() {
        if (theStudyCommands == null) {
            theStudyCommands = new StudyCommands();
        }
        return theStudyCommands;
    }

    public void studyCreate(String jsonpath) {
        try {
            File file = new File(jsonpath);
            StudyRequestModel studyRequestModel = CommandUtils.getObjectMapper().readValue(file, StudyRequestModel.class);
            if (studyRequestModel != null) {
                StudySummaryModel studySummary = DRApis.getRepositoryApi().createStudy(studyRequestModel);
                System.out.println(studySummary.toString());
            }
        } catch (IOException ex) {
            System.out.println("Error parsing file " + jsonpath + ":");
            System.out.println(ex.getMessage());
        } catch (ApiException ex) {
            System.out.println("Error processing study create:");
            CommandUtils.printError(ex);
        }
    }

    public void studyDelete(String studyName) {
        StudySummaryModel summary = CommandUtils.findStudyByName(studyName);

        try {
            DeleteResponseModel deleteResponse = DRApis.getRepositoryApi().deleteStudy(summary.getId());
            System.out.printf("Study deleted: %s (%s)", studyName, deleteResponse.getObjectState().getValue());
        } catch (ApiException ex) {
            System.out.println("Error processing study delete:");
            CommandUtils.printError(ex);
        }
    }

    public void studyShow(String studyName) {
        // Show study is the same as describe
        StudySummaryModel summary = CommandUtils.findStudyByName(studyName);
        DRDataset studyElement = new DRDataset(summary);
        studyElement.describe();
    }
}
