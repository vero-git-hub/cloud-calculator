package com.example.cloudcalc.controller;

import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.builder.ElementsBuilder;
import com.example.cloudcalc.builder.SceneBuilder;
import com.example.cloudcalc.builder.TableBuilder;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.view.MainView;
import com.example.cloudcalc.entity.Profile;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Arrays;

public class MainController {

    private final ServiceFacade serviceFacade;
    private final TableBuilder tableBuilder;
    private final ElementsBuilder elementsBuilder;
    private final SceneBuilder sceneBuilder;
    private final MainView mainView;

    public MainController(ServiceFacade serviceFacade) {
        this.serviceFacade = serviceFacade;
        this.elementsBuilder = new ElementsBuilder();
        this.mainView = new MainView(this);
        this.tableBuilder = new TableBuilder();
        this.sceneBuilder = new SceneBuilder();
    }

    public MainView getMainView() {
        return mainView;
    }

    public void showMainScreen(Stage primaryStage) {
        mainView.showMainScreen(primaryStage);
    }

    public Label getLabel(String text) {
        return elementsBuilder.createLabel(text);
    }

    public TableView<Profile> getTable(Stage primaryStage) {
        return tableBuilder.createTableForMain(primaryStage, serviceFacade.getProfileController(), this, serviceFacade.getScanController());
    }

    public void getScene(ScrollPane scrollPane, Stage primaryStage) {
        sceneBuilder.createScene(scrollPane, primaryStage);
    }

    public HBox initializeButtons(Stage primaryStage) {
        Button statsButton = ButtonFactory.createStatsButton(e -> serviceFacade.showStatsScreen(primaryStage));
        Button prizeButton = ButtonFactory.createPrizeButton(e -> serviceFacade.showPrizesScreen(primaryStage));
        Button programButton = ButtonFactory.createProgramButton(e -> serviceFacade.showProgramScreen(primaryStage));

        return elementsBuilder.createExtendedTopLayout(
                Arrays.asList(statsButton),
                mainView.getTitleLabel(),
                programButton,
                prizeButton
        );
    }

    public void getCreateProfileScreen(Stage primaryStage){
        serviceFacade.showCreateProfileScreen(primaryStage);
    }

    public void getProfilesScreen(Stage primaryStage){
        serviceFacade.showProfilesScreen(primaryStage);
    }
}