package com.example.cloudcalc.statistics;

import com.example.cloudcalc.ButtonFactory;
import com.example.cloudcalc.Constants;
import com.example.cloudcalc.DataExtractor;
import com.example.cloudcalc.UICallbacks;
import com.example.cloudcalc.badge.BadgeCounts;
import com.example.cloudcalc.badge.BadgeManager;
import com.example.cloudcalc.prize.PrizeManager;
import com.example.cloudcalc.profile.Profile;
import com.example.cloudcalc.profile.ProfileDataManager;
import com.example.cloudcalc.profile.ProfileManager;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class StatsManager {

    private final UICallbacks uiCallbacks;

    private final ProfileManager profileManager;

    private final ProfileDataManager profileDataManager;
    private final DataExtractor dataExtractor;
    private final BadgeManager badgeManager;
    private final PrizeManager prizeManager;

    public StatsManager(UICallbacks uiCallbacks, ProfileManager profileManager, ProfileDataManager profileDataManager, DataExtractor dataExtractor, BadgeManager badgeManager, PrizeManager prizeManager) {
        this.uiCallbacks = uiCallbacks;
        this.profileManager = profileManager;
        this.profileDataManager = profileDataManager;
        this.dataExtractor = dataExtractor;
        this.badgeManager = badgeManager;
        this.prizeManager = prizeManager;
    }

    public void showStatsScreen(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.getChildren().addAll(createTopLayout(primaryStage), createSubtitleLabel(), createMainTable());

        uiCallbacks.createScene(layout, primaryStage);
    }

    private HBox createTopLayout(Stage primaryStage) {
        Button backButton = ButtonFactory.createBackButton(e -> uiCallbacks.showMainScreen(primaryStage));
        String title = String.format("STATISTICS");
        Label titleLabel = uiCallbacks.createLabel(title);
        return uiCallbacks.createTopLayout(backButton, titleLabel);
    }

    private Label createSubtitleLabel() {
        Label subtitleLabel = new Label("To get up-to-date results, click the Get prizes button");
        subtitleLabel.setStyle("-fx-font-style: italic;");
        return subtitleLabel;
    }

    private TableView<Profile> createMainTable() {
        TableView<Profile> table = new TableView<>();

        table.getColumns().addAll(
                createNumberColumn(table),
                profileManager.createNameColumn(),
                createPrizesColumn(),
                createGetPrizesColumn(table)
        );
        List<Profile> profiles = profileDataManager.loadProfilesFromFile(Constants.PROFILES_FILE);
        table.getItems().addAll(profiles);
        return table;
    }

    private TableColumn<Profile, Integer> createNumberColumn(TableView<Profile> table) {
        TableColumn<Profile, Integer> numberColumn = new TableColumn<>("No.");
        numberColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(cellData.getValue()) + 1));
        return numberColumn;
    }

    private TableColumn<Profile, String> createPrizesColumn() {
        TableColumn<Profile, String> prizesColumn = new TableColumn<>("Prizes");
        prizesColumn.setCellValueFactory(cellData -> {
            List<String> prizes = cellData.getValue().getPrizes();
            if (prizes == null || prizes.isEmpty()) {
                return new SimpleStringProperty("No prizes");
            }
            return new SimpleStringProperty(String.join(", ", prizes));
        });
        return prizesColumn;
    }

    private TableColumn<Profile, Void> createGetPrizesColumn(TableView<Profile> table) {
        TableColumn<Profile, Void> getPrizesColumn = new TableColumn<>("Get Prizes");
        getPrizesColumn.setCellValueFactory(param -> null);
        getPrizesColumn.setCellFactory(col -> {
            return new TableCell<Profile, Void>() {
                final Button getPrizesButton = new Button("Get Prizes");

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                    } else {
                        Profile profile = getTableView().getItems().get(getIndex());

                        getPrizesButton.setOnAction(e -> {

                            ArrayList<String> siteLinks = dataExtractor.performScan(profile);
                            BadgeCounts badgeCounts = badgeManager.calculateBadgeCounts(profile, siteLinks);
                            List<String> prizes = badgeManager.getPrizeManager().determinePrizesForBadgeCount(
                                    badgeCounts.getPrizePDF(),
                                    badgeCounts.getPrizeSkill(),
                                    badgeCounts.getPrizeActivity(),
                                    badgeCounts.getPrizePL()
                            );
                            profile.setPrizes(prizes);
                            profileDataManager.updateProfile(profile);

                            table.refresh();
                        });
                        setGraphic(getPrizesButton);
                    }
                }
            };
        });
        return getPrizesColumn;
    }

}
