package com.example.cloudcalc.badge;

import com.example.cloudcalc.ButtonFactory;
import com.example.cloudcalc.Constants;
import com.example.cloudcalc.UICallbacks;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class IgnoredBadgeManager {

    private final UICallbacks uiCallbacks;

    public IgnoredBadgeManager(UICallbacks uiCallbacks) {
        this.uiCallbacks = uiCallbacks;
    }

    public List<String> loadIgnoredBadgesFromFile(String fileName) {
        List<String> ignoredBadges = new ArrayList<>();

        Path filePath = Paths.get(fileName);
        if (!Files.exists(filePath)) {
            return ignoredBadges;
        }

        try {
            String content = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(content);

            for (int j = 0; j < jsonArray.length(); j++) {
                ignoredBadges.add(jsonArray.getString(j));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return ignoredBadges;
    }

    public void saveIgnoredBadgesToFile(List<String> ignoredBadges, String fileName) {
        JSONArray jsonArray = new JSONArray(ignoredBadges);

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(jsonArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showIgnoreScreen(Stage primaryStage) {
        VBox layout = new VBox(10);
        HBox topLayout = createTopLayout(primaryStage);
        TableView<String> table = createBadgeTable(primaryStage);

        layout.getChildren().addAll(topLayout, table);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);

        uiCallbacks.createScene(scrollPane, primaryStage);
    }

    private HBox createTopLayout(Stage primaryStage) {
        Button backButton = ButtonFactory.createBackButton(e -> uiCallbacks.showMainScreen(primaryStage));
        Label titleLabel = uiCallbacks.createLabel("IGNORE");
        Button addButton = ButtonFactory.createAddButton(e -> showAddIgnoreBadgeScreen(primaryStage));

        return uiCallbacks.createTopLayout(backButton, titleLabel, addButton);
    }

    private TableView<String> createBadgeTable(Stage primaryStage) {
        TableView<String> table = new TableView<>();

        TableColumn<String, Integer> indexColumn = new TableColumn<>("No.");
        indexColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(cellData.getValue()) + 1));

        TableColumn<String, String> badgeColumn = new TableColumn<>("Badge");
        badgeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));

        TableColumn<String, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            final Button deleteButton = ButtonFactory.createDeleteButton(e -> {
                if (uiCallbacks.showConfirmationAlert("Confirmation Dialog", "Delete Badge", "Are you sure you want to delete this badge?")) {
                    String badge = getTableView().getItems().get(getIndex());
                    List<String> ignoredBadges = getTableView().getItems();
                    ignoredBadges.remove(badge);
                    saveIgnoredBadgesToFile(ignoredBadges, Constants.IGNORE_FILE);
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

        table.getColumns().addAll(indexColumn, badgeColumn, actionColumn);

        List<String> ignoredBadges = loadIgnoredBadgesFromFile(Constants.IGNORE_FILE);
        table.getItems().addAll(ignoredBadges);

        return table;
    }

    private void showAddIgnoreBadgeScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> showIgnoreScreen(primaryStage));
        Label titleLabel = uiCallbacks.createLabel("Add Ignore Badge Screen");

        Button saveIgnoreBadgeButton = ButtonFactory.createSaveIgnoreBadgeButton(
                () -> {
                    List<String> ignoredBadges = loadIgnoredBadgesFromFile(Constants.IGNORE_FILE);
                    TextField nameField = (TextField) layout.getScene().lookup("#nameField");
                    ignoredBadges.add(nameField.getText());
                    saveIgnoredBadgesToFile(ignoredBadges, Constants.IGNORE_FILE);
                    showIgnoreScreen(primaryStage);
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
