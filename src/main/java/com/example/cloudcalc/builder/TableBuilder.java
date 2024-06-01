package com.example.cloudcalc.builder;

import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.constant.FileName;
import com.example.cloudcalc.controller.*;
import com.example.cloudcalc.entity.prize.Prize;
import com.example.cloudcalc.entity.prize.PrizeInfo;
import com.example.cloudcalc.entity.Profile;
import com.example.cloudcalc.entity.ProgramPrize;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

public class TableBuilder {

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
            List<ProgramPrize> programPrizes = cellData.getValue().getProgramPrizes();
            if (programPrizes == null || programPrizes.isEmpty()) {
                return new SimpleStringProperty("No prizes");
            }

            List<String> prizes = programPrizes.stream()
                    .flatMap(programPrize -> programPrize.getPrizeInfoList().stream()
                            .map(PrizeInfo::getPrize))
                    .collect(Collectors.toList());

            if (prizes.isEmpty()) {
                return new SimpleStringProperty("No prizes");
            }

            return new SimpleStringProperty(String.join(", ", prizes));
        });
        return prizesColumn;
    }

    public TableView<Profile> createTableForStats(Stage stage, ProfileModel profileModel, PrizeController prizeController) {
        TableView<Profile> mainTable = new TableView<>();

        TableColumn<Profile, ?> numberColumn = createNumberColumnForStats(mainTable);
        TableColumn<Profile, ?> dateColumn = createLastUpdatedColumnForStats();
        TableColumn<Profile, ?> nameColumn = createColumn("Name");
        TableColumn<Profile, ?> programColumn = createColumn("Programs");
        TableColumn<Profile, ?> prizesColumn = createPrizesColumnForStats();
        TableColumn<Profile, Void> scanColumn = createScanColumn(stage, prizeController.getScanController(), false);

        mainTable.getColumns().addAll(
                numberColumn,
                dateColumn,
                nameColumn,
                programColumn,
                prizesColumn,
                scanColumn
        );

        setColumnWidthForStats(mainTable);

        List<Profile> profiles = profileModel.loadProfilesFromFile(FileName.PROFILES_FILE);
        mainTable.getItems().addAll(profiles);
        return mainTable;
    }

    public void configureTableColumnsWidthForMain(TableView<Profile> mainTable) {
        double numberColumnPercentage = 0.05;
        double nameColumnPercentage = 0.1;
        double linkColumnPercentage = 0.2;
        double scanColumnPercentage = 0.1;
        double editColumnPercentage = 0.1;
        double deleteColumnPercentage = 0.1;

        double programsColumnPercentage = 1.0 - (numberColumnPercentage + nameColumnPercentage + linkColumnPercentage + scanColumnPercentage + editColumnPercentage + deleteColumnPercentage);

        setColumnWidths(mainTable, numberColumnPercentage, nameColumnPercentage, linkColumnPercentage, programsColumnPercentage, scanColumnPercentage, editColumnPercentage, deleteColumnPercentage);
    }

    public void setColumnWidthForStats(TableView<Profile> table) {
        double numberColumnPercentage = 0.05;
        double dateColumnPercentage = 0.15;
        double nameColumnPercentage = 0.15;
        double programsColumnPercentage = 0.2;
        double prizesColumnPercentage = 0.35;
        double scanColumnPercentage = 0.1;

        setColumnWidths(table, numberColumnPercentage, dateColumnPercentage, nameColumnPercentage, programsColumnPercentage, prizesColumnPercentage, scanColumnPercentage);
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

        TableColumn<Profile, Void> scanColumn = createScanColumn(primaryStage, scanController, true);
        TableColumn<Profile, Profile> editColumn = createEditColumnForProfile(primaryStage, profileController, mainController);
        TableColumn<Profile, Profile> actionColumn = createActionColumnForProfile(primaryStage, profileController, mainController);

        mainTable.getColumns().addAll(numberColumn, nameColumn, linkColumn, programsColumn, editColumn, scanColumn, actionColumn);

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
                List<String> programNames = profile.getProgramPrizes().stream()
                        .map(ProgramPrize::getProgram)
                        .collect(Collectors.toList());
                String programsString = String.join(", ", programNames);
                return new ReadOnlyStringWrapper(programsString);
            });
        } else {
            column.setCellValueFactory(cellData -> {
                String propertyValue = "";
                switch (value.toLowerCase()) {
                    case "id":
                        propertyValue = Integer.toString(cellData.getValue().getId());
                        break;
                    case "name":
                        propertyValue = cellData.getValue().getName();
                        break;
                    case "link":
                        propertyValue = cellData.getValue().getLink();
                        break;
                    case "lastscanned":
                        propertyValue = cellData.getValue().getLastScannedDate();
                        break;
                    default:
                        break;
                }
                return new ReadOnlyStringWrapper(propertyValue);
            });
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
        TableColumn<Prize, Integer> countColumn = new TableColumn<>("Points");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("points"));
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
        TableColumn<Profile, Profile> column = new TableColumn<>("Toggle programs");
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
                    //TODO: move to new page Profiles
                    //profileController.showEditProfileScreen(primaryStage, profile);
                    profileController.toggleUserPrograms(primaryStage, profile);
                };

                Button button = ButtonFactory.createEditButton(action);
                setGraphic(button);
            }
        });
        return column;
    }

    public static TableColumn<Profile, Void> createScanColumn(Stage primaryStage, ScanController scanController, boolean isMovedFromMain) {
        TableColumn<Profile, Void> badgesColumn = new TableColumn<>("Details");
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
                            scanController.showScreen(primaryStage, profile, isMovedFromMain);
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