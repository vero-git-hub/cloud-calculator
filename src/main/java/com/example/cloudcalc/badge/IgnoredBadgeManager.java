package com.example.cloudcalc.badge;

import com.example.cloudcalc.ButtonFactory;
import com.example.cloudcalc.Constants;
import com.example.cloudcalc.UICallbacks;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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

        Button backButton = ButtonFactory.createBackButton(e -> uiCallbacks.showMainScreen(primaryStage));
        Label titleLabel = uiCallbacks.createLabel("Ignore Screen");
        Button addButton = ButtonFactory.createAddButton(e -> showAddIgnoreBadgeScreen(primaryStage));

        HBox topLayout = uiCallbacks.createTopLayout(backButton, titleLabel, addButton);

        layout.getChildren().addAll(
                topLayout,
                createIgnoredBadgesList(primaryStage)
        );

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);

        uiCallbacks.createScene(scrollPane, primaryStage);
    }

    private VBox createIgnoredBadgesList(Stage primaryStage) {
        VBox badgesList = new VBox(10);
        List<String> ignoredBadges = loadIgnoredBadgesFromFile(Constants.IGNORE_FILE);

        if (ignoredBadges.isEmpty()) {
            badgesList.getChildren().add(uiCallbacks.createLabel("No ignored badges"));
        } else {
            for (String badge : ignoredBadges) {
                badgesList.getChildren().add(createBadgeRow(primaryStage, badge, ignoredBadges));
            }
        }

        return badgesList;
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

    private HBox createBadgeRow(Stage primaryStage, String badge, List<String> ignoredBadges) {
        HBox badgeRow = new HBox(10);
        Label badgeLabel = new Label(badge);

        EventHandler<ActionEvent> deleteAction = e -> {
            if (uiCallbacks.showConfirmationAlert("Confirmation Dialog", "Delete Badge", "Are you sure you want to delete this badge?")) {
                ignoredBadges.remove(badge);
                saveIgnoredBadgesToFile(ignoredBadges, Constants.IGNORE_FILE);
                showIgnoreScreen(primaryStage);
            }
        };

        Button deleteButton = ButtonFactory.createDeleteButton(deleteAction);

        badgeRow.getChildren().addAll(badgeLabel, deleteButton);
        return badgeRow;
    }

}
