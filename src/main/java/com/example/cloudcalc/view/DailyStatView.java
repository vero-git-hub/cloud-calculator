package com.example.cloudcalc.view;

import com.example.cloudcalc.controller.DailyStatController;
import com.example.cloudcalc.entity.Profile;
import javafx.stage.Stage;

import java.util.List;

/**
 * @author v_code
 **/
public class DailyStatView {

    private final DailyStatController dailyStatController;

    public DailyStatView(DailyStatController dailyStatController) {
        this.dailyStatController = dailyStatController;
    }

    public void showScreen(Stage stage) {
        // TODO: create screen
        List<Profile> profiles = dailyStatController.getProfiles();
        System.out.println(profiles);
    }
}
