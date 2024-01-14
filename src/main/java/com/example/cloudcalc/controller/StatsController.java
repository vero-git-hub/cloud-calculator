package com.example.cloudcalc.controller;

import com.example.cloudcalc.DataExtractor;
import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.builder.ElementsBuilder;
import com.example.cloudcalc.builder.SceneBuilder;
import com.example.cloudcalc.builder.TableBuilder;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.model.ProfileModel;
import com.example.cloudcalc.view.StatsView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StatsController {

    private final ProfileModel profileModel;
    private final PrizeController prizeController;
    private final StatsView statsView;
    private final SceneBuilder sceneBuilder;
    private final TableBuilder tableBuilder;
    private final ElementsBuilder elementsBuilder;
    private final MainController mainController;

    public StatsController(ServiceFacade serviceFacade) {
        this.profileModel = serviceFacade.getProfileDataManager();
        this.prizeController = serviceFacade.getPrizeController();
        this.mainController = serviceFacade.getMainController();
        this.statsView = new StatsView(this);
        this.sceneBuilder = new SceneBuilder();
        this.tableBuilder = new TableBuilder();
        this.elementsBuilder = new ElementsBuilder();
    }

    public void showStatsScreen(Stage primaryStage) {
        statsView.showStatsScreen(primaryStage);
    }

    public void createScene(VBox layout, Stage primaryStage) {
        sceneBuilder.createScene(layout, primaryStage);
    }

     public HBox createTopLayout(Stage primaryStage){
         Button backButton = ButtonFactory.createBackButton(e -> mainController.showMainScreen(primaryStage));
         return elementsBuilder.createTopLayout(backButton, statsView.getTitleLabel());
     }

    public Label createSubtitleLabelForStats() {
        return elementsBuilder.createSubtitleLabel(statsView.getSubtitleLabel());
    }

    public TableView<Profile> createMainTableForStats(Stage stage) {
        return tableBuilder.createMainTableForStats(stage, profileModel, prizeController);
    }
}