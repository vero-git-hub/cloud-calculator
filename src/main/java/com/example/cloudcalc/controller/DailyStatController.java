package com.example.cloudcalc.controller;

import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.view.DailyStatView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

/**
 * @author v_code
 **/
public class DailyStatController extends BaseController {
    private final DailyStatView dailyStatView;
    private final ProfileController profileController;

    public DailyStatController(ServiceFacade serviceFacade) {
        super(serviceFacade);
        this.profileController = serviceFacade.getProfileController();
        this.dailyStatView = new DailyStatView(this);
    }

    public List<Profile> getProfiles() {
        return profileController.getProfilesFromFile();
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

    }
}
