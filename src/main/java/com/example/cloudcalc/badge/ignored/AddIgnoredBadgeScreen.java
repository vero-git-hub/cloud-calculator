package com.example.cloudcalc.badge.ignored;

import com.example.cloudcalc.ButtonFactory;
import com.example.cloudcalc.Constants;
import com.example.cloudcalc.UICallbacks;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class AddIgnoredBadgeScreen {
    private final UICallbacks uiCallbacks;
    private final FileOperationManager fileOperationManager;
    private final IgnoredBadgeScreen ignoredBadgeScreen;

    public AddIgnoredBadgeScreen(UICallbacks uiCallbacks, FileOperationManager fileOperationManager, IgnoredBadgeScreen ignoredBadgeScreen) {
        this.uiCallbacks = uiCallbacks;
        this.fileOperationManager = fileOperationManager;
        this.ignoredBadgeScreen = ignoredBadgeScreen;
    }

    public void showAddIgnoreBadgeScreen(Stage primaryStage) {
        VBox layout = new VBox(10);

        Button backButton = ButtonFactory.createBackButton(e -> ignoredBadgeScreen.showIgnoreScreen(primaryStage));
        Label titleLabel = uiCallbacks.createLabel("Add Ignore Badge Screen");

        Button saveIgnoreBadgeButton = ButtonFactory.createSaveIgnoreBadgeButton(
                () -> {
                    List<String> ignoredBadges = fileOperationManager.loadIgnoredBadgesFromFile(Constants.IGNORE_FILE);
                    TextField nameField = (TextField) layout.getScene().lookup("#nameField");
                    ignoredBadges.add(nameField.getText());
                    fileOperationManager.saveIgnoredBadgesToFile(ignoredBadges, Constants.IGNORE_FILE);
                    ignoredBadgeScreen.showIgnoreScreen(primaryStage);
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
