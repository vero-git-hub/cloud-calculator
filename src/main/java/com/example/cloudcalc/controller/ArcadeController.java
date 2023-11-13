package com.example.cloudcalc.controller;

import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.badge.FileOperationManager;
import com.example.cloudcalc.builder.ElementsBuilder;
import com.example.cloudcalc.builder.SceneBuilder;
import com.example.cloudcalc.builder.TableBuilder;
import com.example.cloudcalc.builder.TextFieldManager;
import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.model.ArcadeModel;
import com.example.cloudcalc.view.arcade.AddArcadeView;
import com.example.cloudcalc.view.arcade.ArcadeView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ArcadeController implements IScreenController{

    private final ArcadeView arcadeView = new ArcadeView(this);
    private final TableBuilder tableBuilder = new TableBuilder();
    private final FileOperationManager fileOperationManager;
    private final SceneBuilder sceneBuilder = new SceneBuilder();
    private final ElementsBuilder elementsBuilder = new ElementsBuilder();
    private final MainController mainController;
    private final AddArcadeView addArcadeView = new AddArcadeView(this);
    private final ArcadeModel arcadeModel = new ArcadeModel(this);
    private final TextFieldManager textFieldManager = new TextFieldManager();


    public ArcadeController(ServiceFacade serviceFacade) {
        this.fileOperationManager = serviceFacade.getFileOperationManager();
        this.mainController = serviceFacade.getMainController();
    }

    //    private final ArcadeView arcadeView;
//    private final FileOperationManager fileOperationManager;
//
//    public ArcadeManager() {
//        this.arcadeView = new ArcadeView(this);
//        this.fileOperationManager = fileOperationManager;
//    }
//
//    public ArcadeView getArcadeScreen() {
//        return arcadeView;
//    }
//
//    public FileOperationManager getFileOperationManager() {
//        return fileOperationManager;
//    }
//

    @Override
    public void showScreen(Stage stage) {
        arcadeView.showArcadeScreen(stage);
    }

    public void initVariablesForTable() {
        tableBuilder.initVariablesForTable(FileName.ARCADE_FILE, fileOperationManager, arcadeView.getTitle());
    }

    public void buildScreen(Stage stage) {
        sceneBuilder.buildScreen(stage, arcadeView.getTitle(), elementsBuilder, tableBuilder, this, mainController);
    }

    @Override
    public void showAddScreen(Stage stage) {
        addArcadeView.showScreen(stage, textFieldManager);
    }

//    public void showAnArcadeAddScreen(Stage primaryStage) {
//        Button backButton = ButtonFactory.createBackButton(e -> showArcadeScreen(primaryStage));
//        sceneBuilder.buildAnArcadeAddScreen(primaryStage, backButton);
//    }

    public void handleSave(Stage stage) {
        arcadeModel.handleSave(stage, fileOperationManager, FileName.ARCADE_FILE, textFieldManager);
    }


    public HBox createTopLayoutForAddScreen(Button backButton, Label titleAddScreenLabel) {
        return elementsBuilder.createTopLayout(backButton, titleAddScreenLabel);
    }

    public void createScene(VBox layout, Stage primaryStage) {
        sceneBuilder.createScene(layout, primaryStage);
    }
}
