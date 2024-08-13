package com.example.cloudcalc.controller;

import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.entity.program.Program;
import com.example.cloudcalc.model.DailyStatModel;
import com.example.cloudcalc.util.Notification;
import com.example.cloudcalc.view.DailyStatView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

/**
 * @author v_code
 **/
public class DailyStatController extends BaseController {
    private final DailyStatView dailyStatView;
    private final DailyStatModel dailyStatModel;
    private final ProfileController profileController;
    private final ProgramController programController;

    public DailyStatController(ServiceFacade serviceFacade) {
        super(serviceFacade);
        this.profileController = serviceFacade.getProfileController();
        this.programController = serviceFacade.getProgramController();
        this.dailyStatView = new DailyStatView(this);
        this.dailyStatModel = new DailyStatModel();
    }

    public List<Profile> getProfiles() {
        return profileController.getProfilesFromFile();
    }

    public List<Program> getPrograms() {
        return programController.loadProgramsFromFile();
    }

    public void saveSelectedProfiles(List<String> selectedProfiles) {
        if (selectedProfiles == null || selectedProfiles.isEmpty()) {
            Notification.showAlert("Saving...", "Please select at least one profile before saving.", "");
            return;
        }
        dailyStatModel.saveSelectedProfiles(selectedProfiles);
    }

    public void saveTemplate(String text) {
        if(text == null || text.isEmpty()) {
            Notification.showAlert("Saving...", "Please select at least one template.", "");
            return;
        }
        dailyStatModel.saveTemplate(text);
    }

    public List<String> loadSelectedProfiles() {
        return dailyStatModel.loadSelectedProfiles();
    }

    public String loadTemplate() {
        return dailyStatModel.loadTemplate();
    }

    public void scanProfiles(List<String> selectedProfiles, List<String> selectedPrograms) {
        List<Profile> profiles = profileController.getProfilesFromFile();
        for (Profile profile : profiles) {
            for (String selectedProfile : selectedProfiles) {
                if (profile.getName().equals(selectedProfile)) {
                    profileController.scanAndUpdateProfile(profile);
                    System.out.println(selectedProfile);
                }
            }
        }

        // TODO: get points for selected programs
    }

    public void saveSelectedPrograms(List<String> selectedPrograms) {
        if (selectedPrograms == null || selectedPrograms.isEmpty()) {
            Notification.showAlert("Saving...", "Please select at least one program before saving.", "");
            return;
        }
        dailyStatModel.saveSelectedPrograms(selectedPrograms);
    }

    @Override
    public void showScreen(Stage stage) {
        dailyStatView.showScreen(stage);
    }

    @Override
    public void showAddScreen(Stage stage) {

    }

    @Override
    public void createScene(VBox layout, Stage stage) {
        sceneBuilder.createScene(layout, stage);
    }
}