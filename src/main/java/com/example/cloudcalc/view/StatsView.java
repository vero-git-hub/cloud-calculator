package com.example.cloudcalc.view;

import com.example.cloudcalc.controller.StatsController;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class StatsView implements Localizable {

    private final StatsController statsController;
    private Label titleLabel = new Label("STATISTICS");
    private Label subtitleLabel = new Label("To get up-to-date results, click the calculator icon.");

    public StatsView(StatsController statsController) {
        this.statsController = statsController;

        LanguageManager.registerLocalizable(this);
    }

    public Label getSubtitleLabel() {
        return subtitleLabel;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public void showStatsScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        layout.getChildren().addAll(
                statsController.createTopLayout(primaryStage),
                statsController.createSubtitleLabelForStats(),
                statsController.createMainTableForStats(primaryStage)
        );

        statsController.createScene(layout, primaryStage);
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        titleLabel.setText(bundle.getString("statsTitle"));
        subtitleLabel.setText(bundle.getString("statsSubtitle"));
    }

    @Override
    public void updateElements(String newTitle, String newAddScreenTitle, ResourceBundle bundle) {
        Localizable.super.updateElements(newTitle, newAddScreenTitle, bundle);
    }
}