package com.example.cloudcalc.badge.ignored;

import com.example.cloudcalc.ButtonFactory;
import com.example.cloudcalc.Constants;
import com.example.cloudcalc.UICallbacks;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class IgnoredBadgeScreen {

    private final UICallbacks uiCallbacks;
    private final FileOperationManager fileOperationManager;
    private final AddIgnoredBadgeScreen addIgnoredBadgeScreen;
    private TableView<String> table;

    public IgnoredBadgeScreen(UICallbacks uiCallbacks, FileOperationManager fileOperationManager) {
        this.uiCallbacks = uiCallbacks;
        this.fileOperationManager = fileOperationManager;
        this.addIgnoredBadgeScreen = new AddIgnoredBadgeScreen(uiCallbacks, fileOperationManager, this);
    }

    public void showIgnoreScreen(Stage primaryStage) {
        VBox layout = new VBox(10);
        HBox topLayout = createTopLayout(primaryStage);
        TableView<String> table = createBadgeTable(primaryStage);

        layout.getChildren().addAll(topLayout, table);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);

        VBox.setVgrow(table, Priority.ALWAYS);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        uiCallbacks.createScene(scrollPane, primaryStage);
    }

    private HBox createTopLayout(Stage primaryStage) {
        Button backButton = ButtonFactory.createBackButton(e -> uiCallbacks.showMainScreen(primaryStage));
        Label titleLabel = uiCallbacks.createLabel("IGNORE");
        Button addButton = ButtonFactory.createAddButton(e -> addIgnoredBadgeScreen.showAddIgnoreBadgeScreen(primaryStage));

        return uiCallbacks.createTopLayout(backButton, titleLabel, addButton);
    }

    private TableView<String> createBadgeTable(Stage primaryStage) {
        table = new TableView<>();

        TableColumn<String, Integer> indexColumn = createIndexColumn(table);
        TableColumn<String, String> badgeColumn = createBadgeColumn();
        TableColumn<String, Void> actionColumn = createActionColumn(primaryStage, table);

        table.getColumns().addAll(indexColumn, badgeColumn, actionColumn);

        List<String> ignoredBadges = fileOperationManager.loadIgnoredBadgesFromFile(Constants.IGNORE_FILE);
        table.getItems().addAll(ignoredBadges);

        configureTableColumnsWidth();

        return table;
    }

    private void configureTableColumnsWidth() {
        double indexColumnPercentage = 0.05;
        double actionColumnPercentage = 0.1;
        double badgeColumnPercentage = 1.0 - (indexColumnPercentage + actionColumnPercentage);

        table.getColumns().get(0).prefWidthProperty().bind(table.widthProperty().multiply(indexColumnPercentage));
        table.getColumns().get(1).prefWidthProperty().bind(table.widthProperty().multiply(badgeColumnPercentage));
        table.getColumns().get(2).prefWidthProperty().bind(table.widthProperty().multiply(actionColumnPercentage));
    }

    private TableColumn<String, Integer> createIndexColumn(TableView<String> table) {
        TableColumn<String, Integer> indexColumn = new TableColumn<>("№");
        indexColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(cellData.getValue()) + 1));
        return indexColumn;
    }

    private TableColumn<String, String> createBadgeColumn() {
        TableColumn<String, String> badgeColumn = new TableColumn<>("Badge");
        badgeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
        return badgeColumn;
    }

    private TableColumn<String, Void> createActionColumn(Stage primaryStage, TableView<String> table) {
        TableColumn<String, Void> actionColumn = new TableColumn<>("Delete");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            final Button deleteButton = ButtonFactory.createDeleteButton(e -> {
                if (uiCallbacks.showConfirmationAlert("Confirmation Dialog", "Delete Badge", "Are you sure you want to delete this badge?")) {
                    String badge = getTableView().getItems().get(getIndex());
                    List<String> ignoredBadges = getTableView().getItems();
                    ignoredBadges.remove(badge);
                    fileOperationManager.saveIgnoredBadgesToFile(ignoredBadges, Constants.IGNORE_FILE);
                    showIgnoreScreen(primaryStage);
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
        return actionColumn;
    }
}
