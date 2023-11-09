package com.example.cloudcalc.builder;

import com.example.cloudcalc.DataExtractor;
import com.example.cloudcalc.PdfLinkItem;
import com.example.cloudcalc.badge.BadgeCounts;
import com.example.cloudcalc.badge.BadgeManager;
import com.example.cloudcalc.badge.FileOperationManager;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.controller.ArcadeController;
import com.example.cloudcalc.controller.MainController;
import com.example.cloudcalc.prize.Prize;
import com.example.cloudcalc.prize.PrizeController;
import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.model.ProfileModel;
import com.example.cloudcalc.controller.ProfileController;
import com.example.cloudcalc.util.AlertGuardian;
import com.example.cloudcalc.util.Notification;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class TableBuilder {

    private String fileName;
    private FileOperationManager fileOperationManager;
    //private ScreenDisplayable screenDisplayable;
    private String addScreenLabel;
    private TableView<String> table;
    private Label titleLabel;
    private Label titleAddScreenLabel;
    //static NameTextFieldUpdatable nameTextFieldUpdatable;


    public void initVariablesForTable(String fileName, FileOperationManager fileOperationManager, String addScreenLabel) {
        this.fileName = fileName;
        this.fileOperationManager = fileOperationManager;
//        TableBuilder.screenDisplayable = screenDisplayable;
        this.addScreenLabel = addScreenLabel;
//        this.nameTextFieldUpdatable = nameTextFieldUpdatable;
    }

    public TableView<String> createBadgeTable(Stage primaryStage, ArcadeController arcadeController) {
        table = new TableView<>();

        TableColumn<String, Integer> indexColumn = createIndexColumn(table);
        TableColumn<String, String> nameColumn = createNameColumnForBadge();
        TableColumn<String, Void> deleteColumn = createDeleteColumn(primaryStage, table, arcadeController);

        table.getColumns().addAll(indexColumn, nameColumn, deleteColumn);

        List<String> badges = fileOperationManager.loadBadgesFromFile(fileName);
        table.getItems().addAll(badges);

        configureTableColumnsWidth();

        return table;
    }

    private TableColumn<String, Integer> createIndexColumn(TableView<String> table) {
        TableColumn<String, Integer> indexColumn = new TableColumn<>("№");
        indexColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(cellData.getValue()) + 1));
        return indexColumn;
    }

    public TableColumn<Profile, Void> createNumberingColumn() {
        TableColumn<Profile, Void> numberColumn = new TableColumn<>("№");
        numberColumn.setMinWidth(40);
        numberColumn.setCellValueFactory(param -> null);
        numberColumn.setCellFactory(col -> {
            return new TableCell<Profile, Void>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setText(null);
                    } else {
                        setText(Integer.toString(getIndex() + 1));
                    }
                }
            };
        });
        return numberColumn;
    }

    private TableColumn<String, String> createNameColumnForBadge() {
        TableColumn<String, String> nameColumn = new TableColumn<>("Badge name");
        nameColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
        return nameColumn;
    }

    public TableColumn<Profile, String> createNameColumn() {
        TableColumn<Profile, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        return nameColumn;
    }

    public VBox createPdfLinksSectionForProfile(Profile profile, Label linksTitle) {
        VBox linksVBox = new VBox(5);

        linksVBox.getChildren().add(linksTitle);

        TableView<PdfLinkItem> table = new TableView<>();

        TableColumn<PdfLinkItem, Integer> indexColumn = new TableColumn<>("No.");
        indexColumn.setCellValueFactory(new PropertyValueFactory<>("index"));

        TableColumn<PdfLinkItem, String> linkColumn = new TableColumn<>("Link");
        linkColumn.setCellValueFactory(new PropertyValueFactory<>("link"));

        indexColumn.setResizable(false);
        linkColumn.setResizable(false);

        indexColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.05));
        linkColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.95));

        table.getColumns().addAll(indexColumn, linkColumn);

        if (profile.getPdfLinks() != null) {
            int index = 1;
            for (String link : profile.getPdfLinks()) {
                table.getItems().add(new PdfLinkItem(index++, link));
            }
        }
        table.setFixedCellSize(Region.USE_COMPUTED_SIZE);
        linksVBox.getChildren().add(table);

        return linksVBox;
    }

    public TableColumn<Profile, Integer> createNumberColumnForStats(TableView<Profile> mainTable) {
        TableColumn<Profile, Integer> numberColumn = new TableColumn<>("№");
        numberColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(mainTable.getItems().indexOf(cellData.getValue()) + 1));
        return numberColumn;
    }

    public TableColumn<Profile, String> createLastUpdatedColumnForStats() {
        TableColumn<Profile, String> lastUpdatedColumn = new TableColumn<>("Date");
        lastUpdatedColumn.setCellValueFactory(new PropertyValueFactory<>("lastScannedDate"));
        return lastUpdatedColumn;
    }

    public TableColumn<Profile, String> createPrizesColumnForStats() {
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

    public TableColumn<Map.Entry<String, Long>, Long> createCountColumnForStats() {
        TableColumn<Map.Entry<String, Long>, Long> countColumn = new TableColumn<>("Count");
        countColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getValue()));
        return countColumn;
    }

    public TableColumn<Map.Entry<String, Long>, String> createPrizeColumnForStats() {
        TableColumn<Map.Entry<String, Long>, String> prizeColumn = new TableColumn<>("Prize");
        prizeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        return prizeColumn;
    }

    public TableColumn<Map.Entry<String, Long>, Number> createNumberColumnForCountTableForStats(TableView<Map.Entry<String, Long>> prizeTable) {
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

    public TableView<Map.Entry<String, Long>> createCountPrizeTableForStats(List<Profile> profiles, TableView<Map.Entry<String, Long>> prizeTable, PrizeController prizeController) {
        prizeTable = new TableView<>();

        Map<String, Long> prizeCounts = getPrizeCountsForStats(profiles, prizeController);

        List<Prize> availablePrizes = prizeController.loadPrizesFromFile(FileName.PRIZES_FILE);

        prizeTable.getColumns().addAll(
                createNumberColumnForCountTableForStats(prizeTable),
                createPrizeColumnForStats(),
                createProgramColumnForStats(availablePrizes),
                createCountColumnForStats()
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

        setColumnWidthForStats(prizeTable);

        return prizeTable;
    }

    private TableColumn<Map.Entry<String, Long>, String> createProgramColumnForStats(List<Prize> availablePrizes) {
        Map<String, String> prizeToProgramMap = availablePrizes.stream()
                .collect(Collectors.toMap(Prize::getName, Prize::getProgram));

        TableColumn<Map.Entry<String, Long>, String> programColumn = new TableColumn<>("Program");
        programColumn.setCellValueFactory(cellData -> {
            String prizeName = cellData.getValue().getKey();
            return new SimpleStringProperty(prizeToProgramMap.getOrDefault(prizeName, ""));
        });
        return programColumn;
    }

    public Map<String, Long> getPrizeCountsForStats(List<Profile> profiles, PrizeController prizeController) {
        List<Prize> availablePrizes = prizeController.loadPrizesFromFile(FileName.PRIZES_FILE);
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

    public TableColumn<Profile, Void> createUpdateColumnForStats(DataExtractor dataExtractor, BadgeManager badgeManager, ProfileModel profileModel, TableView<Profile> mainTable, PrizeController prizeController, TableView<Map.Entry<String, Long>> prizeTable) {
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
//                            List<String> prizes = badgeManager.getPrizeManager().determinePrizesForBadgeCount(
//                                    badgeCounts.getPrizePDF(),
//                                    badgeCounts.getPrizeSkill(),
//                                    badgeCounts.getPrizeActivity(),
//                                    badgeCounts.getPrizePL()
//                            );
                            List<String> prizes = new ArrayList<>();
                                    profile.setPrizes(prizes);

                            LocalDate currentDate = LocalDate.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                            profile.setLastScannedDate(currentDate.format(formatter));

                            profileModel.updateProfile(profile);

                            mainTable.refresh();
                            updatePrizeTableForStats(profileModel, prizeController, prizeTable);
                        });
                        setGraphic(getPrizesButton);
                    }
                }
            };
        });
        return getPrizesColumn;
    }

    private void updatePrizeTableForStats(ProfileModel profileModel, PrizeController prizeController, TableView<Map.Entry<String, Long>> prizeTable) {
        Map<String, Long> updatedPrizeCounts = getPrizeCountsForStats(profileModel.loadProfilesFromFile(FileName.PROFILES_FILE), prizeController);
        prizeTable.getItems().clear();
        prizeTable.getItems().addAll(updatedPrizeCounts.entrySet());
        prizeTable.refresh();
    }

    public TableView<Profile> createMainTableForStats(TableView<Profile> mainTable, ProfileModel profileModel, DataExtractor dataExtractor, BadgeManager badgeManager, PrizeController prizeController, TableView<Map.Entry<String, Long>> prizeTable) {
        mainTable = new TableView<>();

        TableColumn<Profile, ?> numberColumn = createNumberColumnForStats(mainTable);
        numberColumn.prefWidthProperty().bind(mainTable.widthProperty().multiply(0.05));

        TableColumn<Profile, ?> dateColumn = createLastUpdatedColumnForStats();
        dateColumn.prefWidthProperty().bind(mainTable.widthProperty().multiply(0.20));

        TableColumn<Profile, ?> nameColumn = createNameColumn();
        nameColumn.prefWidthProperty().bind(mainTable.widthProperty().multiply(0.10));

        TableColumn<Profile, ?> prizesColumn = createPrizesColumnForStats();
        prizesColumn.prefWidthProperty().bind(mainTable.widthProperty().multiply(0.50));

        TableColumn<Profile, ?> updateColumn = createUpdateColumnForStats(dataExtractor, badgeManager, profileModel, mainTable, prizeController, prizeTable);
        updateColumn.prefWidthProperty().bind(mainTable.widthProperty().multiply(0.10));

        mainTable.getColumns().addAll(
                numberColumn,
                nameColumn,
                prizesColumn,
                updateColumn,
                dateColumn
        );

        List<Profile> profiles = profileModel.loadProfilesFromFile(FileName.PROFILES_FILE);
        mainTable.getItems().addAll(profiles);
        return mainTable;
    }

    private TableColumn<String, Void> createDeleteColumn(Stage primaryStage, TableView<String> table, ArcadeController arcadeController) {
        TableColumn<String, Void> deleteColumn = new TableColumn<>("Delete");
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            final Button deleteButton = ButtonFactory.createDeleteButton(e -> {

                boolean isConfirmationAlert = Notification.showConfirmationAlert(AlertGuardian.alertTitleDeleteBadge, AlertGuardian.alertHeaderDeleteBadge, AlertGuardian.alertContentDeleteBadge);

                if (isConfirmationAlert) {
                    String badge = getTableView().getItems().get(getIndex());
                    List<String> badges = getTableView().getItems();
                    badges.remove(badge);
                    fileOperationManager.saveBadgesToFile(badges, fileName);
                    arcadeController.showScreen(primaryStage);
                }
            });

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
        return deleteColumn;
    }

    private void configureTableColumnsWidth() {
        double indexColumnPercentage = 0.05;
        double deleteColumnPercentage = 0.1;
        double nameColumnPercentage = 1.0 - (indexColumnPercentage + deleteColumnPercentage);

        table.getColumns().get(0).prefWidthProperty().bind(table.widthProperty().multiply(indexColumnPercentage));
        table.getColumns().get(1).prefWidthProperty().bind(table.widthProperty().multiply(nameColumnPercentage));
        table.getColumns().get(2).prefWidthProperty().bind(table.widthProperty().multiply(deleteColumnPercentage));
    }

    public void configureTableColumnsWidthForMain(TableView<Profile> mainTable) {
        double numberColumnPercentage = 0.05;
        double badgesColumnPercentage = 0.1;
        double viewingColumnPercentage = 0.1;
        double actionColumnPercentage = 0.1;
        double nameColumnPercentage = 1.0 - (numberColumnPercentage + badgesColumnPercentage + viewingColumnPercentage + actionColumnPercentage);

        mainTable.getColumns().get(0).prefWidthProperty().bind(mainTable.widthProperty().multiply(numberColumnPercentage));
        mainTable.getColumns().get(1).prefWidthProperty().bind(mainTable.widthProperty().multiply(nameColumnPercentage));
        mainTable.getColumns().get(2).prefWidthProperty().bind(mainTable.widthProperty().multiply(badgesColumnPercentage));
        mainTable.getColumns().get(3).prefWidthProperty().bind(mainTable.widthProperty().multiply(viewingColumnPercentage));
        mainTable.getColumns().get(4).prefWidthProperty().bind(mainTable.widthProperty().multiply(actionColumnPercentage));
    }

    public void setColumnWidthForStats(TableView<Map.Entry<String, Long>> prizeTable) {
        double numberColumnPercentage = 0.1;
        double countColumnPercentage = 0.2;
        double programColumnPercentage = 0.2;
        double prizeColumnPercentage = 1.0 - (numberColumnPercentage + countColumnPercentage + programColumnPercentage);

        prizeTable.getColumns().get(0).prefWidthProperty().bind(prizeTable.widthProperty().multiply(numberColumnPercentage));
        prizeTable.getColumns().get(1).prefWidthProperty().bind(prizeTable.widthProperty().multiply(prizeColumnPercentage));
        prizeTable.getColumns().get(2).prefWidthProperty().bind(prizeTable.widthProperty().multiply(programColumnPercentage));
        prizeTable.getColumns().get(3).prefWidthProperty().bind(prizeTable.widthProperty().multiply(countColumnPercentage));
    }

    public void updateTitle(String newTitle) {
        if (titleLabel != null) {
            titleLabel.setText(newTitle);
        }
    }

    public void updateAddScreenTitle(String newAddScreenTitle) {
        if (titleAddScreenLabel != null) {
            titleAddScreenLabel.setText(newAddScreenTitle);
        }
    }

    public static void updateNotificationAlert(String newTitleAlert, String newHeaderAlert, String newContentAlert) {
        if (AlertGuardian.nameAlertTitle != null) {
            AlertGuardian.nameAlertTitle = newTitleAlert;
        }
        if (AlertGuardian.nameAlertHeader != null) {
            AlertGuardian.nameAlertHeader = newHeaderAlert;
        }
        if (AlertGuardian.nameAlertContent != null) {
            AlertGuardian.nameAlertContent = newContentAlert;
        }

    }

    public static void updateDeleteAlert(String newAlertTitleDeleteBadge, String newAlertHeaderDeleteBadge, String newAlertContentDeleteBadge) {
        if(AlertGuardian.alertTitleDeleteBadge != null) {
            AlertGuardian.alertTitleDeleteBadge = newAlertTitleDeleteBadge;
        }
        if(AlertGuardian.alertHeaderDeleteBadge != null) {
            AlertGuardian.alertHeaderDeleteBadge = newAlertHeaderDeleteBadge;
        }
        if(AlertGuardian.alertContentDeleteBadge != null) {
            AlertGuardian.alertContentDeleteBadge = newAlertContentDeleteBadge;
        }

    }

    public TableView<Profile> createTableForMain(Stage primaryStage, ProfileController profileController, MainController mainController) {
        TableView<Profile> mainTable = new TableView<>();

        TableColumn<Profile, Void> numberColumn = createNumberingColumn();
        TableColumn<Profile, String> nameColumn = createNameColumn();
        TableColumn<Profile, Void> badgesColumn = createBadgesColumnForMain(primaryStage, profileController);
        TableColumn<Profile, Profile> viewingColumn = createViewingColumnForProfile(primaryStage, profileController);
        TableColumn<Profile, Profile> actionColumn = createActionColumnForProfile(primaryStage, profileController, mainController);

        mainTable.getColumns().addAll(numberColumn, nameColumn, badgesColumn, viewingColumn, actionColumn);

        List<Profile> profiles = profileController.getProfilesFromFile();
        mainTable.getItems().addAll(profiles);

        configureTableColumnsWidthForMain(mainTable);
        return mainTable;
    }

    public static TableColumn<Profile, Profile> createActionColumnForProfile(Stage primaryStage, ProfileController profileController, MainController mainController) {
        TableColumn<Profile, Profile> actionColumn = new TableColumn<>("Delete");
        actionColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
        actionColumn.setCellFactory(param -> new TableCell<Profile, Profile>() {
            @Override
            protected void updateItem(Profile profile, boolean empty) {
                super.updateItem(profile, empty);
                if (profile == null || empty) {
                    setGraphic(null);
                    return;
                }

                EventHandler<ActionEvent> deleteAction = e -> {
                    boolean isConfirmationAlert = Notification.showConfirmationAlert(AlertGuardian.alertTitleDeleteProfile, AlertGuardian.alertHeaderDeleteProfile, AlertGuardian.alertContentDeleteProfile);

                    if (isConfirmationAlert) {
                        profileController.handleDeleteAction(primaryStage, profile, mainController);
                    }
                };
                Button deleteButton = ButtonFactory.createDeleteButton(deleteAction);
                setGraphic(deleteButton);
            }
        });
        return actionColumn;
    }

    public static TableColumn<Profile, Profile> createViewingColumnForProfile(Stage primaryStage, ProfileController profileController) {
            TableColumn<Profile, Profile> viewingColumn = new TableColumn<>("View");
            viewingColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
            viewingColumn.setCellFactory(param -> new TableCell<Profile, Profile>() {
                @Override
                protected void updateItem(Profile profile, boolean empty) {
                    super.updateItem(profile, empty);
                    if (profile == null || empty) {
                        setGraphic(null);
                        return;
                    }

                    EventHandler<ActionEvent> viewAction = e -> {
                        profileController.showProfileDetailsScreen(primaryStage, profile);
                    };
                    Button viewButton = ButtonFactory.createViewButton(viewAction);
                    setGraphic(viewButton);
                }
            });
        return viewingColumn;
    }

    public static TableColumn<Profile, Void> createBadgesColumnForMain(Stage primaryStage, ProfileController profileController) {
        TableColumn<Profile, Void> badgesColumn = new TableColumn<>("Scan");
        badgesColumn.setCellValueFactory(param -> null);
        badgesColumn.setCellFactory(col -> {
            return new TableCell<Profile, Void>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                    } else {
                        Profile profile = getTableView().getItems().get(getIndex());
                        EventHandler<ActionEvent> scanAction = e -> {
                            profileController.scanAndUpdateProfile(primaryStage, profile);
//                            ArrayList<String> siteLinks = dataExtractor.performScan(profile);
//                            profile.setLastScannedDate(DateUtils.getCurrentDate());
//                            profileModel.updateProfile(profile);
//
//                            scanManager.showScanScreen(primaryStage, profile, siteLinks);
                        };
                        Button scanButton = ButtonFactory.createScanButton(scanAction);
                        setGraphic(scanButton);
                    }
                }
            };
        });
        return badgesColumn;
    }

}
