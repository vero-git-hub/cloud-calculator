package com.example.cloudcalc.controller;

import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.model.ProgramModel;
import com.example.cloudcalc.view.ProgramView;
import com.example.cloudcalc.view.prize.AddPrizeView;
import com.example.cloudcalc.view.program.AddProgramView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProgramController extends BaseController {
    private ProgramView programView = new ProgramView(this);
    private ProgramModel programModel = new ProgramModel(this);
    private final AddProgramView addProgramView = new AddProgramView(this);

    public ProgramController(ServiceFacade serviceFacade) {
        super(serviceFacade);
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
}