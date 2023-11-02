package com.example.cloudcalc.statistics;

import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.Constants;
import com.example.cloudcalc.DataExtractor;
import com.example.cloudcalc.UICallbacks;
import com.example.cloudcalc.badge.BadgeCounts;
import com.example.cloudcalc.badge.BadgeManager;
import com.example.cloudcalc.language.LanguageManager;
import com.example.cloudcalc.language.Localizable;
import com.example.cloudcalc.prize.Prize;
import com.example.cloudcalc.prize.PrizeManager;
import com.example.cloudcalc.profile.Profile;
import com.example.cloudcalc.profile.ProfileDataManager;
import com.example.cloudcalc.profile.ProfileManager;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StatsManager implements Localizable {

    private final UICallbacks uiCallbacks;
    private final ProfileManager profileManager;
    private final ProfileDataManager profileDataManager;
    private final DataExtractor dataExtractor;
    private final BadgeManager badgeManager;
    private final PrizeManager prizeManager;
    private TableView<Profile> mainTable;
    private TableView<Map.Entry<String, Long>> prizeTable;
    private Label subtitleLabel;
    private Label titleLabel;

    public StatsManager(UICallbacks uiCallbacks, ProfileManager profileManager, ProfileDataManager profileDataManager, DataExtractor dataExtractor, BadgeManager badgeManager, PrizeManager prizeManager) {
        this.uiCallbacks = uiCallbacks;
        this.profileManager = profileManager;
        this.profileDataManager = profileDataManager;
        this.dataExtractor = dataExtractor;
        this.badgeManager = badgeManager;
        this.prizeManager = prizeManager;

        subtitleLabel = new Label();
        titleLabel = new Label();
        LanguageManager.registerLocalizable(this);
    }

    public void showStatsScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        List<Profile> profiles = profileDataManager.loadProfilesFromFile(Constants.PROFILES_FILE);
        prizeTable = createCountPrizeTable(profiles);

        layout.getChildren().addAll(createTopLayout(primaryStage), prizeTable, createSubtitleLabel(), createMainTable());

        uiCallbacks.createScene(layout, primaryStage);
    }

    private TableView<Map.Entry<String, Long>> createCountPrizeTable(List<Profile> profiles) {
        prizeTable = new TableView<>();

        Map<String, Long> prizeCounts = getPrizeCounts(profiles);

        List<Prize> availablePrizes = prizeManager.loadPrizesFromFile(Constants.PRIZES_FILE);

        prizeTable.getColumns().addAll(
                createNumberColumnForCountTable(),
                createPrizeColumn(),
                createProgramColumn(availablePrizes),
                createCountColumn()
        );

        prizeTable.getItems().addAll(prizeCounts.entrySet());

        prizeTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Map.Entry<String, Long> item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && "Total".equals(item.getKey())) {
                    setStyle("-fx-font-weight: bold;");
                } else {
                    setStyle("");
                }
            }
        });

        setColumnWidths();

        return prizeTable;
    }

    private Map<String, Long> getPrizeCounts(List<Profile> profiles) {
        List<Prize> availablePrizes = prizeManager.loadPrizesFromFile(Constants.PRIZES_FILE);
        List<String> allAvailablePrizeNames = availablePrizes.stream().map(Prize::getName).collect(Collectors.toList());

        List<String> allProfilePrizes = profiles.stream()
                .flatMap(profile -> profile.getPrizes().stream())
                .collect(Collectors.toList());

        Map<String, Long> prizeCounts = new LinkedHashMap<>();

        for (String prizeName : allAvailablePrizeNames) {
            prizeCounts.put(prizeName, allProfilePrizes.stream().filter(prizeName::equals).count());
        }

        long totalPrizesCount = prizeCounts.values().stream().mapToLong(Long::longValue).sum();
        prizeCounts.put("Total", totalPrizesCount);

        return prizeCounts;
    }

    private TableColumn<Map.Entry<String, Long>, Number> createNumberColumnForCountTable() {
        TableColumn<Map.Entry<String, Long>, Number> numberColumn = new TableColumn<>("№");
        numberColumn.setCellValueFactory(column -> {
            if (column.getValue().getKey().equals("Total")) {
                return null;
            }
            return new ReadOnlyObjectWrapper<>(prizeTable.getItems().indexOf(column.getValue()) + 1);
        });
        numberColumn.setSortable(false);
        return numberColumn;
    }

    private TableColumn<Map.Entry<String, Long>, String> createPrizeColumn() {
        TableColumn<Map.Entry<String, Long>, String> prizeColumn = new TableColumn<>("Prize");
        prizeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        return prizeColumn;
    }

    private TableColumn<Map.Entry<String, Long>, String> createProgramColumn(List<Prize> availablePrizes) {
        Map<String, String> prizeToProgramMap = availablePrizes.stream()
                .collect(Collectors.toMap(Prize::getName, Prize::getProgram));

        TableColumn<Map.Entry<String, Long>, String> programColumn = new TableColumn<>("Program");
        programColumn.setCellValueFactory(cellData -> {
            String prizeName = cellData.getValue().getKey();
            return new SimpleStringProperty(prizeToProgramMap.getOrDefault(prizeName, ""));
        });
        return programColumn;
    }

    private TableColumn<Map.Entry<String, Long>, Long> createCountColumn() {
        TableColumn<Map.Entry<String, Long>, Long> countColumn = new TableColumn<>("Count");
        countColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getValue()));
        return countColumn;
    }

    private void setColumnWidths() {
        double numberColumnPercentage = 0.1;
        double countColumnPercentage = 0.2;
        double programColumnPercentage = 0.2;
        double prizeColumnPercentage = 1.0 - (numberColumnPercentage + countColumnPercentage + programColumnPercentage);

        prizeTable.getColumns().get(0).prefWidthProperty().bind(prizeTable.widthProperty().multiply(numberColumnPercentage));
        prizeTable.getColumns().get(1).prefWidthProperty().bind(prizeTable.widthProperty().multiply(prizeColumnPercentage));
        prizeTable.getColumns().get(2).prefWidthProperty().bind(prizeTable.widthProperty().multiply(programColumnPercentage));
        prizeTable.getColumns().get(3).prefWidthProperty().bind(prizeTable.widthProperty().multiply(countColumnPercentage));
    }

    private HBox createTopLayout(Stage primaryStage) {
        Button backButton = ButtonFactory.createBackButton(e -> uiCallbacks.showMainScreen(primaryStage));
        return uiCallbacks.createTopLayout(backButton, titleLabel);
    }

    private Label createSubtitleLabel() {
        subtitleLabel.setStyle("-fx-font-style: italic;");
        return subtitleLabel;
    }

    private TableView<Profile> createMainTable() {
        mainTable = new TableView<>();

        TableColumn<Profile, ?> numberColumn = createNumberColumn();
        numberColumn.prefWidthProperty().bind(mainTable.widthProperty().multiply(0.05));

        TableColumn<Profile, ?> dateColumn = createLastUpdatedColumn();
        dateColumn.prefWidthProperty().bind(mainTable.widthProperty().multiply(0.20));

        TableColumn<Profile, ?> nameColumn = profileManager.createNameColumn();
        nameColumn.prefWidthProperty().bind(mainTable.widthProperty().multiply(0.10));

        TableColumn<Profile, ?> prizesColumn = createPrizesColumn();
        prizesColumn.prefWidthProperty().bind(mainTable.widthProperty().multiply(0.50));

        TableColumn<Profile, ?> updateColumn = createUpdateColumn();
        updateColumn.prefWidthProperty().bind(mainTable.widthProperty().multiply(0.10));

        mainTable.getColumns().addAll(
                numberColumn,
                nameColumn,
                prizesColumn,
                updateColumn,
                dateColumn
        );

        List<Profile> profiles = profileDataManager.loadProfilesFromFile(Constants.PROFILES_FILE);
        mainTable.getItems().addAll(profiles);
        return mainTable;
    }

    private TableColumn<Profile, Integer> createNumberColumn() {
        TableColumn<Profile, Integer> numberColumn = new TableColumn<>("№");
        numberColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(mainTable.getItems().indexOf(cellData.getValue()) + 1));
        return numberColumn;
    }

    private TableColumn<Profile, String> createLastUpdatedColumn() {
        TableColumn<Profile, String> lastUpdatedColumn = new TableColumn<>("Date");
        lastUpdatedColumn.setCellValueFactory(new PropertyValueFactory<>("lastScannedDate"));
        return lastUpdatedColumn;
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

    private TableColumn<Profile, Void> createUpdateColumn() {
        TableColumn<Profile, Void> getPrizesColumn = new TableColumn<>("Update");
        getPrizesColumn.setCellValueFactory(param -> null);
        getPrizesColumn.setCellFactory(col -> {
            return new TableCell<Profile, Void>() {
                final Button getPrizesButton = ButtonFactory.createUpdateButton(null);

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

                            LocalDate currentDate = LocalDate.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                            profile.setLastScannedDate(currentDate.format(formatter));

                            profileDataManager.updateProfile(profile);

                            mainTable.refresh();
                            updatePrizeTable();
                        });
                        setGraphic(getPrizesButton);
                    }
                }
            };
        });
        return getPrizesColumn;
    }

    private void updatePrizeTable() {
        Map<String, Long> updatedPrizeCounts = getPrizeCounts(profileDataManager.loadProfilesFromFile(Constants.PROFILES_FILE));
        prizeTable.getItems().clear();
        prizeTable.getItems().addAll(updatedPrizeCounts.entrySet());
        prizeTable.refresh();
    }

    @Override
    public void updateLocalization(ResourceBundle bundle) {
        if(subtitleLabel != null) {
            subtitleLabel.setText(bundle.getString("statsSubtitle"));
        }

        if(titleLabel != null) {
            titleLabel.setText(bundle.getString("statsTitle"));
        }

    }
}
