package com.example.cloudcalc.controller;

import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.badge.FileOperationManager;
import com.example.cloudcalc.builder.ElementsBuilder;
import com.example.cloudcalc.builder.SceneBuilder;
import com.example.cloudcalc.builder.TableBuilder;
import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.view.ignore.IgnoreView;
import javafx.stage.Stage;

public class IgnoreController implements IScreenController {

    private final IgnoreView ignoreView = new IgnoreView(this);
    private final MainController mainController;
    private TableBuilder tableBuilder = new TableBuilder();
    private FileOperationManager fileOperationManager;
    private final SceneBuilder sceneBuilder = new SceneBuilder();
    private final ElementsBuilder elementsBuilder = new ElementsBuilder();

    public IgnoreController(ServiceFacade serviceFacade) {
        this.fileOperationManager = serviceFacade.getFileOperationManager();
        this.mainController = serviceFacade.getMainController();
    }

    @Override
    public void showScreen(Stage stage) {
        ignoreView.showScreen(stage);
    }

    @Override
    public void showAddScreen(Stage stage) {

    }

    public void initVariablesForTable() {
        tableBuilder.initVariablesForTable(FileName.IGNORE_FILE, fileOperationManager, ignoreView.getTitle());
    }

    public void buildScreen(Stage stage) {
        sceneBuilder.buildScreen(stage, ignoreView.getTitle(), elementsBuilder, tableBuilder, this, mainController);
    }
}
