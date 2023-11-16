package com.example.cloudcalc.controller;

import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.badge.FileOperationManager;
import com.example.cloudcalc.builder.ElementsBuilder;
import com.example.cloudcalc.builder.SceneBuilder;
import com.example.cloudcalc.builder.TableBuilder;
import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.model.IgnoreModel;
import com.example.cloudcalc.view.ignore.AddIgnoreView;
import com.example.cloudcalc.view.ignore.IgnoreView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class IgnoreController implements IScreenController {

    private final IgnoreView ignoreView = new IgnoreView(this);
    private final MainController mainController;
    private TableBuilder tableBuilder = new TableBuilder();
    private FileOperationManager fileOperationManager;
    private final SceneBuilder sceneBuilder = new SceneBuilder();
    private final ElementsBuilder elementsBuilder = new ElementsBuilder();
    private final AddIgnoreView addIgnoreView = new AddIgnoreView(this);
    private final IgnoreModel ignoreModel = new IgnoreModel(this);

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
        addIgnoreView.showScreen(stage);
    }

    public void initVariablesForTable() {
        tableBuilder.initVariablesForTable(FileName.IGNORE_FILE, fileOperationManager, ignoreView.getTitle());
    }

    public void buildScreen(Stage stage) {
        sceneBuilder.buildScreen(stage, ignoreView.getTitle(), elementsBuilder, tableBuilder, this, mainController);
    }

    public void handleSave(Stage stage) {
        ignoreModel.handleSave(stage, fileOperationManager);
    }

    public HBox createTopLayoutForAddScreen(Button backButton, Label titleAddScreenLabel) {
        return elementsBuilder.createTopLayout(backButton, titleAddScreenLabel);
    }

    public void createScene(VBox layout, Stage primaryStage) {
        sceneBuilder.createScene(layout, primaryStage);
    }
}
