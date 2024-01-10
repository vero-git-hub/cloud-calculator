package com.example.cloudcalc.builder;

import com.example.cloudcalc.DataExtractor;
import com.example.cloudcalc.badge.BadgeManager;
import com.example.cloudcalc.badge.FileOperationManager;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.controller.*;
import com.example.cloudcalc.entity.Prize;
import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.entity.badge.BadgeCounts;
import com.example.cloudcalc.model.ProfileModel;
import com.example.cloudcalc.util.AlertGuardian;
import com.example.cloudcalc.util.Notification;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TableBuilder {

    private String fileName;
    private FileOperationManager fileOperationManager;
    private String addScreenLabel;
    private TableView<String> table;
    private Label titleLabel;
    private Label titleAddScreenLabel;

    public void initVariablesForTable(String fileName, FileOperationManager fileOperationManager, String addScreenLabel) {
        this.fileName = fileName;
        this.fileOperationManager = fileOperationManager;
        this.addScreenLabel = addScreenLabel;
    }

    public TableView<String> createBadgeTable(Stage primaryStage, IScreenController screenController) {
        table = new TableView<>();

        TableColumn<String, Integer> indexColumn = createIndexColumn(table);
        TableColumn<String, String> nameColumn = createNameColumnForBadge();
        TableColumn<String, Void> deleteColumn = createDeleteColumn(primaryStage, table, screenController);

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

    public VBox createPdfLinksSectionForProfile(Profile profile, Label linksTitle) {
        VBox linksVBox = new VBox(5);

        linksVBox.getChildren().add(linksTitle);

//        TableView<PdfLinkItem> table = new TableView<>();
//
//        TableColumn<PdfLinkItem, Integer> indexColumn = new TableColumn<>("№");
//        indexColumn.setCellValueFactory(new PropertyValueFactory<>("index"));
//
//        TableColumn<PdfLinkItem, String> linkColumn = new TableColumn<>("Link");
//        linkColumn.setCellValueFactory(new PropertyValueFactory<>("link"));
//
//        indexColumn.setResizable(false);
//        linkColumn.setResizable(false);
//
//        indexColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.05));
//        linkColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.95));
//
//        table.getColumns().addAll(indexColumn, linkColumn);
//
//        if (profile.getPdfLinks() != null) {
//            int index = 1;
//            for (String link : profile.getPdfLinks()) {
//                table.getItems().add(new PdfLinkItem(index++, link));
//            }
//        }
//        table.setFixedCellSize(Region.USE_COMPUTED_SIZE);
//        linksVBox.getChildren().add(table);

        return linksVBox;
    }

    public TableColumn<Profile, Integer> createNumberColumnForStats(TableView<Profile> mainTable) {
        TableColumn<Profile, Integer> numberColumn = new TableColumn<>("№");
        numberColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(mainTable.getItems().indexOf(cellData.getValue()) + 1));
        return numberColumn;
    }

    public TableColumn<Profile, String> createLastUpdatedColumnForStats() {
        TableColumn<Profile, String> lastUpdatedColumn = new TableColumn<>("Update Date");
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

        List<Prize> availablePrizes = prizeController.loadPrizesFromFile();

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

        //setColumnWidthForStats(prizeTable);

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
        List<Prize> availablePrizes = prizeController.loadPrizesFromFile();
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

    public TableView<Profile> createMainTableForStats(Stage stage, TableView<Profile> mainTable, ProfileModel profileModel, DataExtractor dataExtractor, BadgeManager badgeManager, PrizeController prizeController, TableView<Map.Entry<String, Long>> prizeTable) {
        mainTable = new TableView<>();

        TableColumn<Profile, ?> numberColumn = createNumberColumnForStats(mainTable);
        TableColumn<Profile, ?> dateColumn = createLastUpdatedColumnForStats();
        TableColumn<Profile, ?> nameColumn = createColumn("Name");
        TableColumn<Profile, ?> programColumn = createColumn("Programs");
        TableColumn<Profile, ?> prizesColumn = createPrizesColumnForStats();
        TableColumn<Profile, ?> updateColumn = createUpdateColumnForStats(dataExtractor, badgeManager, profileModel, mainTable, prizeController, prizeTable);
        TableColumn<Profile, Void> detailsColumn = createScanColumn(stage, prizeController.getScanController());

        mainTable.getColumns().addAll(
                numberColumn,
                dateColumn,
                nameColumn,
                programColumn,
                prizesColumn,
                updateColumn,
                detailsColumn
        );

        setColumnWidthForStats(mainTable);

        List<Profile> profiles = profileModel.loadProfilesFromFile(FileName.PROFILES_FILE);
        mainTable.getItems().addAll(profiles);
        return mainTable;
    }

    private TableColumn<String, Void> createDeleteColumn(Stage primaryStage, TableView<String> table, IScreenController screenController) {
        TableColumn<String, Void> deleteColumn = new TableColumn<>("Delete");
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            final Button deleteButton = ButtonFactory.createDeleteButton(e -> {

                boolean isConfirmationAlert = Notification.showConfirmationAlert(AlertGuardian.alertTitleDeleteBadge, AlertGuardian.alertHeaderDeleteBadge, AlertGuardian.alertContentDeleteBadge);

                if (isConfirmationAlert) {
                    String badge = getTableView().getItems().get(getIndex());
                    List<String> badges = getTableView().getItems();
                    badges.remove(badge);
                    fileOperationManager.saveBadgesToFile(badges, fileName);
                    screenController.showScreen(primaryStage);
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
        double nameColumnPercentage = 0.1;
        double programsColumnPercentage = 0.2;
        double scanColumnPercentage = 0.1;
        double viewingColumnPercentage = 0.1;
        double editColumnPercentage = 0.1;
        double deleteColumnPercentage = 0.1;

        double linkColumnPercentage = 1.0 - (numberColumnPercentage + nameColumnPercentage + programsColumnPercentage + scanColumnPercentage + editColumnPercentage + deleteColumnPercentage);

        mainTable.getColumns().get(0).prefWidthProperty().bind(mainTable.widthProperty().multiply(numberColumnPercentage));
        mainTable.getColumns().get(1).prefWidthProperty().bind(mainTable.widthProperty().multiply(nameColumnPercentage));
        mainTable.getColumns().get(2).prefWidthProperty().bind(mainTable.widthProperty().multiply(linkColumnPercentage));
        mainTable.getColumns().get(3).prefWidthProperty().bind(mainTable.widthProperty().multiply(programsColumnPercentage));
        mainTable.getColumns().get(4).prefWidthProperty().bind(mainTable.widthProperty().multiply(scanColumnPercentage));
        mainTable.getColumns().get(5).prefWidthProperty().bind(mainTable.widthProperty().multiply(editColumnPercentage));
        mainTable.getColumns().get(6).prefWidthProperty().bind(mainTable.widthProperty().multiply(deleteColumnPercentage));
    }

    public void setColumnWidthForStats(TableView<Profile> table) {
        double numberColumnPercentage = 0.05;
        double dateColumnPercentage = 0.15;
        double nameColumnPercentage = 0.15;
        double programsColumnPercentage = 0.2;
        double prizesColumnPercentage = 0.2;
        double updateColumnPercentage = 0.15;
        double scanColumnPercentage = 0.1;

        setColumnWidths(table, numberColumnPercentage, dateColumnPercentage, nameColumnPercentage, programsColumnPercentage, prizesColumnPercentage, updateColumnPercentage, scanColumnPercentage);
    }

    private void setColumnWidths(TableView<?> table, double... percentages) {
        int numColumns = table.getColumns().size();
        if (percentages.length != numColumns) {
            throw new IllegalArgumentException("The number of percentage values must match the number of table columns");
        }

        for (int i = 0; i < numColumns; i++) {
            TableColumn<?, ?> column = table.getColumns().get(i);
            column.prefWidthProperty().bind(table.widthProperty().multiply(percentages[i]));
        }
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

    public TableView<Profile> createTableForMain(Stage primaryStage, ProfileController profileController, MainController mainController, ScanController scanController) {
        TableView<Profile> mainTable = new TableView<>();

        TableColumn<Profile, Void> numberColumn = createNumberingColumn();
        TableColumn<Profile, String> nameColumn = createColumn("Name");
        TableColumn<Profile, String> linkColumn = createLinkColumn("Link");

        TableColumn<Profile, String> programsColumn = createColumn("Programs");

        TableColumn<Profile, Void> badgesColumn = createScanColumn(primaryStage, scanController);
        TableColumn<Profile, Profile> viewingColumn = createViewingColumnForProfile(primaryStage, profileController);
        TableColumn<Profile, Profile> editColumn = createEditColumnForProfile(primaryStage, profileController, mainController);
        TableColumn<Profile, Profile> actionColumn = createActionColumnForProfile(primaryStage, profileController, mainController);

        mainTable.getColumns().addAll(numberColumn, nameColumn, linkColumn, programsColumn, badgesColumn, editColumn, actionColumn);

        List<Profile> profiles = profileController.getProfilesFromFile();
        mainTable.getItems().addAll(profiles);

        configureTableColumnsWidthForMain(mainTable);
        return mainTable;
    }

    private TableColumn<Profile, String> createLinkColumn(String value) {
        TableColumn<Profile, String> column = new TableColumn<>(value);
        column.setCellValueFactory(new PropertyValueFactory<>(value.toLowerCase()));

        column.setCellFactory(tc -> new TableCell<Profile, String>() {
            private Hyperlink hyperlink = new Hyperlink();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    String[] fullLink = item.split("/");
                    hyperlink.setText(fullLink[fullLink.length-1]);
                    hyperlink.setOnAction(e -> {
                        try {
                            Desktop.getDesktop().browse(new URI(item));
                        } catch (IOException | URISyntaxException ex) {
                            ex.printStackTrace();
                        }
                    });

                    setGraphic(hyperlink);
                    setText(null);
                }
            }
        });

        return column;
    }

    private TableColumn<Profile, String> createColumn(String value) {
        TableColumn<Profile, String> column = new TableColumn<>(value);
        if ("Programs".equalsIgnoreCase(value)) {
            column.setCellValueFactory(cellData -> {
                Profile profile = cellData.getValue();
                List<String> programs = profile.getPrograms();
                String programsString = programs != null ? String.join(", ", programs) : "";
                return new ReadOnlyStringWrapper(programsString);
            });
        } else {
            column.setCellValueFactory(new PropertyValueFactory<>(value.toLowerCase()));
        }
        return column;
    }

    public TableView<Prize> createTableForPrize(Stage stage, List<Prize> prizes, PrizeController prizeController) {
        TableView<Prize> table = new TableView<>();

        TableColumn<Prize, Integer> idxColumn = createNumberColumnForPrize(table);
        TableColumn<Prize, String> nameColumn = createColumnForPrize("Name");
        TableColumn<Prize, String> programColumn = createColumnForPrize("Program");
        TableColumn<Prize, Integer> countColumn = createCountColumnForPrize();
        TableColumn<Prize, Prize> editColumn = createEditColumn(stage, prizeController);
        TableColumn<Prize, Void> deleteColumn = createDeleteColumnForPrize(stage, prizeController);

        table.getColumns().addAll(idxColumn, nameColumn, programColumn, countColumn, editColumn, deleteColumn);
        table.getItems().addAll(prizes);

        configureTableColumnsWidthForPrize(table);

        return table;
    }

    private TableColumn<Prize, Prize> createEditColumn(Stage stage, PrizeController prizeController) {
        TableColumn<Prize, Prize> column = new TableColumn<>("Edit");
        column.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
        column.setCellFactory(param -> new TableCell<Prize, Prize>() {
            @Override
            protected void updateItem(Prize prize, boolean empty) {
                super.updateItem(prize, empty);
                if (prize == null || empty) {
                    setGraphic(null);
                    return;
                }

                EventHandler<ActionEvent> action = e -> {
                    prizeController.showEditPrizeScreen(stage, prize);
                };

                Button button = ButtonFactory.createEditButton(action);
                setGraphic(button);
            }
        });
        return column;
    }

    public TableColumn<Prize, Integer> createNumberColumnForPrize(TableView<Prize> mainTable) {
        TableColumn<Prize, Integer> numberColumn = new TableColumn<>("№");
        numberColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(mainTable.getItems().indexOf(cellData.getValue()) + 1));
        return numberColumn;
    }

    private TableColumn<Prize, Void> createDeleteColumnForPrize(Stage stage, PrizeController prizeController) {
        TableColumn<Prize, Void> deleteColumn = new TableColumn<>("Actions");

        Callback<TableColumn<Prize, Void>, TableCell<Prize, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Prize, Void> call(final TableColumn<Prize, Void> param) {
                return new TableCell<>() {
                    final EventHandler<ActionEvent> deleteAction = (ActionEvent event) -> {
                        Prize prize = getTableView().getItems().get(getIndex());
                        prizeController.deleteAction(stage, prize);
                    };

                    final Button deleteButton = ButtonFactory.createDeleteButton(deleteAction);

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                        }
                    }
                };
            }
        };

        deleteColumn.setCellFactory(cellFactory);

        return deleteColumn;
    }

    public void configureTableColumnsWidthForPrize(TableView<Prize> table) {
        TableColumn<Prize, ?> numberColumn = table.getColumns().get(0);
        TableColumn<Prize, ?> nameColumn = table.getColumns().get(1);
        TableColumn<Prize, ?> programColumn = table.getColumns().get(2);
        TableColumn<Prize, ?> countColumn = table.getColumns().get(3);
        TableColumn<Prize, ?> editColumn = table.getColumns().get(4);
        TableColumn<Prize, ?> deleteColumn = table.getColumns().get(5);

        double numberColumnPercentage = 0.05;
        double countColumnPercentage = 0.1;
        double editColumnPercentage = 0.1;
        double deleteColumnPercentage = 0.1;

        double remained = 1.0 - (numberColumnPercentage + countColumnPercentage + editColumnPercentage + deleteColumnPercentage);
        double lastSpace = remained / 2;
        double nameColumnPercentage = lastSpace;
        double programColumnPercentage = lastSpace;

        numberColumn.prefWidthProperty().bind(table.widthProperty().multiply(numberColumnPercentage));
        nameColumn.prefWidthProperty().bind(table.widthProperty().multiply(nameColumnPercentage));
        programColumn.prefWidthProperty().bind(table.widthProperty().multiply(programColumnPercentage));
        countColumn.prefWidthProperty().bind(table.widthProperty().multiply(countColumnPercentage));
        editColumn.prefWidthProperty().bind(table.widthProperty().multiply(editColumnPercentage));
        deleteColumn.prefWidthProperty().bind(table.widthProperty().multiply(deleteColumnPercentage));
    }

    public TableColumn<Prize, Integer> createCountColumnForPrize() {
        TableColumn<Prize, Integer> countColumn = new TableColumn<>("Count");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
        return countColumn;
    }

    public TableColumn<Prize, String> createColumnForPrize(String value) {
        TableColumn<Prize, String> column = new TableColumn<>(value);
        column.setCellValueFactory(new PropertyValueFactory<>(value.toLowerCase()));
        return column;
    }

    public TableColumn<Profile, Profile> createActionColumnForProfile(Stage primaryStage, ProfileController profileController, MainController mainController) {
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

    public TableColumn<Profile, Profile> createEditColumnForProfile(Stage primaryStage, ProfileController profileController, MainController mainController) {
        TableColumn<Profile, Profile> column = new TableColumn<>("Edit");
        column.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
        column.setCellFactory(param -> new TableCell<Profile, Profile>() {
            @Override
            protected void updateItem(Profile profile, boolean empty) {
                super.updateItem(profile, empty);
                if (profile == null || empty) {
                    setGraphic(null);
                    return;
                }

                EventHandler<ActionEvent> action = e -> {
                    profileController.showEditProfileScreen(primaryStage, profile);
                };

                Button button = ButtonFactory.createEditButton(action);
                setGraphic(button);
            }
        });
        return column;
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



    public static TableColumn<Profile, Void> createScanColumn(Stage primaryStage, ScanController scanController) {
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
                            scanController.showScreen(primaryStage, profile);
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
