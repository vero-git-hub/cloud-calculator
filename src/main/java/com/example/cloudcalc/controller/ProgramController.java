package com.example.cloudcalc.controller;

import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.model.ProgramModel;
import com.example.cloudcalc.view.ProgramView;
import com.example.cloudcalc.view.prize.AddPrizeView;
import com.example.cloudcalc.view.program.AddProgramView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class ProgramController extends BaseController {
    private ProgramView programView = new ProgramView(this);
    private ProgramModel programModel = new ProgramModel(this);
    private final AddProgramView addProgramView = new AddProgramView(this);
    public static ResourceBundle bundle;

    public ProgramController(ServiceFacade serviceFacade) {
        super(serviceFacade);
        bundle = LanguageManager.getBundle();
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    @Override
    public void showScreen(Stage stage) {
        programView.showScreen(stage);
    }

    @Override
    public void showAddScreen(Stage stage) {
        addProgramView.showScreen(stage);
    }

    @Override
    public void createScene(VBox layout, Stage stage) {
        sceneBuilder.createScene(layout, stage);
    }

    public HBox createTopLayoutForAddScreen(Button backButton, Label titleAddScreenLabel) {
        return elementsBuilder.createTopLayout(backButton, titleAddScreenLabel);
    }
}