package com.example.cloudcalc.controller;

import com.example.cloudcalc.DataExtractor;
import com.example.cloudcalc.ServiceFacade;
import com.example.cloudcalc.badge.BadgeManager;
import com.example.cloudcalc.builder.ElementsBuilder;
import com.example.cloudcalc.builder.SceneBuilder;
import com.example.cloudcalc.builder.TableBuilder;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import com.example.cloudcalc.prize.PrizeController;
import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.model.ProfileModel;
import com.example.cloudcalc.view.StatsView;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

public class StatsController {

    private final ProfileController profileController;
    private final ProfileModel profileModel;
    private final DataExtractor dataExtractor;
    private final BadgeManager badgeManager;
    private final PrizeController prizeController;
    private final StatsView statsView;
    private final SceneBuilder sceneBuilder;
    private final TableBuilder tableBuilder;
    private final ElementsBuilder elementsBuilder;
    private final MainController mainController;

    public StatsController(ServiceFacade serviceFacade) {
        this.profileController = serviceFacade.getProfileManager();
        this.profileModel = serviceFacade.getProfileDataManager();
        this.dataExtractor = serviceFacade.getDataExtractor();
        this.badgeManager = serviceFacade.getBadgeManager();
        this.prizeController = serviceFacade.getPrizeController();
        this.mainController = serviceFacade.getMainController();

//        subtitleLabel = new Label();
//        titleLabel = new Label();
//        LanguageManager.registerLocalizable(this);

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

    public  List<Profile> loadProfilesFromFile() {
        return profileModel.loadProfilesFromFile(FileName.PROFILES_FILE);
    }

    public TableView<Map.Entry<String, Long>> createCountPrizeTableForStats(List<Profile> profiles, TableView<Map.Entry<String, Long>> prizeTable) {
        return tableBuilder.createCountPrizeTableForStats(profiles, prizeTable, prizeController);
    }

     public HBox createTopLayoutForStats(Stage primaryStage){
         Button backButton = ButtonFactory.createBackButton(e -> mainController.showMainScreen(primaryStage));
         return elementsBuilder.createTopLayout(backButton, statsView.getTitleLabel());
     }

    public Label createSubtitleLabelForStats() {
        return elementsBuilder.createSubtitleLabelForStats(statsView.getSubtitleLabel());
    }

    public TableView<Profile> createMainTableForStats(TableView<Profile> mainTable, TableView<Map.Entry<String, Long>> prizeTable) {
        return tableBuilder.createMainTableForStats(mainTable, profileModel, dataExtractor, badgeManager, prizeController, prizeTable);
    }

}
