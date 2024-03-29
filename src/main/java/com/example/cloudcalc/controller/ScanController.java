package com.example.cloudcalc.controller;

import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.builder.ElementsBuilder;
import com.example.cloudcalc.builder.SceneBuilder;
import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.view.ScanView;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class ScanController {
    private ServiceFacade serviceFacade;
    private final ScanView scanView = new ScanView(this);
    private final ElementsBuilder elementsBuilder = new ElementsBuilder();
    private final SceneBuilder sceneBuilder = new SceneBuilder();

    public ScanController(ServiceFacade serviceFacade) {
        this.serviceFacade = serviceFacade;
    }

    public void showScreen(Stage stage, Profile profile, boolean isMovedFromMain) {
        boolean isScanSuccess = serviceFacade.getProfileController().scanAndUpdateProfile(profile);

        if(isScanSuccess) {
            scanView.showScreen(stage, profile, isMovedFromMain);
        }
    }

    public void showStatsScreen(Stage stage) {
        serviceFacade.getStatsController().showStatsScreen(stage);
    }

    public void showMainScreen(Stage stage) {
        serviceFacade.getMainController().showMainScreen(stage);
    }

    public HBox createTopLayoutWithBackAndText(Button backButton, TextFlow textFlow) {
        return elementsBuilder.createTopLayoutWithBackAndText(backButton, textFlow);
    }

    public void createScene(VBox layout, Stage stage) {
        sceneBuilder.createScene(layout, stage);
    }
}