package com.example.cloudcalc.controller;

import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.entity.Profile;
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

    public DailyStatController(ServiceFacade serviceFacade) {
        super(serviceFacade);
        this.profileController = serviceFacade.getProfileController();
        this.dailyStatView = new DailyStatView(this);
        this.dailyStatModel = new DailyStatModel();
    }

    public List<Profile> getProfiles() {
        return profileController.getProfilesFromFile();
    }

    public void saveSelectedProfiles(Stage stage, List<String> selectedProfiles) {
        if (selectedProfiles == null || selectedProfiles.isEmpty()) {
            Notification.showAlert("Saving...", "Please select at least one profile before saving.", "");
            return;
        }

        dailyStatModel.saveSelectedProfiles(stage, selectedProfiles);
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