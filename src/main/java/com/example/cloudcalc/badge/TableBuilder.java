package com.example.cloudcalc.badge;

import com.example.cloudcalc.Constants;
import com.example.cloudcalc.UICallbacks;
import com.example.cloudcalc.button.ButtonFactory;
import com.example.cloudcalc.util.Notification;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class TableBuilder {

    private static String fileName;
    private static UICallbacks uiCallbacks;
    private static FileOperationManager fileOperationManager;
    private static ScreenDisplayable screenDisplayable;
    private static String addScreenLabel;
    private static TableView<String> table;

    public static void initVariables(String fileName, UICallbacks uiCallbacks, FileOperationManager fileOperationManager, ScreenDisplayable screenDisplayable, String addScreenLabel) {
        TableBuilder.fileName = fileName;
        TableBuilder.uiCallbacks = uiCallbacks;
        TableBuilder.fileOperationManager = fileOperationManager;
        TableBuilder.screenDisplayable = screenDisplayable;
        TableBuilder.addScreenLabel = addScreenLabel;
    }

    public static void buildScreen(Stage primaryStage, String title) {
        VBox layout = new VBox(10);
        HBox topLayout = createTopLayout(primaryStage, title);

        TableView<String> table = createBadgeTable(primaryStage);

        layout.getChildren().addAll(topLayout, table);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);

        VBox.setVgrow(table, Priority.ALWAYS);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        uiCallbacks.createScene(scrollPane, primaryStage);
    }

    private static HBox createTopLayout(Stage primaryStage, String title) {
        Button backButton = ButtonFactory.createBackButton(e -> uiCallbacks.showMainScreen(primaryStage));
        Label titleLabel = uiCallbacks.createLabel(title);
        Button addButton = ButtonFactory.createAddButton(e -> buildAddScreen(primaryStage));

        return uiCallbacks.createTopLayout(backButton, titleLabel, addButton);
    }

    private static TableView<String> createBadgeTable(Stage primaryStage) {
        table = new TableView<>();

        TableColumn<String, Integer> indexColumn = createIndexColumn(table);
        TableColumn<String, String> nameColumn = createNameColumn();
        TableColumn<String, Void> deleteColumn = createDeleteColumn(primaryStage, table);

        table.getColumns().addAll(indexColumn, nameColumn, deleteColumn);

        List<String> badges = fileOperationManager.loadBadgesFromFile(fileName);
        table.getItems().addAll(badges);

        configureTableColumnsWidth();

        return table;
    }

    private static TableColumn<String, Integer> createIndexColumn(TableView<String> table) {
        TableColumn<String, Integer> indexColumn = new TableColumn<>("№");
        indexColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(cellData.getValue()) + 1));
        return indexColumn;
    }

    private static TableColumn<String, String> createNameColumn() {
        TableColumn<String, String> nameColumn = new TableColumn<>("Badge name");
        nameColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
        return nameColumn;
    }

    private static TableColumn<String, Void> createDeleteColumn(Stage primaryStage, TableView<String> table) {
        TableColumn<String, Void> deleteColumn = new TableColumn<>("Delete");
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            final Button deleteButton = ButtonFactory.createDeleteButton(e -> {
                if (uiCallbacks.showConfirmationAlert("Confirmation Dialog", "Delete Badge", "Are you sure you want to delete this badge?")) {
                    String badge = getTableView().getItems().get(getIndex());
                    List<String> badges = getTableView().getItems();
                    badges.remove(badge);
                    fileOperationManager.saveBadgesToFile(badges, fileName);
                    screenDisplayable.showScreen(primaryStage);
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

    private static void configureTableColumnsWidth() {
        double indexColumnPercentage = 0.05;
        double deleteColumnPercentage = 0.1;
        double nameColumnPercentage = 1.0 - (indexColumnPercentage + deleteColumnPercentage);

        table.getColumns().get(0).prefWidthProperty().bind(table.widthProperty().multiply(indexColumnPercentage));
        table.getColumns().get(1).prefWidthProperty().bind(table.widthProperty().multiply(nameColumnPercentage));
        table.getColumns().get(2).prefWidthProperty().bind(table.widthProperty().multiply(deleteColumnPercentage));
    }

    private static void buildAddScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> screenDisplayable.showScreen(primaryStage));
        Label titleLabel = uiCallbacks.createLabel(addScreenLabel);

        Button saveIgnoreBadgeButton = ButtonFactory.createSaveIgnoreBadgeButton(
                () -> {
                    List<String> ignoredBadges = fileOperationManager.loadBadgesFromFile(fileName);
                    TextField nameField = (TextField) layout.getScene().lookup("#nameField");

                    String badgeName = nameField.getText().trim();
                    if (badgeName.isEmpty()) {
                        Notification.showAlert("Input Error", "Empty Badge Name", "Please enter a valid badge name.");
                        return;
                    }

                    ignoredBadges.add(nameField.getText());
                    fileOperationManager.saveBadgesToFile(ignoredBadges, fileName);
                    screenDisplayable.showScreen(primaryStage);
                },
                () -> (TextField) layout.getScene().lookup("#nameField")
        );

        HBox topLayout = uiCallbacks.createTopLayout(backButton, titleLabel);

        layout.getChildren().addAll(
                topLayout,
                uiCallbacks.createNameTextField(),
                saveIgnoreBadgeButton
        );

        uiCallbacks.createScene(layout, primaryStage);
    }
}
