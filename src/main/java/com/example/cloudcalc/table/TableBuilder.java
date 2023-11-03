package com.example.cloudcalc.table;

import com.example.cloudcalc.UICallbacks;
import com.example.cloudcalc.badge.FileOperationManager;
import com.example.cloudcalc.badge.ScreenDisplayable;
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
    private static Label titleLabel;
    private static Label titleAddScreenLabel;
    static NameTextFieldUpdatable nameTextFieldUpdatable;
    private static String nameAlertTitle = "Input Error";
    private static String nameAlertHeader = "Empty Badge Name";
    private static String nameAlertContent = "Please enter a valid badge name.";
    private static String alertTitleDeleteBadge = "Confirmation Dialog";
    private static String alertHeaderDeleteBadge = "Delete Badge";
    private static String alertContentDeleteBadge = "Are you sure you want to delete this badge?";

    public static void initVariables(String fileName, UICallbacks uiCallbacks, FileOperationManager fileOperationManager, ScreenDisplayable screenDisplayable, String addScreenLabel, NameTextFieldUpdatable nameTextFieldUpdatable) {
        TableBuilder.fileName = fileName;
        TableBuilder.uiCallbacks = uiCallbacks;
        TableBuilder.fileOperationManager = fileOperationManager;
        TableBuilder.screenDisplayable = screenDisplayable;
        TableBuilder.addScreenLabel = addScreenLabel;
        TableBuilder.nameTextFieldUpdatable = nameTextFieldUpdatable;
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
        titleLabel = uiCallbacks.createLabel(title);
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

                boolean isConfirmationAlert = Notification.showConfirmationAlert(alertTitleDeleteBadge, alertHeaderDeleteBadge, alertContentDeleteBadge);

                if (isConfirmationAlert) {
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
        titleAddScreenLabel = uiCallbacks.createLabel(addScreenLabel);

        Button saveButton = ButtonFactory.createSaveButton(event -> {
            List<String> ignoredBadges = fileOperationManager.loadBadgesFromFile(fileName);
            TextField nameField = nameTextFieldUpdatable.getNameTextField();

            String badgeName = nameField.getText().trim();
            if (badgeName.isEmpty()) {
                Notification.showAlert(nameAlertTitle, nameAlertHeader, nameAlertContent);
                return;
            }

            ignoredBadges.add(badgeName);
            fileOperationManager.saveBadgesToFile(ignoredBadges, fileName);
            nameField.setText("");

            screenDisplayable.showScreen(primaryStage);
        });

        HBox topLayout = uiCallbacks.createTopLayout(backButton, titleAddScreenLabel);

        layout.getChildren().addAll(
                topLayout,
                nameTextFieldUpdatable.getNameTextField(),
                saveButton
        );

        uiCallbacks.createScene(layout, primaryStage);
    }

    public static void updateTitle(String newTitle) {
        if (titleLabel != null) {
            titleLabel.setText(newTitle);
        }
    }

    public static void updateAddScreenTitle(String newAddScreenTitle) {
        if (titleAddScreenLabel != null) {
            titleAddScreenLabel.setText(newAddScreenTitle);
        }
    }

    public static void updateNotificationAlert(String newTitleAlert, String newHeaderAlert, String newContentAlert) {
        if (nameAlertTitle != null) {
            nameAlertTitle = newTitleAlert;
        }
        if (nameAlertHeader != null) {
            nameAlertHeader = newHeaderAlert;
        }
        if (nameAlertContent != null) {
            nameAlertContent = newContentAlert;
        }

    }

    public static void updateDeleteAlert(String newAlertTitleDeleteBadge, String newAlertHeaderDeleteBadge, String newAlertContentDeleteBadge) {
        if(alertTitleDeleteBadge != null) {
            alertTitleDeleteBadge = newAlertTitleDeleteBadge;
        }
        if(alertHeaderDeleteBadge != null) {
            alertHeaderDeleteBadge = newAlertHeaderDeleteBadge;
        }
        if(alertContentDeleteBadge != null) {
            alertContentDeleteBadge = newAlertContentDeleteBadge;
        }

    }

}
