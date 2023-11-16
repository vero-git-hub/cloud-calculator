package com.example.cloudcalc.controller;

import com.example.cloudcalc.DateUtils;
import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.builder.ElementsBuilder;
import com.example.cloudcalc.builder.SceneBuilder;
import com.example.cloudcalc.entity.Prize;
import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.entity.badge.BadgeCounts;
import com.example.cloudcalc.model.ScanModel;
import com.example.cloudcalc.view.ScanView;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Map;

public class ScanController {

    private ServiceFacade serviceFacade;
    private final ScanModel scanModel = new ScanModel(this);
    private final ScanView scanView = new ScanView(this);
    private final ElementsBuilder elementsBuilder = new ElementsBuilder();
    private final SceneBuilder sceneBuilder = new SceneBuilder();


    public ScanController(ServiceFacade serviceFacade) {
        this.serviceFacade = serviceFacade;
    }

    public void showScreen(Stage stage, Profile profile) {
        serviceFacade.getProfileController().scanAndUpdateProfile(stage, profile);
        ArrayList<String> siteLinks = serviceFacade.getDataExtractor().performScan(profile);

        profile.setLastScannedDate(DateUtils.getCurrentDate());
        serviceFacade.getProfileController().getProfileModel().updateProfile(profile);

        scanView.showScreen(stage, profile, siteLinks);
    }

    public void showMainScreen(Stage stage) {
        serviceFacade.getMainController().showMainScreen(stage);
    }

    public HBox createTopLayoutWithBackAndText(Button backButton, TextFlow textFlow) {
        return elementsBuilder.createTopLayoutWithBackAndText(backButton, textFlow);
    }

    public BadgeCounts calculateBadgeCounts(Profile profile, ArrayList<String> siteLinks) {
        return serviceFacade.getBadgeManager().calculateBadgeCounts(profile, siteLinks);
    }

    public void updateProfile(Profile profile) {
        serviceFacade.getProfileController().getProfileModel().updateProfile(profile);
    }

    public void createScene(VBox layout, Stage stage) {
        sceneBuilder.createScene(layout, stage);
    }

    public Map<String, Prize> getReceivedPrizes() {
        return serviceFacade.getPrizeController().getReceivedPrizes();
    }
}
