package com.example.cloudcalc.view;

import com.example.cloudcalc.controller.StatsController;
import com.example.cloudcalc.language.Localizable;
import com.example.cloudcalc.entity.Profile;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class StatsView implements Localizable {

    private final StatsController statsController;
    private TableView<Map.Entry<String, Long>> prizeTable;
    private Label subtitleLabel;
    private Label titleLabel;
    TableView<Profile> mainTable;

    public StatsView(StatsController statsController) {
        this.statsController = statsController;
        this.titleLabel = new Label("STATISTICS");
        this.subtitleLabel = new Label("To get up-to-date results, click the \"Update\" button.");
    }

    public Label getSubtitleLabel() {
        return subtitleLabel;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public void showStatsScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        List<Profile> profiles = statsController.loadProfilesFromFile();
        prizeTable = statsController.createCountPrizeTableForStats(profiles, prizeTable);

        layout.getChildren().addAll(
                statsController.createTopLayout(primaryStage),
                prizeTable,
                statsController.createSubtitleLabelForStats(),
                statsController.createMainTableForStats(mainTable, prizeTable)
        );

        statsController.createScene(layout, primaryStage);
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {

    }

    @Override
    public void updateElements(String newTitle, String newAddScreenTitle, ResourceBundle bundle) {
        Localizable.super.updateElements(newTitle, newAddScreenTitle, bundle);
    }
}
